# Auth API

Google OpenID Connect ID Token을 검증한 뒤 Novforge에 이미 가입된 사용자를 로그인 처리하고, Novforge API에서 사용할 자체 Access Token을 발급하는 API입니다.

회원가입과 로그인은 분리되어 있습니다. 신규 사용자는 먼저 `POST /api/users`로 가입해야 하며, `POST /api/auth/google`은 DB에 존재하는 사용자에게만 Access Token을 발급합니다.

## API 엔드포인트

| 기능 | 도메인 | 엔드포인트 | 메서드 | 인증 | 설명 |
|---|---|---|---|---|---|
| Google 로그인 | auth | `/api/auth/google` | `POST` | 불필요 | Google ID Token 검증 후 가입된 사용자를 조회하고 Novforge Access Token 발급 |

`/api/auth/google`은 `Authorization` 헤더 없이 호출합니다. Google ID Token은 JSON 요청 Body에 전달합니다.

## Directory

| 파일 | 역할 |
|---|---|
| `AuthController.java` | `/api/auth` 엔드포인트 |
| `AuthDto.java` | Google 로그인 요청 및 로그인 응답 DTO |
| `AuthService.java` | Google ID Token 검증, 가입 사용자 조회, Novforge Access Token 발급 |
| `../config/SecurityConfig.java` | Google 토큰 검증기, Novforge 토큰 인코더·디코더, API 접근 정책 |

## 인증 모델

이 API는 두 종류의 토큰을 사용합니다.

| 토큰 | 발급자 | 사용 목적 |
|---|---|---|
| Google `id_token` | Google | 회원가입 및 Google 로그인 요청 |
| Novforge `accessToken` | Novforge API | 로그인 후 보호된 API 호출 |

인증 흐름:

```text
Google 로그인
→ Google id_token 발급
→ POST /api/auth/google
→ Google 토큰 검증
→ google_uid로 가입 사용자 조회
→ Novforge accessToken 발급
→ Authorization: Bearer <Novforge accessToken>
```

Novforge Access Token의 `sub`에는 DB의 `user_id`가 들어가며, `email` claim도 포함됩니다. 기본 만료 시간은 3,600초이며 `JWT_ACCESS_TOKEN_EXPIRATION`으로 변경할 수 있습니다.

## 서버 설정

서버 실행 전에 다음 환경 변수를 설정해야 합니다.

```env
GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com
JWT_SECRET=replace-with-a-random-secret-of-at-least-32-characters
JWT_ACCESS_TOKEN_EXPIRATION=3600
```

- `GOOGLE_CLIENT_ID`: Google Cloud OAuth 2.0 웹 클라이언트 ID
- `JWT_SECRET`: Novforge Access Token 서명에 사용하는 32바이트 이상의 비밀키
- `JWT_ACCESS_TOKEN_EXPIRATION`: Access Token 유효시간(초)

`JWT_SECRET`은 코드 저장소에 커밋하면 안 됩니다.

## Postman에서 Google ID Token 발급

Postman의 `Authorization → OAuth 2.0 → Configure New Token`에 입력합니다.

| 항목 | 값 |
|---|---|
| Token Name | `Google Login` |
| Grant Type | `Authorization Code` |
| Authorize using browser | 체크 |
| Auth URL | `https://accounts.google.com/o/oauth2/v2/auth` |
| Access Token URL | `https://oauth2.googleapis.com/token` |
| Client ID | Google Cloud Client ID |
| Client Secret | Google Cloud Client Secret |
| Scope | `openid email profile` |
| Client Authentication | `Send client credentials in body` |

Callback URL은 Postman에 표시되는 값을 사용하고 Google Cloud의 `승인된 리디렉션 URI`에도 같은 값을 등록합니다.

`Get New Access Token`으로 Google 로그인을 완료한 뒤 결과의 `access_token`이 아니라 `id_token`을 복사합니다.

## Endpoints

### Google 로그인 — `POST /api/auth/google`

```http
POST /api/auth/google
Content-Type: application/json
```

가입된 사용자의 Google ID Token을 검증하고 Novforge Access Token을 발급합니다. 로그인 과정에서는 신규 사용자를 DB에 저장하지 않습니다.

#### Request

```json
{
  "idToken": "google-id-token"
}
```

#### Required

- `idToken`: Google OAuth/OpenID Connect에서 발급된 ID Token

#### Response

```json
{
  "accessToken": "novforge-access-token",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "userId": 1,
    "userName": "Google 이름",
    "userNickname": "노브작가",
    "userEmail": "user@example.com",
    "profileImage": "https://example.com/profile.jpg",
    "createdAt": "2026-07-27T12:00:00Z",
    "updatedAt": null
  }
}
```

#### Postman 테스트

1. Method를 `POST`로 선택합니다.
2. URL에 `http://localhost:8080/api/auth/google`을 입력합니다.
3. Authorization은 `No Auth`를 선택합니다.
4. Body는 `raw → JSON`을 선택합니다.
5. Google `id_token`을 Body에 넣고 전송합니다.
6. 응답의 `accessToken`을 보호된 API의 Bearer Token으로 사용합니다.

## 보호된 API 호출 예시

```http
GET /api/users/me
Authorization: Bearer <Novforge Access Token>
```

Google `id_token`을 보호된 API의 Bearer Token으로 사용하지 않습니다.

## 오류 응답

| 상태 코드 | 상황 |
|---|---|
| `400 Bad Request` | `idToken`이 누락되거나 비어 있음 |
| `401 Unauthorized` | Google ID Token이 만료됐거나 서명·issuer·audience 검증에 실패 |
| `404 Not Found` | Google 계정이 Novforge DB에 가입되어 있지 않음 |
| `405 Method Not Allowed` | `/api/auth/google`을 `POST`가 아닌 Method로 호출 |

## Notes

- Google ID Token의 issuer, Client ID audience, 만료 시간, 이메일 인증 여부를 검증합니다.
- 신규 사용자 저장은 `POST /api/users`에서만 수행합니다.
- Google ID Token과 Novforge Access Token은 DB에 저장하지 않습니다.
- Access Token이 만료돼도 회원가입 사용자 정보는 DB에 유지됩니다.
- Access Token이 만료되면 새 Google ID Token으로 `/api/auth/google`을 다시 호출합니다.
