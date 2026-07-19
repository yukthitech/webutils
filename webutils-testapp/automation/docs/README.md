# WebUtils Testapp — AutoX Automation

REST and UI automation against the WebUtils widget harness.

## Layout

| Path | Purpose |
|------|---------|
| `src/main/config/` | `app-configuration.xml`, `app.properties` |
| `src/main/test-suites/rest/` | REST suites |
| `src/main/test-suites/ui/` | UI suites (e.g. `ui-lov-field.xml`) |
| `src/main/test-suites/common/` | Shared locators, functions, global setup |
| `src/main/resources/data/` | Data providers |

### Common files

| File | Purpose |
|------|---------|
| `common/global.xml` | Global `<setup>` (login + enable `$restService` API tracking) and `<cleanup>` (quit browser) |
| `common/common-functions.xml` | `enableTrackedApiCalls` / `fetchTrackedApiCalls` / `clearTrackedApiCalls` |
| `common/common-ui-locators.xml` | `ykLov` / `ykEditableLov` custom locators |

## Suite: `webutils-ui-lov-field`

File: `ui/ui-lov-field.xml`. Exercises single-field CATEGORY editable LOV on `/widgets/lov-demo.html`.

| Test case | What it covers |
|-----------|----------------|
| `lovCategoryNoParentClientFilter` | Client-side filter; no LOV API on typing; one CATEGORY fetch on reload |
| `lovCategorySelectClickPersist` | Click Electronics → submit → `TEMP_TABLE.CATEGORY = Electronics` |
| `lovCategoryCaseInsensitivePersist` | Type `ELECTRONICS` → submit → remapped to `Electronics` in `TEMP_TABLE`; no `ELECTRONICS` option row |
| `lovCategoryNewOptionPersist` | Type `AutoxLovGadgets` → submit → new `STORED_LOV_OPTION` + `TEMP_TABLE` row |
| `lovCategoryNewOptionAvailableOnReload` | `dependencies="lovCategoryNewOptionPersist"` — reload shows `AutoxLovGadgets` in suggestions |

Suite setup deletes `TEMP_TABLE` rows and any prior `AutoxLovGadgets` option under CATEGORY.

When selecting with `-tc lovCategoryNewOptionAvailableOnReload`, also include `lovCategoryNewOptionPersist` (AutoX skips dependents if the dependency is not run / failed).

## Run

From `automation/`:

```bash
mvn exec:java
```

Filter by suite / test case with AutoX `-ts` / `-tc` (do not use test-case `groups`).

## Environment

- App: `http://localhost:8091` (see `app.properties`)
- DB: MySQL `webutils-test` / `webutilstest` (align with services `application.properties`)
- ChromeDriver: place under `./drivers/` before UI suites (see `app-configuration.xml`)

## API call tracking helpers

Tracking is enabled once in global setup. In suites:

```xml
<f:clearTrackedApiCalls/>
<!-- ... UI steps ... -->
<f:fetchTrackedApiCalls return-attr="allCalls"/>
<f:fetchTrackedApiCalls uri="string: /api/lov/" return-attr="lovCalls"/>
<s:assert-equals actual="expr: attr.lovCalls.size()" expected="0"/>
```

## Conventions

Follow the same AutoX patterns as Sethu4U (`automation/docs/sethu4u-automation-conventions.md` in that project): dynamic data providers, `ykEditableLov` locators, browser QA before UI automation.
