#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
PASS="${PASS:-secret123}"

# Public endpoints
CREATE_USER_EP="/api/users"
LOGIN_EP="/api/auth/login"

# Protected endpoints
CATEGORIES_EP="/api/categories"
PRODUCTS_EP="/api/products"
MOST_EXPENSIVE_EP="/api/products/most-expensive"
BY_CATEGORY_EP="/api/products/by-category/%s"
BY_CATEGORY_MOST_EXPENSIVE_EP="/api/products/by-category/%s/most-expensive"

# --- helpers ---
need() { command -v "$1" >/dev/null 2>&1 || { echo "❌ Missing command: $1"; exit 1; }; }
need curl
need jq
need date

json_post() {
  local url="$1"
  local data="$2"
  curl -sS -X POST "${BASE_URL}${url}" \
    -H "Content-Type: application/json" \
    -d "$data"
}

auth_get() {
  local token="$1"
  local url="$2"
  curl -sS -X GET "${BASE_URL}${url}" \
    -H "Authorization: Bearer ${token}"
}

auth_post() {
  local token="$1"
  local url="$2"
  local data="$3"
  curl -sS -X POST "${BASE_URL}${url}" \
    -H "Authorization: Bearer ${token}" \
    -H "Content-Type: application/json" \
    -d "$data"
}

# --- domain ops ---
create_user() {
  local i="$1"
  local email="user${i}@test.com"
  local name="User${i}"
  local age=$((18 + i))

  # UserCreateRequest: name, age, email, password
  # If user already exists, ignore creation error and still try login.
  json_post "$CREATE_USER_EP" "{
    \"name\":\"${name}\",
    \"age\":${age},
    \"email\":\"${email}\",
    \"password\":\"${PASS}\"
  }" >/dev/null || true

  echo "$email"
}

login_token() {
  local email="$1"
  # LoginRequest: login, password. AuthResponse: token
  local resp
  resp=$(json_post "$LOGIN_EP" "{
    \"login\":\"${email}\",
    \"password\":\"${PASS}\"
  }")

  echo "$resp" | jq -r '.token // empty'
}

create_category() {
  local token="$1"
  local name="$2"
  local desc="$3"

  # CategoryRequest: id(optional), name, description (both required)
  local resp
  resp=$(auth_post "$token" "$CATEGORIES_EP" "{
    \"id\": null,
    \"name\": \"${name}\",
    \"description\": \"${desc}\"
  }")

  echo "$resp" | jq -r '.id // empty'
}

create_product() {
  local token="$1"
  local name="$2"
  local desc="$3"
  local price="$4"
  local expiry_ms="$5"
  local cat_id="$6"

  # ProductRequest: id(optional), name, description, price>0, expiryDate (future), categoryId>0
  local resp
  resp=$(auth_post "$token" "$PRODUCTS_EP" "{
    \"id\": null,
    \"name\": \"${name}\",
    \"description\": \"${desc}\",
    \"price\": ${price},
    \"expiryDate\": ${expiry_ms},
    \"categoryId\": ${cat_id}
  }")

  echo "$resp" | jq -r '.id // empty'
}

# --- scenarios ---
scenario_read() {
  local token="$1"
  local prod_id="$2"
  local cat_id="$3"

  auth_get "$token" "$CATEGORIES_EP" >/dev/null || true
  auth_get "$token" "$PRODUCTS_EP" >/dev/null || true
  auth_get "$token" "${PRODUCTS_EP}/${prod_id}" >/dev/null || true
  auth_get "$token" "$(printf "$BY_CATEGORY_EP" "$cat_id")" >/dev/null || true

  for _ in $(seq 1 5); do
    auth_get "$token" "$PRODUCTS_EP" >/dev/null || true
  done
}

scenario_write() {
  local token="$1"
  local cat_id="$2"

  # Create 1 category + 1 product to generate DB_WRITE logs
  local cname="WriteCat_$(date +%s)_$RANDOM"
  local cdesc="Write scenario category"
  local new_cat_id
  new_cat_id=$(create_category "$token" "$cname" "$cdesc" || true)

  # Expiry date: now + 30 days (ms since epoch) -> java.util.Date parsed as timestamp
  local expiry_ms
  expiry_ms=$(($(date +%s)*1000 + 30*24*60*60*1000))

  local pname="WriteProd_$(date +%s)_$RANDOM"
  local pdesc="Write scenario product"
  local use_cat="${new_cat_id:-$cat_id}"

  auth_post "$token" "$PRODUCTS_EP" "{
    \"id\": null,
    \"name\": \"${pname}\",
    \"description\": \"${pdesc}\",
    \"price\": 99.99,
    \"expiryDate\": ${expiry_ms},
    \"categoryId\": ${use_cat}
  }" >/dev/null || true
}

scenario_most_expensive() {
  local token="$1"
  local cat_id="$2"

  auth_get "$token" "$MOST_EXPENSIVE_EP" >/dev/null || true
  auth_get "$token" "$(printf "$BY_CATEGORY_MOST_EXPENSIVE_EP" "$cat_id")" >/dev/null || true

  for _ in $(seq 1 3); do
    auth_get "$token" "$MOST_EXPENSIVE_EP" >/dev/null || true
  done
}

# --- main ---
echo "==> BASE_URL=$BASE_URL"
echo "==> Creating seed user + seeding categories/products..."

seed_email=$(create_user 0)
seed_token=$(login_token "$seed_email")
if [[ -z "$seed_token" ]]; then
  echo "❌ Cannot login seed user ($seed_email). Check that app is running and /api/auth/login works."
  exit 1
fi

# Seed categories (need description too)
cat1=$(create_category "$seed_token" "Category_A" "Seed category A")
cat2=$(create_category "$seed_token" "Category_B" "Seed category B")
cat3=$(create_category "$seed_token" "Category_C" "Seed category C")

if [[ -z "$cat1" || -z "$cat2" || -z "$cat3" ]]; then
  echo "❌ Seeding categories failed. Response IDs are empty."
  exit 1
fi

# Seed products (expiryDate must be future)
expiry_ms=$(($(date +%s)*1000 + 60*24*60*60*1000)) # +60 days

prod_ids=()
for i in $(seq 1 9); do
  # spread on categories
  if (( i % 3 == 1 )); then cid="$cat1"
  elif (( i % 3 == 2 )); then cid="$cat2"
  else cid="$cat3"
  fi

  pid=$(create_product "$seed_token" "SeedProd$i" "Seed product $i" $((10 + i)) "$expiry_ms" "$cid" || true)
  if [[ -n "$pid" ]]; then
    prod_ids+=("$pid")
  fi
done

if [[ ${#prod_ids[@]} -lt 1 ]]; then
  echo "❌ Seeding products failed (no product IDs)."
  exit 1
fi

default_prod="${prod_ids[0]}"
default_cat="$cat1"

echo "==> Seed OK: categories=($cat1,$cat2,$cat3), default_prod=$default_prod"

echo
echo "==> Creating 10 users + running scenarios..."
for i in $(seq 1 10); do
  email=$(create_user "$i")
  token=$(login_token "$email")

  if [[ -z "$token" ]]; then
    echo "❌ Login failed for $email"
    continue
  fi

  if (( i <= 4 )); then
    scenario="READ"
    scenario_read "$token" "$default_prod" "$default_cat"
  elif (( i <= 7 )); then
    scenario="WRITE"
    scenario_write "$token" "$default_cat"
  else
    scenario="MOST_EXPENSIVE"
    scenario_most_expensive "$token" "$default_cat"
  fi

  echo "✅ user$i ($email) scenario=$scenario"
done

echo
echo "==> Done. Check logs/app.jsonl"
