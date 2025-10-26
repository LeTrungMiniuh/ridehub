# RideHub Development Guidelines

## Build Commands

### Java/Maven Services
- **Build all services**: `cd backend && ./mvnw clean install`
- **Run single service**: `cd backend/{service-name} && ./mvnw spring-boot:run`
- **Run tests**: `cd backend/{service-name} && ./mvnw test`
- **Run single test**: `cd backend/{service-name} && ./mvnw test -Dtest={ClassName}`
- **Lint/Checkstyle**: `cd backend/{service-name} && ./mvnw checkstyle:check`

### Frontend (Gateway only)
- **Install**: `cd backend/gateway && npm install`
- **Dev server**: `cd backend/gateway && npm start`
- **Build**: `cd backend/gateway && npm run build`
- **Test**: `cd backend/gateway && npm test`
- **Lint**: `cd backend/gateway && npm run lint`

## Code Style Guidelines

### Java
- **Indentation**: 4 spaces
- **Naming**: CamelCase for classes, camelCase for methods/variables
- **Package**: `com.ridehub.{service-name}`
- **Use JHipster patterns** for entity generation and structure
- **Testing**: Use `@IntegrationTest` annotation with embedded containers

### TypeScript/Angular (Gateway)
- **Indentation**: 2 spaces
- **Components**: Prefix with `jhi` (e.g., `jhi-user`)
- **Strict TypeScript**: Enabled with noImplicitAny
- **Imports**: Use explicit imports, avoid `any` type

### General
- **Line endings**: LF
- **Charset**: UTF-8
- **Trim trailing whitespace**
- **Java version**: 17
- **Node version**: >=22.15.0

## Project Structure
- **Microservices**: `backend/ms_{service-name}/`
- **Gateway**: `backend/gateway/` (Angular + Spring Boot)
- **Common**: `backend/ridehub-central-common/`
- **Config**: `backend/ridehub-central-config/`

## Testing Infrastructure
- **Integration Tests**: Use `@IntegrationTest` with embedded Redis, SQL, Kafka
- **TestContainers**: Available for MySQL, Redis, Kafka testing
- **Security**: `TestSecurityConfiguration` for OAuth2 test setup