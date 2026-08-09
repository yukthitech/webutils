# WebUtils — UI Widgets

> **Audience:** Cursor agents building Vue pages on WebUtils static UI.  
> **Related:** [setup-and-configuration.md](./setup-and-configuration.md) · [services.md](./services.md)

## When to read this

- Creating or fixing a page that uses WebUtils form/search/LOV/OTP/markdown/language widgets
- Wiring Vue bootstrap (`newVueApp`), rest client, or SPA shells
- Mapping Java `@Model` annotations to Vue components
- Deciding app vs framework widget ownership

---


## 1. Asset locations

| What | Path |
|------|------|
| Framework libs (source of truth) | `webutils-services/web/lib` |
| App junction | `{app}/services/web/lib` → that folder (gitignored `/lib/`) |
| Framework JS/CSS | `/lib/webutils/*.js`, `/lib/webutils/webutils.css` |
| App theme / config | `/common/app.css`, `/common/app-config.js` |
| App-only widgets | `{app}/services/web/common/widgets/` (e.g. Sethu4U `s4u-login`) |

Static serving: `spring.web.resources.static-locations=file:./web/` (run Maven/`spring-boot:run` from `services/`).

---

## 2. Page bootstrap (canonical)

Every WebUtils page should:

1. Load jQuery / Bootstrap / **Bootstrap Icons** / bootstrap-select (icons required for `yk-search-action` and other icon buttons).
2. Load `/lib/webutils/webutils.css` + app CSS.
3. Include `#webutilsPageLoading` and `#ykApp` (initially hidden).
4. Include `<yk-dialogs ref="ykDialogs"></yk-dialogs>` inside `#ykApp`.
5. Mount with `Webutils.newVueApp(…).mount("#ykApp")`.

### HTML skeleton

```html
<link href="/lib/bootstrap-5.1.0/css/bootstrap.min.css" rel="stylesheet">
<link href="/lib/bootstrap-icons-1.3.0/bootstrap-icons.css" rel="stylesheet">
<link href="/lib/bootstrap-select-1.14.0/bootstrap-select.min.css" rel="stylesheet">
<link href="/lib/webutils/webutils.css" rel="stylesheet">
<link href="/common/app.css" rel="stylesheet">
<script src="/lib/jquery-3.6.0/jquery.min.js"></script>
<script src="/lib/popper-2.9.3/popper.min.js"></script>
<script src="/lib/bootstrap-5.1.0/js/bootstrap.min.js"></script>
<script src="/lib/bootstrap-select-1.14.0/bootstrap-select.min.js"></script>
<script type="module" src="/common/app-config.js"></script>
…
<div id="webutilsPageLoading">Loading…</div>
<div id="ykApp" style="display:none;">
  … widgets …
  <yk-dialogs ref="ykDialogs"></yk-dialogs>
</div>
<script type="module" src="/path/to/page.js"></script>
```

### JS mount

```js
import * as Webutils from "/lib/webutils/webutils-app.js";
import { $restService } from "/lib/webutils/rest-service.js";
import { $utils } from "/lib/webutils/common.js";

Webutils.newVueApp({
  data() { return { /* … */ }; },
  methods: { /* … */ }
}).mount("#ykApp");
```

`newVueApp` (`webutils-app.js`):

- Creates Vue 3 app from ESM build
- Calls `addDefaultComponents` (all framework widgets)
- Hides `#webutilsPageLoading`, shows `#ykApp`
- Sets `$utils.ykDialogs` from `ref="ykDialogs"`

---

## 3. Key framework JS modules

| File | Export / role |
|------|----------------|
| `webutils-app.js` | `newVueApp`, `addDefaultComponents` |
| `rest-service.js` | `$restService` — HTTP + Bearer from `sessionStorage.authToken` |
| `common.js` | `$utils`, `$appConfiguration`, merge helpers |
| `model-def-service.js` | FieldType → Vue component mapping |
| `input-fields.js` | Input / OTP / captcha / file / html / switch |
| `lov-fields.js` | `yk-lov-field`, `yk-editable-lov-field`, `yk-multi-editable-lov-field` (vue-multiselect) |
| `markdown-editor.js` | `yk-markdown-editor` — CodeMirror + marked/DOMPurify; edit / split / preview |
| `language-editor.js` | `yk-language-editor` — JSON / XML / JSON_SCHEMA / PYTHON + fold gutter |
| `forms.js` | `yk-form`, `yk-model-form`, `yk-search-form`, `yk-search-results`, `yk-search-action`, multi-row forms |
| `modal-dialogs.js` | `yk-dialogs`, modal / model-form dialogs |
| `nav-bar.js` | `yk-route-nav-bar`, `yk-route-side-bar` |
| `user-service.js` | Login/logout helpers |
| `validation.js` | Client validation |
| `payment.js` | `yk-payment-checkout` |
| `status-badge.js` | `yk-status-badge` |

---

## 4. Annotation → widget mapping

Backend `@Model` fields drive `yk-model-form` via `/api/model/{name}`. Mapping lives in `model-def-service.js`.

| Field / annotation | Component |
|--------------------|-----------|
| STRING / DATE / PASSWORD / INTEGER / FLOAT | `yk-input-field` |
| MULTI_LINE_STRING (`@MultilineText`) | `yk-textarea-field` |
| LIST_OF_VALUES — `Long` + `@LOV` | `yk-lov-field` |
| Editable LOV — `String` + `@LOV` (STORED) | `yk-editable-lov-field` |
| Multi editable — `Collection<String>` + `@LOV` | `yk-multi-editable-lov-field` |
| BOOLEAN | `yk-switch` |
| `@Otp` / VERIFICATION | `yk-ver-input-field` |
| CAPTCHA | `yk-captcha-field` |
| FILE / IMAGE | `yk-input-file` / `yk-input-image` |
| HTML (`@Html`) | `yk-html-editor` |
| MARKDOWN (`@Markdown` on `String`) | `yk-markdown-editor` (full width; §5.2) |
| LANGUAGE (`@Language(LanguageType.…)` on `String`) | `yk-language-editor` (full width; §5.3) — types: `JSON`, `XML`, `JSON_SCHEMA`, `PYTHON` |

### LOV choice (agents)

| Persist | Java type | Widget |
|---------|-----------|--------|
| Option **id** | `Long` + `@LOV(…, STORED_TYPE)` | `yk-lov-field` |
| Option **label** (create allowed) | `String` + `@LOV(…, STORED_TYPE)` | `yk-editable-lov-field` |
| Multi labels | `List<String>` + `@LOV` | `yk-multi-editable-lov-field` |
| Search filter only | `@LOV(…, persist = false)` | same widgets; no option create |

Demos: `webutils-testapp/services/web/widgets/editable-lov-demo.*`, `multi-editable-lov-demo.*`, `simple-lov-demo.*`, `markdown-demo.*`, `language-demo.*`, `search-demo.*`.

---

## 5. Model forms

```html
<yk-model-form
  ref="lovForm"
  model-name="EditableLovDemoModel"
  v-model="formData"
  :submit-tried="submitTried"
  :field-errors="fieldErrors">
</yk-model-form>
```

Pattern:

1. `this.$refs.lovForm.validate()`
2. `payload = this.$refs.lovForm.getModel()`
3. `$restService.invokePost("/api/…", payload, { context: this, onSuccess, onError })`

On error, map `err.response.fieldErrors` into `fieldErrors` for field highlighting.

---

## 5.1 Dynamic layout (`yk-model-field`)

Use when **standard auto-layout is not enough** — sections, tabs, mixed custom widgets, or large full-width content panes that should not share a row with other fields.

Do **not** fork `yk-model-form` / `yk-model-form-dialog` for this. Own the page or modal shell and compose fields explicitly.

**Recipe:**

1. `$restService.fetchModelDef("YourModel", …)` then `$modelDefService.populateFieldDetails(modelDef)` (required before `yk-model-field`).
2. Place fields with `yk-model-field`:

```html
<div v-if="modelDef">
  <div class="row">
    <yk-model-field ref="name" :model-def="modelDef" field-name="name"
      v-model="formData.name" :column-count="6"
      :enable-error="formSubmitTried" :server-error="fieldErrors.name">
    </yk-model-field>
  </div>
  <!-- tabs / sections / custom widgets as needed -->
</div>
```

3. Validate by calling `this.$refs[<fieldName>].validate()` on each displayable field.
4. Submit with `$restService.invokePost` / `invokePut`; map `response.errors` (or `fieldErrors`) into `fieldErrors`.

**Examples:** Sethu4U employer registration (`employer/registration/`); Twister agents create/edit modal (`services/web/agents/`) — tabbed layout with one large template/schema field per tab.

Prefer `yk-model-form` / `yk-model-form-dialog` when a simple column grid is sufficient.

---

## 5.2 Markdown editor (`yk-markdown-editor`)

**Backend:** `String` + `@Markdown` → `FieldType.MARKDOWN` (full width). No server-side markdown validation beyond normal string constraints (`@Required`, `@MaxLen`, …).

**Frontend:** CodeMirror (markdown mode) + marked + DOMPurify. Libs load on demand from `/lib/codemirror-5.65.16`, `/lib/marked-15.0.7`, `/lib/dompurify-3.2.4`.

| Prop | Default | Notes |
|------|---------|--------|
| `height` | `400px` | Editor / split container height |
| `sync-scroll` | `true` | Keep edit ↔ preview scroll ratio in sync (split mode) |
| `view-mode` | `split` | `edit` \| `split` \| `preview` (also emits `update:viewMode`) |

**UI behavior:**

- Toolbar: edit-only / split / preview-only (ids `{field}-md-mode-edit|split|preview`)
- Split: drag resize handle (`{field}-md-handle`); left pane width clamped ~20–80%
- Live preview HTML is sanitized before `v-html`
- Works inside `yk-model-form` via model def; no extra page script beyond normal form wiring

Demo: `/widgets/markdown-demo.html` (`MarkdownDemoModel`).

---

## 5.3 Language editor (`yk-language-editor`)

**Backend:** `String` + `@Language(LanguageType.…)` → `FieldType.LANGUAGE` + `languageType` on the field def (full width). **Jakarta-validated** on submit (`LanguageValidator`).

| `LanguageType` | Validated as | CodeMirror |
|----------------|--------------|------------|
| `JSON` | Parseable JSON | JS mode, `json: true` |
| `XML` | Secure XML document | `xml` |
| `JSON_SCHEMA` | JSON + draft 2020-12 meta-schema | JS mode, `json: true` |
| `PYTHON` | None (accepted as-is for now) | `python` |

**Frontend:** CodeMirror with line numbers, fold gutter (`Ctrl-Q` / gutter fold), brace/XML/indent fold addons. Libs load on demand from `/lib/codemirror-5.65.16`.

| Prop | Default | Notes |
|------|---------|--------|
| `height` | `220px` | Editor wrap height |

Invalid JSON / XML / JSON Schema is rejected by the server (field errors on the model form). Python currently skips language validation. Empty/blank skips language validation unless `@Required` is also present.

Demo: `/widgets/language-demo.html` (`LanguageDemoModel` — JSON, XML, JSON Schema, Python fields).

---

## 6. Search listings (mandatory for tables)

**Rule:** UI multi-row listings use WebUtils search — not custom list endpoints + hand-rolled tables.

Wire three pieces: **query form** → **results table** → **actions** / customizers.

**Do not** place separate page-level Add / Edit / Delete buttons beside the results. Put CRUD (and other) actions on `yk-search-results` via child `yk-search-action` widgets — global actions in the results toolbar, row actions after selection (toolbar + floating panel).

```html
<yk-search-form
  ref="searchForm"
  query-name="sampleItemSearch"
  :column-count="2"
  :page-size="5"
  @search="onSearch">
</yk-search-form>

<yk-search-results
  ref="results"
  title="Sample Items"
  query-name="sampleItemSearch"
  @select="onSelectRow"
  @page-change="onPageChange"
  @settings-click="onSettingsClick"
  @settings-saved="onSettingsSaved">
  <yk-field-customizer field="category" v-slot="{ value, row }">
    <span class="badge bg-secondary">{{ value }}</span>
  </yk-field-customizer>
  <yk-search-action id="sample-add-action" label="Add" icon="bi-plus-square-fill" color="#198754"
    @action="onAddAction">
  </yk-search-action>
  <yk-search-action id="sample-edit-action" label="Edit" icon="bi-pencil-fill" color="#fd7e14"
    :row-action="true" @action="onEditAction">
  </yk-search-action>
</yk-search-results>
```

```js
onSearch(searchResponse) {
  this.$refs.results.setSearchResults(searchResponse);
},
onPageChange(pageNumber) {
  this.$refs.searchForm.gotoPage(pageNumber);
},
onSettingsSaved(payload) {
  this.$refs.searchForm.refreshSearch();
},
onEditAction({ row, event }) {
  // row is the selected search row map (null for global actions)
}
```

### 6.1 Query form (`yk-search-form`)

Loads criteria fields from `GET /api/search/{queryName}/query/def`, then executes search.

| Prop | Default | Notes |
|------|---------|--------|
| `query-name` | required | Must match `@SearchQueryMethod(name = "…")` |
| `column-count` | `2` | Fields per row (Bootstrap grid flow) |
| `page-size` | `100` | Listing default sent on execute; persisted user settings override once saved |
| `simple-search` | `false` | `true` → `/api/search/search/` instead of `/api/search/execute/` |

| Method | Role |
|--------|------|
| `search()` | New search from page 1 (Search button) |
| `gotoPage(n)` | Re-run last criteria at page `n` |
| `refreshSearch()` | Re-run current page after settings change (no-op if never searched) |

Always sends `fetchCount=true`, `pageNumber`, and `pageSize`. Pagination is **server-side** (`LIMIT`/`OFFSET`). After each response, the form adopts `response.pageSize` so client and server stay in sync.

Event: `@search` → pass `searchResponse` into `results.setSearchResults(…)`.

### 6.2 Results table (`yk-search-results`)

| Prop / event | Notes |
|--------------|--------|
| `title` | Header title (left) |
| `query-name` | Same search query name — **required** for the settings gear/dialog |
| `@select` | Selected row `dataMap` (single-row selection) |
| `@double-click` | Row `dataMap` on double-click |
| `@page-change` | Wire to `searchForm.gotoPage(n)` |
| `@settings-click` | Fired when the gear is clicked (dialog also opens) |
| `@settings-saved` | After settings persist — typically `searchForm.refreshSearch()` |

**Table features:**

- Sticky header, drag-resizable columns (independent pixel widths; horizontal scroll when wider than the pane), zebra rows
- Footer: `(start-end) of total` plus first / prev / page dropdown / next / last
- Header: `title` left, action icons + settings gear right
- Default cell links when no customizer: `SearchResultType.EMAIL` → `mailto:`, `PHONE_NO` → `tel:`

### 6.3 Actions (`yk-search-action`)

Place as children of `yk-search-results`. Icon-only toolbar buttons (Bootstrap Icons class — ensure `/lib/bootstrap-icons-1.3.0/bootstrap-icons.css` is loaded); `label` is the tooltip; optional `color` tints the icon.

| Prop | Default | Notes |
|------|---------|--------|
| `id` | optional | Stable automation id |
| `label` | required | Tooltip text |
| `icon` | `""` | Prefer fill variants for color visibility, e.g. `bi-pencil-fill`, `bi-plus-square-fill`, `bi-trash-fill` |
| `color` | `""` | CSS color for the icon |
| `row-action` | `false` | See below |

| Kind | `row-action` | Visibility | `@action` payload |
|------|--------------|------------|-------------------|
| **Global** | omitted / `false` | Always in toolbar (add, export, …) | `{ row: null, event }` |
| **Row** | `true` | Hidden until a row is selected; then in toolbar **and** floating panel near the click | `{ row: selectedRowMap, event }` |

Typical listing CRUD:

```html
<yk-search-results title="Agents" query-name="agentSearch"
  @page-change="onPageChange" @settings-saved="onSettingsSaved">
  <yk-search-action id="agent-add-action" label="Add Agent" icon="bi-plus-square-fill" color="#198754"
    @action="onAddAction"></yk-search-action>
  <yk-search-action id="agent-edit-action" label="Edit" icon="bi-pencil-fill" color="#fd7e14"
    :row-action="true" @action="onEditAction"></yk-search-action>
  <yk-search-action id="agent-delete-action" label="Delete" icon="bi-trash-fill" color="#dc3545"
    :row-action="true" @action="onDeleteAction"></yk-search-action>
</yk-search-results>
```

```js
onAddAction() { /* open create dialog */ },
onEditAction({ row }) { /* load + edit row.id */ },
onDeleteAction({ row }) { /* confirm + delete row.id */ },
onPageChange(pageNumber) { this.$refs.searchForm.gotoPage(pageNumber); },
onSettingsSaved() { this.$refs.searchForm.refreshSearch(); }
```

Also set `title` and `query-name` on `yk-search-results` (settings gear needs `query-name`). Product examples: Twister Agents / LLM Models / Providers.

### 6.4 Field customizers (`yk-field-customizer`)

Child of `yk-search-results`. `field` must match a result column name. Default slot: `{ value, row }`.

### 6.5 Search settings dialog

Gear on `yk-search-results` (needs `query-name`):

1. Load `GET /api/search/settings/read/{queryName}`
2. Save `POST /api/search/settings/saveOrUpdate`

Dialog supports:

- Page size (**1–1000**)
- Enable/disable display of non-backend columns (backend / required columns stay on)
- Reorder columns (up/down)

Persisted per **user + query**. Execute then applies that page size and column visibility/order. On `@settings-saved`, call `searchForm.refreshSearch()`.

Reference: `webutils-testapp/services/web/widgets/search-demo.html` (+ `.js`). Product example: Sethu4U employer applications (`employerApplicationSearch`). Backend details: [services.md](./services.md) §6.

---

## 7. Auth on the client

| Step | Detail |
|------|--------|
| Login | Store token in `sessionStorage.authToken` |
| Calls | `$restService` adds `Authorization: Bearer …` unless `includeAuthToken: false` |
| Missing token | APIs fail; redirect via `app.login.uri` / login page |

**testapp:** `/login/login.html` → `POST /api/testapp/auth/login` → `test@test.com` / `test` / space `test`.  
**Product:** prefer framework `/api/auth/*` (e.g. Sethu4U `s4u-login` widget).

Always log in before exercising authenticated widget demos.

---

## 8. OTP UI

- Model uses `@Otp` + `OtpVerification`.
- Widget: `yk-ver-input-field`.
- Demo: `/widgets/otp-demo.html`.
- Dev: with `app.devEnvironment=true`, read OTP from `FORM_TOKEN` (no email/SMS).

---

## 9. SPA / shell patterns (product apps)

### HTML / JS separation (required for standard pages)

Keep Vue markup and logic in separate files. Do **not** embed feature/page markup as `template: \`...\`` in JS.

| File | Role |
|------|------|
| `{feature}.html` | Markup only — full page shell **or** a Vue fragment (root element(s), no `<html>`/`<head>`) |
| `{feature}.js` | Logic only — `data`, `methods`, `components`, lifecycle; **no** `template` property for standard pages |

### Top-level nav tabs (`yk-route-nav-bar`)

Preferred pattern (Sethu4U admin / Twister dashboard):

1. Shell page: `{shell}.html` + `{shell}.js` mounts `Webutils.newVueApp`.
2. Nav items declare `componentName`, `uri` (HTML fragment), `script` (JS module), `route`, `label` — **not** preloaded `componentDef`.
3. `yk-route-nav-bar` lazy-loads on first click: `import(script)` + `fetch(uri)`, then sets `componentDef.template`.
4. Shell content: `<component :is="activeComponent"></component>`.
5. On `@nav-changed`, set `activeComponent = markRaw(navItem.componentDef)` (the loaded component **object**; `markRaw` avoids Vue’s reactive-component warning).
6. Hash routing owned by nav bar — do not navigate to separate `.html` files for in-app sections.

```js
// shell.js — nav items
{
  id: "navAgents",
  label: "Agents",
  route: "agents",
  componentName: "agents-content",
  uri: "/agents/agents.html",
  script: "/agents/agents.js"
}

// shell onNavChange — import { markRaw } from "/lib/vue-3.4.31/vue.esm-browser.js"
onNavChange: function(navItem) {
  this.activeComponent = navItem.componentDef
    ? markRaw(navItem.componentDef)
    : null;
}
```

```html
<!-- shell.html content area -->
<component :is="activeComponent"></component>
```

```js
// agents.js — logic only; template comes from agents.html via the nav bar
export default {
  data: function() { return { /* … */ }; },
  methods: { /* … */ }
};
```

### Nested side-bar children

`yk-route-side-bar` does **not** lazy-load `uri`/`script`. When a parent must statically import children, attach HTML with `$restService.fetchHtml`:

```js
import {$restService} from "/lib/webutils/rest-service.js";

const ProvidersContent = {
  data: function() { return { /* … */ }; },
  methods: { /* … */ }
};

ProvidersContent.template = await $restService.fetchHtml("/llm/providers.html");
export default ProvidersContent;
```

Parent registers imported children under `components` and swaps them with `v-if` or `<component :is>`. Nested layout markup still lives in the parent’s sibling `.html` (loaded by top-level nav when applicable).

### App vs framework widgets

| DO | DON'T |
|----|-------|
| Put product widgets under `web/common/widgets/` | Register product widgets inside framework `addDefaultComponents` |
| Export `registerXWidgets(app)` and call after `newVueApp` | Fork/copy `web/lib/webutils` into the app |
| Keep theme in `web/common/app.css` | Duplicate framework styles in every module CSS |
| Keep feature markup in `.html` | Inline `template:` strings for standard SPA panels |

---

## 10. CSS organization

| Location | Contents |
|----------|----------|
| `/lib/webutils/webutils.css` | Framework widget styles |
| `/common/app.css` | App-wide theme (shared buttons, layout, form chrome) |
| Module CSS | **Only** page/module-specific rules |

If a style is used in 2+ modules, move it to `app.css`.

---

## 11. Automation / IDs

- Give stable `id`s to actionable elements (submit buttons, search forms, nav items, page titles).
- Examples: `editable-lov-demo-submit-btn`, `sample-item-search-form`, `empJobs`.
- Prefer browser QA of the happy path before AutoX UI suites (product rule).

---

## 12. Coding DO / DON'T

### DO

| Practice | Example |
|----------|---------|
| Junction `/lib` to framework | testapp + Sethu4U |
| `newVueApp` + `#ykApp` + `#webutilsPageLoading` + `yk-dialogs` | All demos |
| Listings via `yk-search-form` / `yk-search-results` + child `yk-search-action` (not page-level Add/Edit/Delete) | `search-demo`, Twister agents/LLM pages |
| `@Markdown` / `@Language` for rich string fields | `markdown-demo`, `language-demo` |
| `String` + `@LOV` → editable LOV | `EditableLovDemoModel` |
| `Long` + `@LOV` → simple LOV | `SimpleLovDemoModel` |
| `$restService` for API calls | Demo submit handlers |
| Stable automation ids | `id="…"` on controls |
| Markup in `.html`, logic in `.js` | Top-level `uri`/`script` nav; nested `fetchHtml` |
| Shell `<component :is="activeComponent">` | After `yk-route-nav-bar` loads `componentDef` |

### DON'T

| Anti-pattern | Why |
|--------------|-----|
| Hand-rolled listing tables | Breaks search settings / conventions |
| Separate Add/Edit/Delete buttons outside `yk-search-results` | Use child `yk-search-action` (global vs `:row-action="true"`) |
| Skip Bootstrap Icons CSS when using search actions | Icons will not render |
| Copy `web/lib` into git | Drift; use junction + gitignore |
| Skip login before authenticated demos | 401 / interceptor failures |
| Put Sethu4U (or any product) widgets into WebUtils defaults | Coupling |
| Invent new field widgets without checking `input-fields.js` / `model-def-service.js` | Duplicate framework capability |
| Inline `template: \`...\`` for standard SPA feature panels | Keep HTML/JS separate; use `uri`/`script` or `fetchHtml` |

---

## 13. Agent page recipe

1. Ensure `web/lib` junction exists and CSS/JS libs load.
2. Add HTML skeleton with loading + `#ykApp` + `yk-dialogs` (page shell) **or** an HTML fragment + logic-only JS for SPA panels.
3. Keep markup in `.html` and logic in `.js` — top-level tabs via `uri`/`script`; nested children via `fetchHtml`.
4. For forms: prefer `yk-model-form` / `yk-model-form-dialog` when a simple grid is enough; use dynamic layout (`yk-model-field`, §5.1) for tabs, sections, or large single-field panes.
5. For tables: add `@SearchQueryMethod` backend first, then `yk-search-form` / `yk-search-results` with matching `query-name`, wire `@page-change` / `@settings-saved`, and put Add/Edit/Delete on `yk-search-action` children.
6. Use `$restService.invokePost` / `invokeGet` with `context: this` and field-error mapping.
7. Confirm login + token before testing.
8. Use testapp demos as templates when unsure:

| Demo URL | Covers |
|----------|--------|
| `/widgets/editable-lov-demo.html` | Editable LOV + model form submit |
| `/widgets/multi-editable-lov-demo.html` | Multi editable LOV + TEMP_TABLE.CATEGORIES persist |
| `/widgets/simple-lov-demo.html` | Id LOV |
| `/widgets/otp-demo.html` | OTP fields |
| `/widgets/search-demo.html` | Search form + results + global/row actions + settings |
| `/widgets/markdown-demo.html` | Markdown edit / split / preview + sync scroll |
| `/widgets/language-demo.html` | Language editor — `JSON`, `XML`, `JSON_SCHEMA`, `PYTHON` |

Login first: `/login/login.html`.
