# WebUtils — UI Widgets

> **Audience:** Cursor agents building Vue pages on WebUtils static UI.  
> **Related:** [setup-and-configuration.md](./setup-and-configuration.md) · [services.md](./services.md)

## When to read this

- Creating or fixing a page that uses WebUtils form/search/LOV/OTP widgets
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

1. Load jQuery / Bootstrap / bootstrap-select (and icons as needed).
2. Load `/lib/webutils/webutils.css` + app CSS.
3. Include `#webutilsPageLoading` and `#ykApp` (initially hidden).
4. Include `<yk-dialogs ref="ykDialogs"></yk-dialogs>` inside `#ykApp`.
5. Mount with `Webutils.newVueApp(…).mount("#ykApp")`.

### HTML skeleton

```html
<link href="/lib/bootstrap-5.1.0/css/bootstrap.min.css" rel="stylesheet">
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
| `input-fields.js` | Input / LOV / OTP / captcha / file / html / switch |
| `markdown-editor.js` | `yk-markdown-editor` (split edit / live preview) |
| `forms.js` | `yk-form`, `yk-model-form`, `yk-search-form`, `yk-search-results`, multi-row forms |
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
| MARKDOWN (`@Markdown`) | `yk-markdown-editor` |

### LOV choice (agents)

| Persist | Java type | Widget |
|---------|-----------|--------|
| Option **id** | `Long` + `@LOV(…, STORED_TYPE)` | `yk-lov-field` |
| Option **label** (create allowed) | `String` + `@LOV(…, STORED_TYPE)` | `yk-editable-lov-field` |
| Multi labels | `List<String>` + `@LOV` | `yk-multi-editable-lov-field` |
| Search filter only | `@LOV(…, persist = false)` | same widgets; no option create |

Demos: `webutils-testapp/services/web/widgets/editable-lov-demo.*`, `simple-lov-demo.*`, `markdown-demo.*`.

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

## 6. Search listings (mandatory for tables)

**Rule:** UI multi-row listings use WebUtils search — not custom list endpoints + hand-rolled tables.

```html
<yk-search-form
  ref="searchForm"
  query-name="sampleItemSearch"
  :column-count="2"
  @search="onSearch">
</yk-search-form>

<yk-search-results
  ref="results"
  @select="onSelectRow">
</yk-search-results>
```

```js
onSearch(searchResponse) {
  this.$refs.results.setSearchResults(searchResponse);
}
```

`query-name` **must** match `@SearchQueryMethod(name = "…")` on the repository.

Reference: `webutils-testapp/services/web/widgets/search-demo.html` (+ `.js`). Product example: Sethu4U employer applications (`employerApplicationSearch`).

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
5. On `@nav-changed`, set `activeComponent = navItem.componentDef` (the loaded component **object**).
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

// shell onNavChange
onNavChange: function(navItem) {
  this.activeComponent = navItem.componentDef;
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
| Listings via `yk-search-form` / `yk-search-results` | `search-demo`, employer applications |
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
5. For tables: add `@SearchQueryMethod` backend first, then `yk-search-form` / `yk-search-results` with matching `query-name`.
6. Use `$restService.invokePost` / `invokeGet` with `context: this` and field-error mapping.
7. Confirm login + token before testing.
8. Use testapp demos as templates when unsure:

| Demo URL | Covers |
|----------|--------|
| `/widgets/editable-lov-demo.html` | Editable LOV + model form submit |
| `/widgets/simple-lov-demo.html` | Id LOV |
| `/widgets/otp-demo.html` | OTP fields |
| `/widgets/search-demo.html` | Search form + results |
| `/widgets/markdown-demo.html` | Markdown edit / live preview |

Login first: `/login/login.html`.
