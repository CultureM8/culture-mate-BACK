# Repository Guidelines

## Project Structure & Module Organization
Culture-Mate is a Spring Boot 3 service located under `src/main/java/com/culturemate/culturemate_api`. Core packages separate configuration, controllers, services, repositories, DTOs, and domain aggregates such as `event`, `member`, `community`, and `together`. Static assets and templates live in `src/main/resources`, while integration fixtures sit under `src/main/resources/static`. Mirror the production package layout when adding tests in `src/test/java/com/culturemate/culturemate_api` to keep navigation predictable.

## Build, Test, and Development Commands
Use the Gradle wrapper that ships with the repo. `./gradlew bootRun` starts the API with dev-time tooling and reads `.env`. `./gradlew build` produces a full artifact and runs the test suite. `./gradlew test` executes unit and slice tests; scope a single class with `./gradlew test --tests com.culturemate.culturemate_api.service.MemberServiceTest`. Run `./gradlew clean` before release builds to clear generated classes.

## Coding Style & Naming Conventions
Stick to Java 21 language features and 4-space indentation. Classes and enums use PascalCase, beans and methods stay camelCase, and constants remain UPPER_SNAKE_CASE. Place new code in the existing domain-driven packages and keep DTOs in `dto` with suffix `Request`/`Response`. Lombok is enabled; prefer `@Getter`, `@Builder`, and `@RequiredArgsConstructor` over manual boilerplate, but avoid Lombok on JPA entities where it clashes with lazy loading.

## Testing Guidelines
JUnit 5 is enabled through `useJUnitPlatform()`. Co-locate tests alongside their targets, name them `<Feature>Test`, and cover both success and failure paths for service and controller layers. For Spring MVC or WebSocket endpoints, lean on `@WebMvcTest` or `@SpringBootTest` plus MockMvc to validate request contracts. Include sample authentication tokens or stub repositories as needed so tests do not hit external services.

## Commit & Pull Request Guidelines
Follow the observed convention `type: short summary`, e.g., `feat: cancel participation flow`. Reference GitHub issues with `(#id)` when applicable and keep bodies bilingual only if it clarifies intent. Pull requests should describe the change, outline validation commands, and attach screenshots or API samples for user-facing updates. Ensure CI passes locally before requesting review.

## Security & Configuration Tips
Never commit `.env`; load database credentials through the provided `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `DB_DDL_AUTO` keys. The application defaults to in-memory H2 for development, but confirm Oracle connectivity in staging by overriding those variables. Keep JWT secrets and WebSocket origins configurable via environment properties, and update `CorsConfig` when adding new front-end hosts.
