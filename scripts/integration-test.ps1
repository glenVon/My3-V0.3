# Quick integration tests via the gateway (PowerShell)
# Usage: Open PowerShell and run: .\integration-test.ps1
# Requires: curl (or use Invoke-RestMethod) and that gateway runs on http://localhost:8080

$gateway = if ($args.Length -gt 0) { $args[0] } else { 'http://localhost:8080' }

Write-Host "Running quick integration tests against gateway $gateway" -ForegroundColor Cyan

function check {
    param(
        [string]$method,
        [string]$path,
        [string]$body = $null
    )
    $url = "$gateway$path"
    Write-Host "---- $method $url ----"
    if ($body) {
        Write-Host "Body: $body"
        $response = Invoke-RestMethod -Uri $url -Method $method -ContentType 'application/json' -Body $body -ErrorAction SilentlyContinue
    } else {
        $response = Invoke-RestMethod -Uri $url -Method $method -ErrorAction SilentlyContinue
    }
    if ($response -ne $null) {
        Write-Host "Response:" -ForegroundColor Yellow
        $response | ConvertTo-Json -Depth 4 | Write-Host
    } else {
        Write-Host "No response or error (check service logs)." -ForegroundColor Red
    }
}

# 1) List products
check -method 'GET' -path 'products'

# 2) Get product by id
check -method 'GET' -path 'products/1'

# 3) Create a product
$body = '{"id": 999, "name":"Test Product", "price":9.99, "description":"Created by integration script"}'
check -method 'POST' -path 'products' -body $body

# 4) List users
check -method 'GET' -path 'users'

# 5) Login (admin)
$login = '{"email":"admin@admin.com","password":"admin123"}'
check -method 'POST' -path 'users/login' -body $login

# 6) Get user by email
check -method 'GET' -path 'users/byEmail/admin@admin.com'

# 7) Update a user
$body2 = '{"name":"Admin Updated","email":"admin@admin.com","password":"admin123","isAdmin":true}'
check -method 'PUT' -path 'users/1' -body $body2

# 8) Delete the created product (id 999)
check -method 'DELETE' -path 'products/999'

Write-Host "Integration test script done. Inspect responses and logs for any issues." -ForegroundColor Green
