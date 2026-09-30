[한국어](./README.kr.md) | [日本語](./README.ja.md)

# Auth API

Google OpenID Connect の ID Token を検証し、Novforge に登録済みのユーザーをログインさせ、Novforge API で使用する独自の Access Token を発行する API です。

会員登録とログインは分離されています。新規ユーザーは先に `POST /api/users` で会員登録を行う必要があり、`POST /api/auth/google` は DB に存在するユーザーに対してのみ Access Token を発行します。

## API エンドポイント

| 機能 | ドメイン | エンドポイント | メソッド | 認証 | 説明 |
|---|---|---|---|---|---|
| Google ログイン | auth | `/api/auth/google` | `POST` | 不要 | Google ID Token の検証後、登録済みユーザーを照会し、Novforge Access Token を発行 |

`/api/auth/google` は `Authorization` ヘッダーなしで呼び出します。Google ID Token は JSON リクエストボディに含めます。

## Directory

| ファイル | 役割 |
|---|---|
| `AuthController.java` | `/api/auth` エンドポイント |
| `AuthDto.java` | Google ログインのリクエスト・レスポンス DTO |
| `AuthService.java` | Google ID Token の検証、登録済みユーザーの照会、Novforge Access Token の発行 |
| `../config/SecurityConfig.java` | Google Token 検証器、Novforge Token のエンコーダー・デコーダー、API アクセスポリシー |

## 認証モデル

本 API では 2 種類のトークンを使用します。

| トークン | 発行元 | 使用目的 |
|---|---|---|
| Google `id_token` | Google | 会員登録および Google ログインリクエスト |
| Novforge `accessToken` | Novforge API | ログイン後の保護された API の呼び出し |

認証フロー:

```text
Google ログイン
→ Google id_token 発行
→ POST /api/auth/google
→ Google Token 検証
→ google_uid で登録済みユーザーを照会
→ Novforge accessToken 発行
→ Authorization: Bearer <Novforge accessToken>
```

Novforge Access Token の `sub` には DB の `user_id` が入り、`email` claim も含まれます。デフォルトの有効期限は 3,600 秒で、`JWT_ACCESS_TOKEN_EXPIRATION` で変更できます。

## API アクセスポリシー

| 区分 | エンドポイント | 認証 |
|---|---|---|
| 会員登録 | `POST /api/users` | 不要 |
| Google ログイン | `POST /api/auth/google` | 不要 |
| 公開構成の一覧・詳細 | `GET /api/my-builds`, `GET /api/my-builds/{buildId}` | 不要 |
| 自分の情報・構成 | `/api/users/me`, `/api/my-builds/me/**` | Access Token |
| Equipment 照会 | Equipment API の `GET` | Access Token |
| Equipment 登録・変更・削除 | Equipment API の `POST`, `PATCH`, `DELETE` | 管理者 Access Token |

公開構成の照会は Postman で `Authorization: No Auth` を選択します。期限切れ、または不正な Bearer Token を同時に送信すると、公開パスでも JWT 検証段階で `401 Unauthorized` になる場合があります。

Access Token の `email` が `ADMIN_EMAILS` に含まれている場合、Spring Security が `ROLE_ADMIN` を付与します。この権限は Equipment の登録・変更・削除のアクセス制御に使用されます。

## サーバー設定

サーバーを起動する前に、以下の環境変数を設定する必要があります。

```env
GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com
JWT_SECRET=replace-with-a-random-secret-of-at-least-32-characters
JWT_ACCESS_TOKEN_EXPIRATION=3600
ADMIN_EMAILS=admin@example.com
```

- `GOOGLE_CLIENT_ID`: Google Cloud OAuth 2.0 Web クライアント ID
- `JWT_SECRET`: Novforge Access Token の署名に使用する 32 バイト以上の秘密鍵
- `JWT_ACCESS_TOKEN_EXPIRATION`: Access Token の有効期間（秒）
- `ADMIN_EMAILS`: 管理者権限を付与する Google メールアドレス、複数指定はカンマ区切り

`JWT_SECRET` はコードリポジトリにコミットしないでください。

## Postman で Google ID Token を発行する

Postman の `Authorization → OAuth 2.0 → Configure New Token` に以下を入力します。

| 項目 | 値 |
|---|---|
| Token Name | `Google Login` |
| Grant Type | `Authorization Code` |
| Authorize using browser | チェック |
| Auth URL | `https://accounts.google.com/o/oauth2/v2/auth` |
| Access Token URL | `https://oauth2.googleapis.com/token` |
| Client ID | Google Cloud Client ID |
| Client Secret | Google Cloud Client Secret |
| Scope | `openid email profile` |
| Client Authentication | `Send client credentials in body` |

Callback URL には Postman に表示される値を使用し、Google Cloud の「承認済みのリダイレクト URI」にも同じ値を登録します。

`Get New Access Token` で Google ログインを完了した後、結果に含まれる `access_token` ではなく `id_token` をコピーします。

## Endpoints

### Google ログイン — `POST /api/auth/google`

```http
POST /api/auth/google
Content-Type: application/json
```

登録済みユーザーの Google ID Token を検証し、Novforge Access Token を発行します。ログイン処理では、新規ユーザーを DB に保存しません。

#### Request

```json
{
  "idToken": "google-id-token"
}
```

#### Required

- `idToken`: Google OAuth/OpenID Connect で発行された ID Token

#### Response

```json
{
  "accessToken": "novforge-access-token",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "userId": 1,
    "userName": "Google上の名前",
    "userNickname": "ノブ作家",
    "userEmail": "user@example.com",
    "profileImage": "https://example.com/profile.jpg",
    "createdAt": "2026-07-27T12:00:00Z",
    "updatedAt": null
  }
}
```

#### Postman テスト

1. Method に `POST` を選択します。
2. URL に `http://localhost:8080/api/auth/google` を入力します。
3. Authorization は `No Auth` を選択します。
4. Body は `raw → JSON` を選択します。
5. Google `id_token` を Body に入れて送信します。
6. レスポンスの `accessToken` を、保護された API の Bearer Token として使用します。

## 保護された API の呼び出し例

```http
GET /api/users/me
Authorization: Bearer <Novforge Access Token>
```

Google `id_token` を保護された API の Bearer Token として使用しないでください。

## エラーレスポンス

| ステータスコード | 状況 |
|---|---|
| `400 Bad Request` | `idToken` が未指定、または空文字 |
| `401 Unauthorized` | Google ID Token の有効期限切れ、または署名・issuer・audience の検証失敗 |
| `404 Not Found` | Google アカウントが Novforge DB に登録されていない |
| `405 Method Not Allowed` | `/api/auth/google` を `POST` 以外の Method で呼び出した |

## Notes

- Google ID Token の issuer、Client ID audience、有効期限、メール認証状態を検証します。
- 新規ユーザーの保存は `POST /api/users` でのみ行います。
- Google ID Token と Novforge Access Token は DB に保存しません。
- Access Token が失効しても、会員登録済みのユーザー情報は DB に保持されます。
- Access Token が失効した場合は、新しい Google ID Token で `/api/auth/google` を再度呼び出します。
