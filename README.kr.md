[한국어](./README.kr.md) | [日本語](./README.ja.md)

# Novforge API

Novforge의 사용자 인증, 사용자 정보, PC 부품 데이터와 사용자별 PC 견적을 관리하는 Spring Boot 백엔드 API입니다.

Google OpenID Connect 기반 회원가입·로그인을 지원하며, 로그인 성공 시 Novforge Access Token을 발급합니다. 사용자는 등록된 PC 부품을 선택하여 자신의 견적을 생성하고 단계적으로 수정할 수 있습니다.

## 주요 기능

### Auth

- Google ID Token 검증
- 가입된 Google 사용자 로그인
- Novforge Access Token 발급
- Google issuer, audience, 만료 시간 및 이메일 인증 여부 검증

### Users

- Google 계정 기반 회원가입
- 현재 사용자 정보 조회
- 닉네임 및 프로필 이미지 URL 수정
- 회원 탈퇴
- 관리자 전체 사용자 목록 조회

### Equipment

- 메인보드
- CPU
- GPU
- 메모리
- 보조기억장치
- 파워 서플라이
- CPU 쿨러
- PC 케이스
- 부품별 등록·목록 조회·상세 조회·부분 수정·삭제

### My Build

- 사용자별 PC 견적 생성
- 빈 견적 생성 후 부품 단계적 추가
- 단일 부품 추가 및 교체
- 여러 메모리·보조기억장치와 수량 저장
- 부품 가격과 수량을 이용한 총가격 자동 계산
- 본인 견적 목록·상세 조회·수정·삭제
- 다른 사용자의 견적 접근 차단

## 기술 스택

| 구분 | 기술 |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1.0 |
| Web | Spring Web MVC |
| ORM | Spring Data JPA, Hibernate |
| Database | PostgreSQL |
| Security | Spring Security, OAuth2 Resource Server, JWT |
| Validation | Jakarta Bean Validation |
| Environment | dotenv-java |
| Test Database | H2 |
| Build | Gradle Wrapper |

## 프로젝트 구조

```text
src/
├─ main/
│  ├─ java/com/novforge/api/
│  │  ├─ auth/
│  │  ├─ config/
│  │  ├─ users/
│  │  ├─ equipment/
│  │  │  ├─ motherboard/
│  │  │  ├─ cpu/
│  │  │  ├─ gpu/
│  │  │  ├─ memory/
│  │  │  ├─ storage/
│  │  │  ├─ powersupply/
│  │  │  ├─ cpucooler/
│  │  │  └─ pccase/
│  │  └─ mybuild/
│  └─ resources/
│     └─ application.properties
└─ test/
   ├─ java/
   └─ resources/
```

## 상세 문서

| 모듈 | 한국어 | 日本語 |
|---|---|---|
| Auth | [Auth 한국어 문서](./src/main/java/com/novforge/api/auth/README.kr.md) | [Auth 日本語ドキュメント](./src/main/java/com/novforge/api/auth/README.ja.md) |
| Users | [Users 한국어 문서](./src/main/java/com/novforge/api/users/README.kr.md) | [Users 日本語ドキュメント](./src/main/java/com/novforge/api/users/README.ja.md) |
| Equipment | [Equipment 한국어 문서](./src/main/java/com/novforge/api/equipment/README.kr.md) | [Equipment 日本語ドキュメント](./src/main/java/com/novforge/api/equipment/README.ja.md) |
| My Build | [My Build 한국어 문서](./src/main/java/com/novforge/api/mybuild/README.kr.md) | [My Build 日本語ドキュメント](./src/main/java/com/novforge/api/mybuild/README.ja.md) |

## API 엔드포인트

### Auth

| 기능 | 메서드 | 엔드포인트 | 인증 |
|---|---|---|---|
| Google 로그인 | `POST` | `/api/auth/google` | 불필요 |

### Users

| 기능 | 메서드 | 엔드포인트 | 인증 |
|---|---|---|---|
| Google 회원가입 | `POST` | `/api/users` | 불필요 |
| 전체 사용자 조회 | `GET` | `/api/users` | 관리자 Access Token |
| 내 정보 조회 | `GET` | `/api/users/me` | Access Token |
| 닉네임 수정 | `PATCH` | `/api/users/me` | Access Token |
| 프로필 이미지 수정 | `PATCH` | `/api/users/profile-images` | Access Token |
| 회원 탈퇴 | `DELETE` | `/api/users/me` | Access Token |

### Equipment

각 부품은 목록 조회, 상세 조회, 등록, 부분 수정, 삭제를 지원합니다.

```text
GET    /api/{domain}
GET    /api/{domain}/{id}
POST   /api/{domain}
PATCH  /api/{domain}/{id}
DELETE /api/{domain}/{id}
```

| 부품 | Domain |
|---|---|
| 메인보드 | `/api/mainboards` |
| CPU | `/api/cpus` |
| GPU | `/api/gpus` |
| 메모리 | `/api/memorys` |
| 보조기억장치 | `/api/storages` |
| 파워 서플라이 | `/api/power-supplies` |
| CPU 쿨러 | `/api/cpu-coolers` |
| PC 케이스 | `/api/cases` |

### My Build

| 기능 | 메서드 | 엔드포인트 | 인증 |
|---|---|---|---|
| 내 견적 목록 | `GET` | `/api/my-builds` | Access Token |
| 내 견적 상세 | `GET` | `/api/my-builds/{buildId}` | Access Token |
| 내 견적 생성 | `POST` | `/api/my-builds` | Access Token |
| 내 견적 수정 | `PATCH` | `/api/my-builds/{buildId}` | Access Token |
| 내 견적 삭제 | `DELETE` | `/api/my-builds/{buildId}` | Access Token |

## 인증 흐름

```text
Google 로그인
→ Google ID Token 발급
→ POST /api/users로 회원가입
→ 사용자 정보를 users 테이블에 저장
→ POST /api/auth/google로 로그인
→ Novforge Access Token 발급
→ Bearer Token으로 보호된 API 호출
```

이미 가입한 사용자는 회원가입을 생략하고 로그인부터 진행할 수 있습니다.

### 회원가입 요청

```http
POST /api/users
Content-Type: application/json
```

```json
{
  "idToken": "google-id-token",
  "userNickname": "노브작가"
}
```

### 로그인 요청

```http
POST /api/auth/google
Content-Type: application/json
```

```json
{
  "idToken": "google-id-token"
}
```

로그인 응답의 `accessToken`을 보호된 API에서 사용합니다.

```http
Authorization: Bearer <Novforge Access Token>
```

Google ID Token과 Novforge Access Token은 용도가 다릅니다.

```text
Google ID Token
→ 회원가입 및 로그인 요청에 사용

Novforge Access Token
→ Users, Equipment, My Build 보호 API에 사용
```

## My Build 관계

```text
users
  └─ my_build
       ├─ motherboard
       ├─ cpu
       ├─ gpu
       ├─ power
       ├─ cpu_cooler
       ├─ case
       ├─ my_build_ram → memory
       └─ my_build_storage → storage
```

메모리와 보조기억장치는 여러 제품과 수량을 저장할 수 있도록 중간 테이블을 사용합니다.

```text
my_build_ram
- build_id
- memory_id
- quantity

my_build_storage
- build_id
- storage_id
- quantity
```

## 환경변수

프로젝트 루트에 `.env` 파일을 생성합니다.

```env
DB_URL=jdbc:postgresql://localhost:5432/novforge
DB_USERNAME=postgres
DB_PASSWORD=your-database-password

GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com
JWT_SECRET=replace-with-a-random-secret-of-at-least-32-characters
JWT_ACCESS_TOKEN_EXPIRATION=3600
ADMIN_EMAILS=admin@example.com
```

| 환경변수 | 설명 |
|---|---|
| `DB_URL` | PostgreSQL JDBC URL |
| `DB_USERNAME` | PostgreSQL 사용자명 |
| `DB_PASSWORD` | PostgreSQL 비밀번호 |
| `GOOGLE_CLIENT_ID` | Google OAuth 2.0 Web Client ID |
| `JWT_SECRET` | Novforge Access Token 서명 키, 최소 32바이트 |
| `JWT_ACCESS_TOKEN_EXPIRATION` | Access Token 유효기간(초) |
| `ADMIN_EMAILS` | 관리자 Google 이메일, 여러 명은 쉼표로 구분 |

환경변수를 변경한 뒤에는 서버를 재시작해야 합니다.

## 실행 방법

### 요구사항

- Java 21
- PostgreSQL
- Google Cloud OAuth 2.0 Web Client

### Windows

```powershell
.\gradlew.bat bootRun
```

### macOS / Linux

```bash
./gradlew bootRun
```

기본 서버 주소:

```text
http://localhost:8080
```

## 빌드 및 테스트

### Windows

```powershell
.\gradlew.bat clean build
```

### macOS / Linux

```bash
./gradlew clean build
```

테스트는 H2 인메모리 DB를 사용하며 다음 항목을 포함합니다.

- Spring Application Context 구동
- Auth 및 Users 서비스
- My Build JPA 관계
- 사용자별 견적 소유권
- 메모리·보조기억장치 수량
- 견적 총가격 자동 계산

## HTTP 상태 코드

| 상태 코드 | 의미 |
|---|---|
| `200 OK` | 조회 또는 수정 성공 |
| `201 Created` | 회원가입, 부품 또는 견적 생성 성공 |
| `204 No Content` | 삭제 성공 |
| `400 Bad Request` | 요청값 검증 실패 또는 존재하지 않는 부품 선택 |
| `401 Unauthorized` | 인증 정보 누락·오류·만료 |
| `403 Forbidden` | 관리자 권한이 필요한 API에 일반 사용자가 접근 |
| `404 Not Found` | 사용자, 부품 또는 견적을 찾을 수 없음 |
| `409 Conflict` | 가입된 Google 계정 또는 중복 닉네임 |
| `405 Method Not Allowed` | 지원하지 않는 HTTP 메서드 또는 잘못된 경로 |
| `415 Unsupported Media Type` | JSON 요청의 Content-Type 오류 |

## DB 설정

현재 개발 환경은 다음 설정으로 Entity 변경을 DB에 반영합니다.

```properties
spring.jpa.hibernate.ddl-auto=update
```

운영 환경에서는 Flyway 또는 Liquibase 같은 스키마 마이그레이션 도구 도입을 권장합니다.

## 현재 제한사항

- Equipment 등록·수정·삭제에 대한 공통 관리자 권한 검사는 아직 완전히 연결되지 않았습니다.
- My Build의 단일 부품은 추가·교체할 수 있지만 명시적으로 제거하는 API는 아직 없습니다.
- My Build의 메모리·보조기억장치 PATCH는 전달된 배열 전체로 교체합니다.
- 공개 견적 목록·상세 API는 아직 없습니다.
- 부품 간 소켓, 규격, 전력 등의 호환성 검증은 아직 없습니다.
- 자동 부품 데이터 수집 및 단종 상태 관리 기능은 아직 없습니다.

## Notes

- `/api/memorys`는 현재 구현된 실제 엔드포인트입니다.
- 메인보드는 `/api/mainboards`와 호환 경로 `/api/motherboards`를 지원합니다.
- `case`는 Java 및 SQL 예약어이므로 Java 패키지와 Entity 이름에 `pccase`, `PcCase`를 사용합니다.
- 이미지 파일을 직접 업로드하지 않고 이미지 URL 문자열을 저장합니다.
- `totalPrice`는 클라이언트 요청이 아니라 서버의 부품 가격으로 계산합니다.

