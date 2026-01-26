# 2gether

## Security Features

This project implements comprehensive security scanning using SpotBugs with the find-sec-bugs plugin to detect vulnerabilities from the OWASP Top 10 list.

### Security Scanning
- **Static Analysis**: SpotBugs with find-sec-bugs plugin for detecting security vulnerabilities
- **Dependency Scanning**: OWASP Dependency Check for identifying vulnerable dependencies
- **CI/CD Integration**: Automated security checks in GitHub Actions workflows
- **Supported Vulnerabilities**: SQL injection, command injection, weak cryptography, XSS, and other OWASP Top 10 issues

### Running Security Checks
```bash
# Run all security checks
./scripts/run-security-checks.sh

# Or run individual checks:
./gradlew spotbugsSecurity
./gradlew dependencyCheckAnalyze
```

See [SECURITY.md](together/SECURITY.md) for detailed security configuration and implementation.
