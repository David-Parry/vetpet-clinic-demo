#!/bin/bash
# Snyk Code Security Scan Script for vetpet-clinic-demo
# This script runs comprehensive Snyk Code scans across all modules

set -e

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$REPO_ROOT"

echo "=========================================="
echo "Snyk Code Security Scan"
echo "Repository: vetpet-clinic-demo"
echo "Date: $(date -u +"%Y-%m-%d %H:%M:%S UTC")"
echo "Snyk CLI Version: $(snyk --version)"
echo "=========================================="
echo ""

# Create output directory for reports
mkdir -p snyk/reports

# Function to run Snyk Code scan
run_snyk_code_scan() {
    local module_name=$1
    local module_path=$2
    
    echo "----------------------------------------"
    echo "Scanning: $module_name"
    echo "Path: $module_path"
    echo "----------------------------------------"
    
    cd "$REPO_ROOT/$module_path"
    
    # Run Snyk Code test with JSON output
    echo "Running Snyk Code test..."
    if snyk code test --json > "$REPO_ROOT/snyk/reports/snyk-${module_name}-code-report.json" 2>&1; then
        echo "✓ Scan completed successfully (no issues found)"
    else
        echo "⚠ Scan completed with findings (see report)"
    fi
    
    # Also save human-readable output
    echo "Generating human-readable report..."
    snyk code test > "$REPO_ROOT/snyk/reports/snyk-${module_name}-code-report.txt" 2>&1 || true
    
    cd "$REPO_ROOT"
    echo ""
}

# Scan Backend (Spring Boot/GraphQL)
echo "=== Module 1/4: Backend ==="
run_snyk_code_scan "backend" "backend"

# Scan Frontend (React/TypeScript/Vite)
echo "=== Module 2/4: Frontend ==="
run_snyk_code_scan "frontend" "frontend"

# Scan GraphiQL UI
echo "=== Module 3/4: GraphiQL UI ==="
run_snyk_code_scan "graphiql" "petclinic-graphiql"

# Scan E2E Tests
echo "=== Module 4/4: E2E Tests ==="
run_snyk_code_scan "e2e-tests" "e2e-tests"

echo "=========================================="
echo "All scans completed!"
echo "Reports saved to: snyk/reports/"
echo "=========================================="
echo ""
echo "Report files:"
ls -lh snyk/reports/
