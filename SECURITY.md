# Security Policy

## Supported Versions

We actively maintain and provide security updates for the following versions:

| Version | Supported          |
| ------- | ------------------ |
| 1.x.x   | :white_check_mark: |

## Reporting a Vulnerability

If you discover a security vulnerability in this project, please report it by:

1. **Email**: Contact the maintainers directly (do not create a public issue)
2. **GitHub Security Advisories**: Use the "Security" tab to privately report vulnerabilities

### What to Include

- Description of the vulnerability
- Steps to reproduce
- Potential impact
- Suggested fix (if available)

### Response Timeline

- **Initial Response**: Within 48 hours
- **Status Update**: Within 7 days
- **Fix Timeline**: Depends on severity
  - Critical: 1-3 days
  - High: 1-2 weeks
  - Medium: 2-4 weeks
  - Low: Next scheduled release

## Security Update Process

### Dependency Management

We use the following tools to monitor and update dependencies:

- **Snyk**: Automated vulnerability scanning
- **Dependabot**: Automated dependency updates
- **Maven**: Backend dependency management
- **pnpm**: Frontend dependency management

### Recent Security Updates

#### 2025-11-14: Dependency Security Updates

**Backend (Spring Boot)**
- Updated Spring Boot from 3.2.0 to 3.2.11
  - Addresses multiple CVE vulnerabilities in Spring Framework
  - Includes security patches for Spring Security
- Updated Testcontainers from 1.17.3 to 1.19.8
  - Fixes container escape vulnerabilities
  - Improves Docker security

**Frontend (React/Vite)**
- Updated Vite from 4.4.5 to 4.5.5
  - Fixes XSS vulnerabilities in dev server
  - Addresses path traversal issues
- Updated @vitejs/plugin-react to 4.3.3
  - Compatibility update with Vite 4.5.5
- Updated @playwright/test from 1.39.0 to 1.48.2
  - Security improvements in browser automation
  - Fixes for potential code injection

## Security Best Practices

### For Contributors

1. **Never commit secrets**: Use environment variables for sensitive data
2. **Keep dependencies updated**: Regularly check for security updates
3. **Follow secure coding practices**: 
   - Input validation
   - Output encoding
   - Proper authentication/authorization
4. **Run security scans**: Before submitting PRs
5. **Review dependencies**: Check for known vulnerabilities

### For Deployment

1. **Use HTTPS**: Always use TLS/SSL in production
2. **Environment Variables**: Store secrets in secure vaults
3. **Database Security**: Use strong passwords, enable encryption
4. **Regular Updates**: Apply security patches promptly
5. **Monitoring**: Enable security logging and monitoring

## Vulnerability Disclosure Policy

We follow responsible disclosure practices:

1. **Private Reporting**: Report vulnerabilities privately first
2. **Coordinated Disclosure**: We'll work with you on timing
3. **Credit**: We acknowledge security researchers (with permission)
4. **Public Disclosure**: After fix is released and deployed

## Security Contacts

- **Primary**: Project maintainers via GitHub
- **Security Team**: Use GitHub Security Advisories

## Additional Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [Snyk Vulnerability Database](https://snyk.io/vuln/)

---

Last Updated: 2025-11-14
