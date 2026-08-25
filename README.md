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

The API Gateway acts as the main entry point for client requests.

Responsibilities:


Responsible for authentication and token generation.

Features:


Example JWT claims:

{
  "iss": "auth-service",
  "sub": "3",
  "role": "USER",
  "email": "user@example.com"
}



Responsible for user-related information.

Features:


Responsible for product management.

Features:


Responsible for authenticated users' shopping carts.

Features:


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


Provides service discovery for the microservices.

Services register themselves with Eureka and can communicate using service names instead of hard-coded URLs.

Example:

USER-SERVICE
CART-SERVICE
PRODUCT-SERVICE
AUTH-SERVICE
API-GATEWAY

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

Never commit the private key.


**/private.pem
*.key
*.p12
*.pfx
.env
.env.*

The public key may be committed for development and portfolio purposes because it cannot be used to sign valid JWTs.

For a real production environment, private keys and application secrets should be managed using a dedicated secret-management solution.


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
Authorization: Bearer <JWT>

Example:

GET /api/v1/cart
Authorization: Bearer eyJhbGciOiJSUzI1NiJ9...

Spring Security automatically validates the JWT before allowing the request.

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
git clone <your-repository-url>
cd ecommerce-microservices

Create the databases required by the individual microservices and configure the database credentials using environment variables or local configuration.

Example:

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}




Start:

eureka-server

Then visit:

http://localhost:8761

Recommended order:

1. Eureka Server
2. User Service
3. Product Service
4. Cart Service
5. Auth Service
6. API Gateway

Example:

http://localhost:9090
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

Planned improvements include:


This project was created to strengthen practical knowledge of:



Software Engineering Graduate | Java Backend Developer


This project is intended for educational and portfolio purposes.