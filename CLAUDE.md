# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is the backend API server for the CultureMate platform - a Spring Boot application using Java 21 with JPA/Hibernate, H2 database, and Lombok for boilerplate reduction.

## Core Commands

### Development
- `./gradlew bootRun` - Start the Spring Boot application
- `./gradlew build` - Build the project
- `./gradlew clean` - Clean build artifacts
- `./gradlew test` - Run all tests
- `./gradlew test --tests ClassName` - Run specific test class
- `./gradlew test --tests ClassName.testMethodName` - Run specific test method

### Database
- H2 Console: Access at `http://localhost:8080/h2-console` when running
- JDBC URL: `jdbc:h2:tcp://localhost/~/CULTURE-MATE/culture-mate-BACK/testDB`
- Username: `sa` (no password)

## Architecture

### Package Structure
- `com.culturemate.culturemate_api` - Root package
- `domain/` - JPA entities (Member, Together, Role enum)
- `repository/` - Data access layer (currently empty, likely for Spring Data JPA repositories)

### Domain Model
- **Member**: User entity with login_id, password, role (ADMIN/MEMBER), and joined_at timestamp
- **Together**: Entity representing group activities with host relationship to Member
- **Role**: Enum with ADMIN and MEMBER values

### Technology Stack
- **Framework**: Spring Boot 3.5.5
- **Java**: Version 21
- **Database**: H2 (in-memory/file-based)
- **ORM**: Spring Data JPA with Hibernate
- **Template Engine**: Thymeleaf
- **Build Tool**: Gradle with wrapper
- **Development**: Spring Boot DevTools for hot reload
- **Code Generation**: Lombok for getters/setters

### Configuration Notes
- H2 database configured with `ddl-auto: create` (recreates schema on startup)
- SQL logging enabled for debugging
- Application name: "CultureMate API"
- Uses TCP connection to H2 database file

## Coding Conventions
- **Indentation**: 2 spaces (no tabs)
- **Entity naming**: PascalCase (e.g., Member, Together, Participants)
- **Variable naming**: camelCase
- **File naming**: PascalCase for classes, camelCase for other files

## Git Workflow
### Commit Message Convention
```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

**Types:**
- `feat`: 새로운 기능
- `fix`: 버그 수정  
- `docs`: 문서 변경
- `style`: 코드 포맷팅 (기능 변경 없음)
- `refactor`: 코드 리팩토링
- `test`: 테스트 추가/수정
- `chore`: 빌드 프로세스, 도구 설정 등

**Examples:**
```
feat: 네비게이션바 생성

feat!: 사용자 등록 API 추가

유저 회원가입 기능을 추가했습니다.
- 이메일/비밀번호 입력받아 저장  
- 중복 이메일 확인 로직 포함

BREAKING CHANGE: 기존 로그인 시 필수였던 username 파라미터가 제거됨
```

**Special:**
- `!` suffix: Breaking changes를 나타냄 (major 버전 증가 요인)
- `BREAKING CHANGE:` footer: 호환성 없는 변경사항 설명

**Note:** When asked to write commit messages or PR descriptions, only output the message content without executing git commands. Do not include any AI-generated disclaimers or mentions that the content was created by AI.

### Current Development Status
- Basic entity structure in place with relationships:
  - Member ↔ Participants ↔ Together (many-to-many through join entity)
  - Together → Region (many-to-one)
  - Member with status enum (ACTIVE, DORMANT, SUSPENDED, BANNED)
- Repository layer exists but is empty
- Main application includes test Hello class usage
- Git branch: `feature/memeber-together-entity`