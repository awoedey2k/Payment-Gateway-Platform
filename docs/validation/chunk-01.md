# Chunk 1 — Tenant & Identity Module: validation record

Branch `chunk-01-tenant-identity-module` (based on `chunk-00-project-bootstrap`). Validator: `requirements-validator` definition in `.claude/agents/requirements-validator.md`, run through a general-purpose agent bound to that definition (the registered subagent type is not loaded in the session that created it).

**Status: SIGNED OFF BY THE PROJECT OWNER (2026-09-23), on the basis of validator run 2's evidence and the fixes/decisions recorded below.** Run 2 (through the registered `requirements-validator` subagent) returned DO NOT SIGN OFF on two items — both documentation/planning bookkeeping, not code defects — which are now fixed. This is a genuine validator-verified sign-off on all code and tests; the owner's sign-off is on the two doc/plan fixes made after run 2 completed (see below), not a waiver of independent verification.

## Validator run 1 (branch tip `51208fa`)

| Item | Result |
| --- | --- |
| All three KYC score bands produce the documented outcome | MET |
| A revoked key is rejected immediately; a key inside its grace period authenticates | MET |
| Suspending a tenant blocks new transaction creation (cross-checked against Chunk 4) | CANNOT VERIFY: Chunk 4 does not exist |
| KYC/KYB onboarding workflow | MET |
| API key issuance, dual-active rotation, hashing at rest | MET, with a spec deviation (LIVE keys issued on request) |
| Tenant status state machine with guarded transitions | MET, with one added edge (`SUSPENDED -> CLOSED`) |
| Custom-domain resolution and locale hydration | MET |
| `docs/modules/tenant.md` | MET, with inaccuracies (fixed below) |
| Required tests (KYC thresholds, state machine incl. invalid transitions, full rotation sequence) | MET |
| Extended-package rule | MET (all generated changes explained by the `jdl/10-tenant.jdl` diff) |
| JDL single source of truth | MET |
| Tests green, none skipped | MET (reproduced: 372 unit, 1329 integration; Angular 1480) |
| Branch/scope | MET |

## Findings and dispositions

Fixed in this branch, each with a test that fails without the fix (mutation-checked unless noted):

1. **Suspension during in-flight KYC screening was overridden by an auto-approval** (defect; no test existed). Fixed: the screening result is discarded (`TENANT_STATUS_CHANGED`) if the tenant left `PENDING_REVIEW`. Test: `OnboardingInterleavingIT` (a provider double holds the screening open between its two transactions); removing the fix makes it fail.
2. **A crash between the two screening transactions left `kycStatus` stuck at `PENDING`.** Fixed: `POST /tenants/{id}/onboarding/reset`. Tests in `OnboardingInterleavingIT`.
3. **Fail-open configuration.** KYC weights of 20 or less (or negative) could auto-approve; the pepper/stub guards fired only under the literal `prod` profile. Fixed: weights outside 21-100 stop startup; the guards apply to any named profile that is not `dev`/`test*`. Tests in `TenantConfigurationTest`. Trade-off (documented): a run with **no** profile is treated as development, because the generated Cucumber test runs without one and cannot be edited.
4. **Stale-cache risk across nodes** (authentication read entities that can come from the per-node Hibernate second-level cache). Fixed: authentication uses a single projection query and the guard a column query; neither reads the cache.
5. Minor: `WWW-Authenticate: Bearer` on 401 (asserted in `ApiKeyRotationIT`); exact score boundaries 20/21/60/61 exercised through `assess()` (`KycScoringPolicyTest`); README note about in-place changelogs after regeneration; `tenant.md` corrected.

Also found while proving the concurrency tests (not by the validator): the generated test profile pins the Hikari pool to **one connection**, which made every concurrency test pass with no locking at all. `AbstractTenantIT` now overrides the pool, and the lock tests were mutation-checked (removing the tenant row lock fails 6 tests). A boundary test that failed ~57% of runs (23 of 40) because PostgreSQL rounds nanosecond timestamps was fixed by making the test clock microsecond-exact.

## Owner decisions (2026-09-21) and what was done

- **Generated CRUD bypass (security): admin-only chain outside test profiles.** Implemented as `GeneratedCrudLockdownConfiguration` (a second `SecurityFilterChain` requiring `ROLE_ADMIN` for every generated entity CRUD path); active in every real runtime, off only under `test*` profiles so the unmodified generated tests stay green. `GeneratedCrudLockdownIT` (real signed JWTs) proves a plain user gets 403 on tenant/API-key/other-module CRUD while an admin still works; mutation-checked (with the chain off, a plain user got 200 on the same calls).
- **LIVE keys on request after activation** (instead of automatic at approval): accepted as a deliberate deviation from spec §2.2.
- **`SUSPENDED -> CLOSED` kept** as an extension of the spec diagram.
- **Unowned requirements assigned** (see the amendment at the top of `TASK_BREAKDOWN.md`): new **Chunk 1b** (`chunk-01b-tenant-users-rbac`: tenant users, RBAC, MFA, self-serve registration, automatic TEST keys); Chunk 4 (tenant enforcement on charge endpoints incl. the race re-check and the suspended-tenant test); Chunk 7 (settlement-account precondition); Chunk 9 (KYC evidence in the audit log, compliance filing, Tier-2 review queue); Chunk 10 (notifications, `pk_` keys and `key_id`, white-label).

## Validator run 2 (branch tip `4d9f873`, through the registered `requirements-validator` subagent)

| Item | Result |
| --- | --- |
| All three KYC score bands produce the documented outcome | MET |
| A revoked key is rejected immediately; a key inside its grace period authenticates | MET |
| Suspending a tenant blocks new transaction creation (cross-checked against Chunk 4) | CANNOT VERIFY: Chunk 4 does not exist (expected, tracked) |
| Run-1 fix (a) suspension-during-screening no longer overridden | MET, re-verified independently (not taken on trust) |
| Run-1 fix (b) stuck-`PENDING` recovery | MET |
| Run-1 fix (c) fail-open config closed, incl. the zero-profile trade-off | MET, and the trade-off is genuinely documented, not a silent backdoor |
| Run-1 fix (d) stale-cache bypass | MET — confirmed by reading the query definitions, both are non-entity projections |
| `GeneratedCrudLockdownConfiguration`/`Condition` (new mechanism) | MET on all five sub-checks: matcher completeness (checked against Spring's real `PathPattern` semantics, all 30 generated controllers enumerated), authority parity, chain ordering, condition gating incl. empty-profile-array fail-closed behavior, and non-`@WithMockUser` JWT-based test evidence with a built-in positive control |
| `docs/TASK_BREAKDOWN.md` amendment coherence | MET on numbering/branch names/scope-disjointness/Chunk 4+7+9 hand-offs; **NOT MET** on Chunk 10 (deliverable added but no matching test/acceptance criterion) |
| Project-wide rules (extended-package, JDL source of truth, tests green, branch/scope) | MET, all re-verified from scratch (diffed every non-`extended` file against the two JDL diffs; ran a full `./mvnw verify`) |
| Tests green, none skipped | MET (reproduced: 384 unit, 1340 integration, 0 failures/skips, Checkstyle clean) |

New findings (not defects in the code under test, but gaps the code's own correctness exposed):
1. **Chunk 10's 2026-09-21 hand-off had no acceptance criterion or test**, unlike Chunks 4/7/9 — fixed in `TASK_BREAKDOWN.md` (2026-09-23 amendment).
2. **Three more spec requirements were unowned:** §2.1 KYB document payload/submission, §1.1 automated suspension triggers (chargeback ratio, AML/fraud alarms), §1.1 escrow/payout-freeze on `SUSPENDED` and final reconciliation on `CLOSED`. Assigned to Chunks 1b, 8, 9 and 7 respectively (2026-09-23 amendment), each with its own test and acceptance criterion this time.
3. **`docs/modules/tenant.md` contradicted itself** in three places about which profiles trigger the production config guard (said "`prod` profile" where the actual, correctly-documented rule is "any profile that isn't `dev`/`test*`"). Fixed.
4. **Residual gap, not a regression:** `/api/users` is excluded from the CRUD lockdown (the generated self-registration flow reads it) and has no `@PreAuthorize`, so a self-registered plain user can still enumerate staff accounts through it. Unchanged generated behavior, not introduced by this chunk, but `tenant.md` read as if the lockdown closed this class of exposure entirely. Documented and assigned to Chunk 11.

## Owner decisions (2026-09-23) and what was done

- **Scope mapping (Recommended option, "map into existing/new chunks now"):** all three newly unowned requirements and the `/api/users` gap distributed into existing chunks (1b, 7, 8, 9, 11) rather than a new standalone chunk, each with its own acceptance criterion — see the amendment at the top of `TASK_BREAKDOWN.md` and the `docs/modules/tenant.md` "Known gaps and hand-offs" section.
- **Sign-off path (Recommended option, "fix and merge without a 3rd validator run"):** the two DO-NOT-SIGN-OFF items were documentation/planning bookkeeping, not code defects; run 2 already verified all the actual code in depth. The owner accepted the fixes above without requiring a third validator pass.

## Remaining open

- Acceptance criterion 3 (suspension blocks transaction creation, cross-checked against Chunk 4) stays CANNOT VERIFY until Chunk 4; the hand-off is now written into Chunk 4's deliverables, tests and acceptance criteria.
- Chunk 1 is ready to merge.
