# Snyk Security Scanning

This directory contains scripts and reports for Snyk Code security scanning.

## Prerequisites

1. **Install Snyk CLI**
   - macOS: `brew install snyk/tap/snyk`
   - npm: `npm install -g snyk`
   - Other: See https://docs.snyk.io/snyk-cli/install-the-snyk-cli

2. **Authenticate**
   - Interactive: `snyk auth`
   - With token: `snyk auth $SNYK_TOKEN`

## Running Scans

### Quick Start - Scan All Modules

```bash
./snyk/run-all.sh
```

This will scan all four modules:
- Backend (Spring Boot/GraphQL)
- Frontend (React/TypeScript/Vite)
- GraphiQL UI
- E2E Tests

### Manual Scans

#### Backend
```bash
cd backend
snyk code test --json > ../snyk/reports/snyk-backend-code-report.json
snyk code test > ../snyk/reports/snyk-backend-code-report.txt
```

#### Frontend
```bash
cd frontend
snyk code test --json > ../snyk/reports/snyk-frontend-code-report.json
snyk code test > ../snyk/reports/snyk-frontend-code-report.txt
```

#### GraphiQL UI
```bash
cd petclinic-graphiql
snyk code test --json > ../snyk/reports/snyk-graphiql-code-report.json
snyk code test > ../snyk/reports/snyk-graphiql-code-report.txt
```

#### E2E Tests
```bash
cd e2e-tests
snyk code test --json > ../snyk/reports/snyk-e2e-tests-code-report.json
snyk code test > ../snyk/reports/snyk-e2e-tests-code-report.txt
```

## Report Formats

- **JSON Reports** (`*-code-report.json`): Machine-readable format for automation
- **Text Reports** (`*-code-report.txt`): Human-readable format for review

## Understanding Results

Snyk Code analyzes source code for security vulnerabilities and provides:
- **Severity Levels**: Critical, High, Medium, Low
- **CWE Categories**: Common Weakness Enumeration classifications
- **File Locations**: Exact file and line numbers
- **Remediation Guidance**: Suggestions for fixing issues

## CI/CD Integration

To integrate into CI/CD pipelines:

```yaml
# Example GitHub Actions
- name: Run Snyk Code Scan
  run: |
    npm install -g snyk
    snyk auth ${{ secrets.SNYK_TOKEN }}
    ./snyk/run-all.sh
```

## Additional Resources

- [Snyk Documentation](https://docs.snyk.io/)
- [Snyk Code Documentation](https://docs.snyk.io/products/snyk-code)
- [CWE Database](https://cwe.mitre.org/)
