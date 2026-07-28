[한국어](./README.kr.md) | [日本語](./README.ja.md)

# Novforge API

Novforge のユーザー認証、ユーザー情報、PC パーツデータ、ユーザー別 PC 構成を管理する Spring Boot バックエンド API です。

Google OpenID Connect による会員登録・ログインに対応し、ログイン成功時に Novforge Access Token を発行します。ユーザーは登録済みの PC パーツを選択して自分の構成を作成し、段階的に変更できます。

## 主な機能

### Auth

- Google ID Token の検証
- 登録済み Google ユーザーのログイン
- Novforge Access Token の発行
- Google issuer、audience、有効期限、メール認証状態の検証

### Users

- Google アカウントによる会員登録
- ログイン中ユーザー情報の照会
- ニックネームとプロフィール画像 URL の変更
- 退会
- 管理者による全ユーザー一覧照会

### Equipment

- マザーボード
- CPU
- GPU
- メモリ
- ストレージ
- 電源ユニット
- CPU クーラー
- PC ケース
- パーツ別の登録・一覧照会・詳細照会・部分変更・削除

### My Build

- ユーザー別 PC 構成の作成
- 空の構成を作成した後、パーツを段階的に追加
- 単一パーツの追加・交換
- 複数のメモリ・ストレージと数量を保存
- パーツ価格と数量による合計金額の自動計算
- 自分の構成一覧・詳細照会・変更・削除
- 他のユーザーの構成へのアクセス防止

## 技術スタック

| 区分 | 技術 |
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

## プロジェクト構成

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

## 詳細ドキュメント

| モジュール | 한국어 | 日本語 |
|---|---|---|
| Auth | [Auth 한국어 문서](./src/main/java/com/novforge/api/auth/README.kr.md) | [Auth 日本語ドキュメント](./src/main/java/com/novforge/api/auth/README.ja.md) |
| Users | [Users 한국어 문서](./src/main/java/com/novforge/api/users/README.kr.md) | [Users 日本語ドキュメント](./src/main/java/com/novforge/api/users/README.ja.md) |
| Equipment | [Equipment 한국어 문서](./src/main/java/com/novforge/api/equipment/README.kr.md) | [Equipment 日本語ドキュメント](./src/main/java/com/novforge/api/equipment/README.ja.md) |
| My Build | [My Build 한국어 문서](./src/main/java/com/novforge/api/mybuild/README.kr.md) | [My Build 日本語ドキュメント](./src/main/java/com/novforge/api/mybuild/README.ja.md) |

## API エンドポイント

### Auth

| 機能 | メソッド | エンドポイント | 認証 |
|---|---|---|---|
| Google ログイン | `POST` | `/api/auth/google` | 不要 |

### Users

| 機能 | メソッド | エンドポイント | 認証 |
|---|---|---|---|
| Google 会員登録 | `POST` | `/api/users` | 不要 |
| 全ユーザー一覧 | `GET` | `/api/users` | 管理者 Access Token |
| 自分の情報 | `GET` | `/api/users/me` | Access Token |
| ニックネーム変更 | `PATCH` | `/api/users/me` | Access Token |
| プロフィール画像変更 | `PATCH` | `/api/users/profile-images` | Access Token |
| 退会 | `DELETE` | `/api/users/me` | Access Token |

### Equipment

各パーツは一覧照会、詳細照会、登録、部分変更、削除に対応します。

```text
GET    /api/{domain}
GET    /api/{domain}/{id}
POST   /api/{domain}
PATCH  /api/{domain}/{id}
DELETE /api/{domain}/{id}
```

| パーツ | Domain |
|---|---|
| マザーボード | `/api/mainboards` |
| CPU | `/api/cpus` |
| GPU | `/api/gpus` |
| メモリ | `/api/memorys` |
| ストレージ | `/api/storages` |
| 電源ユニット | `/api/power-supplies` |
| CPU クーラー | `/api/cpu-coolers` |
| PC ケース | `/api/cases` |

### My Build

| 機能 | メソッド | エンドポイント | 認証 |
|---|---|---|---|
| 自分の構成一覧 | `GET` | `/api/my-builds` | Access Token |
| 自分の構成詳細 | `GET` | `/api/my-builds/{buildId}` | Access Token |
| 自分の構成作成 | `POST` | `/api/my-builds` | Access Token |
| 自分の構成変更 | `PATCH` | `/api/my-builds/{buildId}` | Access Token |
| 自分の構成削除 | `DELETE` | `/api/my-builds/{buildId}` | Access Token |

## 認証フロー

```text
Google ログイン
→ Google ID Token を発行
→ POST /api/users で会員登録
→ ユーザー情報を users テーブルへ保存
→ POST /api/auth/google でログイン
→ Novforge Access Token を発行
→ Bearer Token で保護された API を呼び出す
```

登録済みのユーザーは会員登録を省略し、ログインから開始できます。

### 会員登録リクエスト

```http
POST /api/users
Content-Type: application/json
```

```json
{
  "idToken": "google-id-token",
  "userNickname": "ノブ作家"
}
```

### ログインリクエスト

```http
POST /api/auth/google
Content-Type: application/json
```

```json
{
  "idToken": "google-id-token"
}
```

ログインレスポンスの `accessToken` を保護された API で使用します。

```http
Authorization: Bearer <Novforge Access Token>
```

```text
Google ID Token
→ 会員登録とログインに使用

Novforge Access Token
→ Users、Equipment、My Build の保護 API に使用
```

## My Build の関係

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

メモリとストレージは、複数製品と数量を保存するために中間テーブルを使用します。

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

## 環境変数

プロジェクトルートに `.env` ファイルを作成します。

```env
DB_URL=jdbc:postgresql://localhost:5432/novforge
DB_USERNAME=postgres
DB_PASSWORD=your-database-password

GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com
JWT_SECRET=replace-with-a-random-secret-of-at-least-32-characters
JWT_ACCESS_TOKEN_EXPIRATION=3600
ADMIN_EMAILS=admin@example.com
```

| 環境変数 | 説明 |
|---|---|
| `DB_URL` | PostgreSQL JDBC URL |
| `DB_USERNAME` | PostgreSQL ユーザー名 |
| `DB_PASSWORD` | PostgreSQL パスワード |
| `GOOGLE_CLIENT_ID` | Google OAuth 2.0 Web Client ID |
| `JWT_SECRET` | Novforge Access Token の署名キー、最低 32 バイト |
| `JWT_ACCESS_TOKEN_EXPIRATION` | Access Token の有効期間（秒） |
| `ADMIN_EMAILS` | 管理者 Google メールアドレス、複数指定はカンマ区切り |

環境変数を変更した後はサーバーを再起動してください。

## 実行方法

### 必要要件

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

デフォルトのサーバー URL:

```text
http://localhost:8080
```

## ビルド・テスト

### Windows

```powershell
.\gradlew.bat clean build
```

### macOS / Linux

```bash
./gradlew clean build
```

テストでは H2 インメモリ DB を使用し、次の項目を検証します。

- Spring Application Context の起動
- Auth・Users サービス
- My Build の JPA 関係
- ユーザー別構成の所有権
- メモリ・ストレージ数量
- 構成合計金額の自動計算

## HTTP ステータスコード

| ステータスコード | 意味 |
|---|---|
| `200 OK` | 照会・変更成功 |
| `201 Created` | 会員登録、パーツ、構成の作成成功 |
| `204 No Content` | 削除成功 |
| `400 Bad Request` | リクエスト検証失敗、または存在しないパーツを選択 |
| `401 Unauthorized` | 認証情報の不足・不正・期限切れ |
| `403 Forbidden` | 管理者権限が必要な API に一般ユーザーがアクセス |
| `404 Not Found` | ユーザー、パーツ、構成が存在しない |
| `409 Conflict` | 登録済み Google アカウント、またはニックネーム重複 |
| `405 Method Not Allowed` | 未対応の HTTP メソッド、または不正なパス |
| `415 Unsupported Media Type` | JSON リクエストの Content-Type が不正 |

## DB 設定

現在の開発環境では、次の設定で Entity の変更を DB スキーマへ反映します。

```properties
spring.jpa.hibernate.ddl-auto=update
```

本番環境では Flyway または Liquibase などのスキーママイグレーションツールの導入を推奨します。

## 現在の制限事項

- Equipment の登録・変更・削除に対する共通の管理者権限検証は、まだ完全には接続されていません。
- My Build の単一パーツは追加・交換できますが、明示的に削除する API はまだありません。
- My Build のメモリ・ストレージ PATCH は送信した配列全体で置き換えます。
- 公開構成の一覧・詳細 API はまだありません。
- パーツ間のソケット、規格、電力などの互換性検証はまだありません。
- パーツデータの自動収集と販売終了ステータス管理はまだありません。

## Notes

- `/api/memorys` は現在実装されている実際のエンドポイントです。
- マザーボードは `/api/mainboards` と互換パス `/api/motherboards` をサポートします。
- `case` は Java と SQL の予約語であるため、Java パッケージと Entity 名には `pccase`、`PcCase` を使用します。
- 画像ファイルを直接アップロードせず、画像 URL の文字列を保存します。
- `totalPrice` はクライアント入力ではなく、サーバー上のパーツ価格から計算します。

