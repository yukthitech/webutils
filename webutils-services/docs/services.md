# WebUtils — Services (Backend)

> **Audience:** Cursor agents implementing or changing backend features on the new stack.  
> **Related:** [setup-and-configuration.md](./setup-and-configuration.md) · [ui-widgets.md](./ui-widgets.md)

## When to read this

- Adding entities, repositories, controllers, or `@Model` DTOs
- Implementing search listings, LOV, OTP, Markdown/Language fields, or auth-related APIs
- Choosing response types / exception patterns
- Deciding where code lives (`*-common` vs `*-services`)

---

## 1. Package map

### Framework (`webutils-services`)

| Package | Responsibility |
|---------|----------------|
| `com.webutils.services.auth` | Login/logout, `SecurityInterceptor`, `UserContext` |
| `com.webutils.services.user` | `USER`, preferences, user APIs |
| `com.webutils.services.token` | `AUTH_TOKEN` |
| `com.webutils.services.search` | Search execute/export, `@SearchQueryMethod`, settings |
| `com.webutils.services.form.model` | Model definitions for UI forms |
| `com.webutils.services.form.lov` (+ `.stored`) | LOV fetch + stored LOV persistence |
| `com.webutils.services.form.otp` | OTP send/verify |
| `com.webutils.services.form.captcha` | Captcha |
| `com.webutils.services.form.token` | `FORM_TOKEN` / `TokenManager` |
| `com.webutils.services.mail` | Email send + optional templates |
| `com.webutils.services.payment` | Razorpay |
| `com.webutils.services.common` | `IWebutilsService`, `WebutilsServiceSupport`, `GlobalExceptionHandler`, `ClassScannerService` |

Also shipped in services jar: `com.webutils.common.repo.EnableRepositories`, `com.webutils.common.IWebUtilsConstants`.

### Shared contracts (`webutils-common`)

Place app-shared types that UI and services both need here (or in `{app}-common`):

- `@Model` form/search DTOs
- Response wrappers under `com.webutils.common.response`
- Form annotations under `com.webutils.common.form.annotations`
- Search / OTP / captcha / payment / mail model types

### App placement

| Layer | Module | Examples |
|-------|--------|----------|
| `@Model` DTOs, search query/result | `{app}-common` | `EditableLovDemoModel`, `SampleItemSearchQuery` |
| Entity, `I*Repository`, service, controller | `{app}-services` | `SampleItemEntity`, `ISampleItemRepository` |
| Liquibase | `{app}-dbschema` | `001-create-…xml` |

---

## 2. Hard rules (agents)

| Rule | Detail |
|------|--------|
| **No `@Data` on entities** | Use `@Getter` + `@Setter` (+ `@NoArgsConstructor` / `@Accessors(chain = true)`). `@Data` recurses through associations → stack overflow. |
| **`@Data` OK on models** | DTOs / `@Model` classes may use `@Data`. |
| **Never return raw entities as public API contract without care** | Prefer `BasicReadResponse<T>`, dedicated `*Response` / `*Model`. |
| **UI listings = search** | `@SearchQueryMethod` + query/result models. Do **not** add custom “list all” REST for page tables. |
| **Search column order = declaration order** | Do **not** add `@SearchFieldInfo(order = …)` just to order columns. Use `@SearchFieldInfo` only for non-default metadata (e.g. `resultType`). |
| **DTO ↔ entity via `CopyUtils`** | Use `CopyUtils.modelToEntity` / `CopyUtils.entityToModel` instead of hand-written property setters. Name FK model fields `{association}Id` (e.g. `llmModelId` ↔ `llmModel`). |
| **Editable LOV before persist** | Call `webutilsServiceSupport.processModel(model, null)` so options map/create correctly. |
| **Repository naming** | `I{Name}Repository extends ICrudRepository<Entity>`. |
| **Tables/columns** | UPPER_CASE via `@Table` / `@Column`. |
| **Repo save vs update** | `save()` for new rows; `update()` / `@UpdateFunction` for existing. Prefer field-specific updates when possible. |

---

## 3. Controller pattern

Thin controllers: validate → service → response wrapper. Do not catch exceptions for business failures — use framework `GlobalExceptionHandler`.

```java
@RestController
@RequestMapping("/api/testapp/editable-lov-demo")
public class EditableLovDemoController {

    @Autowired
    private EditableLovDemoService service;

    @PostMapping("/submit")
    public BasicReadResponse<TempTableEntity> submit(@RequestBody @Valid EditableLovDemoModel model) {
        return new BasicReadResponse<>(service.submit(model));
    }
}
```

### Framework API prefixes (do not reinvent)

| Prefix | Purpose |
|--------|---------|
| `/api/auth/**` | Login / logout / session |
| `/api/search/**` | Query def, result def, execute, export |
| `/api/search/settings/**` | Search column settings |
| `/api/lov/fetch/{STATIC\|DYNAMIC\|STORED}/{name}` | LOV options |
| `/api/model/{name}` | Model field definitions for `yk-model-form` |
| `/api/otp/send/{fieldId}/{value}` | OTP send |
| `/api/user/**` | User APIs |
| `/api/file/**` | File download/upload helpers |
| `/api/payment/**` | Payment |

Auth annotations: `@NoAuthentication`, `@Authorization({"ROLE"})`.

---

## 4. Entity pattern

```java
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "SAMPLE_ITEM")
public class SampleItemEntity {
    @Column(name = "ID")
    @Id
    private Long id;

    @Column(name = "NAME")
    private String name;

    // … CREATED_ON / UPDATED_ON / CREATED_BY_ID / UPDATED_BY_ID when needed
}
```

Password fields: use Yukthi `@DataTypeMapping(converterType = PasswordEncryptionConverter.class)` (see `UserEntity`).

---

## 5. DTO ↔ entity conversion (`CopyUtils`)

Prefer `com.webutils.services.common.CopyUtils` over hand-mapping matching fields.

```java
AgentEntity entity = CopyUtils.modelToEntity(model, AgentEntity.class);
AgentModel model = CopyUtils.entityToModel(entity, AgentModel.class);
```

### Same-name properties

Copied automatically (with type conversion via `ConvertUtils` when needed).

### Association ↔ id fields

Beyond same-name copy, `CopyUtils` maps FK ids and `@Table` associations by naming convention:

| Direction | Model | Entity |
|-----------|--------|--------|
| Entity → model | `Long llmModelId` | `LlmModelEntity llmModel` → copies `llmModel.id` into `llmModelId` |
| Model → entity | `Long llmModelId` | Creates `llmModel` instance and sets its `id` |

Rule: name the model FK field `{associationProperty}Id` so it matches the entity association property plus `Id` (e.g. entity `llmProvider` ↔ model `llmProviderId`, not `providerId`).

### What to still set manually

- Audit / ownership not on the form model (`owner`, `createdOn`, `updatedOn`, `createdBy`, `updatedBy`)
- Fields needing normalization or validation beyond copy (defaults, maps that need defensive copies, etc.)
- On update: preserve `id` and create-audit from the existing row after `modelToEntity` (which builds a new instance)

```java
public void update(long id, AgentModel model) {
    AgentEntity existing = requireOwnedAgent(id, userId);
    AgentEntity entity = CopyUtils.modelToEntity(model, AgentEntity.class);
    entity.setId(id);
    entity.setOwner(existing.getOwner());
    entity.setCreatedOn(existing.getCreatedOn());
    entity.setCreatedBy(existing.getCreatedBy());
    entity.setUpdatedOn(new Date());
    entity.setUpdatedBy(new UserEntity(userId));
    agentRepository.update(entity);
}
```

---

## 6. Repository + search pattern

### End-to-end listing (required for UI tables)

1. **Query model** in `{app}-common` — `@Model` + `@Condition` (+ optional `@LOV` filters).
2. **Result model** in `{app}-common` — `@Model` + `@Field` (+ `@Label`) for columns. Column order follows **field declaration order**; do **not** add `@SearchFieldInfo(order = …)` just to order columns.
3. **Repository method** with `@SearchQueryMethod(name, queryModel)`.
4. **UI** — `yk-search-form` `query-name` matching that name + `yk-search-results`.

```java
// common — SampleItemSearchResult (order = declaration order)
@Data
@Model(name = "SampleItemSearchResult")
public class SampleItemSearchResult {
    @Field("id")
    @Label("Id")
    private Long id;

    @Field("name")
    @Label("Name")
    private String name;
}

// common — SampleItemSearchQuery
@Data
@Model(name = "SampleItemSearchQuery")
public class SampleItemSearchQuery {
    @Label("Name")
    @Condition(value = "name", op = Operator.LIKE, ignoreCase = true)
    private String name;

    @Label("Category")
    @LOV(name = "CATEGORY", type = DynLovType.STORED_TYPE, persist = false)
    @Condition("category")
    private String category;
}

// services — ISampleItemRepository
public interface ISampleItemRepository extends ICrudRepository<SampleItemEntity> {
    @SearchQueryMethod(name = "sampleItemSearch", queryModel = SampleItemSearchQuery.class)
    @OrderBy("name")
    List<SampleItemSearchResult> searchSampleItems(SearchQuery searchQuery);
}
```

### Search result columns

Search result columns follow **field declaration order** on the result model. Prefer declaring fields in the desired display order.

| Annotation / type | Use |
|-------------------|-----|
| Field declaration order | Default column order in results + settings |
| `@SearchFieldInfo(resultType = …)` | Special cell rendering — `SearchResultType.EMAIL`, `PHONE_NO`, or `NONE` (default) |
| `@NonDisplayable` | Hide from UI columns |
| `@NonDisplayable(backend = true)` | Hidden from display/settings toggles but kept as a backend key (e.g. row `id`) |

Use `@SearchFieldInfo` only for non-default metadata (e.g. `resultType`). Do **not** use `@SearchFieldInfo(order = …)` solely to control column order.

### Context filters

- **Dynamic** (from session): `@ContextAttribute("currentUser.employerId")` on the query field.
- **Fixed literal:** prefer field default + `@Setter(AccessLevel.NONE)` rather than FreeMarker string literals in `@ContextAttribute`.

### Search HTTP (framework)

| Method | Path | Purpose |
|--------|------|---------|
| GET | `/api/search/{name}/query/def` | Query model def for `yk-search-form` |
| GET | `/api/search/{name}/result/def` | Result model def |
| GET | `/api/search/execute/{name}` | Execute search (`queryModelJson`, `pageNumber`, `pageSize`, `fetchCount`) |
| GET | `/api/search/search/{name}` | Simple-search variant (same params; used when UI `simple-search` is true) |
| GET | `/api/search/export/{name}` | Export |
| GET | `/api/search/settings/read/{queryName}` | Per-user settings (or defaults) |
| POST | `/api/search/settings/saveOrUpdate` | Persist page size + column visibility/order |
| POST | `/api/search/settings/save` | Insert settings |
| POST | `/api/search/settings/update` | Update settings |
| DELETE | `/api/search/settings/delete/{queryName}` | Delete user settings for query |
| DELETE | `/api/search/settings/deleteAll` | Delete all settings for current user |

**Page size resolution** (execute): persisted user settings (row with id) → request `pageSize` → settings default → framework default. Valid page size is **1–1000** (`SearchSettingsModel`).

Ensure `app.classScanner.packagesToScan` includes packages that contain `@SearchQueryMethod` repos / models.

Reference: `webutils-testapp` `sampleItemSearch` (`SampleItemSearchQuery` / `SampleItemSearchResult`).

---

## 7. Model / form annotations

Annotate DTOs with `@Model(name = "…")`. Scanned by `ModelService` → served as `/api/model/{name}` for `yk-model-form`.

Common annotations (`com.webutils.common.form.annotations`):

| Annotation | Effect |
|------------|--------|
| `@Label` / `@Description` | UI label / help |
| `@LOV` | LOV binding (see below) |
| `@Password` / `@MultilineText` / `@Html` | Field widgets |
| `@Markdown` | Markdown string → `FieldType.MARKDOWN` → `yk-markdown-editor` (full width) |
| `@Language(LanguageType.…)` | Language string → `FieldType.LANGUAGE` → `yk-language-editor` (full width); **server-validated** |
| `@File` / `@Image` | File/image fields |
| `@DateTime` / `@Color` | Specialized inputs |
| `@ReadOnly` / `@NonDisplayable` / `@IgnoreField` | Visibility |
| `@DefaultValue` / `@Conditional` / `@FullWidth` / `@Format` | Layout / defaults |
| `@Otp` | OTP verification field (`OtpVerification` type) |
| `@SearchFieldInfo` | Optional search result column metadata (e.g. `resultType`). **Not** needed for column order — declaration order is used |
| `@ContextAttribute` | Inject search context values |

Validation: Yukthi (`@Required`, `@MaxLen`, …) and/or Jakarta (`@Valid` on controllers). `@Language` is a Jakarta `@Constraint` (`LanguageValidator`).

### Markdown fields (`@Markdown`)

Marker annotation on a `String` field. No server-side markdown parsing — store/return the raw string. `FieldDefBuilder` sets `FieldType.MARKDOWN` and `fullWidth=true`.

```java
@Label("Content")
@Markdown
@Required
@MaxLen(10000)
private String content;
```

Demo: `MarkdownDemoModel` → `/widgets/markdown-demo.html`.

### Language fields (`@Language`)

Annotate a `String` with a required `LanguageType`. Content is validated on `@Valid` submit:

| `LanguageType` | Server validation | UI CodeMirror mode |
|----------------|-------------------|--------------------|
| `JSON` | Parse as JSON (`ObjectMapper`) | `javascript` + `json: true` |
| `XML` | Secure XML parse (XXE hardened) | `xml` |
| `JSON_SCHEMA` | Parse JSON + validate against JSON Schema meta-schema (draft 2020-12 via networknt) | `javascript` + `json: true` |

Blank / null values skip language validation (combine with `@Required` when empty is not allowed).

```java
@Label("JSON")
@Language(LanguageType.JSON)
@Required
private String jsonContent;

@Label("XML")
@Language(LanguageType.XML)
private String xmlContent;

@Label("JSON Schema")
@Language(LanguageType.JSON_SCHEMA)
private String jsonSchemaContent;
```

`FieldDefBuilder` sets `FieldType.LANGUAGE`, `languageType`, and `fullWidth=true`. Demo: `LanguageDemoModel` → `/widgets/language-demo.html`.

---

## 8. LOV patterns

| Java field | `@LOV` | UI widget | Behavior |
|------------|--------|-----------|----------|
| `Long` option id | `name=…, type=STORED_TYPE` | `yk-lov-field` | Selects existing option id |
| `String` | same | `yk-editable-lov-field` | Type-ahead; may create `STORED_LOV_OPTION` |
| `Collection<String>` | same | `yk-multi-editable-lov-field` | Multi editable |
| Dynamic | `type=DYNAMIC_TYPE` + scanned provider | `yk-lov-field` | Method-provided options |
| Search filter | `persist = false` | LOV on search form | Do not create options on filter |

Fetch: `GET /api/lov/fetch/{STATIC|DYNAMIC|STORED}/{name}`.

### Persist editable LOV (required)

```java
@Service
public class EditableLovDemoService {
    @Autowired
    private WebutilsServiceSupport webutilsServiceSupport;

    public TempTableEntity submit(EditableLovDemoModel model) {
        webutilsServiceSupport.processModel(model, null); // map or create option
        // then save entity using remapped model values
        …
    }
}
```

References: `EditableLovDemoModel` (`String`), `SimpleLovDemoModel` (`Long`).

---

## 9. OTP

- Model field: `OtpVerification` + `@Otp(type = EMAIL | MOBILE)`.
- Send API: `POST /api/otp/send/{fieldId}/{value}`.
- With `app.devEnvironment=true`, delivery is skipped; read code from `FORM_TOKEN` for local tests.
- UI widget: `yk-ver-input-field` (field type VERIFICATION).

---

## 10. Auth / user integration

Implement `IWebutilsService` in the app:

```java
@Service
public class TestAppWebutilsService implements IWebutilsService {
    // Override getUserDetails(UserEntity) to attach roles / domain ids when needed
}
```

Use `UserContext.getCurrentUser()` inside services for the authenticated principal.

Session storage: `AUTH_TOKEN` via `AuthTokenService`. Product login: `/api/auth/login`.

---

## 11. Responses and errors

| Type | Use |
|------|-----|
| `BaseResponse` | Success flag + message |
| `BasicReadResponse<T>` | Single `value` |
| `BasicSaveResponse` | Save acknowledgements |
| `BasicListResponse<T>` | Ad-hoc lists (prefer search for UI tables) |
| `ExecuteSearchResponse` | Search execute payload |

Throwables handled by `GlobalExceptionHandler`:

- `InvalidRequestException`
- `UnauthenticatedRequestException`
- `UnauthorizedRequestException`
- `BeanValidationException`

Response shape includes `success`, `message`, `errors` / field errors as applicable.

---

## 12. Mail (optional)

- Configure `webutils.email.*` → `EmailServerSettings` + `EmailService`.
- Optional DB templates: `WEBUTILS_MAIL_TEMPLATE`.
- OTP email template: `webutils.email.verification.template`.

---

## 13. Coding DO / DON'T

### DO

| Practice | Example |
|----------|---------|
| `@Getter`/`@Setter` on entities | `UserEntity`, `SampleItemEntity` |
| `@Data` + `@Model` on DTOs | `EditableLovDemoModel` |
| `CopyUtils` for model ↔ entity | `AgentService`, `LlmModelService` — FK fields named `{association}Id` |
| Search for listings | `sampleItemSearch`, employer application searches |
| `processModel` before LOV persist | `EditableLovDemoService` |
| Implement `IWebutilsService` | `TestAppWebutilsService`, Sethu4U `WebutilsService` |
| Log with Log4j2 parameterized messages | `logger.info("… {}", id)` |
| Batch / avoid N+1 | Fetch by id sets, then map |

### DON'T

| Anti-pattern | Why |
|--------------|-----|
| Hand-map every DTO ↔ entity field | Use `CopyUtils`; keep only audit / special-case logic manual |
| `@Data` on entities / embedded subentities | Association recursion |
| Custom list REST for UI grids | Breaks search widgets / settings |
| Enable Spring Liquibase in services | Schema owned by `dbschema` module |
| Nested duplicate `scanBasePackages` | Double repo registration |
| Legacy table/package names | Wrong schema (`WEBUTILS_USERS` vs `USER`) |
| Log passwords / tokens | Security |
| Put product-only widgets into framework defaults | Keep app widgets in app `web/common/widgets` |

---

## 14. Agent implementation recipe

When adding a new searchable listing feature:

1. Create `{Feature}SearchQuery` + `{Feature}SearchResult` in `{app}-common` with `@Model`.
2. Add entity + `I{Feature}Repository` with `@SearchQueryMethod(name = "…")`.
3. Add Liquibase table if new (in app `dbschema`).
4. Build UI with `yk-search-form` / `yk-search-results` using the same query name.
5. Scope with `@ContextAttribute` or fixed defaults — no hand-rolled filter APIs for the table.
6. Verify `app.classScanner.packagesToScan` includes the new packages.

When adding a model-driven form:

1. `@Model` DTO with field annotations in `{app}-common`.
2. Controller accepts `@Valid` model; service may call `processModel` for LOV/files.
3. UI: `yk-model-form model-name="YourModel"` or explicit field components.
4. Return `BasicReadResponse` / `BasicSaveResponse`.
5. For rich text: `@Markdown` (preview only on client) or `@Language(LanguageType.…)` (validated on server).
