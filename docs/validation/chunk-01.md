# Chunk 1 — Tenant & Identity Module: validation record

Branch `chunk-01-tenant-identity-module` (based on `chunk-00-project-bootstrap`). Validator: `requirements-validator` definition in `.claude/agents/requirements-validator.md`, run through a general-purpose agent bound to that definition (the registered subagent type is not loaded in the session that created it).

**Status: NOT SIGNED OFF.** Run 1 returned DO NOT SIGN OFF. The defects it found are fixed (below); the items that need project-owner decisions are open, and the validator has not yet re-run against the fixes.

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

## Open: needs a project-owner decision

- **Generated CRUD bypasses every guard (security).** `/api/**` is `authenticated()` only and `/api/register` is open, so any self-registered `ROLE_USER` can `PUT`/`PATCH` a tenant's `status`/`kycStatus`/`riskScore` or edit/reactivate API keys through the generated endpoints. Cannot be closed without editing generated code or the generated integration tests, and no chunk owns it. Options are presented to the owner.
- **LIVE keys are issued on request after activation, not automatically at approval** (spec §2.2 says "provisioned automatically"; a once-shown secret needs a recipient). Documented; needs owner sign-off.
- **`SUSPENDED -> CLOSED` edge added** (the spec diagram reaches `DEACTIVATED` only from `ACTIVE`). Documented; needs owner confirmation.
- **Requirements owned by no chunk** (verified against `TASK_BREAKDOWN.md`): RBAC / tenant users / MFA (spec §3); self-serve registration and automatic TEST keys (§1.1); public `pk_` keys and `key_id` (§4.1); rejection notification, compliance-dashboard filing and the Tier-2 review queue (§2); the settlement-account precondition for `ACTIVE`; white-label branding and SSL/DNS state (§5). Chunk 12's matrix must list them as unowned until assigned.
- **Chunk 4 hand-off**: the suspension criterion can only be finished in Chunk 4 (it must call `TenantAccessGuard` or sit behind the `/api/v1/**` chain, add the real test, and re-check status under the tenant lock to close the check-then-act race). Not yet written into Chunk 4's entry in `TASK_BREAKDOWN.md`.
