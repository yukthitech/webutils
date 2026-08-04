# WebUtils Docs (New Stack)

Agent-oriented documentation for **`webutils-common`** and **`webutils-services`** (`com.webutils.*`). Use these guides when building or changing consuming apps (Sethu4U, webutils-testapp, and future apps).


| Doc | Use when |
|-----|----------|
| [setup-and-configuration.md](./setup-and-configuration.md) | Wiring Maven, Spring Boot, Liquibase, `web/lib`, auth, properties |
| [services.md](./services.md) | Controllers, entities, repos, models, search, Markdown/Language fields, LOV, OTP, responses |
| [ui-widgets.md](./ui-widgets.md) | Vue pages, form/search/LOV, markdown & language editors, SPA shells, static assets |

## Scope

- **In scope:** new stack only (`com.webutils.*`, Java 25, Spring Boot 4.x).
- **Out of scope:** legacy `Commons` / `Services` / `Client` / `WebUtils` / `TestWebApp` (`com.yukthitech.webutils.*`). See `WebUtils/README.MD` for legacy.

## Reference apps

| App | Role |
|-----|------|
| `webutils-testapp` | Isolated widget harness + minimal patterns |
| Sethu4U (`sethu4u`) | Full product consuming app |

## Agent rules of thumb

1. Prefer new-stack modules; do not invent APIs from legacy packages.
2. App owns Liquibase under `{app}/dbschema` — never Spring Liquibase at runtime.
3. UI listings always use search queries + `yk-search-form` / `yk-search-results`, with Add/Edit/Delete (and similar) as child `yk-search-action` widgets — not separate page buttons.
4. Entities: `@Getter`/`@Setter` only — never `@Data` on persistence entities.
5. Framework UI libs live in `webutils-services/web/lib`; apps junction `services/web/lib` to that folder.
