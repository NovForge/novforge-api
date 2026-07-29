[한국어](./README.kr.md) | [日本語](./README.ja.md)

# Users API

Google 認証を完了したユーザーを Novforge の `users` テーブルに登録し、ログイン中ユーザーの照会、ニックネーム変更、プロフィール画像変更、退会を処理する API です。管理者アカウントは全ユーザー一覧を照会できます。

Google 上の名前、Google UID、メールアドレスはユーザー自身が変更できません。アプリ内で変更できるユーザー情報は、ニックネームとプロフィール画像 URL です。

## API エンドポイント

| 機能 | ドメイン | エンドポイント | メソッド | 認証 | 説明 |
|---|---|---|---|---|---|
| 会員登録 | users | `/api/users` | `POST` | 不要 | Google ID Token の検証後、ニックネームと Google ユーザー情報を DB に保存 |
| 全ユーザー照会 | users | `/api/users` | `GET` | 管理者 Access Token | `ADMIN_EMAILS` に登録された管理者のみ全ユーザーを照会可能 |
| 自分の情報を照会 | users | `/api/users/me` | `GET` | Access Token | Token の `user_id` でログイン中ユーザーの DB 情報を照会 |
| ニックネーム変更 | users | `/api/users/me` | `PATCH` | Access Token | ログイン中ユーザーのニックネームのみ変更 |
| 退会 | users | `/api/users/me` | `DELETE` | Access Token | ログイン中ユーザーの情報を DB から削除 |
| プロフィール画像変更 | users | `/api/users/profile-images` | `PATCH` | Access Token | ログイン中ユーザーのプロフィール画像 URL を変更 |

`POST /api/users` を除くエンドポイントでは、次の認証ヘッダーが必要です。

```http
Authorization: Bearer <Novforge Access Token>
```

## Directory

| ファイル | 役割 |
|---|---|
| `User.java` | `users` テーブルの JPA Entity |
| `UserController.java` | `/api/users` エンドポイント |
| `UserDto.java` | 会員登録・変更リクエストとユーザーレスポンス DTO |
| `UserRepository.java` | ユーザー ID、Google UID、ニックネームの照会および保存 |
| `UserService.java` | 会員登録、重複検証、照会、変更、退会、管理者権限の検証 |

## ユーザーデータモデル

| Java フィールド | DB カラム | 制約 | 取得元・変更ポリシー |
|---|---|---|---|
| `user_id` | `user_id` | PK、自動採番 | DB が自動生成、変更不可 |
| `googleUid` | `google_uid` | `NOT NULL`, `UNIQUE` | Google ID Token の `sub`、変更不可 |
| `name` | `user_name` | `NOT NULL`、最大 50 文字 | Google ID Token の `name`、変更不可 |
| `nickname` | `user_nickname` | `NOT NULL`, `UNIQUE`、最大 50 文字 | 会員登録時に入力、ニックネーム API でのみ変更可能 |
| `email` | `user_email` | `NOT NULL`, `UNIQUE` | Google ID Token の `email`、変更不可 |
| `profileImage` | `profile_image` | 最大 512 文字 | 初回は Google の `picture` を保存、API で変更可能 |
| `createdAt` | `created_at` | `NOT NULL` | 初回保存時に自動生成 |
| `updatedAt` | `updated_at` | nullable | 変更時に自動更新 |

## 管理者設定

`.env` に管理者権限を付与する Google メールアドレスを指定します。管理者は全ユーザー一覧を照会し、Equipment を登録・変更・削除できます。

```env
ADMIN_EMAILS=admin@example.com
```

複数指定する場合はカンマで区切ります。

```env
ADMIN_EMAILS=admin1@example.com,admin2@example.com
```

環境変数を変更した後は、サーバーを再起動する必要があります。管理者アカウントも先に `POST /api/users` で会員登録を行う必要があります。

## Endpoints

### 会員登録 — `POST /api/users`

```http
POST /api/users
Content-Type: application/json
```

Google ID Token を検証し、Token の `sub`、`name`、`email`、`picture` とリクエストで受け取ったニックネームを `users` テーブルに保存します。

#### Request

```json
{
  "idToken": "google-id-token",
  "userNickname": "ノブ作家"
}
```

#### Required

- `idToken`: Google から発行された ID Token
- `userNickname`: 空白ではない、50 文字以内の一意なニックネーム

#### Response — `201 Created`

```json
{
  "userId": 1,
  "userName": "Google上の名前",
  "userNickname": "ノブ作家",
  "userEmail": "user@example.com",
  "profileImage": "https://example.com/profile.jpg",
  "createdAt": "2026-07-27T12:00:00Z",
  "updatedAt": null
}
```

#### Postman テスト

```text
Method: POST
URL: http://localhost:8080/api/users
Authorization: No Auth
Body: raw → JSON
```

同じ Google アカウント、または同じニックネームで再度登録すると、`409 Conflict` が返されます。

### 全ユーザー照会 — `GET /api/users`

```http
GET /api/users
Authorization: Bearer <管理者の Novforge Access Token>
```

`.env` の `ADMIN_EMAILS` に登録されたメールアドレスの Access Token でのみ呼び出せます。

#### Response — `200 OK`

```json
[
  {
    "userId": 1,
    "userName": "管理者のGoogle上の名前",
    "userNickname": "管理者",
    "userEmail": "admin@example.com",
    "profileImage": null,
    "createdAt": "2026-07-27T12:00:00Z",
    "updatedAt": null
  },
  {
    "userId": 2,
    "userName": "一般ユーザー",
    "userNickname": "一般ユーザー",
    "userEmail": "user@example.com",
    "profileImage": null,
    "createdAt": "2026-07-27T12:05:00Z",
    "updatedAt": null
  }
]
```

#### Postman テスト

1. 管理者の Google ID Token で `POST /api/auth/google` を呼び出します。
2. レスポンスの Novforge `accessToken` をコピーします。
3. `GET http://localhost:8080/api/users` リクエストを作成します。
4. Authorization を `Bearer Token` に設定し、Access Token を入力します。
5. Body は `none` に設定して送信します。

一般ユーザーの Access Token で呼び出すと、`403 Forbidden` が返されます。

### 自分の情報を照会 — `GET /api/users/me`

```http
GET /api/users/me
Authorization: Bearer <Novforge Access Token>
```

Access Token の `sub` から `user_id` を読み取り、DB からログイン中ユーザーの情報を照会します。URL や Body にユーザー ID を直接指定する必要はありません。

#### Response

```json
{
  "userId": 1,
  "userName": "Google上の名前",
  "userNickname": "ノブ作家",
  "userEmail": "user@example.com",
  "profileImage": "https://example.com/profile.jpg",
  "createdAt": "2026-07-27T12:00:00Z",
  "updatedAt": null
}
```

#### Postman テスト

```text
Method: GET
URL: http://localhost:8080/api/users/me
Authorization: Bearer Token
Token: POST /api/auth/google のレスポンスに含まれる accessToken
Body: none
```

### ニックネーム変更 — `PATCH /api/users/me`

```http
PATCH /api/users/me
Authorization: Bearer <Novforge Access Token>
Content-Type: application/json
```

ログイン中ユーザーのニックネームのみを変更します。Google 上の名前、メールアドレス、Google UID は変更しません。

#### Request

```json
{
  "userNickname": "新しいニックネーム"
}
```

#### Response

```json
{
  "userId": 1,
  "userName": "Google上の名前",
  "userNickname": "新しいニックネーム",
  "userEmail": "user@example.com",
  "profileImage": "https://example.com/profile.jpg",
  "createdAt": "2026-07-27T12:00:00Z",
  "updatedAt": "2026-07-27T13:00:00Z"
}
```

他のユーザーが使用中のニックネームを指定すると、`409 Conflict` が返されます。

### プロフィール画像変更 — `PATCH /api/users/profile-images`

```http
PATCH /api/users/profile-images
Authorization: Bearer <Novforge Access Token>
Content-Type: application/json
```

画像ファイルをアップロードするのではなく、プロフィール画像 URL の文字列のみを DB に保存します。

#### Request

```json
{
  "profileImage": "https://picsum.photos/200"
}
```

#### Response

```json
{
  "userId": 1,
  "userName": "Google上の名前",
  "userNickname": "ノブ作家",
  "userEmail": "user@example.com",
  "profileImage": "https://picsum.photos/200",
  "createdAt": "2026-07-27T12:00:00Z",
  "updatedAt": "2026-07-27T13:05:00Z"
}
```

### 退会 — `DELETE /api/users/me`

```http
DELETE /api/users/me
Authorization: Bearer <Novforge Access Token>
```

ログイン中ユーザーを `users` テーブルから削除します。

#### Response

```text
204 No Content
```

レスポンス Body はありません。

## Postman の総合テスト手順

```text
1. Postman OAuth 2.0 で Google id_token を発行
2. POST /api/users で会員登録
3. POST /api/auth/google で Novforge accessToken を発行
4. GET /api/users/me で DB のユーザー情報を確認
5. PATCH /api/users/me でニックネームを変更
6. PATCH /api/users/profile-images でプロフィール画像 URL を変更
7. 管理者アカウントの場合は GET /api/users で全ユーザー一覧を確認
8. 必要に応じて DELETE /api/users/me で退会を確認
```

## エラーレスポンス

| ステータスコード | 状況 |
|---|---|
| `400 Bad Request` | 必須項目の不足、空白のニックネーム、文字数制限超過、Google の必須 claim 不足 |
| `401 Unauthorized` | Access Token の不足・不正・有効期限切れ |
| `403 Forbidden` | 管理者ではないユーザーが全ユーザー一覧を照会 |
| `404 Not Found` | Access Token のユーザー ID に該当する DB ユーザーが存在しない |
| `409 Conflict` | 登録済みの Google アカウント、または重複したニックネーム |
| `415 Unsupported Media Type` | JSON Body のリクエストで `Content-Type: application/json` を使用していない |

## Notes

- `GET /api/users/me` は URL から `user_id` を受け取りません。Access Token の `sub` でログイン中ユーザーを識別します。
- `/api/users/1` のような特定ユーザーを照会するエンドポイントは提供していません。
- Google 上の名前とメールアドレスは、ニックネーム変更 API では変更されません。
- 会員登録時はニックネームの前後の空白を除去してから重複を検証します。
- プロフィール画像を削除するための `null` または空文字は現在許可していません。
- DB スキーマには `user_nickname VARCHAR(50) NOT NULL UNIQUE` が必要です。
