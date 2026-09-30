[한국어](./README.kr.md) | [日本語](./README.ja.md)

# My Build API

ログイン中のユーザーが PC パーツを選択し、自分の構成を作成・照会・変更・削除する API です。

空の構成を先に作成してから、パーツを一つずつ追加できます。マザーボード、CPU、GPU、電源ユニット、CPU クーラー、PC ケースは単一パーツとして関連付け、メモリとストレージは複数の製品と数量を保存するために中間テーブルを使用します。

ユーザーと合計金額はクライアントの入力を信用せず、サーバー側で決定します。

- ユーザーは Novforge Access Token の `sub` から識別します。
- 合計金額は DB に保存されたパーツ価格と数量から自動計算します。
- 他のユーザーの構成は照会・変更・削除できません。

## API エンドポイント

| 機能 | ドメイン | エンドポイント | メソッド | 認証 | 説明 |
|---|---|---|---|---|---|
| 公開構成一覧 | my-builds | `/api/my-builds` | `GET` | 不要 | 公開された構成一覧を照会 |
| 公開構成詳細 | my-builds | `/api/my-builds/{buildId}` | `GET` | 不要 | 公開された特定構成を照会 |
| 自分の構成一覧 | my-builds | `/api/my-builds/me` | `GET` | Access Token | ログイン中ユーザーの全構成を照会 |
| 自分の構成詳細 | my-builds | `/api/my-builds/me/{buildId}` | `GET` | Access Token | ログイン中ユーザーの特定構成を照会 |
| 自分の構成作成 | my-builds | `/api/my-builds/me` | `POST` | Access Token | 空の構成、またはパーツを選択した構成を作成 |
| 自分の構成変更 | my-builds | `/api/my-builds/me/{buildId}` | `PATCH` | Access Token | 構成名、公開設定、パーツ、数量を変更 |
| 単一パーツ削除 | my-builds | `/api/my-builds/me/{buildId}/parts/{partType}` | `DELETE` | Access Token | 選択した単一パーツの関連を削除し価格を再計算 |
| 自分の構成削除 | my-builds | `/api/my-builds/me/{buildId}` | `DELETE` | Access Token | ログイン中ユーザーの構成を削除 |

自分の構成照会と作成・変更・削除エンドポイントでは次の認証ヘッダーが必要です。公開構成の照会には認証ヘッダーは不要です。

```http
Authorization: Bearer <Novforge Access Token>
```

Google ID Token ではなく、`POST /api/auth/google` のレスポンスに含まれる Novforge `accessToken` を使用します。

## 認証フロー

```text
Google ID Token を発行
→ POST /api/users で会員登録
→ POST /api/auth/google でログイン
→ Novforge Access Token を発行
→ Authorization: Bearer <accessToken>
→ /api/my-builds/me を呼び出す
```

登録済みのユーザーは会員登録を省略してログインから開始できます。

## Directory

```text
mybuild/
├─ MyBuild.java
├─ MyBuildController.java
├─ MyBuildService.java
├─ MyBuildRepository.java
├─ MyBuildMemory.java
├─ MyBuildMemoryId.java
├─ MyBuildStorage.java
├─ MyBuildStorageId.java
├─ MyBuildNotFoundException.java
├─ InvalidBuildPartException.java
├─ MyBuildExceptionHandler.java
└─ dto/
   ├─ MyBuildCreateRequest.java
   ├─ MyBuildUpdateRequest.java
   └─ MyBuildResponse.java
```

| ファイル | 役割 |
|---|---|
| `MyBuild.java` | ユーザー、単一パーツ、構成情報の関連を管理する JPA Entity |
| `MyBuildMemory.java` | 構成とメモリの関連および数量を管理 |
| `MyBuildMemoryId.java` | `build_id + memory_id` 複合キー |
| `MyBuildStorage.java` | 構成とストレージの関連および数量を管理 |
| `MyBuildStorageId.java` | `build_id + storage_id` 複合キー |
| `MyBuildController.java` | `/api/my-builds` エンドポイント |
| `MyBuildService.java` | 所有権検証、パーツ照会、関連保存、合計金額計算 |
| `MyBuildRepository.java` | ユーザー別構成照会と関連パーツの一括ロード |
| `MyBuildCreateRequest.java` | 構成作成リクエスト DTO |
| `MyBuildUpdateRequest.java` | 構成部分変更リクエスト DTO |
| `MyBuildResponse.java` | 構成と選択パーツのレスポンス DTO |
| `MyBuildExceptionHandler.java` | 400・404 エラーレスポンス処理 |

## DB 関係

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

### 単一パーツ関係

| My Build FK | 参照テーブル | JPA 関係 |
|---|---|---|
| `user_id` | `users.user_id` | `ManyToOne`, 必須 |
| `motherboard_id` | `motherboard.motherboard_id` | `ManyToOne`, 任意 |
| `cpu_id` | `cpu.cpu_id` | `ManyToOne`, 任意 |
| `gpu_id` | `gpu.gpu_id` | `ManyToOne`, 任意 |
| `power_id` | `power.power_id` | `ManyToOne`, 任意 |
| `cpu_cooler_id` | `cpu_cooler.cpu_cooler_id` | `ManyToOne`, 任意 |
| `case_id` | `case.case_id` | `ManyToOne`, 任意 |

### メモリ関係

一つの構成に複数種類のメモリを数量付きで保存できます。

```text
my_build_ram
- build_id
- memory_id
- quantity
```

`build_id + memory_id` が複合キーです。同じメモリ ID を配列に重複して指定することはできません。個数は `quantity` で指定します。

### ストレージ関係

一つの構成に複数種類の SSD または HDD を数量付きで保存できます。

```text
my_build_storage
- build_id
- storage_id
- quantity
```

`build_id + storage_id` が複合キーです。同じストレージ ID を配列に重複して指定することはできません。

### 構成の削除

構成を削除すると、その構成に属する `my_build_ram` と `my_build_storage` の関連行も `cascade` と `orphanRemoval` により削除されます。

CPU、GPU、メモリなどの実際のパーツデータは削除されません。

## My Build データモデル

| Java フィールド | DB カラム | 制約・ポリシー |
|---|---|---|
| `id` | `build_id` | PK、自動採番 |
| `user` | `user_id` | FK、`NOT NULL`、Access Token から決定 |
| `motherboard` | `motherboard_id` | FK、nullable |
| `gpu` | `gpu_id` | FK、nullable |
| `cpu` | `cpu_id` | FK、nullable |
| `powerSupply` | `power_id` | FK、nullable |
| `cpuCooler` | `cpu_cooler_id` | FK、nullable |
| `pcCase` | `case_id` | FK、nullable |
| `name` | `build_name` | `VARCHAR(100)`, `NOT NULL` |
| `totalPrice` | `total_price` | `BIGINT`, `NOT NULL`、サーバーで自動計算 |
| `publicBuild` | `is_public` | `BOOLEAN`, `NOT NULL` |
| `createdAt` | `created_at` | `TIMESTAMP`、初回保存時に自動生成 |
| `updatedAt` | `updated_at` | `TIMESTAMP`、変更時に自動更新 |

## 合計金額の計算

クライアントは `totalPrice` をリクエストに含めません。

```text
マザーボード価格
+ CPU 価格
+ GPU 価格
+ 電源ユニット価格
+ CPU クーラー価格
+ PC ケース価格
+ Σ(メモリ価格 × 数量)
+ Σ(ストレージ価格 × 数量)
= totalPrice
```

未選択の単一パーツは 0 円として計算します。

## Endpoints

### 空の構成を作成 — `POST /api/my-builds/me`

#### Request

```http
POST /api/my-builds/me
Authorization: Bearer <Novforge Access Token>
Content-Type: application/json
```

```json
{
  "buildName": "後で完成させる構成",
  "publicBuild": false
}
```

#### Response — `201 Created`

```json
{
  "buildId": 1,
  "userId": 3,
  "buildName": "後で完成させる構成",
  "totalPrice": 0,
  "publicBuild": false,
  "motherboard": null,
  "gpu": null,
  "cpu": null,
  "powerSupply": null,
  "cpuCooler": null,
  "pcCase": null,
  "memories": [],
  "storages": [],
  "createdAt": "2026-07-28T21:53:21.854878",
  "updatedAt": null
}
```

`userId` と `totalPrice` はリクエストから受け取りません。

### パーツを含む構成を作成 — `POST /api/my-builds/me`

```json
{
  "buildName": "7800X3D ゲーミング構成",
  "motherboardId": 3,
  "gpuId": 3,
  "cpuId": 4,
  "powerId": 3,
  "cpuCoolerId": 4,
  "caseId": 3,
  "memories": [
    {
      "id": 3,
      "quantity": 2
    }
  ],
  "storages": [
    {
      "id": 3,
      "quantity": 1
    },
    {
      "id": 4,
      "quantity": 1
    }
  ],
  "publicBuild": false
}
```

各パーツ ID は既存のパーツテーブルに存在する必要があります。

### 公開構成一覧 — `GET /api/my-builds`

```http
GET /api/my-builds
```

認証なしで呼び出すことができ、`publicBuild=true` の構成のみを ID の降順で返します。

### 公開構成詳細 — `GET /api/my-builds/{buildId}`

```http
GET /api/my-builds/1
```

公開された構成のみを返します。存在しない構成と非公開構成はどちらも `404 Not Found` とし、非公開構成の存在を公開しません。

### 自分の構成一覧 — `GET /api/my-builds/me`

```http
GET /api/my-builds/me
Authorization: Bearer <Novforge Access Token>
```

公開パスは Postman で `Authorization: No Auth`、`Body: none` として呼び出します。期限切れ、または不正な Bearer Token を送信すると、公開パスでも `401 Unauthorized` になる場合があります。

`/api/my-builds/me` 配下のすべての API は Access Token の `sub` と構成の所有者を比較します。公開構成であっても、所有者以外は変更・パーツ削除・構成削除を実行できません。

Access Token のユーザーが所有する公開・非公開の構成をすべて ID の降順で返します。

### 自分の構成詳細 — `GET /api/my-builds/me/{buildId}`

```http
GET /api/my-builds/me/1
Authorization: Bearer <Novforge Access Token>
```

指定した構成が存在しない場合、または他のユーザーの構成である場合は `404 Not Found` を返します。

### パーツの追加・交換 — `PATCH /api/my-builds/me/{buildId}`

```http
PATCH /api/my-builds/me/1
Authorization: Bearer <Novforge Access Token>
Content-Type: application/json
```

CPU とマザーボードを追加:

```json
{
  "cpuId": 4,
  "motherboardId": 3
}
```

GPU、電源、クーラー、ケースを追加:

```json
{
  "gpuId": 3,
  "powerId": 3,
  "cpuCoolerId": 4,
  "caseId": 3
}
```

構成名と公開設定を変更:

```json
{
  "buildName": "変更後のゲーミング構成",
  "publicBuild": true
}
```

単一パーツ ID に新しい ID を指定すると、既存パーツが新しいパーツへ交換されます。

### メモリを変更

```json
{
  "memories": [
    {
      "id": 3,
      "quantity": 2
    },
    {
      "id": 4,
      "quantity": 2
    }
  ]
}
```

`memories` を送信すると、既存一覧への追加ではなく、送信した配列全体で置き換えます。

すべて削除する場合:

```json
{
  "memories": []
}
```

### ストレージを変更

異なるストレージを一つずつ選択:

```json
{
  "storages": [
    {
      "id": 3,
      "quantity": 1
    },
    {
      "id": 4,
      "quantity": 1
    }
  ]
}
```

同じストレージを二つ選択:

```json
{
  "storages": [
    {
      "id": 3,
      "quantity": 2
    }
  ]
}
```

`storages` を送信すると、既存一覧への追加ではなく、送信した配列全体で置き換えます。

すべて削除する場合:

```json
{
  "storages": []
}
```

### 単一パーツ削除 — `DELETE /api/my-builds/me/{buildId}/parts/{partType}`

マザーボード、GPU、CPU、電源ユニット、CPU クーラー、PC ケースのうち一つを構成から削除します。

```http
DELETE /api/my-builds/me/1/parts/cpu
Authorization: Bearer {accessToken}
```

使用可能な `partType`:

```text
motherboard
gpu
cpu
power-supply
cpu-cooler
case
```

リクエスト Body は使用しません。選択した Foreign Key を `NULL` に変更し、合計金額を再計算した後、更新された構成を `200 OK` で返します。すでに空のパーツを削除しても正常に応答し、他のパーツには影響しません。

### 自分の構成を削除 — `DELETE /api/my-builds/me/{buildId}`

```http
DELETE /api/my-builds/me/1
Authorization: Bearer <Novforge Access Token>
```

成功時は Body なしで `204 No Content` を返します。

## リクエストフィールド

### 作成リクエスト

| フィールド | 必須 | 説明 |
|---|---|---|
| `buildName` | 必須 | 空白ではない最大 100 文字の構成名 |
| `motherboardId` | 任意 | マザーボード ID |
| `gpuId` | 任意 | GPU ID |
| `cpuId` | 任意 | CPU ID |
| `powerId` | 任意 | 電源ユニット ID |
| `cpuCoolerId` | 任意 | CPU クーラー ID |
| `caseId` | 任意 | PC ケース ID |
| `memories` | 任意 | メモリ ID と数量の一覧 |
| `storages` | 任意 | ストレージ ID と数量の一覧 |
| `publicBuild` | 必須 | 公開設定 |

### 変更リクエスト

すべて任意項目です。送信した項目だけを変更します。

メモリとストレージの配列を送信した場合、その配列全体で置き換えます。

## 所有権ポリシー

URL や Body から `userId` を受け取りません。

```text
Novforge Access Token の sub
→ users.user_id を照会
→ user_id と build_id の両方が一致する構成を照会
```

他のユーザーの構成 ID を指定した場合も、データの存在を公開しないため `404 Not Found` を返します。

## エラーレスポンス

| ステータスコード | 状況 |
|---|---|
| `400 Bad Request` | 存在しないパーツ ID、重複したメモリ・ストレージ ID、不正な数量、必須項目不足 |
| `401 Unauthorized` | Novforge Access Token の不足・不正・期限切れ |
| `404 Not Found` | 構成が存在しない、または他のユーザーの構成 |
| `405 Method Not Allowed` | `PATCH /api/my-builds/me` のように `{buildId}` なしで変更を要求 |
| `415 Unsupported Media Type` | JSON リクエストで `Content-Type: application/json` を使用していない |

## Postman 総合テスト手順

```text
1. Google ID Token を発行
2. POST /api/users で会員登録
3. POST /api/auth/google で Novforge Access Token を発行
4. Equipment 一覧 API でパーツ ID を確認
5. POST /api/my-builds/me で空の構成を作成
6. GET /api/my-builds/me で自分の構成一覧を確認
7. PATCH /api/my-builds/me/{buildId} で単一パーツを追加
8. PATCH でメモリ・ストレージと数量を追加
9. totalPrice の自動計算を確認
10. GET /api/my-builds/me/{buildId} で自分の構成詳細を確認
11. 他のユーザーの Token で同じ buildId を照会し 404 を確認
12. DELETE /api/my-builds/me/{buildId} で削除
13. 削除した buildId の再照会で 404 を確認
```

## テスト

My Build のサービス統合テストでは次の項目を検証します。

- ユーザーとパーツデータの保存
- 構成の作成・変更・照会
- メモリ・ストレージ数量の保存
- 数量を含む合計金額の自動計算
- 構成名と公開設定の変更
- メモリ数量の変更
- ストレージ一覧の削除
- 他のユーザーからのアクセス拒否
- JPA 複合キーと関連のロード

## 現在の制限事項

- メモリとストレージは一部追加方式ではなく、送信した配列全体で置き換えます。
- パーツ間の互換性検証はまだありません。
- パーツ価格を変更しても、既存構成の `totalPrice` は構成を変更した時に再計算されます。

## Notes

- 実際のパーツ ID は各 Equipment 一覧 API で確認します。
- 同じメモリまたはストレージ ID を配列に重複して指定せず、`quantity` を使用してください。
- 構成内のメモリ・ストレージ関連を削除しても、実際のパーツテーブルの製品は削除されません。
