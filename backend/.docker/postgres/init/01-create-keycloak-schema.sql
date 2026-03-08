-- Create schemas
CREATE SCHEMA IF NOT EXISTS keycloak;
CREATE SCHEMA IF NOT EXISTS nutrition;

-- Create dedicated users
CREATE USER keycloak WITH PASSWORD 'keycloak';
CREATE USER nutrition WITH PASSWORD 'nutrition';

-- Grant schema ownership
ALTER SCHEMA keycloak OWNER TO keycloak;
ALTER SCHEMA nutrition OWNER TO nutrition;

-- Grant usage and create on respective schemas
GRANT USAGE, CREATE ON SCHEMA keycloak TO keycloak;
GRANT USAGE, CREATE ON SCHEMA nutrition TO nutrition;

-- Grant all privileges on future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA keycloak GRANT ALL ON TABLES TO keycloak;
ALTER DEFAULT PRIVILEGES IN SCHEMA keycloak GRANT ALL ON SEQUENCES TO keycloak;
ALTER DEFAULT PRIVILEGES IN SCHEMA nutrition GRANT ALL ON TABLES TO nutrition;
ALTER DEFAULT PRIVILEGES IN SCHEMA nutrition GRANT ALL ON SEQUENCES TO nutrition;

-- Grant all on existing objects (if any)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA keycloak TO keycloak;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA keycloak TO keycloak;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA nutrition TO nutrition;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA nutrition TO nutrition;
