# Run the microservices and gateway in separate PowerShell windows (Windows)
# Usage: Open PowerShell and run: .\run-backend.ps1
# Each service will be started in a new window so logs are visible.

$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Definition)

# Start User Service (port 8081)
Start-Process powershell -ArgumentList "-NoExit", "-Command", ".\gradlew.bat -p backend\user-service bootRun" -WorkingDirectory $root -WindowStyle Normal

# Start Product Service (port 8082)
Start-Process powershell -ArgumentList "-NoExit", "-Command", ".\gradlew.bat -p backend\product-service bootRun" -WorkingDirectory $root -WindowStyle Normal

# Start Gateway Service (port 8080)
Start-Process powershell -ArgumentList "-NoExit", "-Command", ".\gradlew.bat -p backend\gateway-service bootRun" -WorkingDirectory $root -WindowStyle Normal

Write-Host "Started user-service (8081), product-service (8082) and gateway (8080). Tail logs in windows." -ForegroundColor Green
