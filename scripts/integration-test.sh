#!/usr/bin/env bash
set -euo pipefail

BASE_URL=${1:-"http://localhost:8080"}
echo "Running quick integration tests against $BASE_URL"

function check() {
  METHOD=$1
  PATH=$2
  BODY=${3:-}
  URL="$BASE_URL/$PATH"
  echo "---- $METHOD $URL ----"
  if [ -n "$BODY" ]; then
    echo "Body: $BODY"
    curl -s -X $METHOD -H "Content-Type: application/json" -d "$BODY" "$URL" | jq || true
  else
    curl -s -X $METHOD "$URL" | jq || true
  fi
}

# 1) List products
check GET "products"

# 2) Get product by id
check GET "products/1"

# 3) Create a product
BODY='{"id":999,"name":"Test Product","price":9.99,"description":"Created by integration script"}'
check POST "products" "$BODY"

# 4) List users
check GET "users"

# 5) Login (admin)
LOGIN='{"email":"admin@admin.com","password":"admin123"}'
check POST "users/login" "$LOGIN"

# 6) Get user by email
check GET "users/byEmail/admin@admin.com"

# 7) Update a user
BODY2='{"name":"Admin Updated","email":"admin@admin.com","password":"admin123","isAdmin":true}'
check PUT "users/1" "$BODY2"

# 8) Delete the created product (id 999)
check DELETE "products/999"

echo "Integration test script done."
