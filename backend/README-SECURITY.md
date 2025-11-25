# Security Configuration Guide

## Overview
This document describes the security configuration for the VetPet Clinic Demo application and provides guidance for secure deployment.

## Security Fixes Implemented

### 1. ✅ Removed Hardcoded Private Keys
**Issue:** RSA private keys were stored in the Git repository  
**Fix:** Keys are now loaded from environment variables or external files  
**Impact:** Prevents unauthorized access to JWT signing keys

### 2. ✅ Password Encryption Support
**Issue:** Passwords stored with `{noop}` prefix (no encryption)  
**Fix:** Added BCrypt password encoder with backward compatibility  
**Impact:** Protects user passwords if database is compromised

### 3. ✅ CORS Configuration
**Issue:** No CORS configuration implemented  
**Fix:** Proper CORS configuration with environment-based origins  
**Impact:** Prevents unauthorized cross-origin requests

### 4. ✅ Secure Bearer Token Handling
**Issue:** JWT tokens allowed in URL parameters  
**Fix:** Tokens now only accepted in Authorization header  
**Impact:** Prevents token leakage in logs and browser history

### 5. ✅ GraphQL Introspection Control
**Issue:** Introspection always enabled  
**Fix:** Disabled in production profile, enabled in development  
**Impact:** Prevents API schema exposure in production

### 6. ✅ Appropriate Logging Levels
**Issue:** TRACE-level security logging  
**Fix:** INFO/WARN for production, DEBUG for development  
**Impact:** Reduces sensitive information in logs

## Environment Configuration

### Required Environment Variables

#### JWT Keys (Production)
```bash
# Path to RSA public key file
export JWT_PUBLIC_KEY_PATH=/path/to/public_key.pem

# Path to RSA private key file
export JWT_PRIVATE_KEY_PATH=/path/to/private_key.pem
```

#### CORS Configuration
```bash
# Comma-separated list of allowed origins
export CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://app.yourdomain.com
```

### Generating RSA Keys

For production, generate new RSA keys:

```bash
# Generate private key (4096-bit RSA)
openssl genpkey -out private_key.pem -algorithm RSA -pkeyopt rsa_keygen_bits:4096

# Generate public key from private key
openssl rsa -pubout -outform pem -in private_key.pem -out public_key.pem

# Set appropriate permissions
chmod 600 private_key.pem
chmod 644 public_key.pem
```

**IMPORTANT:** 
- Never commit these keys to Git
- Store them securely (e.g., AWS Secrets Manager, HashiCorp Vault)
- Rotate keys periodically
- Use different keys for each environment

### Development Setup

For local development, you can generate keys locally:

```bash
cd backend/src/main/resources
mkdir -p keys
cd keys

# Generate keys
openssl genpkey -out private_key.pem -algorithm RSA -pkeyopt rsa_keygen_bits:4096
openssl rsa -pubout -outform pem -in private_key.pem -out public_key.pem
```

**Note:** These keys are gitignored and should never be committed.

## Spring Profiles

### Development Profile (`dev`)
```bash
# Run with development profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Features:
- Verbose logging (DEBUG/TRACE)
- GraphQL introspection enabled
- Permissive CORS for localhost
- Keys can be loaded from classpath

### Production Profile (`prod`)
```bash
# Run with production profile
java -jar app.jar --spring.profiles.active=prod
```

Features:
- Minimal logging (INFO/WARN)
- GraphQL introspection disabled
- Restrictive CORS
- Keys MUST be from environment variables

## Password Migration

### Current State
The application supports both encrypted (BCrypt) and legacy (NoOp) passwords for backward compatibility.

### Migrating Existing Passwords

To migrate existing passwords to BCrypt:

1. **Option A: Database Migration (Recommended)**
   - Create a new Flyway migration
   - Use BCrypt to hash existing passwords
   - Update user records

2. **Option B: User-Initiated Migration**
   - Users change their passwords
   - New passwords automatically use BCrypt
   - Old passwords still work until changed

### Creating New Users with Encrypted Passwords

```sql
-- Use BCrypt-hashed password (example hash for "password123")
INSERT INTO users (USERNAME, PASSWORD, ENABLED, FULLNAME) 
VALUES ('newuser', '{bcrypt}$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, 'New User');
```

Generate BCrypt hash in Java:
```java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hashedPassword = encoder.encode("yourPassword");
System.out.println("{bcrypt}" + hashedPassword);
```

## Security Best Practices

### 1. Key Management
- ✅ Never commit keys to Git
- ✅ Use environment variables or secret management systems
- ✅ Rotate keys periodically (every 90 days recommended)
- ✅ Use different keys for each environment
- ✅ Restrict file permissions (600 for private keys)

### 2. Password Security
- ✅ Use BCrypt for all new passwords
- ✅ Enforce strong password policies
- ✅ Implement password expiration
- ✅ Add rate limiting on login attempts
- ✅ Log failed authentication attempts

### 3. API Security
- ✅ Always use HTTPS in production
- ✅ Validate all input data
- ✅ Implement rate limiting
- ✅ Use appropriate CORS policies
- ✅ Disable introspection in production
- ✅ Implement query depth limiting for GraphQL

### 4. Logging
- ✅ Never log sensitive data (passwords, tokens, PII)
- ✅ Use appropriate log levels
- ✅ Implement log rotation
- ✅ Secure log storage
- ✅ Monitor logs for security events

### 5. Deployment
- ✅ Use HTTPS/TLS for all connections
- ✅ Keep dependencies up to date
- ✅ Run security scans regularly
- ✅ Implement proper firewall rules
- ✅ Use container security scanning

## Monitoring & Alerting

### Security Events to Monitor
- Failed authentication attempts
- Unusual token usage patterns
- CORS policy violations
- SQL injection attempts
- Unexpected error rates
- Configuration changes

### Recommended Tools
- Application Performance Monitoring (APM)
- Security Information and Event Management (SIEM)
- Intrusion Detection System (IDS)
- Vulnerability scanning (Snyk, OWASP Dependency-Check)

## Compliance

### OWASP Top 10 Coverage
- ✅ A01:2021 – Broken Access Control
- ✅ A02:2021 – Cryptographic Failures
- ✅ A03:2021 – Injection
- ✅ A05:2021 – Security Misconfiguration
- ✅ A07:2021 – Identification and Authentication Failures

### Data Protection
- Passwords encrypted with BCrypt
- JWT tokens for stateless authentication
- HTTPS required for production
- Secure key storage

## Troubleshooting

### Keys Not Loading
```
Error: Could not load RSA keys
```
**Solution:** Ensure environment variables are set correctly:
```bash
echo $JWT_PUBLIC_KEY_PATH
echo $JWT_PRIVATE_KEY_PATH
```

### CORS Errors
```
Access to fetch at 'http://localhost:9977/graphql' from origin 'http://localhost:3000' has been blocked by CORS policy
```
**Solution:** Add your origin to `CORS_ALLOWED_ORIGINS`:
```bash
export CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080
```

### Authentication Failures
```
Bad credentials
```
**Solution:** 
1. Verify password format in database
2. Check if BCrypt migration is needed
3. Ensure PasswordEncoder is configured correctly

## Additional Resources

- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [BCrypt Password Hashing](https://en.wikipedia.org/wiki/Bcrypt)

## Support

For security issues, please contact the security team immediately.
Do not create public issues for security vulnerabilities.
