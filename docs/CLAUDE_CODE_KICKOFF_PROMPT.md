# Claude Code Kickoff Prompt — Payment Gateway Platform

Copy everything below the line into Claude Code as your first message in a fresh session, run from the root of the (currently empty, or freshly `git init`'d) project repository. Before running it, place these files in the repo at exactly these paths — the prompt refers to them by path:

```
docs/technical-spec.md          ← the original 10-chunk technical specification
docs/adr-jhipster.md            ← the ADR ("Adopting JHipster for the Payment Gateway Platform")
docs/TASK_BREAKDOWN.md          ← the chunk-by-chunk work breakdown
jdl/00-app-config.jdl
jdl/10-tenant.jdl
jdl/20-catalog-fx.jdl
jdl/30-routing.jdl
jdl/40-transaction.jdl
jdl/50-settlement.jdl
jdl/60-audit-aml.jdl
jdl/90-options.jdl
```

---

I'm building a multi-tenant payment gateway platform on JHipster (Java/Spring Boot backend, Angular admin client, PostgreSQL). The full requirements live in `docs/technical-spec.md`. The decision to use JHipster, and exactly where it helps versus where we still write everything by hand, is recorded in `docs/adr-jhipster.md` — read that before writing any code, it tells you what JHipster generates for us and what stays custom (the ledger, fee, routing, dispute, and idempotency engines, and the row-level-security tenant-isolation layer are all hand-written regardless of the generator).

The full implementation plan is `docs/TASK_BREAKDOWN.md` — thirteen chunks, Chunk 0 through Chunk 12, each with a fixed branch name, deliverables, documentation to update, required tests, and acceptance criteria. Follow it exactly as written; do not reorder, merge, or skip chunks, and do not invent additional chunks without telling me first. Chunk 12 is the project's completion gate — the project is not "done" until it passes, however complete any individual chunk looks.

## Workflow rules — apply to every chunk without exception

1. **One branch per chunk, using the exact branch name given in `docs/TASK_BREAKDOWN.md`.** Do not improvise a different name. Branch from the tip of the previous completed chunk's branch (or `main` once merged), matching the "Depends on" field for that chunk.

2. **Documentation before commit, not after.** For each chunk, write or update the documentation files listed under that chunk's "Documentation to update" *before* making the commit that finishes the chunk's implementation — the doc update and the code it describes land together, never the doc as a follow-up afterthought.

3. **Every chunk ships with its required tests, green, before it's considered done.** No chunk is complete with a failing, skipped, or commented-out test. If a test is genuinely not yet meaningful (e.g., blocked on a later chunk), say so explicitly rather than faking a pass.

4. **Stop and ask me when a routine has more than one reasonable implementation approach.** This applies to things like: choice of locking strategy for the wallet/ledger writes, retry/backoff policy shape, how to structure the RLS tenant-context filter, library choices beyond what JHipster already picked, or any place the spec describes *what* must happen but leaves *how* genuinely open. When this happens: stop, lay out the realistic options with their real tradeoffs (not a false balance — say which one you'd pick and why), and wait for my answer before writing the code. Don't ask about things that only have one sane answer, and don't ask about things `docs/technical-spec.md` or `docs/adr-jhipster.md` already settled.

5. **Set up a dedicated requirements-validator subagent before starting Chunk 1**, and use it on every chunk from then on. Concretely:
   - Create `.claude/agents/requirements-validator.md` as a subagent whose only job is: given a chunk's diff (or the finished branch) plus that chunk's entry in `docs/TASK_BREAKDOWN.md` and the relevant section(s) of `docs/technical-spec.md`, check the implementation against the stated acceptance criteria and report which are met, which are not, and why — nothing else. It does not write code and does not rubber-stamp; it reports discrepancies plainly, including ones you'd rather it didn't find.
   - Run this subagent against every chunk's branch before that chunk is merged. A chunk with an unresolved discrepancy from the validator is not done — fix the discrepancy (or come back to me if the discrepancy reveals a genuine ambiguity in the spec) and re-run the validator, don't merge around it.
   - Keep the validator's findings for each chunk somewhere durable (a comment on the PR, or a section in that chunk's documentation file) so Chunk 12 can point back to them.

6. **Chunk 0 (bootstrap) is special — do it first, exactly as `docs/TASK_BREAKDOWN.md` describes.** Run `jhipster jdl` first against `jdl/00-app-config.jdl` alone to scaffold the shell, confirm it builds, then run it again against all the JDL files together to generate the full entity/service/admin-UI layer. Review the generated diff before committing — if the JDL produces something clearly wrong (a field type, a missing relationship), fix the `.jdl` source file and regenerate rather than hand-patching generated code, so the JDL stays the single source of truth for the data model. If you don't have working JHipster CLI / npm registry access in this environment, stop and tell me rather than hand-writing a substitute scaffold — the whole point of Chunk 0 is that it's generated, not approximated.

7. **Chunk 12 (validation) is the completion gate.** Do not tell me the project is complete until Chunk 12's requirements-traceability matrix (`docs/REQUIREMENTS_TRACEABILITY.md`) has every row satisfied, backed by real code and a real passing test, and signed off by the requirements-validator subagent. If anything is genuinely unsatisfiable as written (a requirement that turned out to be ambiguous or in conflict with another), surface it to me explicitly in that matrix rather than marking it done anyway.

## Start here

Read `docs/adr-jhipster.md`, then `docs/TASK_BREAKDOWN.md` in full, then begin Chunk 0. Confirm your understanding of the module boundaries and the workflow rules above before running any generator commands.
