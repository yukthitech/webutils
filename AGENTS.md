# WebUtils — Agent Context

## Overview

WebUtils is a multi-tier framework based on Spring that provides common functionality required by REST-based web applications. The framework expects certain default database tables to be maintained by each consuming application (schema versioning via Liquibase).

## Modules

### New framework (target for new projects)

| Module | Purpose |
|--------|---------|
| `webutils-common` | Shared models, form annotations, validators, response types (`com.webutils.common`) |
| `webutils-services` | Spring services, REST controllers, repositories, auth, search, mail, LOV (`com.webutils.services`); also hosts centralized UI assets under `web/lib/` |
| `webutils-testapp` | Isolated harness for testing new-stack UI widgets (editable-lov, multi-editable-lov, OTP, search) with Liquibase + AutoX scaffold — not a product app. Login at `/login/login.html` as `test@test.com` / `test` (user space `test`) before using demos. |

Built with Java 25 and Spring Boot 4.x. There is **no** shared Maven parent POM for these new modules (each is standalone / testapp has its own parent). Excludes legacy functionality and follows new patterns.

### Legacy framework (maintained for existing apps)

| Module | Purpose |
|--------|---------|
| `Commons` | Request/response POJOs and shared contracts (`com.yukthitech.webutils.common`) |
| `Services` | Backend services, generic CRUD, auth, search, mail (`com.yukthitech.webutils`) |
| `Client` | Base Java client library for application-specific clients |
| `WebUtils` | Parent POM; `dbschema/` (legacy Liquibase `WEBUTILS_*` tables), `vue-based/` UI framework |
| `TestWebApp` | Sample/test application for the **legacy** stack (not the same as `webutils-testapp`) |

Built with Java 17. See `WebUtils/README.MD` for full documentation.

## UI assets (centralized while stabilizing)

Framework static libraries (Vue, Bootstrap, `webutils/*.js`) live in **`webutils-services/web/lib`**. Consuming apps (Sethu4U, `webutils-testapp`, etc.) create a **symlink/junction** at `{app}/web/lib` → that folder so Maven/Spring treat paths as local while changes stay centralized. `{app}/web/.gitignore` typically ignores `/lib/`.

## Database schema for the new stack

New entities use tables such as `USER`, `STORED_LOV`, `STORED_LOV_OPTION`, `FORM_TOKEN`, `AUTH_TOKEN`, `WEBUTILS_SEARCH_SETTINGS` — **not** the legacy `WEBUTILS_USERS` / `WEBUTILS_STORED_LOV*` names in `WebUtils/dbschema`. Each consuming app owns Liquibase under its own `dbschema/` module (see Sethu4U and `webutils-testapp/dbschema`).

## Working in this repo

- **New features and refactors** → `webutils-common` / `webutils-services`
- **Isolated widget / AutoX testing (new stack)** → `webutils-testapp`
- **Legacy behavior reference or migration** → `Commons` / `Services` / `Client` / `WebUtils` / `TestWebApp`
- **Package namespace**: new code uses `com.webutils.*`; legacy uses `com.yukthitech.webutils.*`

### Entity Lombok annotations

Do **not** use Lombok `@Data` on persistence entities (or embedded subentities). `@Data` generates `equals`/`hashCode`/`toString` that can recurse through bidirectional associations and cause stack overflows.

Use `@Getter` and `@Setter` instead (optionally with `@NoArgsConstructor` / `@Accessors` as needed). `@Data` remains fine for DTOs, models, and other non-entity POJOs.

### webutils-testapp login

APIs require a session. Open `/login/login.html` and sign in as:

- **Username:** `test@test.com`
- **Password:** `test`
- **User space:** `test`

Then use `/index.html` and the widget demo pages.
