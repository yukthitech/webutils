# WebUtils — Agent Context

## Overview

WebUtils is a multi-tier framework based on Spring that provides common functionality required by REST-based web applications. The framework expects certain default database tables to be maintained by each consuming application (schema versioning via Liquibase).

## Modules

### New framework (target for new projects)

| Module | Purpose |
|--------|---------|
| `webutils-common` | Shared models, form annotations, validators, response types (`com.webutils.common`) |
| `webutils-services` | Spring services, REST controllers, repositories, auth, search, mail, LOV (`com.webutils.services`) |

Built with Java 25 and Spring Boot 4.x. Excludes legacy functionality and follows new patterns.

### Legacy framework (maintained for existing apps)

| Module | Purpose |
|--------|---------|
| `Commons` | Request/response POJOs and shared contracts (`com.yukthitech.webutils.common`) |
| `Services` | Backend services, generic CRUD, auth, search, mail (`com.yukthitech.webutils`) |
| `Client` | Base Java client library for application-specific clients |
| `WebUtils` | Parent POM; `dbschema/` (Liquibase changelogs), `vue-based/` UI framework |
| `TestWebApp` | Sample/test application for the legacy stack |

Built with Java 17. See `WebUtils/README.MD` for full documentation.

## Working in this repo

- **New features and refactors** → `webutils-common` / `webutils-services`
- **Legacy behavior reference or migration** → `Commons` / `Services` / `Client` / `WebUtils`
- **Package namespace**: new code uses `com.webutils.*`; legacy uses `com.yukthitech.webutils.*`
