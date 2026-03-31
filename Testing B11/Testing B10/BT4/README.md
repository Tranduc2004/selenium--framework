# BT4 - Kiem thu Authorization va Error Handling

## 1) Muc tieu bai tap

Trien khai bai tap 4 gom 2 phan:
- Phan A: Authorization/Login/Register
- Phan B: Data-driven test cho login error handling

## 2) Cau truc project

- `src/test/java/bt4/ApiBaseTest.java`
  - Cau hinh RequestSpecification chung: base URI, base path, content type, x-api-key, logging.
- `src/test/java/bt4/AuthorizationTest.java`
  - Chua cac test login/register theo phan A.
- `src/test/java/bt4/LoginDataDrivenTest.java`
  - Chua @DataProvider va test data-driven cho phan B.
- `src/test/resources/testng.xml`
  - Cau hinh suite TestNG.

## 3) Mapping theo de bai

### Phan A - Authorization (1.0 diem)

1. Login thanh cong (`POST /api/login`)
- Input: email = `eve.holt@reqres.in`, password = `cityslicka`
- Assert: status 200, co token khong rong

2. Login thieu password
- Input: chi co email
- Assert: status 400, error contains `Missing password`

3. Login thieu email
- Input: chi co password
- Assert: status 400, error contains `Missing email or username`

4. Register thanh cong (`POST /api/register`)
- Input: email + password hop le
- Assert: status 200, co `id` va `token`

5. Register thieu password
- Input: chi co email
- Assert: status 400, error contains `Missing password`

### Phan B - Data-driven cho Error Handling (0.5 diem)

`@DataProvider(name = "loginScenarios")` gom 5 scenario:

1. `eve.holt@reqres.in`, `cityslicka` -> 200, `null`
2. `eve.holt@reqres.in`, empty password -> 400, `Missing password`
3. empty email, `cityslicka` -> 400, `Missing email or username`
4. `notexist@reqres.in`, `wrongpass` -> 400, `user not found`
5. `invalid-email`, `pass123` -> 400, `user not found`

Test method su dung bo tham so: `{email, password, expectedStatus, expectedError}`.

## 4) API key va cach chay

ReqRes bat buoc API key hop le trong header `x-api-key`.

Dat key:

```powershell
$env:REQRES_API_KEY="your_key"
```

Chay test:

```powershell
mvn test
```

## 5) Ket qua mong doi

- Tong test duoc chay: 10
  - 5 test phan A
  - 5 scenario phan B
- Failures: 0
- Errors: 0
- Skipped: 0 (neu key hop le)
