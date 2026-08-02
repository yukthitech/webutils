# WebUtils Testapp — AutoX Automation

REST and UI automation against the WebUtils widget harness.

## Layout

| Path | Purpose |
|------|---------|
| `src/main/config/` | `app-configuration.xml`, `app.properties` |
| `src/main/test-suites/rest/` | REST suites |
| `src/main/test-suites/ui/` | UI suites (e.g. `ui-editable-lov-field.xml`, `ui-simple-lov-field.xml`) |
| `src/main/test-suites/common/` | Shared locators, functions, global setup |
| `src/main/resources/data/` | Data providers |

### Common files

| File | Purpose |
|------|---------|
| `common/global.xml` | Global `<setup>` (login + enable `$restService` API tracking) and `<cleanup>` (quit browser) |
| `common/common-functions.xml` | `enableTrackedApiCalls` / `fetchTrackedApiCalls` / `clearTrackedApiCalls` |
| `common/common-ui-locators.xml` | `ykLov` / `ykEditableLov` custom locators |

## Suite: `webutils-ui-editable-lov-field`

File: `ui/ui-editable-lov-field.xml`. Exercises single-field CATEGORY editable LOV on `/widgets/editable-lov-demo.html`.

| Test case | What it covers |
|-----------|----------------|
| `lovCategoryNoParentClientFilter` | Client-side filter; no LOV API on typing; one CATEGORY fetch on reload |
| `lovCategorySelectClickPersist` | Click Electronics → submit → `TEMP_TABLE.CATEGORY = Electronics` |
| `lovCategoryCaseInsensitivePersist` | Type `ELECTRONICS` → submit → remapped to `Electronics` in `TEMP_TABLE`; no `ELECTRONICS` option row |
| `lovCategoryNewOptionPersist` | Type `AutoxLovGadgets` → submit → new `STORED_LOV_OPTION` + `TEMP_TABLE` row |
| `lovCategoryNewOptionAvailableOnReload` | `dependencies="lovCategoryNewOptionPersist"` — reload shows `AutoxLovGadgets` in suggestions |

Suite setup deletes `TEMP_TABLE` rows and any prior `AutoxLovGadgets` option under CATEGORY.

When selecting with `-tc lovCategoryNewOptionAvailableOnReload`, also include `lovCategoryNewOptionPersist` (AutoX skips dependents if the dependency is not run / failed).

## Suite: `webutils-ui-simple-lov-field`

File: `ui/ui-simple-lov-field.xml`. Exercises simple (id-based) CATEGORY LOV on `/widgets/simple-lov-demo.html`.

| Test case | What it covers |
|-----------|----------------|
| `simpleLovCategorySearchAndSelect` | Open dropdown → filter `Ele` → select Electronics → submit → `TEMP_TABLE.CATEGORY = Electronics` |
| `simpleLovCategorySelectViaLocator` | `c:ykLov:categoryId` = Books → submit → `TEMP_TABLE.CATEGORY = Books` |

Suite setup deletes `TEMP_TABLE` rows.

## Suite: `webutils-ui-markdown-editor`

File: `ui/ui-markdown-editor.xml`. Exercises `@Markdown` / `yk-markdown-editor` on `/widgets/markdown-demo.html` via `MarkdownDemoModel`.

| Test case | What it covers |
|-----------|----------------|
| `markdownLivePreviewAndSubmit` | Set CodeMirror markdown via JS → live preview → submit → echoed content in response panel |
| `markdownViewModes` | Edit / Preview / Split toggles pane and resize-handle visibility |
| `markdownServerToClientAndSubmit` | Sample from server shown in CM → edit → submit echo |

## Suite: `webutils-ui-language-editor`

File: `ui/ui-language-editor.xml`. Exercises `@Language` / `yk-language-editor` on `/widgets/language-demo.html` via `LanguageDemoModel`.

| Test case | What it covers |
|-----------|----------------|
| `languageServerContentDisplayed` | GET sample → CodeMirror shows ServerJson / ServerXml / autoxSchema |
| `languageValidSubmitEcho` | Submit valid sample → response echoes markers |
| `languageInvalidJsonRejected` | Invalid JSON submit → error, no success alert/response |

## Suite: `webutils-rest-search-settings`

File: `rest/rest-search-settings.xml`. Search settings APIs for `sampleItemSearch`.

| Test case | What it covers |
|-----------|----------------|
| `readDefaultSearchSettings` | GET defaults — no id, columns present |
| `saveOrUpdateAndExecuteReflectsSettings` | pageSize 3, hide Description, Status before Category; execute reflects |
| `pageSizeOverMaxRejected` | pageSize 1001 rejected |

## Suite: `webutils-ui-search-settings`

File: `ui/ui-search-settings.xml`. Settings dialog on `/widgets/search-demo.html`.

| Test case | What it covers |
|-----------|----------------|
| `searchSettingsDialogPersistAndRefresh` | page size 3, hide Description, move Status up → save → table/footer updated |

## Run

From `automation/`:

```bash
mvn exec:java
```

Filter by suite / test case with AutoX `-ts` / `-tc` (do not use test-case `groups`):

```bash
mvn exec:java "-Dexec.args=src/main/config/app-configuration.xml -rf test-reports -prop src/main/config/app.properties -ts webutils-ui-editable-lov-field -rod true -dport 0"
mvn exec:java "-Dexec.args=src/main/config/app-configuration.xml -rf test-reports -prop src/main/config/app.properties -ts webutils-ui-simple-lov-field -rod true -dport 0"
mvn exec:java "-Dexec.args=src/main/config/app-configuration.xml -rf test-reports -prop src/main/config/app.properties -ts webutils-ui-markdown-editor -rod true -dport 0"
mvn exec:java "-Dexec.args=src/main/config/app-configuration.xml -rf test-reports -prop src/main/config/app.properties -ts webutils-ui-language-editor -rod true -dport 0"
mvn exec:java "-Dexec.args=src/main/config/app-configuration.xml -rf test-reports -prop src/main/config/app.properties -ts webutils-rest-search-settings -rod true -dport 0"
mvn exec:java "-Dexec.args=src/main/config/app-configuration.xml -rf test-reports -prop src/main/config/app.properties -ts webutils-ui-search-settings -rod true -dport 0"
```

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

Follow the same AutoX patterns as Sethu4U (`automation/docs/sethu4u-automation-conventions.md` in that project): dynamic data providers, `ykEditableLov` / `ykLov` locators, browser QA before UI automation.
