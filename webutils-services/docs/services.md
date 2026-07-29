# WebUtils — Services (Backend)

> **Audience:** Cursor agents implementing or changing backend features on the new stack.  
> **Related:** [setup-and-configuration.md](./setup-and-configuration.md) · [ui-widgets.md](./ui-widgets.md)

## When to read this

- Adding entities, repositories, controllers, or `@Model` DTOs
- Implementing search listings, LOV, OTP, or auth-related APIs
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

## 5. Repository + search pattern

### End-to-end listing (required for UI tables)

1. **Query model** in `{app}-common` — `@Model` + `@Condition` (+ optional `@LOV` filters).
2. **Result model** in `{app}-common` — `@Model` + `@Field` + `@SearchFieldInfo` for columns.
3. **Repository method** with `@SearchQueryMethod(name, queryModel)`.
4. **UI** — `yk-search-form` `query-name` matching that name + `yk-search-results`.

```java
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

### Context filters

- **Dynamic** (from session): `@ContextAttribute("currentUser.employerId")` on the query field.
- **Fixed literal:** prefer field default + `@Setter(AccessLevel.NONE)` rather than FreeMarker string literals in `@ContextAttribute`.

### Search HTTP (framework)

- `GET /api/search/{name}/query/def`
- `GET /api/search/{name}/result/def`
- `GET /api/search/execute/{name}`
- `GET /api/search/export/{name}`

Ensure `app.classScanner.packagesToScan` includes packages that contain `@SearchQueryMethod` repos / models.

---

## 6. Model / form annotations

Annotate DTOs with `@Model(name = "…")`. Scanned by `ModelService` → served as `/api/model/{name}` for `yk-model-form`.

Common annotations (`com.webutils.common.form.annotations`):

| Annotation | Effect |
|------------|--------|
| `@Label` / `@Description` | UI label / help |
| `@LOV` | LOV binding (see below) |
| `@Password` / `@MultilineText` / `@Html` | Field widgets |
| `@File` / `@Image` | File/image fields |
| `@DateTime` / `@Color` | Specialized inputs |
| `@ReadOnly` / `@NonDisplayable` / `@IgnoreField` | Visibility |
| `@DefaultValue` / `@Conditional` / `@FullWidth` / `@Format` | Layout / defaults |
| `@Otp` | OTP verification field (`OtpVerification` type) |
| `@SearchFieldInfo` | Search result column metadata |
| `@ContextAttribute` | Inject search context values |

Validation: Yukthi (`@Required`, `@MaxLen`, …) and/or Jakarta (`@Valid` on controllers).

---

## 7. LOV patterns

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

## 8. OTP

- Model field: `OtpVerification` + `@Otp(type = EMAIL | MOBILE)`.
- Send API: `POST /api/otp/send/{fieldId}/{value}`.
- With `app.devEnvironment=true`, delivery is skipped; read code from `FORM_TOKEN` for local tests.
- UI widget: `yk-ver-input-field` (field type VERIFICATION).

---

## 9. Auth / user integration

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

## 10. Responses and errors

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

## 11. Mail (optional)

- Configure `webutils.email.*` → `EmailServerSettings` + `EmailService`.
- Optional DB templates: `WEBUTILS_MAIL_TEMPLATE`.
- OTP email template: `webutils.email.verification.template`.

---

## 12. Coding DO / DON'T

### DO

| Practice | Example |
|----------|---------|
| `@Getter`/`@Setter` on entities | `UserEntity`, `SampleItemEntity` |
| `@Data` + `@Model` on DTOs | `EditableLovDemoModel` |
| Search for listings | `sampleItemSearch`, employer application searches |
| `processModel` before LOV persist | `EditableLovDemoService` |
| Implement `IWebutilsService` | `TestAppWebutilsService`, Sethu4U `WebutilsService` |
| Log with Log4j2 parameterized messages | `logger.info("… {}", id)` |
| Batch / avoid N+1 | Fetch by id sets, then map |

### DON'T

| Anti-pattern | Why |
|--------------|-----|
| `@Data` on entities / embedded subentities | Association recursion |
| Custom list REST for UI grids | Breaks search widgets / settings |
| Enable Spring Liquibase in services | Schema owned by `dbschema` module |
| Nested duplicate `scanBasePackages` | Double repo registration |
| Legacy table/package names | Wrong schema (`WEBUTILS_USERS` vs `USER`) |
| Log passwords / tokens | Security |
| Put product-only widgets into framework defaults | Keep app widgets in app `web/common/widgets` |

---

## 13. Agent implementation recipe

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
