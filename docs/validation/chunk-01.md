# Chunk 1 — Tenant & Identity Module: validation record

Branch `chunk-01-tenant-identity-module` (based on `chunk-00-project-bootstrap`). Validator: `requirements-validator` definition in `.claude/agents/requirements-validator.md`, run through a general-purpose agent bound to that definition (the registered subagent type is not loaded in the session that created it).

**Status: NOT SIGNED OFF.** Run 1 returned DO NOT SIGN OFF. The defects it found are fixed and the project owner has decided the open items (below); the validator has not yet re-run against the fixed branch. Update this file with run 2.

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

## Remaining open

- Acceptance criterion 3 (suspension blocks transaction creation, cross-checked against Chunk 4) stays CANNOT VERIFY until Chunk 4; the hand-off is now written into Chunk 4's deliverables, tests and acceptance criteria.
- Validator run 2 against the fixed branch.
