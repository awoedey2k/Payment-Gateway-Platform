# Chunk 0 — Project Bootstrap: validation record

Branch `chunk-00-project-bootstrap`. Validator: `requirements-validator` (definition in `.claude/agents/requirements-validator.md`).

**Status: NOT SIGNED OFF.** The validator's verdict on the first run was DO NOT SIGN OFF, because three acceptance criteria could not be independently verified from the repository (they need a running app and a browser). The author's evidence is recorded below. The chunk is signed off only when the project owner accepts that evidence or a re-run of the validator verifies it. Update this file when that happens.

> Note on how it was run: the subagent type was not yet registered in the session that created it (agent definitions load at session start), so the first run used a general-purpose agent instructed to follow `requirements-validator.md` verbatim (read-only, mandated report format). Later chunks should run the registered subagent.

## Validator run 1 — summary (branch tip `6e264db`)

| Item | Result |
| --- | --- |
| All entities in the JDL present with REST resources, services, Liquibase changelogs (25) | MET |
| App builds and starts locally against Postgres | CANNOT VERIFY (see evidence below) |
| Angular admin screens load, one entity per module | CANNOT VERIFY (browser check) |
| README lets a new developer run the app with no undocumented steps | CANNOT VERIFY literally; two inaccuracies found, fixed |
| Scaffold from `00-app-config.jdl`, then all-entity generation | MET |
| `./mvnw compile` / `./mvnw test` pass; generated tests unmodified | MET (validator re-ran `test`: 276 tests, 0 failed/skipped; also Angular Vitest: 1480 tests pass) |
| docker-compose for local PostgreSQL | MET (generator output, `src/main/docker/postgresql.yml`) |
| README + ADR + task breakdown in `docs/` | MET |
| `--skip-fake-data` state coherent | MET |
| JDL edits justified | MET |
| Extended-package rule | MET (only hand-written Java is `extended/package-info.java`) |
| JDL is single source of truth | MET |
| Tests green, none skipped/disabled | MET for Java and Angular suites; generated Cypress specs contain `skip` (see below) |
| Branch name / scope | MET, with two notes (below) |

## Author evidence for the CANNOT VERIFY items (gathered 2026-09-21)

Run from the `jhipster9.4.0-docker` container with PostgreSQL 18.6 from `src/main/docker/postgresql.yml`, app started with the `dev` profile and the datasource pointed at that PostgreSQL:

- **Full test run:** `./mvnw verify` on the final tree: surefire 276 tests, failsafe 1268 tests, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`. (The failsafe summary is in `target/failsafe-reports/failsafe-summary.xml`; the validator confirmed 1268 completed, 0 failed there.)
- **Start against Postgres:** on an empty database the app started with no errors and Liquibase applied 44 changesets (`select count(*) from databasechangelog` = 44) creating 25 entity tables. An earlier run against the same database exposed a real defect (random fake data violating `ux_country__iso_code`), which is why `--skip-fake-data` was adopted.
- **REST:** authenticated as the dev `admin` account, `GET /api/<entity>` returned 200 for all 25 entities; the same request unauthenticated returned 401. `/v3/api-docs` lists 31 `/api/...` paths.
- **Angular admin screens:** signed in by the project owner in the in-app browser, then the list screens for Corporate Tenants (Tenant), Countries (Catalog & FX), Routing Rules (Routing), Transactions (Transaction), Journal Entries (Settlement) and Audit Log Entries (Audit & AML) each rendered ("No … found", as expected with no seed data), with the API calls succeeding.

What still cannot be independently reproduced by the validator: the browser check, and the README followed literally on a plain host (host JDK here is 26, not 21, and the app was run from the container where the Docker Compose integration is disabled).

## Findings and dispositions

1. README: `./mvnw test` / `verify` do not run the Angular unit tests. **Fixed** in the README (`./npmw test`).
2. README: PostgreSQL keeps running after the app stops and has no persistent volume. **Fixed** in the README.
3. Generated Cypress specs contain `it.skip` / `this.skip()` for `dispute`, `dispute-evidence`, `refund`, `tenant-fee-config`, `webhook-delivery-attempt` (JHipster skips create-flows for entities with required relationships). They are generator output, are not run by `test`/`verify`, and cannot be edited under the `extended`-package rule. **Decision needed from the project owner**: accept as generator output (recommended; documented in README) or require otherwise.
4. Commit `6e264db` adds the validator subagent, which is tooling rather than a Chunk 0 deliverable. It is a separate commit so it can be dropped or moved.
5. `docs/TASK_BREAKDOWN.md`: besides the 24 → 25 correction, the Module → Chunk quick-reference table was corrected (Chunk 8 is Disputes; Audit & AML is Chunk 9). Both are recorded in the Chunk 0 PR description.
6. The task breakdown's "module-to-package mapping (below)" is not defined anywhere. The README maps modules to JDL files, entities and chunks, and states that hand-written code goes in `io.paymentgateway.core.extended` (project owner's rule, 2026-09-21).
7. Security notes for Chunk 11 (not Chunk 0 criteria): `.yo-rc.json` contains a generated `jwtSecretKey` and `src/main/resources/config/tls/keystore.p12` is committed. Both are JHipster development defaults and must be replaced or externalised before any real deployment.
8. ADR "Next Steps" item 3 (confirm Java/Spring Boot compatibility) is satisfied by the working Java 21 / Spring Boot 4.1.1 build; no ADR text change was made.
