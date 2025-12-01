Param(
    [string]$baseUrl = "",
    [switch]$useDevice
)

# CI quick orchestration script to start backend, install debug app, set base URL on device and run integration tests.
Write-Host "CI-run integration script starting..." -ForegroundColor Cyan

if ($useDevice) {
    Write-Host "Expecting a device to be connected; install app and run tests on device..." -ForegroundColor Cyan
}

Write-Host "Step 1: Starting backend via run-backend.ps1..." -ForegroundColor Cyan
Start-Process -NoNewWindow -FilePath powershell -ArgumentList "-File ./scripts/run-backend.ps1"
Start-Sleep -Seconds 10

if ($useDevice) {
    Write-Host "Installing debug app to device (if gradle/app installDebug is set up)" -ForegroundColor Cyan
    cd ..\
    .\gradlew.bat :app:installDebug
    if (![string]::IsNullOrEmpty($baseUrl)) {
        Write-Host "Broadcasting base URL to device: $baseUrl" -ForegroundColor Cyan
        .\scripts\set-baseurl-adb.ps1 $baseUrl
    }
} else {
    if (![string]::IsNullOrEmpty($baseUrl)) {
        Write-Host "Running integration tests against $baseUrl" -ForegroundColor Cyan
        .\scripts\integration-test.ps1 $baseUrl
        exit 0
    }
}

Write-Host "Running default integration-test via gateway..." -ForegroundColor Cyan
.\scripts\integration-test.ps1

Write-Host "CI integration orchestration finished." -ForegroundColor Green
