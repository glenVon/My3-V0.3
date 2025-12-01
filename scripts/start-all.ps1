Param(
    [switch]$StartEmulator,
    [switch]$InstallApp,
    [switch]$RunIntegration,
    [string]$BaseUrl = "http://10.0.2.2:8080/",
    [string]$DeviceSerial = "",
    [int]$TimeoutSec = 120
)

function Write-Info($msg) { Write-Host $msg -ForegroundColor Cyan }
function Write-OK($msg) { Write-Host $msg -ForegroundColor Green }
function Write-Warn($msg) { Write-Host $msg -ForegroundColor Yellow }
function Write-Err($msg) { Write-Host $msg -ForegroundColor Red }

Set-Location -Path (Split-Path -Parent $MyInvocation.MyCommand.Definition)

Write-Info "Starting full environment..."

# --- Start backend using the existing script ---
if (Test-Path "./run-backend.ps1") {
    Write-Info "Invoking run-backend.ps1 to start microservices..."
    Start-Process powershell -ArgumentList "-NoExit","-Command","./run-backend.ps1" -WorkingDirectory (Get-Location)
} else {
    Write-Warn "run-backend.ps1 not found; try starting services manually from backend/* subfolders"
}

function WaitForUrl($url, [int]$timeoutSec) {
    $end = (Get-Date).AddSeconds($timeoutSec)
    while ((Get-Date) -lt $end) {
        try {
            $resp = Invoke-RestMethod -Uri $url -Method GET -TimeoutSec 5 -ErrorAction Stop
            return $true
        } catch {
            Start-Sleep -Seconds 2
            continue
        }
    }
    return $false
}

# Wait for the gateway to be up (assumes gateway listening on /users or /actuator/health)
$healthUrl = [Uri]::new($BaseUrl)
$tryPaths = @('users', 'actuator/health', '')
$available = $false
Write-Info "Waiting for gateway at $BaseUrl (timeout: $TimeoutSec sec)"
foreach ($p in $tryPaths) {
    $url = $BaseUrl.TrimEnd('/') + '/' + $p.TrimStart('/')
    Write-Info "Checking $url"
    if (WaitForUrl $url $TimeoutSec) {
        Write-OK "Gateway responded at $url"
        $available = $true
        break
    }
}

if (-not $available) {
    Write-Warn "Can't reach backend at $BaseUrl. Continue anyway? (y/n)"
    $ans = Read-Host
    if ($ans -ne 'y') { Write-Err "Aborting."; exit 1 }
}

# Optionally start emulator (if asked)
if ($StartEmulator) {
    Write-Info "StartEmulator requested: trying to start an AVD..."
    # Try to locate emulator in PATH
    $emulatorCmd = Get-Command -ErrorAction SilentlyContinue emulator
    if ($null -eq $emulatorCmd) {
        Write-Warn "Emulator tool not found in PATH. Please start an emulator manually or add the emulator tool to PATH.";
    } else {
        # Start the default AVD; user can modify to a specific AVD if needed
        Write-Info "Starting default AVD (no window)"
        Start-Process -NoNewWindow -FilePath emulator -ArgumentList "-avd","Pixel_3a_API_30_x86","-no-window","-gpu","swiftshader_indirect","-no-audio" -WindowStyle Hidden -ErrorAction SilentlyContinue
        Write-Info "Waiting for device to be ready (adb)...";
        adb wait-for-device
        Write-OK "Emulator started or existing device connected visible to adb.";
    }
}

# If device serial specified, ensure device is present
if ($DeviceSerial -ne "") {
    $devices = adb devices | Select-String -Pattern ": device$" -SimpleMatch
    if ($devices -notmatch $DeviceSerial) {
        Write-Warn "Device $DeviceSerial not found in `adb devices` list. You can omit DeviceSerial to use any available device."
    }
}

# Optionally install app
if ($InstallApp) {
    Write-Info "Installing debug app on device/emulator..."
    & ..\gradlew.bat :app:installDebug
    Write-OK "App installed (if gradle succeeded)."
}

# Set base URL via ADB (broadcast helper script)
if ($DeviceSerial -ne "") { $adbArgs = "-s $DeviceSerial" } else { $adbArgs = "" }
try {
    # Try to set base URL on device via helper script
    if (Test-Path "./set-baseurl-adb.ps1") {
        & .\set-baseurl-adb.ps1 $BaseUrl
        Write-OK "Broadcasted base URL ($BaseUrl) to device via set-baseurl-adb.ps1"
    } else {
        Write-Warn "set-baseurl-adb.ps1 not found; attempting to broadcast with adb directly..."
        adb $adbArgs shell am broadcast -a com.egon.my3.action.SET_BASE_URL --es base_url "$BaseUrl"
    }
} catch {
    Write-Warn "Failed to broadcast base URL via ADB. The app may need to be pointed manually via DevSettings."
}

# Optionally run integration tests (PowerShell or Bash depending on platform)
if ($RunIntegration) {
    Write-Info "Running integration tests via scripts/integration-test.ps1 (or .sh) using base URL $BaseUrl"
    if (Test-Path "./integration-test.ps1") {
        & .\integration-test.ps1 $BaseUrl
    } elseif (Test-Path "./integration-test.sh") {
        bash ./integration-test.sh $BaseUrl
    } else {
        Write-Warn "No integration test script found."
    }
}

Write-OK "start-all completed. Verify logs in backend windows, and check the app UI on device/emulator." 
