# Futsal Project

## 🚀 로컬 개발 환경 설정

### 필수 요구사항

- Java 17
- Docker Desktop
- MySQL 8.0
- Redis

### 환경변수 설정 (선택사항)

```shell
export MYSQL_USER=local
export MYSQL_PASSWORD=password
export MYSQL_ROOT_PASSWORD=password
```

### 실행 방법

```shell
./gradlew clean
rm -rf .gradle
rm -rf build
./gradlew build -x test

docker compose down
docker compose build
docker compose up
```

### Docker를 사용한 실행 방법

1. 프로젝트 빌드

```shell
./gradlew build -x test
```

2. Docker Compose로 실행

```shell
docker compose up -d
```

이 명령어로 다음 서비스들이 실행됩니다:

- MySQL (port: 3306)
- Redis (port: 6379)
- Spring Boot Application (port: 8080)

### 유용한 Docker 명령어

```shell
# 컨테이너 로그 확인
docker compose logs -f

# 특정 서비스 로그 확인
docker compose logs -f app    # 애플리케이션 로그
docker compose logs -f mysql-docker  # MySQL 로그
docker compose logs -f redis-docker  # Redis 로그

# 컨테이너 중지
docker compose down

# 컨테이너 재시작
docker compose restart

# 특정 서비스만 재시작
docker compose restart app
```

### 데이터베이스 접속 정보

- **MySQL**

  - Host: localhost
  - Port: 3306
  - Database: futsal
  - Username: local
  - Password: password

- **Redis**
  - Host: localhost
  - Port: 6379

## 🔧 문제 해결

### MySQL 연결 오류 시

1. MySQL 컨테이너 상태 확인

```shell
docker ps | grep mysql
```

2. MySQL 로그 확인

```shell
docker compose logs mysql-docker
```

### 애플리케이션 오류 시

1. 애플리케이션 로그 확인

```shell
docker compose logs app
```

2. 컨테이너 재시작

```shell
docker compose restart app
```
