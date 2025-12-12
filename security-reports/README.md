# Security Reports

This directory contains Snyk Code security scan reports for the VetPet Clinic Demo application.

## Report Naming Convention

Reports are named using the format: `snyk-code-YYYY-MM-DD-{status}.sarif`

- **Date**: The date the scan was performed
- **Status**: Either `pre-fix` (before remediation) or `post-fix` (after remediation)

## Current Findings

### Scan Date: 2025-12-12 (Pre-Fix)

**File**: `snyk-code-2025-12-12-pre-fix.sarif`

#### Vulnerability Summary

| Severity | Rule ID | File | Line | Description |
|----------|---------|------|------|-------------|
| High | javascript/DOMXSS | frontend/src/components/Nav.tsx | 204 | DOM-based Cross-site Scripting (XSS) |

#### Detailed Finding

**Vulnerability**: DOM-based XSS in ProfileImage component

**Location**: `frontend/src/components/Nav.tsx`, line 204

**Issue**: Unsanitized input from a React useState value flows into an `<img src>` attribute. The `imageData` state variable is populated from a remote fetch response without proper validation, allowing potential XSS attacks if the server returns malicious content.

**Data Flow**:
1. Line 172: `imageData` state variable declared
2. Line 204: `imageData` used directly in `<img src={imageData}>`
3. No validation between fetch response and DOM rendering

**Risk**: An attacker could potentially inject malicious JavaScript through crafted image responses, leading to:
- Session hijacking
- Credential theft
- Unauthorized actions on behalf of users
- Data exfiltration

**Remediation**: Implement validation to ensure only safe data URLs are rendered:
- Validate MIME type is `image/*`
- Restrict to data URLs starting with `data:image/`
- Add error handling and fallback UI
- Implement loading states

## How to Run Snyk Code Tests

### Prerequisites

1. Install Snyk CLI:
   ```bash
   npm install -g snyk
   ```

2. Authenticate with Snyk:
   ```bash
   snyk auth
   ```

### Running a Scan

To run a full Snyk Code test and generate a SARIF report:

```bash
# From the project root
snyk code test --sarif > security-reports/snyk-code-$(date +%Y-%m-%d)-{status}.sarif
```

Replace `{status}` with either `pre-fix` or `post-fix` depending on when the scan is run.

### Interpreting Results

- **SARIF Format**: Reports are in SARIF (Static Analysis Results Interchange Format) JSON
- **Severity Levels**: `error` (high), `warning` (medium), `note` (low)
- **Priority Score**: Snyk assigns a priority score (0-1000) based on severity and exploitability
- **Code Flows**: Each finding includes the data flow from source to sink

### Storage Policy

- Keep both pre-fix and post-fix reports for audit trail
- Archive reports older than 6 months
- Store reports in version control for team visibility
- Update this README when new vulnerabilities are found or fixed

## Remediation Tracking

| Date | Vulnerability | Status | Fixed By | PR Link |
|------|---------------|--------|----------|---------|
| 2025-12-12 | DOM XSS in Nav.tsx | In Progress | Agent | TBD |

---

*Last Updated: 2025-12-12*
