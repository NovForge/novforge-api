[한국어](./README.kr.md) | [日本語](./README.ja.md)

# My Build API

로그인한 사용자가 PC 부품을 선택하여 자신의 견적을 생성하고 조회·수정·삭제하는 API입니다.

사용자는 빈 견적을 먼저 생성한 뒤 부품을 하나씩 추가할 수 있습니다. 메인보드, CPU, GPU, 파워 서플라이, CPU 쿨러, 케이스는 단일 부품으로 연결되며 메모리와 보조기억장치는 여러 제품과 수량을 저장할 수 있도록 별도의 관계 테이블을 사용합니다.

견적의 사용자와 총가격은 클라이언트 요청을 신뢰하지 않고 서버에서 결정합니다.

- 사용자는 Novforge Access Token의 `sub`로 식별합니다.
- 총가격은 선택한 부품의 DB 가격과 수량으로 자동 계산합니다.
- 다른 사용자의 견적은 조회·수정·삭제할 수 없습니다.

## API 엔드포인트

| 기능 | 도메인 | 엔드포인트 | 메서드 | 인증 | 설명 |
|---|---|---|---|---|---|
| 내 견적 목록 조회 | my-builds | `/api/my-builds` | `GET` | Access Token | 로그인한 사용자의 견적 목록 조회 |
| 내 견적 상세 조회 | my-builds | `/api/my-builds/{buildId}` | `GET` | Access Token | 로그인한 사용자의 특정 견적 조회 |
| 내 견적 생성 | my-builds | `/api/my-builds` | `POST` | Access Token | 빈 견적 또는 선택한 부품이 포함된 견적 생성 |
| 내 견적 수정 | my-builds | `/api/my-builds/{buildId}` | `PATCH` | Access Token | 견적명, 공개 여부, 부품 및 수량 수정 |
| 내 견적 삭제 | my-builds | `/api/my-builds/{buildId}` | `DELETE` | Access Token | 로그인한 사용자의 견적 삭제 |

모든 엔드포인트에 다음 인증 헤더가 필요합니다.

```http
Authorization: Bearer <Novforge Access Token>
```

Google ID Token이 아니라 `POST /api/auth/google` 응답의 Novforge `accessToken`을 사용해야 합니다.

## 인증 흐름

```text
Google ID Token 발급
→ POST /api/users로 회원가입
→ POST /api/auth/google로 로그인
→ Novforge Access Token 발급
→ Authorization: Bearer <accessToken>
→ /api/my-builds 호출
```

이미 가입된 사용자는 회원가입을 생략하고 로그인부터 진행할 수 있습니다.

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

| 파일 | 역할 |
|---|---|
| `MyBuild.java` | 사용자, 단일 부품, 견적 정보와 관계를 관리하는 JPA Entity |
| `MyBuildMemory.java` | 견적과 메모리의 관계 및 수량 관리 |
| `MyBuildMemoryId.java` | `build_id + memory_id` 복합키 |
| `MyBuildStorage.java` | 견적과 보조기억장치의 관계 및 수량 관리 |
| `MyBuildStorageId.java` | `build_id + storage_id` 복합키 |
| `MyBuildController.java` | `/api/my-builds` 엔드포인트 |
| `MyBuildService.java` | 소유권 검사, 부품 조회, 관계 저장, 총가격 계산 |
| `MyBuildRepository.java` | 사용자별 견적 조회 및 연관 부품 일괄 로딩 |
| `MyBuildCreateRequest.java` | 견적 생성 요청 DTO |
| `MyBuildUpdateRequest.java` | 견적 부분 수정 요청 DTO |
| `MyBuildResponse.java` | 견적 및 선택 부품 응답 DTO |
| `MyBuildExceptionHandler.java` | 400·404 오류 응답 처리 |

## DB 관계

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

### 단일 부품 관계

다음 부품은 한 견적에서 하나씩 선택합니다.

| My Build FK | 참조 테이블 | JPA 관계 |
|---|---|---|
| `user_id` | `users.user_id` | `ManyToOne`, 필수 |
| `motherboard_id` | `motherboard.motherboard_id` | `ManyToOne`, 선택 |
| `cpu_id` | `cpu.cpu_id` | `ManyToOne`, 선택 |
| `gpu_id` | `gpu.gpu_id` | `ManyToOne`, 선택 |
| `power_id` | `power.power_id` | `ManyToOne`, 선택 |
| `cpu_cooler_id` | `cpu_cooler.cpu_cooler_id` | `ManyToOne`, 선택 |
| `case_id` | `case.case_id` | `ManyToOne`, 선택 |

### 메모리 관계

한 견적에 여러 종류의 메모리를 수량과 함께 저장할 수 있습니다.

```text
my_build_ram
- build_id
- memory_id
- quantity
```

`build_id + memory_id`가 복합키입니다. 같은 메모리 ID를 요청 배열에 두 번 입력할 수 없으며 수량은 `quantity`로 지정합니다.

### 보조기억장치 관계

한 견적에 여러 종류의 SSD 또는 HDD를 수량과 함께 저장할 수 있습니다.

```text
my_build_storage
- build_id
- storage_id
- quantity
```

`build_id + storage_id`가 복합키입니다. 같은 저장장치 ID를 요청 배열에 두 번 입력할 수 없으며 수량은 `quantity`로 지정합니다.

### 견적 삭제

견적을 삭제하면 해당 견적의 `my_build_ram`, `my_build_storage` 관계 행도 `cascade`와 `orphanRemoval`로 함께 삭제됩니다.

CPU, GPU, 메모리 등 실제 부품 데이터는 삭제되지 않습니다.

## My Build 데이터 모델

| Java 필드 | DB 컬럼 | 제약·정책 |
|---|---|---|
| `id` | `build_id` | PK, 자동 증가 |
| `user` | `user_id` | FK, `NOT NULL`, Access Token에서 결정 |
| `motherboard` | `motherboard_id` | FK, nullable |
| `gpu` | `gpu_id` | FK, nullable |
| `cpu` | `cpu_id` | FK, nullable |
| `powerSupply` | `power_id` | FK, nullable |
| `cpuCooler` | `cpu_cooler_id` | FK, nullable |
| `pcCase` | `case_id` | FK, nullable |
| `name` | `build_name` | `VARCHAR(100)`, `NOT NULL` |
| `totalPrice` | `total_price` | `BIGINT`, `NOT NULL`, 서버 자동 계산 |
| `publicBuild` | `is_public` | `BOOLEAN`, `NOT NULL` |
| `createdAt` | `created_at` | `TIMESTAMP`, 최초 저장 시 자동 생성 |
| `updatedAt` | `updated_at` | `TIMESTAMP`, 수정 시 자동 갱신 |

## 총가격 계산

클라이언트는 `totalPrice`를 요청으로 전달하지 않습니다.

서버가 DB에 저장된 가격으로 다음과 같이 계산합니다.

```text
메인보드 가격
+ CPU 가격
+ GPU 가격
+ 파워 서플라이 가격
+ CPU 쿨러 가격
+ 케이스 가격
+ Σ(메모리 가격 × 수량)
+ Σ(보조기억장치 가격 × 수량)
= totalPrice
```

선택하지 않은 단일 부품은 `0원`으로 계산합니다.

## Endpoints

### 빈 견적 생성 — `POST /api/my-builds`

견적명과 공개 여부만 전달하여 부품이 없는 견적을 먼저 생성할 수 있습니다.

```http
POST /api/my-builds
Authorization: Bearer <Novforge Access Token>
Content-Type: application/json
```

#### Request

```json
{
  "buildName": "나중에 완성할 견적",
  "publicBuild": false
}
```

#### Response — `201 Created`

```json
{
  "buildId": 1,
  "userId": 3,
  "buildName": "나중에 완성할 견적",
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

`userId`와 `totalPrice`는 요청에서 받지 않습니다.

### 전체 부품 견적 생성 — `POST /api/my-builds`

부품을 선택한 상태로 견적을 바로 생성할 수도 있습니다.

```json
{
  "buildName": "7800X3D 게이밍 견적",
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

각 부품 ID는 기존 부품 테이블에 실제로 존재해야 합니다.

### 내 견적 목록 조회 — `GET /api/my-builds`

```http
GET /api/my-builds
Authorization: Bearer <Novforge Access Token>
```

Access Token 사용자가 소유한 견적만 ID 내림차순으로 반환합니다.

```json
[
  {
    "buildId": 1,
    "userId": 3,
    "buildName": "7800X3D 게이밍 견적",
    "totalPrice": 2810000,
    "publicBuild": false,
    "motherboard": {
      "id": 3,
      "name": "MAG B760M MORTAR WIFI II",
      "price": 259000
    },
    "gpu": {
      "id": 3,
      "name": "GeForce RTX 4070 SUPER",
      "price": 899000
    },
    "cpu": {
      "id": 4,
      "name": "Ryzen 7 7800X3D",
      "price": 529000
    },
    "powerSupply": {
      "id": 3,
      "name": "FOCUS GX-850",
      "price": 189000
    },
    "cpuCooler": {
      "id": 4,
      "name": "Kraken 360 RGB",
      "price": 299000
    },
    "pcCase": {
      "id": 3,
      "name": "H7 Flow RGB",
      "price": 189000
    },
    "memories": [
      {
        "id": 3,
        "name": "Samsung DDR5-5600 16GB",
        "price": 59000,
        "quantity": 2
      }
    ],
    "storages": [
      {
        "id": 3,
        "name": "Samsung 990 PRO 2TB",
        "price": 239000,
        "quantity": 1
      },
      {
        "id": 4,
        "name": "WD Blue SN580 1TB",
        "price": 89000,
        "quantity": 1
      }
    ],
    "createdAt": "2026-07-28T21:53:21.854878",
    "updatedAt": "2026-07-28T22:03:20.255212"
  }
]
```

### 내 견적 상세 조회 — `GET /api/my-builds/{buildId}`

```http
GET /api/my-builds/1
Authorization: Bearer <Novforge Access Token>
```

요청한 견적이 없거나 다른 사용자의 견적이면 `404 Not Found`를 반환합니다.

### 견적에 부품 추가·교체 — `PATCH /api/my-builds/{buildId}`

```http
PATCH /api/my-builds/1
Authorization: Bearer <Novforge Access Token>
Content-Type: application/json
```

CPU와 메인보드 추가:

```json
{
  "cpuId": 4,
  "motherboardId": 3
}
```

GPU, 파워, 쿨러, 케이스 추가:

```json
{
  "gpuId": 3,
  "powerId": 3,
  "cpuCoolerId": 4,
  "caseId": 3
}
```

견적명과 공개 여부 수정:

```json
{
  "buildName": "수정된 게이밍 견적",
  "publicBuild": true
}
```

단일 부품 ID를 새 ID로 전달하면 기존 부품이 새 부품으로 교체됩니다.

### 메모리 수정

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

`memories`가 전달되면 기존 목록에 추가되는 것이 아니라 전달된 배열 전체로 교체됩니다.

메모리를 모두 제거하려면 빈 배열을 전달합니다.

```json
{
  "memories": []
}
```

### 보조기억장치 수정

서로 다른 저장장치를 하나씩 선택:

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

같은 저장장치 두 개를 선택:

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

`storages`가 전달되면 기존 목록에 추가되는 것이 아니라 전달된 배열 전체로 교체됩니다.

저장장치를 모두 제거하려면 빈 배열을 전달합니다.

```json
{
  "storages": []
}
```

### 내 견적 삭제 — `DELETE /api/my-builds/{buildId}`

```http
DELETE /api/my-builds/1
Authorization: Bearer <Novforge Access Token>
```

#### Response

```text
204 No Content
```

응답 Body는 없습니다.

## 요청 필드

### 생성 요청

| 필드 | 필수 | 설명 |
|---|---|---|
| `buildName` | 필수 | 공백이 아닌 최대 100자의 견적명 |
| `motherboardId` | 선택 | 메인보드 ID |
| `gpuId` | 선택 | GPU ID |
| `cpuId` | 선택 | CPU ID |
| `powerId` | 선택 | 파워 서플라이 ID |
| `cpuCoolerId` | 선택 | CPU 쿨러 ID |
| `caseId` | 선택 | 케이스 ID |
| `memories` | 선택 | 메모리 ID와 수량 목록 |
| `storages` | 선택 | 저장장치 ID와 수량 목록 |
| `publicBuild` | 필수 | 공개 여부 |

### 수정 요청

모든 필드가 선택값이며 전달한 필드만 수정합니다.

메모리와 저장장치는 배열을 전달하면 해당 배열 전체로 교체합니다.

## 소유권 정책

URL이나 Body에서 `userId`를 받지 않습니다.

```text
Novforge Access Token의 sub
→ users.user_id 조회
→ user_id와 build_id가 모두 일치하는 견적 조회
```

다른 사용자의 견적 ID를 요청해도 데이터 존재 여부를 노출하지 않도록 `404 Not Found`를 반환합니다.

## 오류 응답

| 상태 코드 | 상황 |
|---|---|
| `400 Bad Request` | 존재하지 않는 부품 ID, 중복 메모리·저장장치 ID, 잘못된 수량, 필수값 누락 |
| `401 Unauthorized` | Novforge Access Token 누락·오류·만료 |
| `404 Not Found` | 견적이 존재하지 않거나 다른 사용자의 견적인 경우 |
| `405 Method Not Allowed` | `PATCH /api/my-builds`처럼 `{buildId}` 없이 수정 요청 |
| `415 Unsupported Media Type` | JSON 요청에 `Content-Type: application/json`을 사용하지 않음 |

존재하지 않는 부품을 선택한 경우:

```json
{
  "type": "about:blank",
  "title": "Invalid build part",
  "status": 400,
  "detail": "선택한 CPU 부품을 찾을 수 없습니다. id=999"
}
```

견적을 찾을 수 없는 경우:

```json
{
  "type": "about:blank",
  "title": "My build not found",
  "status": 404,
  "detail": "내 견적을 찾을 수 없습니다. buildId=999"
}
```

## 전체 Postman 테스트 순서

```text
1. Google ID Token 발급
2. POST /api/users로 회원가입
3. POST /api/auth/google로 Novforge Access Token 발급
4. 필요한 부품 목록 API에서 부품 ID 확인
5. POST /api/my-builds로 빈 견적 생성
6. GET /api/my-builds로 목록 확인
7. PATCH /api/my-builds/{buildId}로 단일 부품 추가
8. PATCH로 메모리와 보조기억장치 및 수량 추가
9. totalPrice 자동 계산 확인
10. GET /api/my-builds/{buildId}로 상세 확인
11. 다른 사용자 토큰으로 같은 buildId 조회 시 404 확인
12. DELETE /api/my-builds/{buildId}로 삭제
13. 삭제한 buildId 재조회 시 404 확인
```

## 테스트

My Build 서비스 통합 테스트에서 다음 항목을 검증합니다.

- 사용자 및 부품 데이터 저장
- 빈 견적과 부품 견적 생성
- 메모리·보조기억장치 수량 저장
- 수량을 포함한 총가격 자동 계산
- 견적명과 공개 여부 수정
- 메모리 수량 변경
- 보조기억장치 전체 제거
- 다른 사용자의 견적 접근 차단
- JPA 복합키 및 연관관계 로딩

## 현재 제한사항

- 단일 부품은 추가하거나 다른 제품으로 교체할 수 있지만, PATCH에서 명시적으로 `null`을 전달하여 제거하는 기능은 아직 제공하지 않습니다.
- 메모리와 보조기억장치는 배열 일부 추가 방식이 아니라 전달한 배열 전체로 교체합니다.
- `publicBuild` 값은 저장되지만 공개 견적 목록·상세 API는 아직 제공하지 않습니다.
- 부품 간 호환성 검증은 아직 제공하지 않습니다.
- 부품 가격이 변경되어도 기존 견적의 `totalPrice`는 견적을 수정할 때 다시 계산됩니다.
- 부품 조회는 로그인 사용자에게 허용되며, 등록·수정·삭제는 `ADMIN_EMAILS`에 등록된 관리자만 실행할 수 있습니다.

## Notes

- 실제 부품 ID는 각 Equipment 목록 API에서 확인합니다.
- `userId`와 `totalPrice`를 요청 Body에 포함해도 My Build 생성 요청 DTO에서 사용하지 않습니다.
- 메모리와 저장장치의 같은 ID를 배열에 중복 입력하지 말고 `quantity`를 사용합니다.
- 견적의 메모리·저장장치 관계를 삭제해도 실제 부품 테이블의 제품은 삭제되지 않습니다.

