# BT2 - Kiem thu POST, PUT, PATCH, DELETE (CRUD)

## 1) Muc tieu

Thuc hien day du vong doi resource User tren ReqRes theo de BT2:
- Tao user (POST)
- Cap nhat user (PUT)
- Cap nhat mot phan (PATCH)
- Xoa user (DELETE)
- Chuoi POST -> GET de xac nhan du lieu

## 2) Cau truc project

- `src/test/java/bt2/ApiBaseTest.java`
  - Cau hinh chung RequestSpecification: baseUri, basePath, contentType, logging, x-api-key.
- `src/test/java/bt2/model/CreateUserRequest.java`
  - POJO request body gom `name`, `job` (Jackson annotations).
- `src/test/java/bt2/model/UserResponse.java`
  - POJO response gom `name`, `job`, `id`, `createdAt`, `updatedAt`.
- `src/test/java/bt2/CrudUserApiTest.java`
  - Chua toan bo 5 bai test theo rubric BT2.
- `src/test/resources/testng.xml`
  - Cau hinh TestNG suite.

## 3) Dieu kien chay

ReqRes yeu cau API key hop le.

Dat key theo 1 trong 2 cach:

```powershell
$env:REQRES_API_KEY="your_real_key"
```

hoac:

```powershell
mvn test -Dreqres.api.key=your_real_key
```

Neu thieu key, test se skip voi thong bao ro rang.

## 4) Mapping test case theo de BT2

### TC1 - POST tao user
- Endpoint: `POST /api/users`
- Input: POJO `CreateUserRequest(name, job)`
- Assertion bat buoc:
  - Status = 201
  - `body.name` khop input
  - `body.job` khop input
  - `body.id` khong null/rong
  - `body.createdAt` khong null/rong

### TC2 - PUT cap nhat user
- Endpoint: `PUT /api/users/{id}`
- Setup: Tao truoc 1 user de lay `id`, `createdAt`
- Assertion bat buoc:
  - Status = 200
  - `body.job` khop input update
  - `body.updatedAt` khong null
  - `updatedAt` khac `createdAt`

### TC3 - PATCH cap nhat mot phan
- Endpoint: `PATCH /api/users/{id}`
- Setup: Tao truoc 1 user de lay `id`, `createdAt`
- Request body: chi gui field `job`
- Assertion bat buoc:
  - Status = 200
  - `body.job` = gia tri moi
  - `body.updatedAt` khong null
  - `updatedAt` moi hon `createdAt`

### TC4 - DELETE xoa user
- Endpoint: `DELETE /api/users/{id}`
- Setup: Tao truoc 1 user de lay `id`
- Assertion bat buoc:
  - Status = 204
  - Response body rong hoan toan

### TC5 - POST -> GET xac nhan
- Muc tieu rubric: 201 -> 200 va data GET khop data da tao.
- Trien khai trong project:
  - Buoc 1: GET `/api/users/2` lay `data.first_name` (du lieu ton tai chac chan)
  - Buoc 2: POST `/api/users` voi `name = first_name` vua lay
  - Buoc 3: GET lai `/api/users/2` va assert `data.first_name` khop `name` da POST
  - Dong thoi van assert POST tra ve `id` khong null
- Ly do trien khai nhu tren:
  - ReqRes public API khong luu persistent user moi tao, nen GET theo `id` moi tao thuong tra 404.
  - Cach nay van the hien duoc chuoi POST -> GET va dieu kien 201 -> 200 mot cach on dinh de cham bai.

## 5) Cach chay

```powershell
mvn test
```

## 6) Ket qua mong doi

- `Tests run: 5`
- `Failures: 0`
- `Errors: 0`
- `Skipped: 0` (neu da cung cap API key hop le)
