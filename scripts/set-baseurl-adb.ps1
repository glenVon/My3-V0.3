Param(
    [string]$baseUrl = "",
    [switch]$clear
)

if ($clear) {
    Write-Host "Broadcasting clear override to device..." -ForegroundColor Cyan
    adb shell am broadcast -a com.egon.my3.action.SET_BASE_URL --ez clear true
    exit 0
}

if ([string]::IsNullOrEmpty($baseUrl)) {
    Write-Host "Usage: .\set-baseurl-adb.ps1 <baseUrl>   or  .\set-baseurl-adb.ps1 -clear" -ForegroundColor Yellow
    exit 1
}

Write-Host "Broadcasting base URL $baseUrl to device..." -ForegroundColor Cyan
adb shell am broadcast -a com.egon.my3.action.SET_BASE_URL --es base_url "$baseUrl"
