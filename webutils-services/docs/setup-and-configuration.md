# WebUtils — Setup and Configuration

> **Audience:** Cursor agents and developers wiring a new or existing app onto `webutils-common` + `webutils-services`.  
> **Related:** [services.md](./services.md) · [ui-widgets.md](./ui-widgets.md)


## When to read this

- Adding WebUtils to a consuming app
- Fixing startup, auth, DB, or static UI loading
- Creating/updating Liquibase for framework tables
- Setting up `web/lib` junction

---

## 1. Modules and coordinates

| Artifact | Maven | Package | Role |
|----------|-------|---------|------|
| Models / annotations / responses | `com.yukthitech:webutils-common:1.0.0-SNAPSHOT` | `com.webutils.common` | Shared DTOs, `@Model`, form/search annotations |
| Backend + UI libs | `com.yukthitech:webutils-services:1.0.0-SNAPSHOT` | `com.webutils.services` | Controllers, services, entities, `web/lib` |

**Target stack:** Java **25**, Spring Boot **4.0.2**. No shared WebUtils parent POM — each app owns its parent.

### Dependency split (required)

```
{app}-common     → webutils-common
{app}-services   → webutils-services (+ {app}-common)
{app}-dbschema   → Liquibase only (NOT a services Maven dependency)
```

Install framework jars locally before building the app:

```bash
# from webutils-common, then webutils-services
mvn clean install -DskipTests
```

---

## 2. Expected app layout

```
{app}/
  common/                 # @Model DTOs, search query/result, requests
  services/               # Spring Boot app, entities, repos, controllers, web/
    web/                  # static UI (file:./web/)
      lib/                # JUNCTION → webutils-services/web/lib (gitignored)
      common/             # app.css, app-config.js, app widgets
      login/
      …
  dbschema/               # Liquibase changelogs only
  automation/             # optional AutoX
  pom.xml / parent/
```

References: `webutils-testapp`, Sethu4U.

---

## 3. Spring Boot entry (checklist)

App `Application` class **must**:

1. Scan framework + app packages (avoid nested duplicates — see below).
2. Enable Yukthi repositories for both.
3. Provide `RepositoryFactory` bean from `app.db.*`.
4. Provide `Encryptor` bean from `app.encryptor.*`.
5. Provide `@Service implements IWebutilsService` (defaults OK; enrich for roles/domain ids).

### Scan packages — DO / DON'T

```java
// DO (product app with its own package)
@SpringBootApplication(scanBasePackages = {"com.sethu4u", "com.webutils"})
@EnableRepositories(basePackages = {"com.sethu4u", "com.webutils"})

// DO (app code already under com.webutils.* — list once)
@SpringBootApplication(scanBasePackages = {"com.webutils"})
@EnableRepositories(basePackages = {"com.webutils"})

// DON'T — nested packages cause double repository registration
@SpringBootApplication(scanBasePackages = {"com.webutils", "com.webutils.testapp"})
```

`@EnableRepositories` is `com.webutils.common.repo.EnableRepositories` (shipped in **webutils-services** jar).

### Required beans (pattern)

```java
@Bean
public RepositoryFactory buildRepositoryFactory() {
    BasicDataSource dataSource = new BasicDataSource();
    dataSource.setUrl(dbUrl);
    dataSource.setUsername(dbUsername);
    dataSource.setPassword(dbPassword);
    dataSource.setDriverClassName(dbDriver);

    RdbmsDataStore dataStore = new RdbmsDataStore(dbType);
    dataStore.setDataSource(dataSource);

    RepositoryFactory factory = new RepositoryFactory();
    factory.setDataStore(dataStore);
    return factory;
}

@Bean
public Encryptor buildEncryptor() {
    return new Encryptor(keyStoreFile, keyStoreAlias, keyStorePassword);
}
```

Also: `spring.main.allow-circular-references=true` (framework wiring).

---

## 4. application.properties (canonical keys)

Serve UI from the services module working directory:

```properties
spring.web.resources.static-locations=file:./web/
spring.liquibase.enabled=false
spring.main.allow-circular-references=true
```

| Key | Purpose |
|-----|---------|
| `app.db.url` / `username` / `password` / `driver` / `type` | Yukthi `RepositoryFactory` (e.g. `type=mysql`) |
| `app.session.timeout.seconds` | Auth token lifetime |
| `app.session.renewal.seconds` | Sliding renewal window |
| `app.login.uri` | Unauthenticated UI redirect (e.g. `/login/login.html`) |
| `app.webutils.userSpaceEnabled` | When `true`, login must send `userSpace` |
| `app.classScanner.packagesToScan` | Models, `@SearchQueryMethod`, dynamic LOVs — e.g. `com.webutils` or `com.sethu4u, com.webutils` |
| `app.devEnvironment` | `true` skips OTP email/SMS delivery (codes stay in `FORM_TOKEN`) |
| `app.encryptor.keystore` / `alias` / `password` | `Encryptor` bean (or override via `-D`) |
| `webutils.email.*` | SMTP for `EmailServerSettings` / OTP |
| `webutils.email.verification.template` | OTP email template path |
| `webutils.payment.razorpay.*` | Payment (if used) |

Reference file: `webutils-testapp/services/src/main/resources/application.properties`.

### Encryptor JVM overrides (product apps)

Often preferred over committing secrets:

```
-Dapp.encryptor.keystore=…
-Dapp.encryptor.alias=…
-Dapp.encryptor.password=…
```

---

## 5. Liquibase (app-owned schema)

### Rules (agents must follow)

1. **`spring.liquibase.enabled=false`** in services.
2. Do **not** add `dbschema` as a Maven dependency of `services`.
3. Run migrations from `dbschema/`:

```bash
mvn liquibase:dropAll liquibase:update   # early/dev reset
mvn process-resources liquibase:update   # incremental
```

4. New-stack table names ≠ legacy `WEBUTILS_USERS` / `WEBUTILS_STORED_LOV*`.

### Required framework tables

Define in the app’s changelog (see `webutils-testapp/dbschema/.../001-create-webutils-common-tables.xml`):

| Table | Purpose |
|-------|---------|
| `USER` | Auth identity; unique `(EMAIL, CUSTOM_SPACE)`, `(MOBILE, CUSTOM_SPACE)` |
| `USER_PREFERENCES` | Per-user prefs |
| `AUTH_TOKEN` | Session tokens |
| `STORED_LOV` | Named LOV definitions |
| `STORED_LOV_OPTION` | LOV options (editable LOV may create rows) |
| `FORM_TOKEN` | Captcha / OTP short-lived tokens |

Include when the matching framework features are used:

| Table | Feature |
|-------|---------|
| `WEBUTILS_SEARCH_SETTINGS` | Column prefs for search results |
| `WEBUTILS_MAIL_TEMPLATE` | DB-backed mail templates |
| `PAYMENT_ORDER` / `PAYMENT_WEBHOOK_LOG` | Razorpay payment module |

**Greenfield style:** fold column changes into the original create changeset; prefer `dropAll` + `update` during early development rather than long alter chains.

---

## 6. UI lib junction (`web/lib`)

| | Path |
|-|------|
| Source of truth | `webutils-services/web/lib` |
| App path | `{app}/services/web/lib` |
| Gitignore | `{app}/services/web/.gitignore` → `/lib/` |

Windows (from `services/`):

```bat
mklink /J web\lib D:\Kranthi\github\webutils\webutils-services\web\lib
```

Unix:

```bash
ln -s /path/to/webutils-services/web/lib web/lib
```

**DO:** junction/symlink. **DON'T:** copy `lib` into the app repo.

Contents include: `vue-3.4.31`, `bootstrap-*`, `jquery-*`, `tinymce-*`, `webutils/` (framework JS/CSS).

---

## 7. Auth / session basics

| Mechanism | Detail |
|-----------|--------|
| Token header / cookie | `Authorization: Bearer <token>` or cookie `Authorization` |
| UI storage | `sessionStorage.authToken` |
| Framework login | `POST /api/auth/login` (password min length enforced) |
| Logout | `POST /api/auth/logout` |
| Public endpoints | `@NoAuthentication` |
| Role checks | `@Authorization({"ROLE"})` |
| Security model | Spring Security permits broadly; `SecurityInterceptor` enforces token |

When `app.webutils.userSpaceEnabled=true`, clients must send `userSpace` on login.

**testapp note:** short demo password uses `POST /api/testapp/auth/login` (`test@test.com` / `test` / space `test`). Product apps use framework `/api/auth/login`.

---

## 8. New consuming app — agent checklist

Copy this checklist when scaffolding or verifying setup:

- [ ] `{app}-common` depends on `webutils-common`
- [ ] `{app}-services` depends on `webutils-services` + app-common
- [ ] `Application` scans app + `com.webutils` without nested duplicate packages
- [ ] `@EnableRepositories` covers same packages
- [ ] `RepositoryFactory` + `Encryptor` beans present
- [ ] `@Service implements IWebutilsService` present
- [ ] `app.classScanner.packagesToScan` includes app model packages + `com.webutils`
- [ ] `spring.liquibase.enabled=false`; dbschema has framework tables
- [ ] `spring.web.resources.static-locations=file:./web/`
- [ ] `web/lib` junction to `webutils-services/web/lib`; `/lib/` gitignored
- [ ] Login page stores `sessionStorage.authToken`; `$restService` sends Bearer token
- [ ] `app.devEnvironment` / encryptor props set for local runs

---

## 9. Verification references

| What | Where |
|------|--------|
| Minimal harness | `webutils-testapp` (port **8091**) |
| Product wiring | Sethu4U `Sethu4uApplication`, `application.properties` |
| Framework agent overview | repo root `AGENTS.md` |

After local/agent testing that starts the app: **stop the process and free the port** so the developer can restart manually.
