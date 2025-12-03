#!/bin/bash
# Snyk Scan Results Analysis Script

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$REPO_ROOT"

echo "=========================================="
echo "Snyk Code Scan Results Summary"
echo "Date: $(date -u +"%Y-%m-%d %H:%M:%S UTC")"
echo "=========================================="
echo ""

# Function to count issues by severity from JSON
count_issues() {
    local json_file=$1
    local severity=$2
    
    if [ ! -f "$json_file" ]; then
        echo "0"
        return
    fi
    
    # Count results with matching severity level
    python3 -c "
import json
import sys
try:
    with open('$json_file', 'r') as f:
        data = json.load(f)
    count = sum(1 for run in data.get('runs', []) 
                for result in run.get('results', []) 
                if result.get('level', '') == '$severity')
    print(count)
except:
    print(0)
"
}

# Analyze each module
echo "Module Analysis:"
echo "----------------"

total_critical=0
total_high=0
total_medium=0
total_low=0

for module in backend frontend graphiql e2e-tests; do
    json_file="snyk/reports/snyk-${module}-code-report.json"
    
    if [ -f "$json_file" ]; then
        # Snyk uses 'error' for high/critical, 'warning' for medium, 'note' for low
        errors=$(count_issues "$json_file" "error")
        warnings=$(count_issues "$json_file" "warning")
        notes=$(count_issues "$json_file" "note")
        
        total=$((errors + warnings + notes))
        
        echo ""
        echo "📦 $module:"
        echo "   Total Issues: $total"
        echo "   - High/Critical: $errors"
        echo "   - Medium: $warnings"
        echo "   - Low: $notes"
        
        total_high=$((total_high + errors))
        total_medium=$((total_medium + warnings))
        total_low=$((total_low + notes))
    fi
done

echo ""
echo "=========================================="
echo "Overall Summary:"
echo "----------------"
echo "Total Issues: $((total_high + total_medium + total_low))"
echo "  - High/Critical: $total_high"
echo "  - Medium: $total_medium"
echo "  - Low: $total_low"
echo "=========================================="
