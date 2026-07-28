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

## Spring Boot를 선택한 이유

Novforge는 사용자 인증, 부품 CRUD, 여러 테이블의 관계와 사용자별 견적을 처리해야 합니다. Spring Boot는 이러한 기능을 계층별로 분리하고 보안·DB·검증 기능을 일관된 방식으로 연결하기 적합하여 사용했습니다.

### 선택 이유

- Spring MVC를 이용해 REST API의 요청과 응답을 명확하게 구성할 수 있습니다.
- Spring Data JPA를 통해 반복적인 SQL과 CRUD 코드를 줄일 수 있습니다.
- Spring Security와 OAuth2 Resource Server를 사용해 JWT 인증을 API 앞단에서 공통 처리할 수 있습니다.
- Bean Validation으로 Controller 진입 시 요청값을 검증할 수 있습니다.
- 의존성 주입을 통해 Controller, Service, Repository의 책임을 분리하고 테스트하기 쉽습니다.
- Spring Boot 자동 설정으로 웹 서버, Jackson JSON 변환, JPA와 DB 연결을 빠르게 구성할 수 있습니다.

### 장점

| 장점 | 설명 |
|---|---|
| 빠른 개발 | 자동 설정과 Starter 의존성으로 초기 환경 구성이 간단합니다. |
| 계층 분리 | Controller, Service, Repository, Entity 역할을 명확하게 나눌 수 있습니다. |
| 보안 통합 | JWT 검증과 보호 경로 설정을 `SecurityFilterChain`에서 관리할 수 있습니다. |
| DB 생산성 | JPA Repository가 기본 CRUD와 트랜잭션 처리를 지원합니다. |
| 검증과 예외 처리 | Bean Validation과 `RestControllerAdvice`로 일관된 오류 응답을 구성할 수 있습니다. |
| 테스트 지원 | Spring Context, Security, JPA를 포함한 통합 테스트 도구가 잘 갖춰져 있습니다. |
| 확장성 | 공개 견적, 호환성 검사, 자동 부품 수집 등의 기능을 기존 계층에 추가하기 쉽습니다. |

### 단점 및 트레이드오프

| 단점 | 현재 프로젝트의 대응 |
|---|---|
| 프레임워크 학습 범위가 넓음 | 패키지와 계층 구조를 기능별로 통일하고 README에 처리 흐름을 기록합니다. |
| 자동 설정으로 실제 동작을 파악하기 어려울 수 있음 | Security, JPA, 환경변수 설정을 명시적인 Configuration과 문서로 관리합니다. |
| JPA 연관관계를 잘못 설정하면 N+1 조회가 발생할 수 있음 | My Build 조회에서 `EntityGraph`로 필요한 연관 부품을 함께 로딩합니다. |
| Entity 변경이 운영 DB에 바로 영향을 줄 수 있음 | 현재는 `ddl-auto=update`를 사용하지만 운영 전 마이그레이션 도구 도입이 필요합니다. |
| 애플리케이션 시작 시간과 메모리 사용량이 단순 프레임워크보다 큼 | 현재 서비스 규모에서는 개발 생산성과 유지보수성을 우선합니다. |
| 잘못된 트랜잭션 범위에서 Lazy Loading 오류가 발생할 수 있음 | Service 계층에서 트랜잭션과 Entity→DTO 변환을 처리합니다. |

## 주요 기술 선택

### Spring Web MVC

HTTP 요청을 Controller로 전달하고 Java 객체를 JSON 응답으로 변환합니다.

```text
HTTP Request
→ DispatcherServlet
→ Controller
→ Service
→ Repository
→ PostgreSQL
→ Response DTO
→ JSON Response
```

동기식 요청 처리 방식이라 현재 CRUD 중심 API에 적합합니다. 대규모 실시간 스트리밍이 필요해지면 WebFlux 또는 별도 이벤트 시스템을 검토할 수 있습니다.

### Spring Data JPA와 Hibernate

Java Entity와 PostgreSQL 테이블을 매핑하고 Repository를 통해 데이터를 조회·저장합니다.

장점:

- 기본 CRUD 구현량 감소
- 객체 관계로 사용자, 견적, 부품 연결
- 트랜잭션과 변경 감지 지원
- DB 종류가 바뀌어도 Service 코드의 변경 범위가 작음

주의점:

- 복잡한 조회는 생성 SQL을 확인해야 합니다.
- 연관관계 Fetch 전략과 N+1 문제를 관리해야 합니다.
- 운영 스키마 변경은 JPA 자동 생성에만 의존하지 않는 것이 안전합니다.

### PostgreSQL

사용자, 부품, 견적처럼 관계와 무결성이 중요한 데이터를 저장하기 위해 관계형 DB를 사용합니다.

- Foreign Key로 존재하는 사용자와 부품만 견적에 연결
- 트랜잭션을 통한 일관된 데이터 변경
- 복합키를 이용한 메모리·스토리지 중복 관계 방지
- 향후 검색, 정렬, 통계 쿼리 확장 가능

### Spring Security와 JWT

Google은 사용자의 신원을 확인하고, Novforge 서버는 자체 Access Token을 발급합니다.

```text
Google ID Token
→ Google 서명·issuer·audience·만료 검증
→ users 테이블에서 가입 사용자 확인
→ Novforge JWT Access Token 발급
→ 이후 API 요청의 Bearer Token 검증
```

JWT는 서버 세션을 저장하지 않아 API 서버 확장에 유리하지만, 발급 후 즉시 강제 만료시키기 어렵습니다. 로그아웃·토큰 폐기 기능이 필요해지면 Refresh Token과 차단 목록 또는 토큰 버전 정책을 추가해야 합니다.

### H2 테스트 DB

테스트에서는 실제 PostgreSQL 데이터를 변경하지 않도록 H2 인메모리 DB를 사용합니다.

빠르고 독립적인 테스트가 가능하지만 PostgreSQL과 SQL 문법 및 타입 동작이 완전히 같지는 않습니다. 운영 전에는 PostgreSQL을 사용하는 통합 테스트 환경도 추가하는 것이 좋습니다.

### Gradle Wrapper

개발자가 별도로 같은 Gradle 버전을 설치하지 않아도 프로젝트에 포함된 Wrapper로 빌드할 수 있습니다.

```text
Windows: .\gradlew.bat
macOS/Linux: ./gradlew
```

## 애플리케이션 동작 구조

### 일반적인 API 요청

```text
1. 클라이언트가 HTTP 요청 전송
2. Spring Security가 인증이 필요한 경로의 Bearer Token 검증
3. Controller가 URL, HTTP Method, JSON Body를 DTO로 변환
4. Bean Validation이 필수값, 길이, 숫자 범위를 검증
5. Service가 비즈니스 규칙과 소유권을 검사
6. Repository가 JPA를 통해 PostgreSQL 조회·변경
7. Entity를 Response DTO로 변환
8. Spring MVC가 JSON과 HTTP 상태 코드로 응답
```

### My Build 생성 요청

```text
POST /api/my-builds
→ JWT sub에서 user_id 확인
→ 요청한 부품 ID 조회
→ 단일 부품 Foreign Key 연결
→ 메모리·스토리지와 수량을 중간 테이블에 저장
→ 서버에서 전체 가격 계산
→ my_build 및 관계 테이블 저장
→ 생성된 견적을 JSON으로 반환
```

## 계층별 책임

| 계층 | 책임 |
|---|---|
| Controller | HTTP 경로, Method, 인증 사용자, 요청·응답 처리 |
| Request DTO | 클라이언트 입력 구조와 Validation |
| Service | 비즈니스 규칙, 트랜잭션, 소유권, 가격 계산 |
| Repository | JPA 기반 DB 조회·저장 |
| Entity | 테이블 및 연관관계 매핑, 상태 변경 |
| Response DTO | 외부에 공개할 응답 구조 |
| Exception Handler | 예외를 HTTP 상태 코드와 Problem Detail로 변환 |
| SecurityConfig | 공개·보호 API와 JWT 검증 설정 |

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

### 실행 전 준비

```text
1. PostgreSQL에서 Novforge용 데이터베이스 생성
2. Google Cloud Console에서 OAuth 2.0 Web Client 생성
3. 프로젝트 루트에 .env 작성
4. Java 21 사용 여부 확인
5. Gradle Wrapper로 서버 실행
```

Java 버전 확인:

```bash
java -version
```

정상적으로 Java 21이 표시되어야 합니다.

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

서버가 실행되면 Spring Boot가 Entity를 확인하고 PostgreSQL에 필요한 테이블을 생성하거나 변경합니다.

### 실행 확인

별도의 Health Check API는 아직 없으므로 서버 콘솔에서 다음 내용을 확인합니다.

```text
Started ApiApplication
Tomcat started on port 8080
```

그다음 Postman에서 공개 API인 `POST /api/users` 또는 `POST /api/auth/google`을 호출하여 동작을 확인할 수 있습니다.

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

- Equipment 조회는 로그인 사용자에게 허용하며, 등록·수정·삭제는 `ADMIN_EMAILS`에 등록된 관리자만 실행할 수 있습니다.
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
