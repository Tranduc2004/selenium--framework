# BT6 - API + UI Integration

## Covered requirements

Part A:
- `@BeforeMethod` calls `POST /api/login` on ReqRes and logs token to console.
- `dependsOnMethods` is used so UI verification is skipped if API precondition does not pass.
- UI test logs in on SauceDemo via form input (not injection).
- Verifies URL contains `inventory` and title is `Swag Labs`.

Part B:
- Calls `GET /api/users` to check API health and stores result in `isApiAlive`.
- If `isApiAlive = false`, UI flow is skipped using `SkipException`.
- UI flow: login -> add 2 products -> verify badge `2` -> open cart -> verify 2 items.
- In-code comments explain API check, UI action, and assertions.

## Run

```powershell
cd "d:\1150080131_TranDuc_CNPM2\Testing\Testing B10\BT6"
$env:REQRES_API_KEY="pub_0e30a5c7ce45a945c2000c6925cb61a3"
mvn test
```

## Notes

- Tests run Chrome in headless mode.
- If your machine blocks browser startup, install/update Chrome and try again.
