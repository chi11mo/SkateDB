# Security Guidelines for SkateDB

## Environment Variables Setup

### Required Environment Variables

Create a `.env` file in the root directory with the following variables:



### Security Checklist

- [ ] Never commit `.env` files to version control
- [ ] Use strong, randomly generated JWT secrets (minimum 256 bits)
- [ ] Regularly rotate JWT secrets in production
- [ ] Use application-specific email passwords (not your personal password)
- [ ] Enable 2FA on email accounts used for application emails
- [ ] Review and limit API key permissions for external services

### Production Deployment

1. Use environment-specific configuration files
2. Store secrets in secure secret management systems (AWS Secrets Manager, Azure Key Vault, etc.)
3. Enable HTTPS only
4. Configure proper CORS settings
5. Set up monitoring and alerting for security events

### Password Policy

The application enforces the following password requirements:
- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter  
- At least one digit
- At least one special character

### API Security

- JWT tokens expire after 1 hour by default
- Email confirmation required for account activation
- Input validation on all API endpoints
- Proper exception handling to prevent information leakage 