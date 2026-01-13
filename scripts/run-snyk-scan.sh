#!/bin/bash
set -e

# Snyk Scanning Orchestration Script
# Runs comprehensive Snyk scans across all project modules

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
SNYK_SEVERITY_THRESHOLD="${SNYK_SEVERITY_THRESHOLD:-high}"
SNYK_ADDITIONAL_FLAGS="${SNYK_ADDITIONAL_FLAGS:-}"
DRY_RUN=false
REPORTS_DIR="reports"

# Parse arguments
for arg in "$@"; do
  case $arg in
    --dry-run)
      DRY_RUN=true
      shift
      ;;
  esac
done

echo -e "${GREEN}=== Snyk Security Scanning ===${NC}"
echo "Severity Threshold: $SNYK_SEVERITY_THRESHOLD"
echo "Reports Directory: $REPORTS_DIR"
echo ""

# Validate prerequisites
echo -e "${YELLOW}[1/6] Validating prerequisites...${NC}"

if [ -z "$SNYK_TOKEN" ]; then
  echo -e "${RED}ERROR: SNYK_TOKEN environment variable is not set${NC}"
  echo "Please set your Snyk API token: export SNYK_TOKEN=your-token-here"
  exit 1
fi

if ! command -v snyk &> /dev/null; then
  echo -e "${RED}ERROR: Snyk CLI is not installed${NC}"
  echo "Install via: npm install -g snyk"
  exit 1
fi

if ! command -v pnpm &> /dev/null; then
  echo -e "${YELLOW}WARNING: pnpm not found, will attempt to use npm${NC}"
fi

if ! command -v docker &> /dev/null; then
  echo -e "${YELLOW}WARNING: docker not found, container scans will be skipped${NC}"
fi

if ! command -v java &> /dev/null; then
  echo -e "${YELLOW}WARNING: java not found, backend scans may fail${NC}"
fi

echo -e "${GREEN}Prerequisites validated${NC}"
echo ""

# Create reports directory
mkdir -p "$REPORTS_DIR"

# Authenticate with Snyk
echo -e "${YELLOW}[2/6] Authenticating with Snyk...${NC}"
if [ "$DRY_RUN" = true ]; then
  echo "[DRY RUN] Would run: snyk auth"
else
  snyk auth "$SNYK_TOKEN" || echo "Already authenticated"
fi
echo ""

# Backend scan
echo -e "${YELLOW}[3/6] Scanning backend (Maven)...${NC}"
if [ "$DRY_RUN" = true ]; then
  echo "[DRY RUN] Would run: cd backend && snyk test --all-projects --detection-depth=6 --severity-threshold=$SNYK_SEVERITY_THRESHOLD --json-file-output=../$REPORTS_DIR/backend-snyk.json $SNYK_ADDITIONAL_FLAGS"
else
  cd backend
  echo "Installing dependencies..."
  ../mvnw dependency:resolve -q || true
  echo "Running Snyk scan..."
  snyk test --all-projects --detection-depth=6 --severity-threshold="$SNYK_SEVERITY_THRESHOLD" --json-file-output="../$REPORTS_DIR/backend-snyk.json" $SNYK_ADDITIONAL_FLAGS || echo "Backend scan completed with findings"
  cd ..
fi
echo ""

# Frontend scan
echo -e "${YELLOW}[4/6] Scanning frontend (pnpm/npm)...${NC}"
if [ "$DRY_RUN" = true ]; then
  echo "[DRY RUN] Would run: cd frontend && pnpm install && snyk test --severity-threshold=$SNYK_SEVERITY_THRESHOLD --json-file-output=../$REPORTS_DIR/frontend-snyk.json $SNYK_ADDITIONAL_FLAGS"
else
  cd frontend
  echo "Installing dependencies..."
  if command -v pnpm &> /dev/null; then
    pnpm install --frozen-lockfile || pnpm install
  else
    npm install
  fi
  echo "Running Snyk scan..."
  snyk test --severity-threshold="$SNYK_SEVERITY_THRESHOLD" --json-file-output="../$REPORTS_DIR/frontend-snyk.json" $SNYK_ADDITIONAL_FLAGS || echo "Frontend scan completed with findings"
  cd ..
fi
echo ""

# E2E tests scan
echo -e "${YELLOW}[5/6] Scanning e2e-tests...${NC}"
if [ "$DRY_RUN" = true ]; then
  echo "[DRY RUN] Would run: cd e2e-tests && npm install && snyk test --file=package.json --severity-threshold=$SNYK_SEVERITY_THRESHOLD --json-file-output=../$REPORTS_DIR/e2e-snyk.json $SNYK_ADDITIONAL_FLAGS"
else
  cd e2e-tests
  echo "Installing dependencies..."
  npm install
  echo "Running Snyk scan..."
  snyk test --file=package.json --severity-threshold="$SNYK_SEVERITY_THRESHOLD" --json-file-output="../$REPORTS_DIR/e2e-snyk.json" $SNYK_ADDITIONAL_FLAGS || echo "E2E scan completed with findings"
  cd ..
fi
echo ""

# Container scan
echo -e "${YELLOW}[6/6] Scanning Docker containers...${NC}"
if [ "$DRY_RUN" = true ]; then
  echo "[DRY RUN] Would run: docker-compose build && snyk container test for each image"
else
  if command -v docker &> /dev/null; then
    echo "Building containers..."
    docker-compose -f docker-compose-petclinic.yml build || echo "Container build completed"
    
    echo "Scanning backend container..."
    BACKEND_IMAGE=$(docker-compose -f docker-compose-petclinic.yml config | grep "image:" | grep backend | awk '{print $2}' | head -1)
    if [ -n "$BACKEND_IMAGE" ]; then
      snyk container test "$BACKEND_IMAGE" --severity-threshold="$SNYK_SEVERITY_THRESHOLD" --json-file-output="$REPORTS_DIR/backend-container-snyk.json" $SNYK_ADDITIONAL_FLAGS || echo "Backend container scan completed with findings"
    fi
    
    echo "Scanning frontend container..."
    FRONTEND_IMAGE=$(docker-compose -f docker-compose-petclinic.yml config | grep "image:" | grep frontend | awk '{print $2}' | head -1)
    if [ -n "$FRONTEND_IMAGE" ]; then
      snyk container test "$FRONTEND_IMAGE" --severity-threshold="$SNYK_SEVERITY_THRESHOLD" --json-file-output="$REPORTS_DIR/frontend-container-snyk.json" $SNYK_ADDITIONAL_FLAGS || echo "Frontend container scan completed with findings"
    fi
  else
    echo "Docker not available, skipping container scans"
  fi
fi
echo ""

# Generate summary report
echo -e "${YELLOW}Generating summary report...${NC}"
if [ "$DRY_RUN" = true ]; then
  echo "[DRY RUN] Would run: node scripts/merge-snyk-reports.mjs"
else
  if command -v node &> /dev/null; then
    node scripts/merge-snyk-reports.mjs
  else
    echo -e "${YELLOW}Node.js not found, skipping report aggregation${NC}"
    echo "Individual JSON reports are available in $REPORTS_DIR/"
  fi
fi

echo ""
echo -e "${GREEN}=== Snyk Scanning Complete ===${NC}"
echo "Reports available in: $REPORTS_DIR/"
echo ""
echo "Next steps:"
echo "1. Review the summary report: $REPORTS_DIR/snyk-summary.md"
echo "2. Check individual JSON reports for detailed findings"
echo "3. Address critical and high severity vulnerabilities"
echo "4. Attach reports to Jira ticket or CI artifacts"
