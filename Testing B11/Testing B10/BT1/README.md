# BT1 - API BaseTest va GET tests (Rest Assured)

## Cau truc

- `src/test/java/bt1/ApiBaseTest.java`: Cau hinh chung RequestSpecification
- `src/test/java/bt1/UserGetApiTest.java`: 4 test GET theo de BT1
- `src/test/resources/testng.xml`: Cau hinh suite TestNG
- `pom.xml`: Dependencies + Surefire plugin

## Yeu cau API key ReqRes

ReqRes hien tai yeu cau API key hop le.

Set API key theo mot trong hai cach:

1. Environment variable:

```powershell
$env:REQRES_API_KEY="your_real_key"
```

2. Maven system property:

```powershell
mvn test -Dreqres.api.key=your_real_key
```

Neu khong co key, test se duoc skip de tranh fail 401 do thieu auth.

## Chay test

```powershell
mvn test
```
