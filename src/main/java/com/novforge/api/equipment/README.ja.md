[한국어](./README.kr.md) | [日本語](./README.ja.md)

# Equipment API

Novforge で PC パーツの製品情報と仕様を保存し、一覧照会・詳細照会・登録・変更・削除を行う API です。

現在、マザーボード、CPU、GPU、メモリ、ストレージ、電源ユニット、CPU クーラー、PC ケースに対応しています。各パーツは JPA Entity、Repository、Service、Controller、リクエスト・レスポンス DTO、例外処理クラスで構成されています。

## API エンドポイント

| パーツ | ドメイン | 一覧・登録 | 詳細・変更・削除 |
|---|---|---|---|
| マザーボード | mainboards | `/api/mainboards` | `/api/mainboards/{id}` |
| CPU | cpus | `/api/cpus` | `/api/cpus/{id}` |
| GPU | gpus | `/api/gpus` | `/api/gpus/{id}` |
| メモリ | memorys | `/api/memorys` | `/api/memorys/{id}` |
| ストレージ | storages | `/api/storages` | `/api/storages/{id}` |
| 電源ユニット | power-supplies | `/api/power-supplies` | `/api/power-supplies/{id}` |
| CPU クーラー | cpu-coolers | `/api/cpu-coolers` | `/api/cpu-coolers/{id}` |
| PC ケース | cases | `/api/cases` | `/api/cases/{id}` |

すべてのパーツ API は次の CRUD ルールを共通で使用します。

| 機能 | メソッド | パス | 正常レスポンス |
|---|---|---|---|
| 一覧照会 | `GET` | `/api/{domain}` | `200 OK` |
| 詳細照会 | `GET` | `/api/{domain}/{id}` | `200 OK` |
| 登録 | `POST` | `/api/{domain}` | `201 Created` |
| 部分変更 | `PATCH` | `/api/{domain}/{id}` | `200 OK` |
| 削除 | `DELETE` | `/api/{domain}/{id}` | `204 No Content` |

## 認証と権限

パーツブランチで使用していた Postman テスト用の一時的な `SecurityConfig` は、ログインブランチとの競合を防ぐため削除しました。ログインブランチをマージした後、そのブランチの `SecurityConfig` に Equipment API のアクセスポリシーを追加する必要があります。

ログインブランチをマージした後の推奨ポリシーは次のとおりです。

| 機能 | 推奨権限 |
|---|---|
| 一覧・詳細照会 | 認証不要 |
| 登録・変更・削除 | `ADMIN` 権限が必要 |

ログインブランチの `SecurityFilterChain` に Equipment API のパスを追加し、一つのセキュリティ設定として管理してください。

## 共通リクエスト・レスポンス規則

- 登録・変更リクエストでは `Content-Type: application/json` を使用します。
- `POST` ではすべての必須項目を送信します。
- `PATCH` では変更する項目だけを送信します。
- 価格や個数などの数値に負数は使用できません。
- `description` と `imageUrl` は任意項目です。
- `id` は DB が自動生成するため、リクエスト Body には含めません。
- `createdAt` は初回保存時に自動生成されます。
- `updatedAt` は変更時に自動更新されます。
- 一覧は ID の降順で返されます。

## Directory

```text
equipment/
├─ motherboard/
├─ cpu/
├─ gpu/
├─ memory/
├─ storage/
├─ powersupply/
├─ cpucooler/
└─ pccase/
```

各パーツフォルダーは次の基本構成を使用します。

```text
{Equipment}.java
{Equipment}Controller.java
{Equipment}Service.java
{Equipment}Repository.java
{Equipment}NotFoundException.java
{Equipment}ExceptionHandler.java
dto/
├─ {Equipment}CreateRequest.java
├─ {Equipment}UpdateRequest.java
└─ {Equipment}Response.java
```

`case` は Java の予約語であるため、ケースのパッケージは `pccase`、クラス名は `PcCase` を使用します。

---

## マザーボード

- テーブル: `motherboard`
- Entity: `Motherboard`
- API: `/api/mainboards`
- 互換パス: `/api/motherboards`

| JSON フィールド | DB カラム | 型・制約 |
|---|---|---|
| `id` | `motherboard_id` | `BIGINT`, PK |
| `manufacturer` | `motherboard_manufacturer` | `VARCHAR(100)`, 必須 |
| `name` | `motherboard_name` | `VARCHAR(255)`, 必須 |
| `price` | `motherboard_price` | `BIGINT`, 必須 |
| `socket` | `motherboard_socket` | `VARCHAR(30)`, 必須 |
| `chipset` | `motherboard_chipset` | `VARCHAR(30)`, 必須 |
| `formFactor` | `motherboard_form_factor` | `VARCHAR(30)`, 必須 |
| `memorySupport` | `motherboard_memory_support` | `VARCHAR(20)`, 必須 |
| `memorySlots` | `motherboard_memory_slots` | `INT`, 必須 |
| `maxMemory` | `motherboard_max_memory` | `INT`, 必須 |
| `maxMemoryClock` | `motherboard_max_memory_clock` | `INT`, 必須 |
| `pcieVersion` | `motherboard_pcie_version` | `VARCHAR(20)`, 必須 |
| `pcieX16Slots` | `motherboard_pcie_x16_slots` | `INT`, 必須 |
| `m2Slots` | `motherboard_m2_slots` | `INT`, 必須 |
| `sataPorts` | `motherboard_sata_ports` | `INT`, 必須 |
| `wifi` | `motherboard_wifi` | `BOOLEAN`, 必須 |
| `bluetooth` | `motherboard_bluetooth` | `BOOLEAN`, 必須 |
| `description` | `motherboard_description` | `TEXT`, 任意 |
| `imageUrl` | `motherboard_image_url` | `TEXT`, 任意 |
| `createdAt` | `created_at` | `TIMESTAMP`, 自動生成 |
| `updatedAt` | `updated_at` | `TIMESTAMP`, 変更時に生成 |

```json
{
  "manufacturer": "ASUS",
  "name": "ROG STRIX B650E-F GAMING WIFI",
  "price": 389000,
  "socket": "AM5",
  "chipset": "B650E",
  "formFactor": "ATX",
  "memorySupport": "DDR5",
  "memorySlots": 4,
  "maxMemory": 192,
  "maxMemoryClock": 8000,
  "pcieVersion": "PCIe 5.0",
  "pcieX16Slots": 2,
  "m2Slots": 4,
  "sataPorts": 4,
  "wifi": true,
  "bluetooth": true,
  "description": "AM5 ゲーミングマザーボード",
  "imageUrl": "https://example.com/mainboard.jpg"
}
```

## CPU

- テーブル: `cpu`
- Entity: `Cpu`
- API: `/api/cpus`

| JSON フィールド | DB カラム | 型・制約 |
|---|---|---|
| `id` | `cpu_id` | `BIGINT`, PK |
| `manufacturer` | `cpu_manufacture` | `VARCHAR(100)`, 必須 |
| `name` | `cpu_name` | `VARCHAR(100)`, 必須 |
| `price` | `cpu_price` | `BIGINT`, 必須 |
| `socket` | `cpu_socket` | `VARCHAR(30)`, 必須 |
| `cores` | `cpu_cores` | `INT`, 必須 |
| `threads` | `cpu_threads` | `INT`, 必須 |
| `baseClock` | `cpu_base_clock` | `DECIMAL(3,2)`, 必須 |
| `boostClock` | `cpu_boost_clock` | `DECIMAL(3,2)`, 必須 |
| `cache` | `cpu_cache` | `VARCHAR(30)`, 必須 |
| `tdp` | `cpu_tdp` | `INT`, 必須 |
| `integratedGraphics` | `cpu_integrated_graphics` | `BOOLEAN`, 必須 |
| `memorySupport` | `cpu_memory_support` | `VARCHAR(100)`, 必須 |
| `pcieVersion` | `cpu_pcie_version` | `VARCHAR(20)`, 必須 |
| `description` | `cpu_description` | `TEXT`, 任意 |
| `imageUrl` | `cpu_image_url` | `TEXT`, 任意 |
| `createdAt` | `created_at` | `TIMESTAMP`, 自動生成 |
| `updatedAt` | `updated_at` | `TIMESTAMP`, 変更時に生成 |

```json
{
  "manufacturer": "AMD",
  "name": "Ryzen 7 7800X3D",
  "price": 529000,
  "socket": "AM5",
  "cores": 8,
  "threads": 16,
  "baseClock": 4.20,
  "boostClock": 5.00,
  "cache": "96MB",
  "tdp": 120,
  "integratedGraphics": true,
  "memorySupport": "DDR5-5200",
  "pcieVersion": "PCIe 5.0",
  "description": "3D V-Cache ゲーミング CPU",
  "imageUrl": "https://example.com/cpu.jpg"
}
```

## GPU

- テーブル: `gpu`
- Entity: `Gpu`
- API: `/api/gpus`

| JSON フィールド | DB カラム | 型・制約 |
|---|---|---|
| `id` | `gpu_id` | `BIGINT`, PK |
| `manufacturer` | `gpu_manufacturer` | `VARCHAR(100)`, 必須 |
| `name` | `gpu_name` | `VARCHAR(255)`, 必須 |
| `price` | `gpu_price` | `BIGINT`, 必須 |
| `memorySize` | `gpu_memory_size` | `INT`, 必須 |
| `memoryType` | `gpu_memory_type` | `VARCHAR(20)`, 必須 |
| `length` | `gpu_length` | `INT`, 必須 |
| `powerConsumption` | `gpu_power_consumption` | `INT`, 必須 |
| `recommendedPsu` | `gpu_recommended_psu` | `INT`, 必須 |
| `description` | `gpu_description` | `TEXT`, 任意 |
| `imageUrl` | `gpu_image_url` | `TEXT`, 任意 |
| `createdAt` | `created_at` | `TIMESTAMP`, 自動生成 |
| `updatedAt` | `updated_at` | `TIMESTAMP`, 変更時に生成 |

```json
{
  "manufacturer": "NVIDIA",
  "name": "GeForce RTX 4070 SUPER",
  "price": 899000,
  "memorySize": 12,
  "memoryType": "GDDR6X",
  "length": 267,
  "powerConsumption": 220,
  "recommendedPsu": 650,
  "description": "12GB GDDR6X グラフィックスカード",
  "imageUrl": "https://example.com/gpu.jpg"
}
```

## メモリ

- テーブル: `memory`
- Entity: `Memory`
- API: `/api/memorys`

| JSON フィールド | DB カラム | 型・制約 |
|---|---|---|
| `id` | `memory_id` | `BIGINT`, PK |
| `manufacturer` | `memory_manufacturer` | `VARCHAR(100)`, 必須 |
| `name` | `memory_name` | `VARCHAR(255)`, 必須 |
| `price` | `memory_price` | `BIGINT`, 必須 |
| `type` | `memory_type` | `VARCHAR(20)`, 必須 |
| `capacity` | `memory_capacity` | `INT`, 必須 |
| `clock` | `memory_clock` | `INT`, 必須 |
| `moduleCount` | `memory_module_count` | `INT`, 必須 |
| `moduleCapacity` | `memory_module_capacity` | `INT`, 必須 |
| `formFactor` | `memory_form_factor` | `VARCHAR(20)`, 必須 |
| `casLatency` | `memory_cas_latency` | `INT`, 必須 |
| `voltage` | `memory_voltage` | `DECIMAL(3,2)`, 必須 |
| `ecc` | `memory_ecc` | `BOOLEAN`, 必須 |
| `description` | `memory_description` | `TEXT`, 任意 |
| `imageUrl` | `memory_image_url` | `TEXT`, 任意 |
| `createdAt` | `created_at` | `TIMESTAMP`, 自動生成 |
| `updatedAt` | `update_at` | `TIMESTAMP`, 変更時に生成 |

```json
{
  "manufacturer": "Samsung",
  "name": "Samsung DDR5-5600 32GB Kit",
  "price": 129000,
  "type": "DDR5",
  "capacity": 32,
  "clock": 5600,
  "moduleCount": 2,
  "moduleCapacity": 16,
  "formFactor": "DIMM",
  "casLatency": 46,
  "voltage": 1.10,
  "ecc": false,
  "description": "16GB モジュール 2 枚構成",
  "imageUrl": "https://example.com/memory.jpg"
}
```

## ストレージ

- テーブル: `storage`
- Entity: `Storage`
- API: `/api/storages`

| JSON フィールド | DB カラム | 型・制約 |
|---|---|---|
| `id` | `storage_id` | `BIGINT`, PK |
| `manufacturer` | `storage_manufacturer` | `VARCHAR(100)`, 必須 |
| `name` | `storage_name` | `VARCHAR(255)`, 必須 |
| `price` | `storage_price` | `BIGINT`, 必須 |
| `type` | `storage_type` | `VARCHAR(20)`, 必須 |
| `interfaceType` | `storage_interface` | `VARCHAR(30)`, 必須 |
| `capacity` | `storage_capacity` | `INT`, 必須 |
| `readSpeed` | `storage_read_speed` | `INT`, 必須 |
| `formFactor` | `storage_form_factor` | `VARCHAR(20)`, 必須 |
| `cacheSize` | `storage_cache_size` | `INT`, 必須 |
| `description` | `storage_description` | `TEXT`, 任意 |
| `imageUrl` | `storage_image_url` | `TEXT`, 任意 |
| `createdAt` | `created_at` | `TIMESTAMP`, 自動生成 |
| `updatedAt` | `updated_at` | `TIMESTAMP`, 変更時に生成 |

`interface` は Java の予約語であるため、JSON と Java フィールドでは `interfaceType`、DB では `storage_interface` を使用します。

```json
{
  "manufacturer": "Samsung",
  "name": "Samsung 990 PRO 2TB",
  "price": 239000,
  "type": "SSD",
  "interfaceType": "PCIe 4.0 NVMe",
  "capacity": 2000,
  "readSpeed": 7450,
  "formFactor": "M.2 2280",
  "cacheSize": 2048,
  "description": "高性能 NVMe SSD",
  "imageUrl": "https://example.com/storage.jpg"
}
```

## 電源ユニット

- テーブル: `power`
- Entity: `PowerSupply`
- API: `/api/power-supplies`

| JSON フィールド | DB カラム | 型・制約 |
|---|---|---|
| `id` | `power_id` | `BIGINT`, PK |
| `manufacturer` | `power_manufacturer` | `VARCHAR(100)`, 必須 |
| `name` | `power_name` | `VARCHAR(255)`, 必須 |
| `price` | `power_price` | `BIGINT`, 必須 |
| `wattage` | `power_wattage` | `INT`, 必須 |
| `efficiency` | `power_efficiency` | `VARCHAR(30)`, 必須 |
| `modularType` | `power_modular_type` | `VARCHAR(30)`, 必須 |
| `formFactor` | `power_form_factor` | `VARCHAR(20)`, 必須 |
| `description` | `power_description` | `TEXT`, 任意 |
| `imageUrl` | `power_image_url` | `TEXT`, 任意 |
| `createdAt` | `created_at` | `TIMESTAMP`, 自動生成 |
| `updatedAt` | `updated_at` | `TIMESTAMP`, 変更時に生成 |

```json
{
  "manufacturer": "Seasonic",
  "name": "FOCUS GX-850",
  "price": 189000,
  "wattage": 850,
  "efficiency": "80 PLUS Gold",
  "modularType": "Full Modular",
  "formFactor": "ATX",
  "description": "850W フルモジュラー電源",
  "imageUrl": "https://example.com/power-supply.jpg"
}
```

## CPU クーラー

- テーブル: `cpu_cooler`
- Entity: `CpuCooler`
- API: `/api/cpu-coolers`

| JSON フィールド | DB カラム | 型・制約 |
|---|---|---|
| `id` | `cpu_cooler_id` | `BIGINT`, PK |
| `manufacturer` | `cpu_cooler_manufacturer` | `VARCHAR(100)`, 必須 |
| `name` | `cpu_cooler_name` | `VARCHAR(255)`, 必須 |
| `price` | `cpu_cooler_price` | `BIGINT`, 必須 |
| `type` | `cpu_cooler_type` | `VARCHAR(20)`, 必須 |
| `socket` | `cpu_cooler_socket` | `VARCHAR(100)`, 必須 |
| `fanSize` | `cpu_cooler_fan_size` | `INT`, 必須 |
| `radiatorSize` | `cpu_cooler_radiator_size` | `INT`, 必須、0 可 |
| `height` | `cpu_cooler_height` | `INT`, 必須 |
| `airflow` | `cpu_cooler_airflow` | `DECIMAL(5,2)`, 必須 |
| `noiseLevel` | `cpu_cooler_noise_level` | `DECIMAL(4,1)`, 必須 |
| `rgb` | `cpu_cooler_rgb` | `BOOLEAN`, 必須 |
| `description` | `cpu_cooler_description` | `TEXT`, 任意 |
| `imageUrl` | `cpu_cooler_image_url` | `TEXT`, 任意 |
| `createdAt` | `created_at` | `TIMESTAMP`, 自動生成 |
| `updatedAt` | `updated_at` | `TIMESTAMP`, 変更時に生成 |

空冷クーラーなどラジエーターがない製品は、`radiatorSize` に `0` を使用します。

```json
{
  "manufacturer": "DeepCool",
  "name": "AK620 DIGITAL",
  "price": 99000,
  "type": "Air",
  "socket": "AM5, AM4, LGA1700, LGA1200",
  "fanSize": 120,
  "radiatorSize": 0,
  "height": 162,
  "airflow": 68.99,
  "noiseLevel": 28.0,
  "rgb": true,
  "description": "デュアルタワー空冷クーラー",
  "imageUrl": "https://example.com/cpu-cooler.jpg"
}
```

## PC ケース

- テーブル: `case`
- Entity: `PcCase`
- API: `/api/cases`

| JSON フィールド | DB カラム | 型・制約 |
|---|---|---|
| `id` | `case_id` | `BIGINT`, PK |
| `manufacturer` | `case_manufacturer` | `VARCHAR(100)`, 必須 |
| `name` | `case_name` | `VARCHAR(255)`, 必須 |
| `price` | `case_price` | `BIGINT`, 必須 |
| `type` | `case_type` | `VARCHAR(30)`, 必須 |
| `supportedFormFactor` | `case_supported_form_factor` | `VARCHAR(100)`, 必須 |
| `maxGpuLength` | `case_max_gpu_length` | `INT`, 必須 |
| `maxCpuCoolerHeight` | `case_max_cpu_cooler_height` | `INT`, 必須 |
| `supportedRadiatorSize` | `case_supported_radiator_size` | `VARCHAR(100)`, 必須 |
| `fanCount` | `case_fan_count` | `INT`, 必須 |
| `description` | `case_description` | `TEXT`, 任意 |
| `imageUrl` | `case_image_url` | `TEXT`, 任意 |
| `createdAt` | `created_at` | `TIMESTAMP`, 自動生成 |
| `updatedAt` | `updated_at` | `TIMESTAMP`, 変更時に生成 |

`case` は Java と SQL の予約語であるため、Java パッケージは `pccase` を使用し、JPA のテーブル名は引用符付きで処理します。

```json
{
  "manufacturer": "NZXT",
  "name": "H7 Flow RGB",
  "price": 189000,
  "type": "Middle Tower",
  "supportedFormFactor": "ATX, Micro-ATX, Mini-ITX",
  "maxGpuLength": 400,
  "maxCpuCoolerHeight": 185,
  "supportedRadiatorSize": "120, 240, 280, 360mm",
  "fanCount": 4,
  "description": "フロントメッシュのミドルタワーケース",
  "imageUrl": "https://example.com/case.jpg"
}
```

## PATCH の例

`PATCH` では変更する値だけを送信します。

```http
PATCH /api/gpus/1
Content-Type: application/json
```

```json
{
  "price": 849000,
  "powerConsumption": 215
}
```

## DELETE の例

```http
DELETE /api/gpus/1
```

成功時は Body なしで `204 No Content` を返します。

## エラーレスポンス

| ステータスコード | 状況 |
|---|---|
| `400 Bad Request` | 必須項目の不足、空文字、文字数制限超過、負数、許可範囲外の数値 |
| `401 Unauthorized` | ログインブランチ統合後、認証情報がない、または不正な場合 |
| `403 Forbidden` | ログインブランチ統合後、一般ユーザーが管理者 API を呼び出した場合 |
| `404 Not Found` | 指定 ID のパーツが存在しない場合 |
| `415 Unsupported Media Type` | JSON リクエストで `Content-Type: application/json` を使用していない場合 |

404 レスポンスは次のような Problem Detail 形式です。

```json
{
  "type": "about:blank",
  "title": "GPU not found",
  "status": 404,
  "detail": "GPU를 찾을 수 없습니다. id=999"
}
```

## Postman 総合テスト手順

各パーツについて次の順序で確認します。

```text
1. POST /api/{domain} で製品を登録
2. GET /api/{domain} で一覧を確認
3. GET /api/{domain}/{id} で詳細を確認
4. PATCH /api/{domain}/{id} で一部フィールドを変更
5. updatedAt が更新されたことを確認
6. DELETE /api/{domain}/{id} で削除
7. 削除した ID を再度照会し、404 を確認
```

## DB・運用上の注意

- 現在は `spring.jpa.hibernate.ddl-auto=update` で Entity を DB スキーマへ反映します。
- 本番環境では Flyway または Liquibase などのマイグレーションツールの導入を推奨します。
- 現在の `DELETE` は行を実際に削除するハードデリートです。
- 製品の販売終了管理を追加する場合、削除せず `ACTIVE`、`DISCONTINUED`、`HIDDEN` などのステータスを使用することを推奨します。
- 自動製品収集機能は現在の製品テーブルと CRUD をそのまま利用し、別途収集待機テーブルと管理者承認機能を追加する形で拡張できます。
- `manufacturer + name` または外部製品 ID を基準に重複防止ポリシーを追加できます。

## Notes

- `/api/memorys` は現在実装されている実際のパスです。英語の標準的な複数形 `/api/memories` に変更する場合は、フロントエンド接続前に確定してください。
- マザーボードは仕様上の `/api/mainboards` と互換パス `/api/motherboards` の両方をサポートしています。
- CPU の DB カラム `cpu_manufacture` は、提供された DB 仕様の名前をそのまま使用しています。
- メモリの更新日時 DB カラムは、提供された DB 仕様どおり `update_at` です。
- 画像ファイル自体はアップロードせず、`imageUrl` 文字列のみを保存します。
