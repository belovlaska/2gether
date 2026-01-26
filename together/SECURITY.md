# Security Configuration

## Overview
This document describes the security measures implemented in the Together application to protect against OWASP Top 10 vulnerabilities.

## Implemented Security Features

### 1. Static Code Analysis with SpotBugs and Find-Sec-Bugs

#### Configuration
- **Plugin**: `find-sec-bugs-plugin:1.12.0`
- **Effort Level**: Max for thorough analysis
- **Report Level**: High priority issues
- **Security Patterns Detected**:
  - SQL Injection
  - Command Injection
  - Path Manipulation
  - Weak Cryptography
  - Hard-coded Passwords
  - Cross-Site Scripting (XSS)
  - Trust Boundary Violations

#### Gradle Tasks
```bash
# Run standard SpotBugs analysis
./gradlew spotbugsMain

# Run security-focused scan that fails on critical issues
./gradlew spotbugsSecurity
```

### 2. Dependency Vulnerability Scanning

#### OWASP Dependency Check Integration
- Automated scanning for known vulnerabilities in dependencies
- CVSS score thresholds:
  - Low: 0
  - Medium: 4.0
  - High: 7.0
  - Critical: 9.0

### 3. Cryptographic Security Improvements

#### JWT Security
- **Algorithm**: HS256 with proper key validation
- **Key Length Validation**: Minimum 256-bit (32 bytes) keys enforced
- **Encoding**: Using BASE64URL instead of BASE64 for better security

#### Password Security
- **Hashing Algorithm**: BCrypt with adaptive cost factor
- **Implementation**: Spring Security's BCryptPasswordEncoder

### 4. Web Security Measures

#### CORS Configuration
- **Restricted Origins**: Only whitelisted domains allowed
- **Limited HTTP Methods**: GET, POST, PUT, PATCH, DELETE, OPTIONS only
- **Specific Headers**: No wildcard (*) for allowed headers
- **Secure Credentials**: Proper handling of credentials

#### CSRF Protection
- Disabled for API endpoints (stateless JWT authentication)
- Protected against cross-site request forgery

### 5. CI/CD Security Pipeline

#### GitHub Actions Workflow
- **Trigger Events**: Push to main/develop, Pull Requests
- **Security Scans**:
  - SpotBugs security analysis
  - OWASP dependency check
  - Test coverage validation
- **Build Failure Conditions**:
  - High/critical security vulnerabilities detected
  - Dependency vulnerabilities above threshold
  - Failed security tests

## Security Scan Reports

### Generated Artifacts
- SpotBugs HTML/XML reports: `build/reports/spotbugs/`
- Dependency check reports: `build/reports/dependency-check-report.html`
- Test coverage reports: `build/reports/jacoco/`

## Development Guidelines

### Security Coding Standards
1. **Input Validation**: Always validate and sanitize user inputs
2. **Parameterized Queries**: Use JPA/Hibernate safely; avoid dynamic queries
3. **Secure Configuration**: Never hard-code sensitive information
4. **Proper Error Handling**: Don't expose internal details in error responses

### Pre-commit Checks
Run security scans before committing:
```bash
./gradlew spotbugsMain spotbugsTest dependencyCheckAnalyze
```

## Incident Response

### Security Issue Discovery
- Critical vulnerabilities will cause build failures
- Security reports are uploaded as build artifacts
- Team notification via CI/CD pipeline

### Vulnerability Remediation
1. Identify the root cause of security issues
2. Apply fixes according to secure coding standards
3. Re-run security scans to verify resolution
4. Update security configurations as needed