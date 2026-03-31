# BT3 - JSON Schema Validation

## 1) Muc tieu bai tap

Thuc hien JSON Schema Validation cho cac endpoint chinh cua ReqRes:
- GET `/api/users` (danh sach user)
- GET `/api/users/2` (chi tiet user)
- POST `/api/users` (tao user)

Yeu cau de bai duoc dap ung:
- Tao 3 file schema theo endpoint.
- Tat ca schema dat `additionalProperties: false` de phat hien field du/thieu.
- Viet test cho 3 schema.
- Co demo test fail khi schema thieu field so voi response.

## 2) Cau truc project

- `src/test/java/bt3/ApiBaseTest.java`
  - Cau hinh RequestSpecification chung (base URI/path, header auth, logging).
- `src/test/java/bt3/JsonSchemaValidationTest.java`
  - Chua 4 test:
    - 3 test validate schema chinh.
    - 1 test demo expected FAIL.
- `src/test/resources/schemas/user-list-schema.json`
  - Schema cho GET `/api/users`.
- `src/test/resources/schemas/user-schema.json`
  - Schema cho GET `/api/users/2`.
- `src/test/resources/schemas/create-user-schema.json`
  - Schema cho POST `/api/users`.
- `src/test/resources/schemas/user-schema-demo-missing-avatar.json`
  - Schema demo co tinh bo field `avatar` de tao mismatch.

## 3) Mapping schema voi de BT3

### 3.1 user-list-schema.json
Kiem tra response GET `/api/users`:
- Co `page` (integer), `total_pages` (integer), `data` (array).
- Moi item trong `data` co: `id`, `email`, `first_name`, `last_name`, `avatar`.
- Dat `additionalProperties: false` o root va item object.

### 3.2 user-schema.json
Kiem tra response GET `/api/users/2`:
- Cau truc nested object `data`, `support`.
- `data` phai co du cac field user.
- Dat `additionalProperties: false`.

### 3.3 create-user-schema.json
Kiem tra response POST `/api/users`:
- Co `name` (string), `job` (string), `id` (string), `createdAt` (string date-time).
- Dat `additionalProperties: false`.

## 4) Test demo FAIL ro rang

Test `testSchemaShouldFailWhenFieldMissingInSchema`:
- Goi GET `/api/users/2`.
- Validate bang schema demo khong khai bao field `avatar` trong `data` nhung lai dat `additionalProperties: false`.
- Ky vong nem `AssertionError`.
- Assert message loi co chua thong tin mismatch (`avatar`/`additional`/`schema`) de thay ro ly do fail.

## 5) Dieu kien chay

ReqRes yeu cau API key hop le.

Dat key theo 1 trong 2 cach:

```powershell
$env:REQRES_API_KEY="your_real_key"
```

hoac:

```powershell
mvn test -Dreqres.api.key=your_real_key
```

## 6) Lenh chay

```powershell
mvn test
```

## 7) Ket qua mong doi

- `Tests run: 4`
- `Failures: 0`
- `Errors: 0`
- `Skipped: 0` (neu co API key hop le)

Luu y: Test demo FAIL da duoc bat loi chu dong bang `expectThrows`, nen toan suite van PASS khi mismatch xay ra nhu mong doi.
