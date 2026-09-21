# Payment Gateway Platform

Multi-tenant payment gateway built on JHipster 9.4.0: Spring Boot 4.1 (Java 21), Angular 22 admin client, PostgreSQL, JWT auth for staff accounts, Maven.

It launches as a **modular monolith** with a planned path to microservices. Why JHipster, and exactly where it helps versus where the code is hand-written, is recorded in [`docs/adr-jhipster.md`](docs/adr-jhipster.md). The requirements are in [`docs/technical-spec.md`](docs/technical-spec.md) and the implementation plan (chunks, branches, acceptance criteria) is in [`docs/TASK_BREAKDOWN.md`](docs/TASK_BREAKDOWN.md).

## Prerequisites

- **JDK 21** (JHipster 9 requires 21+)
- **Docker** with Compose v2 — used for the local PostgreSQL and by the test suite (Testcontainers)
- Node.js is **not** required: the Maven build downloads and uses its own copy (`./npmw` is the wrapper)

## Run it locally

```bash
./mvnw
```

This starts the backend with the `dev` profile. Spring Boot's Docker Compose integration starts PostgreSQL for you from [`src/main/docker/services.yml`](src/main/docker/services.yml) (database `paymentgateway` on `localhost:5432`), Liquibase creates the schema, and the built Angular client is served at <http://localhost:8080>.

To start PostgreSQL yourself instead:

```bash
docker compose -f src/main/docker/postgresql.yml up -d
```

Sign in at <http://localhost:8080> with the dev accounts JHipster creates: `admin` / `admin` and `user` / `user`. **These are for local development only.** The entity screens are under the _Entities_ menu; the REST API docs are under _Administration → API_.

For client hot-reload while editing Angular code, run the backend as above and, in a second terminal:

```bash
./npmw start        # http://localhost:4200, proxied to the backend on :8080
```

No sample data is loaded (`skipFakeData` is on): random fake rows collide with the unique columns in this model and would put fictitious tenants and transactions in a payments database. Reference data is seeded deliberately (Chunk 2).

## Build and test

```bash
./mvnw test      # unit tests + Cucumber
./mvnw verify    # everything above plus the integration tests (*IT)
```

Integration tests use Testcontainers, so Docker must be running. The generated suite is expected to pass unmodified.

## Modules and where they live

The six modules from the spec (§2.1) are modelled in numbered JDL files. **The JDL files are the single source of truth for the data model** — change the `.jdl` and regenerate, never hand-patch generated entity code.

| Module                        | JDL file                              | Entities                                                                                                               | Hand-written logic arrives in                     |
| ----------------------------- | ------------------------------------- | ---------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------- |
| App shell / options           | `00-app-config.jdl`, `90-options.jdl` | none (application config; DTO, service, pagination and filtering options for every entity)                             | Chunk 0                                           |
| Tenant                        | `10-tenant.jdl`                       | `CorporateTenant`, `TenantDirector`, `ApiKey`, `TenantDomain`                                                          | Chunk 1 (RLS layer: Chunk 5)                      |
| Catalog & FX                  | `20-catalog-fx.jdl`                   | `Currency`, `Country`, `PaymentMethod`, `CountryPaymentMethod`, `ForexRate`                                            | Chunk 2                                           |
| Routing                       | `30-routing.jdl`                      | `RoutingRule`                                                                                                          | Chunk 3                                           |
| Transaction                   | `40-transaction.jdl`                  | `Transaction`, `Refund`                                                                                                | Chunk 4                                           |
| Settlement                    | `50-settlement.jdl`                   | `TenantWallet`, `LedgerAccount`, `JournalEntry`, `JournalLine`, `TenantFeeConfig`, `PayoutSchedule`, `SettlementBatch` | Chunks 6, 7                                       |
| Audit & AML (and Integration) | `60-audit-aml.jdl`                    | `AmlCheck`, `AuditLogEntry`, `Dispute`, `DisputeEvidence`, `WebhookSubscription`, `WebhookDeliveryAttempt`             | Chunks 8 (disputes), 9 (AML/audit), 10 (webhooks) |

That is 25 entities. Note that `Dispute`/`DisputeEvidence` sit in `60-audit-aml.jdl` although the ADR lists disputes under Settlement, and the webhook entities live there too; the table above follows the files as they are.

JHipster generates by layer, under `io.paymentgateway.core`:

| Package                                    | Contents                                                        |
| ------------------------------------------ | --------------------------------------------------------------- |
| `domain`                                   | JPA entities                                                    |
| `repository`                               | Spring Data repositories                                        |
| `service`, `service.dto`, `service.mapper` | Services (`*Service`, `*QueryService`), DTOs, MapStruct mappers |
| `web.rest`                                 | REST resources (`/api/<plural-entity-name>`)                    |
| `security`, `config`                       | JWT security and Spring configuration                           |

### The `extended` package rule

**All hand-written implementation lives in `io.paymentgateway.core.extended`.** Generated classes (everything else under `io.paymentgateway.core`) are never modified, so the business logic we add is always identifiable and regeneration from the JDL can never overwrite it. If a generated repository, service or other class needs more behaviour, extend or wrap it inside `extended` and use that implementation. Data-model changes are the one exception: they go in the `jdl/` files and are regenerated.

Generated: entity CRUD, REST, admin screens, Liquibase changelogs, JWT staff auth. Hand-written (later chunks): ledger, fees, routing, disputes, idempotency, and the row-level-security tenant layer. See the ADR table for the full split.

Merchant API-key authentication (`sk_live_…`) is a separate, hand-written mechanism and is unrelated to the JHipster staff accounts above.

## Regenerating from the JDL

Run from the project root, with all JDL files in this order (`90-options.jdl` last):

```bash
jhipster jdl jdl/00-app-config.jdl jdl/10-tenant.jdl jdl/20-catalog-fx.jdl jdl/30-routing.jdl \
  jdl/40-transaction.jdl jdl/50-settlement.jdl jdl/60-audit-aml.jdl jdl/90-options.jdl \
  --skip-git --skip-fake-data
```

`--skip-fake-data` is stored in `.yo-rc.json` by the first run; `skipFakeData` is not a JDL keyword, so keep the flag on the command line. The generator prints a `paginate option is deprecated` warning; it is harmless (`paginate` is still the only JDL spelling).

## Working from the JHipster Docker container

If you run the generator from the `jhipster/jhipster:v9.4.0` container instead of a local install, mount the project directory and run `jhipster` from it. To build and test from inside that container:

- Give it the Docker socket (`-v /var/run/docker.sock:/var/run/docker.sock`, plus `--group-add 0` on Docker Desktop) so Testcontainers works, and set `TESTCONTAINERS_HOST_OVERRIDE=host.docker.internal` on macOS/Windows.
- The container has no `docker` CLI, so set `SPRING_DOCKER_COMPOSE_ENABLED=false` and start PostgreSQL with the `docker compose` command above; point the app at it with `SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/paymentgateway`.
- If your Maven `settings.xml` lists private repositories that the container cannot reach, builds stall on connection timeouts. Pass a Central-only settings file with `./mvnw -s <file>`.

## Documentation

- [`docs/adr-jhipster.md`](docs/adr-jhipster.md) — the JHipster adoption decision
- [`docs/technical-spec.md`](docs/technical-spec.md) — the full technical specification
- [`docs/TASK_BREAKDOWN.md`](docs/TASK_BREAKDOWN.md) — chunk-by-chunk plan and acceptance criteria
- [`docs/CLAUDE_CODE_KICKOFF_PROMPT.md`](docs/CLAUDE_CODE_KICKOFF_PROMPT.md) — the workflow rules for implementation sessions
- [`docs/JHIPSTER_GENERATED_README.md`](docs/JHIPSTER_GENERATED_README.md) — the README JHipster generated (tooling reference: Sonar, Cypress, Docker, CI, …)
