# PickDo Backend

Spring Boot와 PostgreSQL 기반 PickDo 백엔드입니다.

## Team

백엔드 리더: 강준석

팀원: 강준석, 김강, 이효상

## Branch

- `master`: 제품 출시 버전을 관리하는 메인 브랜치
- `develop`: 다음 출시 버전을 위해 개발하는 브랜치
- `feature`: 새로운 기능을 개발하는 브랜치
- `release`: 다음 출시 버전을 준비하는 브랜치
- `hotfix`: 출시된 제품의 버그를 고치기 위한 브랜치

## Requirements

- Java 17
- Docker Desktop

Homebrew로 설치한 Java 17은 이 프로젝트의 Gradle 설정에서 자동으로 사용합니다.

## Run PostgreSQL

```bash
cp .env.example .env
docker compose up -d
```

## Run Backend

```bash
./gradlew bootRun
```

## Check

```bash
curl http://localhost:8080/api/health
curl http://localhost:8080/actuator/health
```

## Test

```bash
./gradlew test
```
