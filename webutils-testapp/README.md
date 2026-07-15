# WebUtils Testapp

Isolated Spring Boot harness for testing **new-stack** WebUtils UI widgets outside product apps (e.g. Sethu4U).

## Modules

| Module | Artifact | Role |
|--------|----------|------|
| parent | `webutils-testapp-parent` | BOM / aggregator |
| `common` | `webutils-testapp-common` | `@Model` DTOs (LOV / OTP / search) |
| `services` | `webutils-testapp-services` | Spring Boot app + static `web/` |
| `dbschema` | `webutils-testapp-dbschema` | Liquibase schema + seed data |
| `automation` | `webutils-testapp-automation` | AutoX scaffold (suites later) |

## Prerequisites

1. MySQL database and credentials as in `dbschema/src/main/resources/liquibase.properties` and `services/.../application.properties`
2. Install `webutils-common` and `webutils-services` (`1.0.0-SNAPSHOT`) locally
3. From `dbschema/`: `mvn process-resources liquibase:update`  
   (Fresh reset: `mvn liquibase:dropAll liquibase:update`)

## Login / session

WebUtils APIs require an authenticated session. Use the login page before opening widget demos.

| Field | Value |
|-------|--------|
| URL | `/login/login.html` |
| Username | `test@test.com` |
| Password | `test` |
| User space | `test` |

Login calls `POST /api/testapp/auth/login` (allows short passwords; framework `/api/auth/login` requires min length 8). The auth token is stored in `sessionStorage` as `authToken`.

If the seed user was changed, re-run Liquibase after `dropAll` so `003-seed-lov-and-sample-data.xml` inserts `test@test.com`.

## Run the app

From `services/`:

```bash
mvn spring-boot:run "-Dspring-boot.run.jvmArguments=-Dapp.encryptor.keystore=testapp-keystore -Dapp.encryptor.alias=testapp -Dapp.encryptor.password=testapp123"
```

1. Open `http://localhost:<port>/login/login.html` and sign in  
2. Then open demos from `http://localhost:<port>/index.html`

Demo pages:

- `/widgets/lov-demo.html` — editable + multi-editable LOV  
- `/widgets/otp-demo.html` — OTP verification fields  
- `/widgets/search-demo.html` — search form + results (`sampleItemSearch`)  

`web/lib` is a junction/symlink to `webutils-services/web/lib` so framework UI changes reflect immediately.
