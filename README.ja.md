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
- 公開構成の一覧・詳細照会
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

## Spring Boot を選択した理由

Novforge では、ユーザー認証、パーツ CRUD、複数テーブルの関連、ユーザー別 PC 構成を処理する必要があります。Spring Boot は、これらの機能をレイヤー別に分離し、セキュリティ・DB・検証を一貫した方法で接続するのに適しているため採用しました。

### 選択理由

- Spring MVC で REST API のリクエストとレスポンスを明確に構成できます。
- Spring Data JPA により、反復的な SQL と CRUD コードを削減できます。
- Spring Security と OAuth2 Resource Server で JWT 認証を API の前段で共通処理できます。
- Bean Validation で Controller に入る前後の入力値を検証できます。
- 依存性注入により Controller、Service、Repository の責任を分離し、テストしやすくなります。
- Spring Boot の自動設定で Web サーバー、JSON 変換、JPA、DB 接続を迅速に構成できます。

### メリット

| メリット | 説明 |
|---|---|
| 開発速度 | 自動設定と Starter 依存関係により初期設定が簡単です。 |
| レイヤー分離 | Controller、Service、Repository、Entity の役割を明確に分けられます。 |
| セキュリティ統合 | JWT 検証と保護パスを `SecurityFilterChain` で管理できます。 |
| DB 生産性 | JPA Repository が基本 CRUD とトランザクションを支援します。 |
| 検証・例外処理 | Bean Validation と `RestControllerAdvice` で一貫したエラー形式を構成できます。 |
| テスト支援 | Spring Context、Security、JPA の統合テスト環境が整っています。 |
| 拡張性 | 公開構成、互換性検証、自動パーツ収集を既存レイヤーへ追加しやすいです。 |

### デメリットとトレードオフ

| デメリット | 現在の対応 |
|---|---|
| 学習範囲が広い | パッケージとレイヤー構造を機能別に統一し、README に処理フローを記録します。 |
| 自動設定により内部動作が見えにくい | Security、JPA、環境変数を明示的な設定と文書で管理します。 |
| JPA 関連の設定を誤ると N+1 が発生する | My Build では `EntityGraph` で必要な関連パーツをまとめてロードします。 |
| Entity 変更が DB に直接影響する可能性がある | 現在は `ddl-auto=update` ですが、本番前にマイグレーションツールが必要です。 |
| 軽量フレームワークより起動時間とメモリ使用量が大きい | 現在の規模では開発生産性と保守性を優先します。 |
| トランザクション範囲を誤ると Lazy Loading エラーが発生する | Service レイヤーでトランザクションと Entity→DTO 変換を処理します。 |

## 主な技術選択

### Spring Web MVC

HTTP リクエストを Controller に渡し、Java オブジェクトを JSON レスポンスへ変換します。

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

同期型の処理方式であり、現在の CRUD 中心 API に適しています。大規模なリアルタイムストリーミングが必要になった場合は WebFlux やイベントシステムを検討できます。

### Spring Data JPA と Hibernate

Java Entity と PostgreSQL テーブルをマッピングし、Repository からデータを照会・保存します。

メリット:

- 基本 CRUD の実装量を削減
- オブジェクト関連でユーザー、構成、パーツを接続
- トランザクションと変更検知を支援
- DB を変更しても Service コードへの影響を抑制

注意点:

- 複雑な照会では生成される SQL を確認する必要があります。
- Fetch 戦略と N+1 問題を管理する必要があります。
- 本番スキーマ変更を JPA の自動生成だけに依存しない方が安全です。

### PostgreSQL

ユーザー、パーツ、構成など、関連と整合性が重要なデータを保存するためにリレーショナル DB を使用します。

- Foreign Key により存在するユーザーとパーツのみ構成へ接続
- トランザクションによる一貫したデータ変更
- 複合キーによるメモリ・ストレージ関連の重複防止
- 検索、並び替え、統計クエリへ拡張可能

### Spring Security と JWT

Google がユーザーの本人確認を行い、Novforge サーバーが独自の Access Token を発行します。

```text
Google ID Token
→ Google 署名・issuer・audience・有効期限を検証
→ users テーブルで登録済みユーザーを確認
→ Novforge JWT Access Token を発行
→ 以後の API で Bearer Token を検証
```

JWT はサーバーセッションを保存しないため API サーバーの拡張に向いていますが、発行後すぐに強制失効させることが難しいという特徴があります。ログアウトやトークン失効が必要になった場合は、Refresh Token、拒否リスト、トークンバージョンなどのポリシーが必要です。

### H2 テスト DB

テスト時に実際の PostgreSQL データを変更しないよう、H2 インメモリ DB を使用します。

高速で独立したテストが可能ですが、PostgreSQL と SQL 文法・型の動作が完全に同一ではありません。本番前には PostgreSQL を使用する統合テスト環境も追加することが望ましいです。

### Gradle Wrapper

開発者が同じ Gradle バージョンを個別にインストールしなくても、プロジェクトに含まれる Wrapper でビルドできます。

```text
Windows: .\gradlew.bat
macOS/Linux: ./gradlew
```

## アプリケーション動作構造

### 一般的な API リクエスト

```text
1. クライアントが HTTP リクエストを送信
2. Spring Security が保護されたパスの Bearer Token を検証
3. Controller が URL、HTTP Method、JSON Body を DTO に変換
4. Bean Validation が必須値、文字数、数値範囲を検証
5. Service がビジネスルールと所有権を検証
6. Repository が JPA を通して PostgreSQL を照会・変更
7. Entity を Response DTO に変換
8. Spring MVC が JSON と HTTP ステータスコードで応答
```

### My Build 作成リクエスト

```text
POST /api/my-builds/me
→ JWT sub から user_id を確認
→ リクエストされたパーツ ID を照会
→ 単一パーツの Foreign Key を接続
→ メモリ・ストレージと数量を中間テーブルへ保存
→ サーバーで合計金額を計算
→ my_build と関連テーブルを保存
→ 作成された構成を JSON で返す
```

## レイヤー別の責任

| レイヤー | 責任 |
|---|---|
| Controller | HTTP パス、Method、認証ユーザー、リクエスト・レスポンス処理 |
| Request DTO | クライアント入力構造と Validation |
| Service | ビジネスルール、トランザクション、所有権、価格計算 |
| Repository | JPA による DB 照会・保存 |
| Entity | テーブル・関連マッピングと状態変更 |
| Response DTO | 外部へ公開するレスポンス構造 |
| Exception Handler | 例外を HTTP ステータスと Problem Detail へ変換 |
| SecurityConfig | 公開・保護 API と JWT 検証設定 |

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
| 公開構成一覧 | `GET` | `/api/my-builds` | 不要 |
| 公開構成詳細 | `GET` | `/api/my-builds/{buildId}` | 不要 |
| 自分の構成一覧 | `GET` | `/api/my-builds/me` | Access Token |
| 自分の構成詳細 | `GET` | `/api/my-builds/me/{buildId}` | Access Token |
| 自分の構成作成 | `POST` | `/api/my-builds/me` | Access Token |
| 自分の構成変更 | `PATCH` | `/api/my-builds/me/{buildId}` | Access Token |
| 単一パーツ削除 | `DELETE` | `/api/my-builds/me/{buildId}/parts/{partType}` | Access Token |
| 自分の構成削除 | `DELETE` | `/api/my-builds/me/{buildId}` | Access Token |

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

### 実行前の準備

```text
1. PostgreSQL に Novforge 用データベースを作成
2. Google Cloud Console で OAuth 2.0 Web Client を作成
3. プロジェクトルートに .env を作成
4. Java 21 が使用されていることを確認
5. Gradle Wrapper でサーバーを起動
```

Java バージョンの確認:

```bash
java -version
```

Java 21 が表示される必要があります。

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

サーバー起動時に Spring Boot が Entity を確認し、PostgreSQL に必要なテーブルを作成または変更します。

### 起動確認

専用の Health Check API はまだないため、サーバーコンソールで次のログを確認します。

```text
Started ApiApplication
Tomcat started on port 8080
```

その後、Postman で公開 API の `POST /api/users` または `POST /api/auth/google` を呼び出して動作を確認できます。

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

- Equipment の照会はログインユーザーに許可し、登録・変更・削除は `ADMIN_EMAILS` に登録された管理者のみ実行できます。
- My Build のメモリ・ストレージ PATCH は送信した配列全体で置き換えます。
- パーツ間のソケット、規格、電力などの互換性検証はまだありません。
- パーツデータの自動収集と販売終了ステータス管理はまだありません。

## Notes

- `/api/memorys` は現在実装されている実際のエンドポイントです。
- マザーボードは `/api/mainboards` と互換パス `/api/motherboards` をサポートします。
- `case` は Java と SQL の予約語であるため、Java パッケージと Entity 名には `pccase`、`PcCase` を使用します。
- 画像ファイルを直接アップロードせず、画像 URL の文字列を保存します。
- `totalPrice` はクライアント入力ではなく、サーバー上のパーツ価格から計算します。
