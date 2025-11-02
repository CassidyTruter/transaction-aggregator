#!/bin/bash

# Financial Transaction Aggregation System - API Test Script

# Configuration
ENVIRONMENT="${1:-dev}"  # Default to dev, pass 'prod' as argument for production

if [ "$ENVIRONMENT" = "prod" ]; then
    BASE_URL="https://localhost:8443"
    AUTH="-k -u prodAdmin:kiBkwb84th9Kj2Bm0"
    echo "=========================================="
    echo "PRODUCTION MODE - HTTPS with Authentication"
    echo "=========================================="
else
    BASE_URL="http://localhost:8080"
    AUTH="-u admin:devPassword123"
    echo "=========================================="
    echo "DEVELOPMENT MODE - HTTP with Authentication"
    echo "=========================================="
fi

echo "Base URL: ${BASE_URL}"
echo "=========================================="
echo ""

# Test 1: Health Check (No Auth Required)
echo "1. Health Check"
echo "GET ${BASE_URL}/actuator/health"
if [ "$ENVIRONMENT" = "prod" ]; then
    curl -s -k "${BASE_URL}/actuator/health" | jq '.'
else
    curl -s "${BASE_URL}/actuator/health" | jq '.'
fi
echo ""
echo "Press Enter to continue..."
read

# Test 2: Get All Transactions (First Page)
echo "2. Get All Transactions (Page 0, Size 10)"
echo "GET ${BASE_URL}/api/transactions?page=0&size=10"
curl -s ${AUTH} "${BASE_URL}/api/transactions?page=0&size=10" | jq '.'
echo ""
echo "Press Enter to continue..."
read

# Test 3: Get Transaction by ID
echo "3. Get Transaction by ID (ID=1)"
echo "GET ${BASE_URL}/api/transactions/1"
curl -s ${AUTH} "${BASE_URL}/api/transactions/1" | jq '.'
echo ""
echo "Press Enter to continue..."
read

# Test 4: Filter by Category
echo "4. Filter Transactions by Category (INCOME)"
echo "GET ${BASE_URL}/api/transactions?category=INCOME"
curl -s ${AUTH} "${BASE_URL}/api/transactions?category=INCOME&size=5" | jq '.content[] | {id, description, amount, category, subcategory}'
echo ""
echo "Press Enter to continue..."
read

# Test 5: Filter by Source Type
echo "5. Filter Transactions by Source Type (CARD)"
echo "GET ${BASE_URL}/api/transactions?sourceType=CARD"
curl -s ${AUTH} "${BASE_URL}/api/transactions?sourceType=CARD&size=5" | jq '.content[] | {id, description, amount, sourceType, category}'
echo ""
echo "Press Enter to continue..."
read

# Test 6: Filter by Date Range
echo "6. Filter Transactions by Date Range (Last 7 days)"
START_DATE=$(date -u -v-7d +'%Y-%m-%dT00:00:00' 2>/dev/null || date -u -d '7 days ago' +'%Y-%m-%dT00:00:00')
END_DATE=$(date -u +'%Y-%m-%dT23:59:59')
echo "GET ${BASE_URL}/api/transactions?startDate=${START_DATE}&endDate=${END_DATE}"
curl -s ${AUTH} "${BASE_URL}/api/transactions?startDate=${START_DATE}&endDate=${END_DATE}&size=5" | jq '.content[] | {id, transactionDate, description, amount}'
echo ""
echo "Press Enter to continue..."
read

# Test 7: Get Summary by Category
echo "7. Get Summary by Category"
echo "GET ${BASE_URL}/api/transactions/summary"
curl -s ${AUTH} "${BASE_URL}/api/transactions/summary" | jq '.'
echo ""
echo "Press Enter to continue..."
read

# Test 8: Get Summary by Account
echo "8. Get Summary by Account Number"
echo "GET ${BASE_URL}/api/transactions/summary/by-account"
curl -s ${AUTH} "${BASE_URL}/api/transactions/summary/by-account" | jq '.'
echo ""
echo "Press Enter to continue..."
read

# Test 9: Test Error Handling (Invalid ID)
echo "9. Test Error Handling - Transaction Not Found (ID=99999)"
echo "GET ${BASE_URL}/api/transactions/99999"
curl -s ${AUTH} "${BASE_URL}/api/transactions/99999" | jq '.'
echo ""
echo "Press Enter to continue..."
read

# Test 10: Test Validation - Invalid Date Range
echo "10. Test Validation - Invalid Date Range (startDate > endDate)"
echo "GET ${BASE_URL}/api/transactions?startDate=2024-12-31T00:00:00&endDate=2024-01-01T00:00:00"
curl -s ${AUTH} "${BASE_URL}/api/transactions?startDate=2024-12-31T00:00:00&endDate=2024-01-01T00:00:00" | jq '.'
echo ""
echo "Press Enter to continue..."
read

# Test 11: Sorting
echo "11. Sort Transactions by Amount (Descending)"
echo "GET ${BASE_URL}/api/transactions?sortBy=amount&sortDir=desc&size=5"
curl -s ${AUTH} "${BASE_URL}/api/transactions?sortBy=amount&sortDir=desc&size=5" | jq '.content[] | {id, description, amount}'
echo ""
echo "Press Enter to continue..."
read

# Test 12: Pagination
echo "12. Test Pagination - Get Second Page"
echo "GET ${BASE_URL}/api/transactions?page=1&size=5"
curl -s ${AUTH} "${BASE_URL}/api/transactions?page=1&size=5" | jq '{totalElements, totalPages, currentPage: .number, size, numberOfElements}'
echo ""
echo "Press Enter to continue..."
read

# Test 13: Filter by Subcategory
echo "13. Filter Transactions by Subcategory (GROCERIES)"
echo "GET ${BASE_URL}/api/transactions?category=SHOPPING&subcategory=GROCERIES"
curl -s ${AUTH} "${BASE_URL}/api/transactions?category=SHOPPING&size=5" | jq '.content[] | select(.subcategory == "GROCERIES") | {id, description, amount, subcategory}'
echo ""
echo "Press Enter to continue..."
read

# Test 14: Test Authentication Failure
echo "14. Test Authentication - Invalid Credentials"
echo "GET ${BASE_URL}/api/transactions"
if [ "$ENVIRONMENT" = "prod" ]; then
    curl -s -k -u wronguser:wrongpass "${BASE_URL}/api/transactions" -w "\nHTTP Status: %{http_code}\n"
else
    curl -s -u wronguser:wrongpass "${BASE_URL}/api/transactions" -w "\nHTTP Status: %{http_code}\n"
fi
echo ""
echo "Press Enter to continue..."
read

# Test 15: Count Total Transactions
echo "15. Count Total Transactions in Database"
echo "GET ${BASE_URL}/api/transactions?size=1"
curl -s ${AUTH} "${BASE_URL}/api/transactions?size=1" | jq '{totalElements, totalPages}'
echo ""

echo "=========================================="
echo "All tests completed!"
echo "=========================================="
echo ""
echo "To view Swagger UI documentation, visit:"
echo "${BASE_URL}/swagger-ui.html"
echo ""
echo "Usage:"
echo "  ./test-api.sh          # Test development environment (HTTP:8080)"
echo "  ./test-api.sh prod     # Test production environment (HTTPS:8443)"
echo ""
