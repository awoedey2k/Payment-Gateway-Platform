# Payment Gateway Platform

Multi-tenant payment gateway, built on JHipster (Spring Boot + Angular, JWT auth, Maven, PostgreSQL). Launching as a modular monolith with a planned migration to microservices — see `docs/adr-jhipster.md` for why JHipster and where it does/doesn't help, and `docs/TASK_BREAKDOWN.md` for the full implementation plan.

## Layout

- `docs/technical-spec.md` — the full technical specification (10 chunks).
- `docs/adr-jhipster.md` — the architecture decision record for adopting JHipster.
- `docs/TASK_BREAKDOWN.md` — chunk-by-chunk work breakdown (branches, docs, tests, acceptance criteria).
- `docs/CLAUDE_CODE_KICKOFF_PROMPT.md` — paste this into Claude Code to start implementation.
- `jdl/` — the JDL files that generate the base app and all entities (Chunk 0).

## Getting started

1. Read `docs/adr-jhipster.md`, then `docs/TASK_BREAKDOWN.md`.
2. Run Chunk 0: `jhipster jdl jdl/00-app-config.jdl` to scaffold the shell, then `jhipster jdl jdl/*.jdl` to generate all entities.
   **Note:** generating requires npm registry access. If you're running this from within a Claude Cowork session, the sandboxed environment's network policy currently blocks `registry.npmjs.org` — run this step from a normal Terminal on this machine instead (or your CI), where your regular network access applies.
3. Follow `docs/TASK_BREAKDOWN.md` chunk by chunk from there.
