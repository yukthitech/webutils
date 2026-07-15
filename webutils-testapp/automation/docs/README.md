# WebUtils Testapp — AutoX Automation

Scaffold for REST and UI automation against the WebUtils widget harness.

## Layout

| Path | Purpose |
|------|---------|
| `src/main/config/` | `app-configuration.xml`, `app.properties` |
| `src/main/test-suites/rest/` | REST suites (not added yet) |
| `src/main/test-suites/ui/` | UI suites (not added yet) |
| `src/main/test-suites/common/` | Shared functions / custom locators |
| `src/main/resources/data/` | Data providers |

## Run (once suites exist)

From `automation/`:

```bash
mvn exec:java
```

Filter by suite / test case with AutoX `-ts` / `-tc` (do not use test-case `groups`).

## Environment

- App: `http://localhost:8090` (see `app.properties`)
- DB: MySQL `test` / `test`
- ChromeDriver: place under `./drivers/` before UI suites (see `app-configuration.xml`)

## Conventions

Follow the same AutoX patterns as Sethu4U (`automation/docs/sethu4u-automation-conventions.md` in that project): dynamic data providers, `ykEditableLov` locators, browser QA before UI automation.
