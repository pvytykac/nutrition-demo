## ADDED Requirements

### Requirement: OIDC authentication with Keycloak

The application authenticates users via the authorization code flow with PKCE against the configured Keycloak realm. Tokens are stored in session storage and automatically attached to API requests. The user must be redirected to the Keycloak login page when not authenticated.

#### Scenario: Unauthenticated user is redirected to login
- **GIVEN** the user is not authenticated
- **WHEN** they navigate to any protected application URL
- **THEN** they are redirected to the Keycloak login page

#### Scenario: Successful login redirects back to application
- **GIVEN** the user is on the Keycloak login page
- **WHEN** they enter valid credentials and submit
- **THEN** they are redirected back to the application with a valid access token
- **AND** the originally requested page is displayed

#### Scenario: Logout clears session
- **GIVEN** the user is authenticated
- **WHEN** they click the logout button
- **THEN** the tokens are cleared from session storage
- **AND** the user is redirected to the Keycloak logout endpoint

#### Scenario: Expired token triggers silent renewal
- **GIVEN** the user has an active session
- **WHEN** the access token expires
- **THEN** a silent token renewal is attempted using the refresh token
- **AND** the API request that triggered the renewal completes successfully
