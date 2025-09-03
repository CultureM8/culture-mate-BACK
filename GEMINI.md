# GEMINI Project Context: CultureMate API

## 1. Project Overview & Status

- **Purpose**: A backend API server for the CultureMate platform.
- **Current Status**: A consistent architecture has been applied across all controllers and services. DTOs have been standardized into Request/Response patterns, and all entities include creation/modification timestamps.

## 2. Development Environment & Commands

### Technology Stack
- **Java**: Version 21
- **Framework**: Spring Boot
- **ORM**: Spring Data JPA with Hibernate
- **Build Tool**: Gradle with Java toolchain
- **Validation**: Jakarta Bean Validation
- **Development Tools**: Spring Boot DevTools for hot reload

### Key Commands
- **Clean build artifacts**: `./gradlew clean`
- **Run a specific test method**: `./gradlew test --tests ClassName.testMethodName`

### Endpoints & Configuration
- **H2 Console**: `http://localhost:8080/h2-console`
- **API Documentation (Swagger)**: `http://localhost:8080/swagger-ui.html`
- **Database Configuration**: Uses environment-based configuration for different deployment targets.
- **Required Environment Variables**: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_DRIVER`

## 3. Architecture & Design Patterns

### 3.1. Multi-Layered Architecture
The project follows a strict layered architecture:
**Controller Layer** → **Service Layer** → **Repository Layer** → **Entity Layer**

### 3.2. DTO (Data Transfer Object) Pattern
- **Request DTOs**: Used for input validation (`@Valid`) and carrying data from the client.
- **Response DTOs**: Used to provide clean API contracts, often including computed fields.

### 3.3. Exception Handling
- A global exception handling architecture is implemented using `@RestControllerAdvice`.
- Custom, specific exceptions are used to represent business rule violations.
  - Examples: `TogetherNotFoundException`, `TogetherFullException`, `TogetherExpiredException`
- Controllers are kept clean of try-catch blocks, relying on Spring's automatic exception handling.

### 3.4. Advanced JPA Patterns
- **Computed Fields**: `@Formula` annotations are used for calculated fields (e.g., participant counts).
- **Efficient Joins**: The codebase employs efficient JOIN strategies for complex relationships.
- **Entity Lifecycle**: Cascading operations are managed with `orphanRemoval = true` for clean data removal.

### 3.5. Core Architectural Principles
- **Consistent Dependency Injection**: All services use the same constructor-based DI pattern (`private final`).
  ```java
  // Good Pattern
  private final MemberService memberService;
  
  public MyService(MemberService memberService) {
      this.memberService = memberService;
  }
  
  // ❌ Bad Pattern
  @Autowired
  private MemberService memberService;
  ```
- **Layer & Domain Separation**:
  - To access entities from a different domain, always go through its `Service`.
  - **Example**: `memberService.findById()` should be used instead of directly accessing the `MemberRepository` from another domain's service.
- **Clear Exception Handling in Services**:
  - Use `orElseThrow` for clear, concise error handling when entities are not found.
  - **Example**: `repository.findById(id).orElseThrow(() -> new IllegalArgumentException("..."))`
- **Single Responsibility Principle**: Each service has a clear responsibility for its own domain. Avoid creating duplicate services; integrate functionality into existing ones where appropriate.

## 4. Core Domain Models

- **Member Domain**:
  - `Member`: The central user entity.
  - `MemberDetail`: Extended profile information (one-to-one).
  - `Role`: User roles (`ADMIN`, `MEMBER`).
  - `InterestEvents` / `InterestTogethers`: Many-to-many join tables for user preferences.

- **Event Domain**:
  - `Event`: The main cultural event entity with comprehensive metadata.
  - `EventReview`: User reviews and ratings for events.

- **Together Domain**:
  - `Together`: The main entity for user-organized group meetups.
  - Implements sophisticated state management (recruiting status, capacity limits).
  - `VisibleType`: Enum for visibility settings (e.g., public, private).

- **Community Domain**:
  - `Board`: Community posts, which can be organized hierarchically.
  - `Comment`: Comments on `Board` posts.

## 5. Coding & Git Conventions

### 5.1. General Coding Conventions
- **Indentation**: 2 spaces (no tabs).
- **Variable Naming**: `camelCase`.

### 5.2. Lombok Usage
- **`@Builder`**: Used for constructing complex entities.
- **`@Getter`, `@Setter`, `@NoArgsConstructor`, etc.**: Used to reduce boilerplate.
- **Boolean Field Naming**: For a field like `isRecruiting`, Lombok generates `isRecruiting()` as the getter and `setRecruiting()` as the setter.

### 5.3. Git Commit Message Convention
Follow the **Conventional Commits** specification:
```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```
- **Types**:
  - `feat`: A new feature
  - `fix`: A bug fix
  - `docs`: Documentation only changes
  - `style`: Code style changes (formatting, etc.)
  - `refactor`: A code change that neither fixes a bug nor adds a feature
  - `perf`: A code change that improves performance
  - `test`: Adding missing tests or correcting existing tests
- **Footer**:
  - Use `BREAKING CHANGE:` for changes that are not backward-compatible.

**Note**: When asked to write commit messages or PR descriptions, only output the message content without executing git commands. Do not include any AI-generated disclaimers or mentions that the content was created by AI.
