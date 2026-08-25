Spring Boot E-Commerce Microservices

A backend e-commerce platform built using Java, Spring Boot, Spring Cloud, Spring Security, JWT, PostgreSQL, Eureka, OpenFeign, and API Gateway.

The project follows a microservices architecture, where each service is responsible for a specific business capability and can be developed, deployed, and scaled independently.

Architecture
Client
│
▼
┌─────────────┐
│ API Gateway │
│    :9090    │
└──────┬──────┘
│
┌────────────────┼────────────────┐
│                │                │
▼                ▼                ▼
Auth Service      User Service     Product Service
:8083             :8082              :8080
│
│
└──────────────────────┐
│
▼
Cart Service
:8081

                     ┌─────────────────┐
                     │ Eureka Server   │
                     │      :8761      │
                     └─────────────────┘
Microservices
API Gateway

The API Gateway acts as the main entry point for client requests.

Responsibilities:

Route requests to the appropriate microservice
Validate JWT access tokens
Protect secured endpoints
Allow public endpoints such as authentication and product browsing
Integrate with Eureka service discovery
Auth Service

Responsible for authentication and token generation.

Features:

User registration
User login
BCrypt password verification
RSA-signed JWT generation
JWT expiration handling
Role information inside JWT claims
Communication with User Service using OpenFeign

Example JWT claims:

{
"iss": "auth-service",
"sub": "3",
"role": "USER",
"email": "user@example.com"
}

The sub claim represents the authenticated user's ID.

User Service

Responsible for user-related information.

Features:

User registration
User profile management
Profile image upload
User lookup
Authentication credential lookup for internal communication
JWT validation
PostgreSQL persistence
Product Service

Responsible for product management.

Features:

Create products
Update products
Delete products
Retrieve products
Product image management
Public product browsing
Secured administrative operations
Cart Service

Responsible for authenticated users' shopping carts.

Features:

Create cart automatically for a user
Add products to cart
Retrieve current user's cart
Update cart items
Remove cart items
Identify users using the JWT sub claim

Example:

@GetMapping
public ResponseEntity<CartResponse> getCart(
@AuthenticationPrincipal Jwt jwt
) {

    Long userId = Long.parseLong(jwt.getSubject());

    return ResponseEntity.ok(
            cartService.getCart(userId)
    );
}

The user ID is extracted from the signed JWT instead of being trusted from the client request.

Eureka Server

Provides service discovery for the microservices.

Services register themselves with Eureka and can communicate using service names instead of hard-coded URLs.

Example:

USER-SERVICE
CART-SERVICE
PRODUCT-SERVICE
AUTH-SERVICE
API-GATEWAY
Technology Stack
Backend
Java
Spring Boot
Spring MVC
Spring Data JPA
Hibernate
Microservices
Spring Cloud
Spring Cloud Gateway
Netflix Eureka
OpenFeign
Security
Spring Security
OAuth2 Resource Server
JWT
RSA public/private keys
BCrypt password hashing
Database
PostgreSQL
Image Storage
Cloudinary
Development Tools
Maven
IntelliJ IDEA
Git
GitHub
Postman
Security Architecture

The Auth Service is responsible for creating JWT access tokens.

User Login
│
▼
Auth Service
│
├── Validate email/password
│
├── BCrypt password matching
│
└── Generate JWT using private key
│
▼
JWT Token
│
▼
API Gateway
│
Validate JWT
│
▼
Microservice
│
Validate JWT
│
▼
Process Request

Only the Auth Service owns the RSA private key.

Other services use the corresponding public key to verify tokens.

AUTH-SERVICE
├── private.pem
└── public.pem

API-GATEWAY
└── public.pem

USER-SERVICE
└── public.pem

PRODUCT-SERVICE
└── public.pem

CART-SERVICE
└── public.pem
RSA Keys

Generate an RSA private key:

openssl genpkey \
-algorithm RSA \
-out private.pem \
-pkeyopt rsa_keygen_bits:2048

Generate the public key:

openssl rsa \
-pubout \
-in private.pem \
-out public.pem
Important

Never commit the private key.

Add this to .gitignore:

**/private.pem
*.key
*.p12
*.pfx
.env
.env.*

The public key may be committed for development and portfolio purposes because it cannot be used to sign valid JWTs.

For a real production environment, private keys and application secrets should be managed using a dedicated secret-management solution.

JWT Resource Server Configuration

Protected services use Spring Security OAuth2 Resource Server.

Example:

@Bean
SecurityFilterChain securityFilterChain(
HttpSecurity http
) throws Exception {

    return http
            .csrf(AbstractHttpConfigurer::disable)

            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/actuator/health")
                    .permitAll()

                    .anyRequest()
                    .authenticated()
            )

            .oauth2ResourceServer(oauth ->
                    oauth.jwt(Customizer.withDefaults())
            )

            .build();
}

Public-key configuration:

spring.security.oauth2.resourceserver.jwt.public-key-location=classpath:keys/public.pem
API Gateway Security

The API Gateway uses reactive Spring Security.

@Bean
SecurityWebFilterChain securityWebFilterChain(
ServerHttpSecurity http
) {

    return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)

            .authorizeExchange(exchange -> exchange

                    .pathMatchers(
                            "/auth-service/api/v1/auth/signin",
                            "/auth-service/api/v1/auth/register"
                    )
                    .permitAll()

                    .pathMatchers(
                            HttpMethod.GET,
                            "/api/v1/products/**"
                    )
                    .permitAll()

                    .anyExchange()
                    .authenticated()
            )

            .oauth2ResourceServer(oauth ->
                    oauth.jwt(Customizer.withDefaults())
            )

            .build();
}
Authentication Flow
Registration
Client
│
▼
API Gateway
│
▼
Auth Service
│
▼
User Service
│
▼
PostgreSQL
Login
Client
│
│ Email + Password
▼
Auth Service
│
├── Retrieve credentials
├── Verify BCrypt password
└── Generate JWT
│
▼
Client
Authenticated Request
Authorization: Bearer <JWT>

Example:

GET /api/v1/cart
Authorization: Bearer eyJhbGciOiJSUzI1NiJ9...

Spring Security automatically validates the JWT before allowing the request.

Project Structure
ecommerce-microservices/
│
├── api-gateway/
│   ├── pom.xml
│   └── src/
│
├── auth-service/
│   ├── pom.xml
│   └── src/
│
├── cart-service/
│   ├── pom.xml
│   └── src/
│
├── product-service/
│   ├── pom.xml
│   └── src/
│
├── user-service/
│   ├── pom.xml
│   └── src/
│
├── eureka-server/
│   ├── pom.xml
│   └── src/
│
├── .gitignore
├── pom.xml
└── README.md
Running the Project
1. Clone the repository
   git clone <your-repository-url>
   cd ecommerce-microservices
2. Configure PostgreSQL

Create the databases required by the individual microservices and configure the database credentials using environment variables or local configuration.

Example:

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
3. Generate RSA keys

Generate the private and public keys and place them in the required resources/keys directories.

Do not commit private.pem.

4. Start Eureka Server

Start:

eureka-server

Then visit:

http://localhost:8761
5. Start the microservices

Recommended order:

1. Eureka Server
2. User Service
3. Product Service
4. Cart Service
5. Auth Service
6. API Gateway
6. Access the APIs through the Gateway

Example:

http://localhost:9090
Example Authentication Request
Login
POST /auth-service/api/v1/auth/signin
Content-Type: application/json
{
"email": "user@example.com",
"password": "Password@123"
}

Example response:

{
"accessToken": "eyJhbGciOiJSUzI1NiJ9...",
"tokenType": "Bearer"
}
Future Improvements

Planned improvements include:

ROLE_USER and ROLE_ADMIN authorization
Refresh tokens
Redis caching
Kafka-based event-driven communication
Docker
Docker Compose
Kubernetes
Distributed tracing
Prometheus
Grafana
Centralized logging
Rate limiting
Circuit breakers
Resilience4j
Testcontainers
Integration testing
GitHub Actions CI/CD
OAuth2 / OpenID Connect
JWKS-based public-key distribution
Cloud deployment
Purpose

This project was created to strengthen practical knowledge of:

Java backend development
Spring Boot
Microservices architecture
API security
Authentication and authorization
JWT
Distributed systems
Inter-service communication
Database design
REST API development
Author

Mohamed Thabith Shahul Hameed

Software Engineering Graduate | Java Backend Developer

License

This project is intended for educational and portfolio purposes.