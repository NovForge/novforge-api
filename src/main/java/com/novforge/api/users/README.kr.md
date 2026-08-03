[한국어](./README.kr.md) | [日本語](./README.ja.md)

# Users API

Google 인증을 완료한 사용자를 Novforge `users` 테이블에 등록하고, 현재 사용자 조회·닉네임 수정·프로필 이미지 수정·회원 탈퇴를 처리하는 API입니다. 관리자 계정은 전체 사용자 목록을 조회할 수 있습니다.

Google 이름, Google UID, 이메일은 사용자가 직접 수정할 수 없습니다. 앱에서 수정 가능한 사용자 정보는 닉네임과 프로필 이미지입니다. 이미지 파일은 Supabase Storage에 저장하고 공개 URL만 사용자 데이터에 보관합니다.

## API 엔드포인트

| 기능 | 도메인 | 엔드포인트 | 메서드 | 인증 | 설명 |
|---|---|---|---|---|---|
| 회원가입 | users | `/api/users` | `POST` | 불필요 | Google ID Token 검증 후 닉네임과 Google 사용자 정보를 DB에 저장 |
| 전체 사용자 조회 | users | `/api/users` | `GET` | 관리자 Access Token | `ADMIN_EMAILS`에 등록된 관리자만 모든 사용자 조회 |
| 내 정보 조회 | users | `/api/users/me` | `GET` | Access Token | 토큰의 `user_id`로 현재 사용자 DB 정보 조회 |
| 닉네임 수정 | users | `/api/users/me` | `PATCH` | Access Token | 현재 사용자의 닉네임만 수정 |
| 회원 탈퇴 | users | `/api/users/me` | `DELETE` | Access Token | 현재 사용자 정보를 DB에서 삭제 |
| 프로필 이미지 업로드 | users | `/api/users/profile-images` | `PATCH` | Access Token | 이미지 파일을 Supabase Storage에 업로드하고 공개 URL 저장 |

`POST /api/users`를 제외한 엔드포인트는 다음 인증 헤더가 필요합니다.

```http
Authorization: Bearer <Novforge Access Token>
```

## Directory

| 파일 | 역할 |
|---|---|
| `User.java` | `users` 테이블 JPA Entity |
| `UserController.java` | `/api/users` 엔드포인트 |
| `UserDto.java` | 회원가입·수정 요청과 사용자 응답 DTO |
| `UserRepository.java` | 사용자 ID, Google UID, 닉네임 조회 및 저장 |
| `UserService.java` | 회원가입, 중복 검증, 조회, 수정, 탈퇴, 관리자 권한 검사 |
| `ProfileImageStorageService.java` | 이미지 검증, Supabase Storage 업로드와 이전 이미지 삭제 |

## 사용자 데이터 모델

| Java 필드 | DB 컬럼 | 제약조건 | 출처·수정 정책 |
|---|---|---|---|
| `user_id` | `user_id` | PK, 자동 증가 | DB 자동 생성, 수정 불가 |
| `googleUid` | `google_uid` | `NOT NULL`, `UNIQUE` | Google ID Token `sub`, 수정 불가 |
| `name` | `user_name` | `NOT NULL`, 최대 50자 | Google ID Token `name`, 수정 불가 |
| `nickname` | `user_nickname` | `NOT NULL`, `UNIQUE`, 최대 50자 | 회원가입 시 입력, 닉네임 API로만 수정 |
| `email` | `user_email` | `NOT NULL`, `UNIQUE` | Google ID Token `email`, 수정 불가 |
| `profileImage` | `profile_image` | 최대 512자 | Google `picture`로 최초 저장, API로 수정 가능 |
| `createdAt` | `created_at` | `NOT NULL` | 최초 저장 시 자동 생성 |
| `updatedAt` | `updated_at` | nullable | 수정 시 자동 갱신 |

## 관리자 설정

`.env`에 관리자 권한을 부여할 Google 이메일을 지정합니다. 관리자는 전체 사용자 목록을 조회하고 장비를 등록·수정·삭제할 수 있습니다.

```env
ADMIN_EMAILS=admin@example.com
```

여러 명이면 쉼표로 구분합니다.

```env
ADMIN_EMAILS=admin1@example.com,admin2@example.com
```

환경 변수를 변경한 뒤에는 서버를 재시작해야 합니다. 관리자 계정도 먼저 `POST /api/users`로 회원가입해야 합니다.

## Endpoints

### 회원가입 — `POST /api/users`

```http
POST /api/users
Content-Type: application/json
```

Google ID Token을 검증하고 토큰의 `sub`, `name`, `email`, `picture`와 요청의 닉네임을 `users` 테이블에 저장합니다.

#### Request

```json
{
  "idToken": "google-id-token",
  "userNickname": "노브작가"
}
```

#### Required

- `idToken`: Google에서 발급된 ID Token
- `userNickname`: 공백이 아닌 50자 이하의 고유 닉네임

#### Response — `201 Created`

```json
{
  "userId": 1,
  "userName": "Google 이름",
  "userNickname": "노브작가",
  "userEmail": "user@example.com",
  "profileImage": "https://example.com/profile.jpg",
  "createdAt": "2026-07-27T12:00:00Z",
  "updatedAt": null
}
```

#### Postman 테스트

```text
Method: POST
URL: http://localhost:8080/api/users
Authorization: No Auth
Body: raw → JSON
```

같은 Google 계정이나 같은 닉네임으로 다시 가입하면 `409 Conflict`가 발생해야 합니다.

### 전체 사용자 조회 — `GET /api/users`

```http
GET /api/users
Authorization: Bearer <관리자 Novforge Access Token>
```

`.env`의 `ADMIN_EMAILS`에 등록된 이메일의 Access Token으로만 호출할 수 있습니다.

#### Response — `200 OK`

```json
[
  {
    "userId": 1,
    "userName": "관리자 Google 이름",
    "userNickname": "관리자",
    "userEmail": "admin@example.com",
    "profileImage": null,
    "createdAt": "2026-07-27T12:00:00Z",
    "updatedAt": null
  },
  {
    "userId": 2,
    "userName": "일반 사용자",
    "userNickname": "일반사용자",
    "userEmail": "user@example.com",
    "profileImage": null,
    "createdAt": "2026-07-27T12:05:00Z",
    "updatedAt": null
  }
]
```

#### Postman 테스트

1. 관리자 Google ID Token으로 `POST /api/auth/google`을 호출합니다.
2. 응답의 Novforge `accessToken`을 복사합니다.
3. `GET http://localhost:8080/api/users` 요청을 만듭니다.
4. Authorization을 `Bearer Token`으로 설정하고 Access Token을 입력합니다.
5. Body는 `none`으로 설정하고 전송합니다.

일반 사용자 Access Token으로 호출하면 `403 Forbidden`이 발생해야 합니다.

### 내 정보 조회 — `GET /api/users/me`

```http
GET /api/users/me
Authorization: Bearer <Novforge Access Token>
```

Access Token의 `sub`에서 `user_id`를 읽고 DB의 현재 사용자 정보를 조회합니다. URL이나 Body에 사용자 ID를 직접 전달하지 않습니다.

#### Response

```json
{
  "userId": 1,
  "userName": "Google 이름",
  "userNickname": "노브작가",
  "userEmail": "user@example.com",
  "profileImage": "https://example.com/profile.jpg",
  "createdAt": "2026-07-27T12:00:00Z",
  "updatedAt": null
}
```

#### Postman 테스트

```text
Method: GET
URL: http://localhost:8080/api/users/me
Authorization: Bearer Token
Token: POST /api/auth/google 응답의 accessToken
Body: none
```

### 닉네임 수정 — `PATCH /api/users/me`

```http
PATCH /api/users/me
Authorization: Bearer <Novforge Access Token>
Content-Type: application/json
```

현재 사용자의 닉네임만 수정합니다. Google 이름, 이메일, Google UID는 수정하지 않습니다.

#### Request

```json
{
  "userNickname": "새닉네임"
}
```

#### Response

```json
{
  "userId": 1,
  "userName": "Google 이름",
  "userNickname": "새닉네임",
  "userEmail": "user@example.com",
  "profileImage": "https://example.com/profile.jpg",
  "createdAt": "2026-07-27T12:00:00Z",
  "updatedAt": "2026-07-27T13:00:00Z"
}
```

이미 다른 사용자가 사용 중인 닉네임이면 `409 Conflict`가 발생합니다.

### 프로필 이미지 업로드 — `PATCH /api/users/profile-images`

```http
PATCH /api/users/profile-images
Authorization: Bearer <Novforge Access Token>
Content-Type: multipart/form-data
```

이미지 파일을 Supabase Storage에 업로드하고 반환된 공개 URL을 DB에 저장합니다. 웹과 모바일 클라이언트는 `profileImage` 이름으로 파일을 전송합니다.

#### Request

```text
profileImage: avatar.jpg
```

JPEG, PNG, WebP 형식만 허용하며 최대 크기는 5MB입니다. 파일 확장자나 요청의 Content-Type이 아니라 실제 파일 헤더를 검사합니다.

#### Response

```json
{
  "userId": 1,
  "userName": "Google 이름",
  "userNickname": "노브작가",
  "userEmail": "user@example.com",
  "profileImage": "https://project.supabase.co/storage/v1/object/public/profiles/users/1/uuid.jpg",
  "createdAt": "2026-07-27T12:00:00Z",
  "updatedAt": "2026-07-27T13:05:00Z"
}
```

새 이미지 저장과 사용자 URL 갱신이 성공하면 같은 Supabase 버킷에 있던 이전 프로필 이미지를 삭제합니다. Google 기본 프로필 등 외부 URL은 삭제하지 않습니다.

기존 JSON URL 수정 요청도 이전 클라이언트 호환을 위해 유지합니다.

```json
{
  "profileImage": "https://example.com/profile.jpg"
}
```

### Supabase Storage 설정

API `.env`에 다음 값을 설정합니다.

```env
SUPABASE_URL=https://project-ref.supabase.co
SUPABASE_SECRET_KEY=sb_secret_server-key
SUPABASE_PROFILE_BUCKET=profiles
```

`SUPABASE_SECRET_KEY`는 모바일이나 웹 클라이언트에 포함하지 않습니다. `profiles` 버킷은 이미지 공개 URL을 표시할 수 있도록 Public으로 생성합니다.

### 회원 탈퇴 — `DELETE /api/users/me`

```http
DELETE /api/users/me
Authorization: Bearer <Novforge Access Token>
```

현재 사용자를 `users` 테이블에서 삭제합니다. 사용자가 저장한 `my_build`와 해당 견적의 `my_build_ram`, `my_build_storage` 관계 행은 JPA cascade로 함께 삭제됩니다. Supabase Storage에 업로드한 프로필 이미지도 함께 정리합니다.

#### Response

```text
204 No Content
```

Body는 반환하지 않습니다.

## 전체 Postman 테스트 순서

```text
1. Postman OAuth 2.0에서 Google id_token 발급
2. POST /api/users로 회원가입
3. POST /api/auth/google로 Novforge accessToken 발급
4. GET /api/users/me로 DB 사용자 정보 확인
5. PATCH /api/users/me로 닉네임 변경
6. PATCH /api/users/profile-images에 이미지 파일을 전송하여 프로필 이미지 변경
7. 관리자 계정이면 GET /api/users로 전체 목록 확인
8. 필요 시 DELETE /api/users/me로 탈퇴 확인
```

## 오류 응답

| 상태 코드 | 상황 |
|---|---|
| `400 Bad Request` | 필수값 누락, 공백 닉네임, 파일 읽기 실패, Google 필수 claim 누락 |
| `401 Unauthorized` | Access Token 누락·오류·만료 |
| `403 Forbidden` | 관리자가 아닌 사용자가 전체 사용자 목록 조회 |
| `404 Not Found` | Access Token의 사용자 ID에 해당하는 DB 사용자가 없음 |
| `409 Conflict` | 이미 가입된 Google 계정 또는 중복 닉네임 |
| `413 Payload Too Large` | 프로필 이미지가 5MB를 초과 |
| `415 Unsupported Media Type` | 요청 Content-Type이 올바르지 않거나 이미지가 JPEG, PNG, WebP 형식이 아님 |
| `502 Bad Gateway` | Supabase Storage 업로드 실패 |
| `503 Service Unavailable` | Supabase 환경변수 누락 또는 업로드 중단 |

## Notes

- `GET /api/users/me`는 URL에 `user_id`를 받지 않습니다. Access Token의 `sub`로 현재 사용자를 식별합니다.
- `/api/users/1`과 같은 특정 사용자 조회 엔드포인트는 제공하지 않습니다.
- Google 이름과 이메일은 닉네임 수정 API에서 변경되지 않습니다.
- 회원가입 시 닉네임 앞뒤 공백을 제거한 후 중복 여부를 확인합니다.
- 프로필 이미지 삭제를 위한 `null` 또는 빈 파일은 현재 허용하지 않습니다.
- DB 스키마에 `user_nickname VARCHAR(50) NOT NULL UNIQUE`가 필요합니다.
