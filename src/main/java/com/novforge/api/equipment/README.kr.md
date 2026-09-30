[한국어](./README.kr.md) | [日本語](./README.ja.md)

# Equipment API

Novforge에서 PC 부품의 제품 정보와 사양을 저장하고 조회·등록·수정·삭제하는 API입니다.

현재 메인보드, CPU, GPU, 메모리, 보조기억장치, 파워 서플라이, CPU 쿨러, 케이스를 지원합니다. 각 부품은 JPA Entity, Repository, Service, Controller, 요청·응답 DTO와 예외 처리 클래스로 구성됩니다.

## API 엔드포인트

| 부품 | 도메인 | 목록·등록 | 상세·수정·삭제 |
|---|---|---|---|
| 메인보드 | mainboards | `/api/mainboards` | `/api/mainboards/{id}` |
| CPU | cpus | `/api/cpus` | `/api/cpus/{id}` |
| GPU | gpus | `/api/gpus` | `/api/gpus/{id}` |
| 메모리 | memorys | `/api/memorys` | `/api/memorys/{id}` |
| 보조기억장치 | storages | `/api/storages` | `/api/storages/{id}` |
| 파워 서플라이 | power-supplies | `/api/power-supplies` | `/api/power-supplies/{id}` |
| CPU 쿨러 | cpu-coolers | `/api/cpu-coolers` | `/api/cpu-coolers/{id}` |
| 케이스 | cases | `/api/cases` | `/api/cases/{id}` |

모든 부품 API는 다음 CRUD 규칙을 공통으로 사용합니다.

| 기능 | 메서드 | 경로 | 정상 응답 |
|---|---|---|---|
| 목록 조회 | `GET` | `/api/{domain}` | `200 OK` |
| 상세 조회 | `GET` | `/api/{domain}/{id}` | `200 OK` |
| 등록 | `POST` | `/api/{domain}` | `201 Created` |
| 부분 수정 | `PATCH` | `/api/{domain}/{id}` | `200 OK` |
| 삭제 | `DELETE` | `/api/{domain}/{id}` | `204 No Content` |

## 인증 및 권한

장비 접근 정책은 애플리케이션의 공통 `SecurityConfig`에서 관리합니다.

| 기능 | 권한 |
|---|---|
| 목록·상세 조회 | Novforge Access Token |
| 등록·수정·삭제 | `ADMIN` 권한 필요 |

공통 `SecurityFilterChain`이 Access Token의 `email`과 `ADMIN_EMAILS`를 비교하여 관리자 권한을 적용합니다. 미로그인 요청은 `401 Unauthorized`, 일반 사용자의 등록·수정·삭제 요청은 `403 Forbidden`을 반환합니다.

## 공통 요청·응답 규칙

- 등록과 수정 요청은 `Content-Type: application/json`을 사용합니다.
- `POST` 요청은 필수 필드를 모두 전달해야 합니다.
- `PATCH` 요청은 변경할 필드만 전달합니다.
- 가격과 개수 등 숫자 필드에는 음수를 사용할 수 없습니다.
- `description`, `imageUrl`은 선택 필드입니다.
- `id`는 DB에서 자동 생성되므로 요청 Body에 포함하지 않습니다.
- `createdAt`은 최초 저장 시 자동 생성됩니다.
- `updatedAt`은 수정 시 자동 갱신됩니다.
- 목록은 ID 내림차순으로 반환됩니다.

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

각 부품 폴더의 기본 구조는 다음과 같습니다.

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

`case`는 Java 예약어이므로 케이스 패키지는 `pccase`, 클래스명은 `PcCase`를 사용합니다.

---

## Endpoints

### 메인보드

#### DB

- 테이블: `motherboard`
- Entity: `Motherboard`
- API: `/api/mainboards`
- 호환 경로: `/api/motherboards`

| JSON 필드 | DB 컬럼 | 타입·제약 |
|---|---|---|
| `id` | `motherboard_id` | `BIGINT`, PK |
| `manufacturer` | `motherboard_manufacturer` | `VARCHAR(100)`, 필수 |
| `name` | `motherboard_name` | `VARCHAR(255)`, 필수 |
| `price` | `motherboard_price` | `BIGINT`, 필수 |
| `socket` | `motherboard_socket` | `VARCHAR(30)`, 필수 |
| `chipset` | `motherboard_chipset` | `VARCHAR(30)`, 필수 |
| `formFactor` | `motherboard_form_factor` | `VARCHAR(30)`, 필수 |
| `memorySupport` | `motherboard_memory_support` | `VARCHAR(20)`, 필수 |
| `memorySlots` | `motherboard_memory_slots` | `INT`, 필수 |
| `maxMemory` | `motherboard_max_memory` | `INT`, 필수 |
| `maxMemoryClock` | `motherboard_max_memory_clock` | `INT`, 필수 |
| `pcieVersion` | `motherboard_pcie_version` | `VARCHAR(20)`, 필수 |
| `pcieX16Slots` | `motherboard_pcie_x16_slots` | `INT`, 필수 |
| `m2Slots` | `motherboard_m2_slots` | `INT`, 필수 |
| `sataPorts` | `motherboard_sata_ports` | `INT`, 필수 |
| `wifi` | `motherboard_wifi` | `BOOLEAN`, 필수 |
| `bluetooth` | `motherboard_bluetooth` | `BOOLEAN`, 필수 |
| `description` | `motherboard_description` | `TEXT`, 선택 |
| `imageUrl` | `motherboard_image_url` | `TEXT`, 선택 |
| `createdAt` | `created_at` | `TIMESTAMP`, 자동 생성 |
| `updatedAt` | `updated_at` | `TIMESTAMP`, 수정 시 생성 |

#### 등록 예시

```http
POST /api/mainboards
Content-Type: application/json
```

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
  "description": "AM5 게이밍 메인보드",
  "imageUrl": "https://example.com/mainboard.jpg"
}
```

### CPU

#### DB

- 테이블: `cpu`
- Entity: `Cpu`
- API: `/api/cpus`

| JSON 필드 | DB 컬럼 | 타입·제약 |
|---|---|---|
| `id` | `cpu_id` | `BIGINT`, PK |
| `manufacturer` | `cpu_manufacture` | `VARCHAR(100)`, 필수 |
| `name` | `cpu_name` | `VARCHAR(100)`, 필수 |
| `price` | `cpu_price` | `BIGINT`, 필수 |
| `socket` | `cpu_socket` | `VARCHAR(30)`, 필수 |
| `cores` | `cpu_cores` | `INT`, 필수 |
| `threads` | `cpu_threads` | `INT`, 필수 |
| `baseClock` | `cpu_base_clock` | `DECIMAL(3,2)`, 필수 |
| `boostClock` | `cpu_boost_clock` | `DECIMAL(3,2)`, 필수 |
| `cache` | `cpu_cache` | `VARCHAR(30)`, 필수 |
| `tdp` | `cpu_tdp` | `INT`, 필수 |
| `integratedGraphics` | `cpu_integrated_graphics` | `BOOLEAN`, 필수 |
| `memorySupport` | `cpu_memory_support` | `VARCHAR(100)`, 필수 |
| `pcieVersion` | `cpu_pcie_version` | `VARCHAR(20)`, 필수 |
| `description` | `cpu_description` | `TEXT`, 선택 |
| `imageUrl` | `cpu_image_url` | `TEXT`, 선택 |
| `createdAt` | `created_at` | `TIMESTAMP`, 자동 생성 |
| `updatedAt` | `updated_at` | `TIMESTAMP`, 수정 시 생성 |

#### 등록 예시

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
  "description": "3D V-Cache 게이밍 CPU",
  "imageUrl": "https://example.com/cpu.jpg"
}
```

### GPU

#### DB

- 테이블: `gpu`
- Entity: `Gpu`
- API: `/api/gpus`

| JSON 필드 | DB 컬럼 | 타입·제약 |
|---|---|---|
| `id` | `gpu_id` | `BIGINT`, PK |
| `manufacturer` | `gpu_manufacturer` | `VARCHAR(100)`, 필수 |
| `name` | `gpu_name` | `VARCHAR(255)`, 필수 |
| `price` | `gpu_price` | `BIGINT`, 필수 |
| `memorySize` | `gpu_memory_size` | `INT`, 필수 |
| `memoryType` | `gpu_memory_type` | `VARCHAR(20)`, 필수 |
| `length` | `gpu_length` | `INT`, 필수 |
| `powerConsumption` | `gpu_power_consumption` | `INT`, 필수 |
| `recommendedPsu` | `gpu_recommended_psu` | `INT`, 필수 |
| `description` | `gpu_description` | `TEXT`, 선택 |
| `imageUrl` | `gpu_image_url` | `TEXT`, 선택 |
| `createdAt` | `created_at` | `TIMESTAMP`, 자동 생성 |
| `updatedAt` | `updated_at` | `TIMESTAMP`, 수정 시 생성 |

#### 등록 예시

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
  "description": "12GB GDDR6X 그래픽카드",
  "imageUrl": "https://example.com/gpu.jpg"
}
```

### 메모리

#### DB

- 테이블: `memory`
- Entity: `Memory`
- API: `/api/memorys`

| JSON 필드 | DB 컬럼 | 타입·제약 |
|---|---|---|
| `id` | `memory_id` | `BIGINT`, PK |
| `manufacturer` | `memory_manufacturer` | `VARCHAR(100)`, 필수 |
| `name` | `memory_name` | `VARCHAR(255)`, 필수 |
| `price` | `memory_price` | `BIGINT`, 필수 |
| `type` | `memory_type` | `VARCHAR(20)`, 필수 |
| `capacity` | `memory_capacity` | `INT`, 필수 |
| `clock` | `memory_clock` | `INT`, 필수 |
| `moduleCount` | `memory_module_count` | `INT`, 필수 |
| `moduleCapacity` | `memory_module_capacity` | `INT`, 필수 |
| `formFactor` | `memory_form_factor` | `VARCHAR(20)`, 필수 |
| `casLatency` | `memory_cas_latency` | `INT`, 필수 |
| `voltage` | `memory_voltage` | `DECIMAL(3,2)`, 필수 |
| `ecc` | `memory_ecc` | `BOOLEAN`, 필수 |
| `description` | `memory_description` | `TEXT`, 선택 |
| `imageUrl` | `memory_image_url` | `TEXT`, 선택 |
| `createdAt` | `created_at` | `TIMESTAMP`, 자동 생성 |
| `updatedAt` | `update_at` | `TIMESTAMP`, 수정 시 생성 |

#### 등록 예시

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
  "description": "16GB 모듈 2개 구성",
  "imageUrl": "https://example.com/memory.jpg"
}
```

### 보조기억장치

#### DB

- 테이블: `storage`
- Entity: `Storage`
- API: `/api/storages`

| JSON 필드 | DB 컬럼 | 타입·제약 |
|---|---|---|
| `id` | `storage_id` | `BIGINT`, PK |
| `manufacturer` | `storage_manufacturer` | `VARCHAR(100)`, 필수 |
| `name` | `storage_name` | `VARCHAR(255)`, 필수 |
| `price` | `storage_price` | `BIGINT`, 필수 |
| `type` | `storage_type` | `VARCHAR(20)`, 필수 |
| `interfaceType` | `storage_interface` | `VARCHAR(30)`, 필수 |
| `capacity` | `storage_capacity` | `INT`, 필수 |
| `readSpeed` | `storage_read_speed` | `INT`, 필수 |
| `formFactor` | `storage_form_factor` | `VARCHAR(20)`, 필수 |
| `cacheSize` | `storage_cache_size` | `INT`, 필수 |
| `description` | `storage_description` | `TEXT`, 선택 |
| `imageUrl` | `storage_image_url` | `TEXT`, 선택 |
| `createdAt` | `created_at` | `TIMESTAMP`, 자동 생성 |
| `updatedAt` | `updated_at` | `TIMESTAMP`, 수정 시 생성 |

`interface`는 Java 예약어이므로 JSON과 Java 필드에서는 `interfaceType`을 사용하고 DB에는 `storage_interface`로 저장합니다.

#### 등록 예시

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
  "description": "고성능 NVMe SSD",
  "imageUrl": "https://example.com/storage.jpg"
}
```

### 파워 서플라이

#### DB

- 테이블: `power`
- Entity: `PowerSupply`
- API: `/api/power-supplies`

| JSON 필드 | DB 컬럼 | 타입·제약 |
|---|---|---|
| `id` | `power_id` | `BIGINT`, PK |
| `manufacturer` | `power_manufacturer` | `VARCHAR(100)`, 필수 |
| `name` | `power_name` | `VARCHAR(255)`, 필수 |
| `price` | `power_price` | `BIGINT`, 필수 |
| `wattage` | `power_wattage` | `INT`, 필수 |
| `efficiency` | `power_efficiency` | `VARCHAR(30)`, 필수 |
| `modularType` | `power_modular_type` | `VARCHAR(30)`, 필수 |
| `formFactor` | `power_form_factor` | `VARCHAR(20)`, 필수 |
| `description` | `power_description` | `TEXT`, 선택 |
| `imageUrl` | `power_image_url` | `TEXT`, 선택 |
| `createdAt` | `created_at` | `TIMESTAMP`, 자동 생성 |
| `updatedAt` | `updated_at` | `TIMESTAMP`, 수정 시 생성 |

#### 등록 예시

```json
{
  "manufacturer": "Seasonic",
  "name": "FOCUS GX-850",
  "price": 189000,
  "wattage": 850,
  "efficiency": "80 PLUS Gold",
  "modularType": "Full Modular",
  "formFactor": "ATX",
  "description": "850W 풀 모듈러 파워",
  "imageUrl": "https://example.com/power-supply.jpg"
}
```

### CPU 쿨러

#### DB

- 테이블: `cpu_cooler`
- Entity: `CpuCooler`
- API: `/api/cpu-coolers`

| JSON 필드 | DB 컬럼 | 타입·제약 |
|---|---|---|
| `id` | `cpu_cooler_id` | `BIGINT`, PK |
| `manufacturer` | `cpu_cooler_manufacturer` | `VARCHAR(100)`, 필수 |
| `name` | `cpu_cooler_name` | `VARCHAR(255)`, 필수 |
| `price` | `cpu_cooler_price` | `BIGINT`, 필수 |
| `type` | `cpu_cooler_type` | `VARCHAR(20)`, 필수 |
| `socket` | `cpu_cooler_socket` | `VARCHAR(100)`, 필수 |
| `fanSize` | `cpu_cooler_fan_size` | `INT`, 필수 |
| `radiatorSize` | `cpu_cooler_radiator_size` | `INT`, 필수, 0 허용 |
| `height` | `cpu_cooler_height` | `INT`, 필수 |
| `airflow` | `cpu_cooler_airflow` | `DECIMAL(5,2)`, 필수 |
| `noiseLevel` | `cpu_cooler_noise_level` | `DECIMAL(4,1)`, 필수 |
| `rgb` | `cpu_cooler_rgb` | `BOOLEAN`, 필수 |
| `description` | `cpu_cooler_description` | `TEXT`, 선택 |
| `imageUrl` | `cpu_cooler_image_url` | `TEXT`, 선택 |
| `createdAt` | `created_at` | `TIMESTAMP`, 자동 생성 |
| `updatedAt` | `updated_at` | `TIMESTAMP`, 수정 시 생성 |

공랭 쿨러처럼 라디에이터가 없는 제품은 `radiatorSize`에 `0`을 사용합니다.

#### 등록 예시

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
  "description": "듀얼 타워 공랭 쿨러",
  "imageUrl": "https://example.com/cpu-cooler.jpg"
}
```

### 케이스

#### DB

- 테이블: `case`
- Entity: `PcCase`
- API: `/api/cases`

| JSON 필드 | DB 컬럼 | 타입·제약 |
|---|---|---|
| `id` | `case_id` | `BIGINT`, PK |
| `manufacturer` | `case_manufacturer` | `VARCHAR(100)`, 필수 |
| `name` | `case_name` | `VARCHAR(255)`, 필수 |
| `price` | `case_price` | `BIGINT`, 필수 |
| `type` | `case_type` | `VARCHAR(30)`, 필수 |
| `supportedFormFactor` | `case_supported_form_factor` | `VARCHAR(100)`, 필수 |
| `maxGpuLength` | `case_max_gpu_length` | `INT`, 필수 |
| `maxCpuCoolerHeight` | `case_max_cpu_cooler_height` | `INT`, 필수 |
| `supportedRadiatorSize` | `case_supported_radiator_size` | `VARCHAR(100)`, 필수 |
| `fanCount` | `case_fan_count` | `INT`, 필수 |
| `description` | `case_description` | `TEXT`, 선택 |
| `imageUrl` | `case_image_url` | `TEXT`, 선택 |
| `createdAt` | `created_at` | `TIMESTAMP`, 자동 생성 |
| `updatedAt` | `updated_at` | `TIMESTAMP`, 수정 시 생성 |

`case`는 Java와 SQL의 예약어이므로 Java 패키지는 `pccase`를 사용하며 JPA 테이블 이름은 인용 처리합니다.

#### 등록 예시

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
  "description": "전면 메시 미들타워 케이스",
  "imageUrl": "https://example.com/case.jpg"
}
```

### PATCH 예시

`PATCH`는 변경할 값만 전달합니다.

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

### DELETE 예시

```http
DELETE /api/gpus/1
```

성공하면 Body 없이 `204 No Content`를 반환합니다.

## 오류 응답

| 상태 코드 | 상황 |
|---|---|
| `400 Bad Request` | 필수값 누락, 빈 문자열, 길이 제한 초과, 음수 또는 허용 범위 밖의 숫자 |
| `401 Unauthorized` | 로그인 브랜치 병합 후 인증 정보가 없거나 잘못된 경우 |
| `403 Forbidden` | 로그인 브랜치 병합 후 일반 사용자가 관리자 API를 호출한 경우 |
| `404 Not Found` | 요청한 ID의 부품이 존재하지 않는 경우 |
| `415 Unsupported Media Type` | JSON 요청에 `Content-Type: application/json`을 사용하지 않은 경우 |

404 응답은 다음과 같은 Problem Detail 형식을 사용합니다.

```json
{
  "type": "about:blank",
  "title": "GPU not found",
  "status": 404,
  "detail": "GPU를 찾을 수 없습니다. id=999"
}
```

## 전체 Postman 테스트 순서

각 부품에 대해 다음 순서로 확인합니다.

```text
1. POST /api/{domain}으로 제품 등록
2. GET /api/{domain}으로 목록 확인
3. GET /api/{domain}/{id}로 상세 확인
4. PATCH /api/{domain}/{id}로 일부 필드 수정
5. 수정일(updatedAt) 갱신 확인
6. DELETE /api/{domain}/{id}로 삭제
7. 삭제한 ID를 다시 조회하여 404 확인
```

## DB 및 운영 참고사항

- 현재 `spring.jpa.hibernate.ddl-auto=update`로 Entity를 DB 스키마에 반영합니다.
- 운영 환경에서는 Flyway 또는 Liquibase 같은 마이그레이션 도구 도입을 권장합니다.
- 현재 `DELETE`는 실제 행을 삭제하는 하드 삭제입니다.
- 제품 단종 관리가 추가되면 행을 삭제하지 않고 `ACTIVE`, `DISCONTINUED`, `HIDDEN` 같은 상태값을 사용하는 것을 권장합니다.
- 자동 제품 수집 기능은 기존 부품 테이블과 CRUD를 그대로 사용하고, 별도의 수집 대기 테이블과 관리자 승인 기능을 추가하는 방식으로 확장할 수 있습니다.
- `manufacturer + name` 또는 외부 제품 ID를 기준으로 중복 방지 정책을 추가할 수 있습니다.

## Notes

- `/api/memorys`는 현재 구현된 실제 경로입니다. 영어 표준 복수형인 `/api/memories`로 변경하려면 프론트엔드 연결 전에 확정해야 합니다.
- 메인보드는 문서 기준 경로 `/api/mainboards`와 호환 경로 `/api/motherboards`를 모두 지원합니다.
- CPU DB 컬럼 `cpu_manufacture`는 제공된 DB 명세의 이름을 그대로 사용합니다.
- 메모리 수정일 DB 컬럼은 제공된 DB 명세대로 `update_at`입니다.
- 관리 API는 이미지 파일 업로드 대신 `imageUrl` 문자열을 저장합니다. 현재 장비 이미지는 API 정적 리소스로 제공합니다.
