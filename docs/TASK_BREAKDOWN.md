# Payment Gateway Platform — Task Breakdown

This is the work breakdown structure for implementing the platform on JHipster, referenced by `CLAUDE_CODE_KICKOFF_PROMPT.md`. Every chunk below becomes exactly one branch, one pull request, and one reviewable unit of work. A chunk is not done until its deliverables exist, its documentation is updated, its tests pass, and the requirements-validator subagent (see the kickoff prompt) has signed off against the acceptance criteria listed here.

Source documents this traces back to:
- **Spec** = `Payment Gateway Platform — Technical Specification.docx` (the 10-chunk technical spec)
- **ADR** = `adr-jhipster.md` (the JHipster adoption decision record)

**Amendment 2026-09-21 (project owner decision, after Chunk 1 validation):** spec requirements that no chunk owned were assigned. A new **Chunk 1b** was added after Chunk 1, and the remaining items were added to Chunks 4, 7, 9 and 10 (each marked "added 2026-09-21"). The document now describes fourteen chunks (0, 1, 1b, 2-12).

General rules that apply to every chunk, not repeated in each entry below:
- Branch names are exactly as written here — self-descriptive, `chunk-NN-short-name` format.
- Update the relevant documentation *before* the commit that finishes the chunk, not after.
- Every chunk ships with automated tests (unit at minimum; integration where the acceptance criteria require crossing a real boundary — database, HTTP, Kafka once it exists). No chunk is complete with failing or skipped tests.
- Every chunk's PR description restates its acceptance criteria and checks each one off explicitly.

---

## Chunk 0 — Project Bootstrap

**Branch:** `chunk-00-project-bootstrap`
**Depends on:** nothing

**Deliverables**
- Run `jhipster jdl jdl/00-app-config.jdl` to scaffold the monolith (this is the one step in the whole project that only creates the shell — no business entities yet).
- Run `jhipster jdl jdl/00-app-config.jdl jdl/10-tenant.jdl jdl/20-catalog-fx.jdl jdl/30-routing.jdl jdl/40-transaction.jdl jdl/50-settlement.jdl jdl/60-audit-aml.jdl jdl/90-options.jdl` to generate all entities, services, and the Angular admin CRUD screens in one pass.
- Confirm the app builds (`./mvnw compile`) and the generated test suite passes (`./mvnw test`) before touching anything else.
- docker-compose for local PostgreSQL (devDatabaseType is postgresql, not H2 — Chunk 5's RLS work depends on this).
- `README.md` documenting: how to run the app locally, the module-to-package mapping (below), and a link to the ADR.

**Documentation to update**
- `README.md` (new)
- `docs/adr-jhipster.md` and `docs/TASK_BREAKDOWN.md` copied into the repo under `docs/` so they travel with the code

**Tests required**
- The generated smoke tests must pass unmodified (proves the scaffold itself is sound before any custom code is added).

**Acceptance criteria**
- [ ] App builds and starts locally against Postgres.
- [ ] All 25 entities from the JDL are present with generated REST resources, services, and Liquibase changelogs.
- [ ] Generated Angular admin screens load for at least one entity per module, confirming the client build is sound.
- [ ] README lets a new developer get the app running with no undocumented steps.

---

## Chunk 1 — Tenant & Identity Module

**Branch:** `chunk-01-tenant-identity-module`
**Depends on:** Chunk 0
**Spec reference:** Chunk 2 (Tenant Lifecycle, Onboarding, KYC/KYB, API Key Rotation)

**Deliverables**
- KYC/KYB onboarding workflow (sanctions/PEP screening call-out, registry cross-reference, score-based auto-approve / manual-review / auto-reject per Figure 2.2).
- API key issuance, dual-active-window rotation (Key A/Key B, grace period, explicit revoke), and hashing at rest.
- Tenant status state machine (PENDING_REVIEW → ACTIVE → SUSPENDED/CLOSED) with guarded transitions.
- Custom-domain (CNAME) resolution and locale hydration for the checkout page.

**Documentation to update**
- `docs/modules/tenant.md`: the onboarding state machine, the API key rotation sequence, and which parts are generated JHipster CRUD vs. hand-written service logic.

**Tests required**
- Unit tests for the KYC scoring thresholds and the tenant status state machine's guarded transitions (including invalid-transition rejection).
- Integration test for the full API key rotation sequence (issue B, dual-active window, revoke A, confirm A stops authenticating).

**Acceptance criteria**
- [ ] All three KYC score bands from Figure 2.2 produce the documented outcome.
- [ ] A revoked API key is rejected by auth immediately, and a key inside its grace period still authenticates.
- [ ] Suspending a tenant blocks new transaction creation for that tenant (cross-checked against Chunk 4's Transaction module).

---

## Chunk 1b — Tenant Users, RBAC & Self-Serve Registration

**Branch:** `chunk-01b-tenant-users-rbac`
**Depends on:** Chunk 1
**Spec reference:** Chunk 2 §1.1 (self-serve signup, sandbox test keys), §3 (RBAC & User Management)
**Added 2026-09-21** by project owner decision: these spec requirements were owned by no chunk.

**Deliverables**
- `AppUser` (merchant-side user, tenant-scoped; separate from JHipster's staff `User`, per the ADR/JDL note that the two auth systems are not unified): email, password hash, role, MFA secret and flag. Data-model changes go through the JDL.
- The four tenant roles (`ROLE_TENANT_ADMIN`, `ROLE_FINANCE`, `ROLE_DEVELOPER`, `ROLE_SUPPORT`) with the capability/restriction matrix of spec §3.1 enforced, including tenant-scoped authorization (a tenant user can never act on another tenant).
- MFA enrolment and verification for tenant users.
- Team-member invitation by a tenant admin.
- Self-serve registration `POST /api/v1/auth/register-tenant`: creates a `PENDING_REVIEW` tenant with `kycStatus = NOT_STARTED`, its first `ROLE_TENANT_ADMIN` user, and automatically issued TEST API keys (LIVE stays locked until activation).

**Documentation to update**
- `docs/modules/tenant-users.md`: the role/permission matrix as implemented, the registration flow, and how merchant-user authentication relates to staff JWT and API-key authentication.

**Tests required**
- Unit tests for the permission matrix: every role allowed/denied for every capability the spec lists.
- Integration test: registration yields a `PENDING_REVIEW` tenant, a tenant-admin user and a working TEST key, and no ability to create LIVE keys.
- Integration test: a tenant user cannot read or change another tenant's data or keys.

**Acceptance criteria**
- [ ] Each role restriction in spec §3.1 is enforced (for example, only `ROLE_TENANT_ADMIN` can trigger key rotation; `ROLE_SUPPORT` cannot see raw API secrets).
- [ ] Self-serve registration produces a sandbox-only tenant that cannot process live requests until KYC approval.
- [ ] Merchant users cannot authenticate to staff-only endpoints, and staff accounts cannot act as merchant users.

---

## Chunk 2 — Catalog, Currency & FX Module

**Branch:** `chunk-02-catalog-fx-module`
**Depends on:** Chunk 0
**Spec reference:** Chunk 3 (Country-Specific Payment Method Matrix) and Chunk 1 §3.2 (ForexRate engine)

**Deliverables**
- Seed data / admin management for Country, Currency, PaymentMethod, and CountryPaymentMethod (min/max transaction limits, recurring and instant-refund support flags).
- ForexRate quoting and locking service: issue a rate, lock it for a bounded window, expire it, and reject use of an expired quote.

**Documentation to update**
- `docs/modules/catalog-fx.md`: which corridors are seeded at launch, and the rate-lock lifecycle diagram.

**Tests required**
- Unit tests for rate-lock expiry boundary conditions.
- Integration test proving two concurrent requests for the same corridor get independently locked rates (no shared mutable "current rate" race).

**Acceptance criteria**
- [ ] Every launch country/payment-method pair in the spec's Chunk 3 matrix (Figure/table in §2.2) exists as seed data.
- [ ] An expired ForexRate cannot be used to complete a transaction.

---

## Chunk 3 — Routing Engine

**Branch:** `chunk-03-routing-engine`
**Depends on:** Chunk 1, Chunk 2
**Spec reference:** Chunk 4 (Gateway Adapter Architecture, Dynamic Checkout Rail Discovery)

**Deliverables**
- Rule evaluator implementing the two-tier precedence from Figure 4.2 (tenant-custom rules, priority 1–100, evaluated before platform-global fallback rules, priority 101+).
- GatewayAdapter interface plus at least two concrete adapter implementations (one card acquirer, one mobile-money rail) behind it, with circuit breaker and failover per the spec's Chunk 4 language.

**Documentation to update**
- `docs/modules/routing.md`: the adapter interface contract, and how to add a new acquirer without touching the rule evaluator.

**Tests required**
- Unit tests for rule precedence (tenant rule beats global rule; among tenant rules, lower priority number wins).
- Integration test for failover: primary adapter forced to fail, confirm fallback adapter is invoked and max-retries is respected.

**Acceptance criteria**
- [ ] A tenant-specific rule always overrides a platform-global rule for the same corridor.
- [ ] Exceeding `maxRetries` on a rule surfaces a routable failure rather than retrying indefinitely.

---

## Chunk 4 — Transaction Processing & Idempotency

**Branch:** `chunk-04-transaction-processing`
**Depends on:** Chunk 1, Chunk 3
**Spec reference:** Chunk 5 (End-to-End Transaction Processing Engine, State Machine, Idempotency) and Chunk 10 §2 (REST API)

**Deliverables**
- Transaction state machine (PENDING → AUTHORIZED/SUCCESSFUL/FAILED/REVERSED) matching Figure 5.2's full request lifecycle, including the asynchronous challenge/webhook-confirmation path.
- Distributed idempotency enforcement on the charge-creation endpoint, backed by the unique `idempotencyKey` constraint already in the JDL.
- `POST /api/v1/charges`, authorize/capture, and refund REST endpoints per Chunk 10 §2's documented payload contracts.
- *(added 2026-09-21, hand-off from Chunk 1)* Tenant status enforcement on every one of those endpoints: call `TenantAccessGuard.assertCanTransact` (or sit behind the merchant API-key chain) and **re-check the tenant status inside the charge transaction under the tenant row lock**, so a suspension committing between the check and the insert cannot let a charge through.

**Documentation to update**
- `docs/modules/transaction.md`: the state diagram, and the idempotency guarantee stated precisely (what "duplicate" means, what the retried caller gets back).
- `docs/api/charges.md`: the REST contract, kept in sync with the actual generated OpenAPI spec.

**Tests required**
- Unit tests for every state transition, including rejected illegal transitions.
- Integration test: fire the same idempotency key twice concurrently, assert exactly one Transaction row is created and both callers get the same response.
- *(added 2026-09-21)* Integration test: suspend a tenant, `POST /api/v1/charges` with its key, expect 403 `TENANT_SUSPENDED` and no Transaction row; plus a race test proving a suspension concurrent with a charge cannot produce a charge after the suspension commits. This completes Chunk 1's "suspending a tenant blocks new transaction creation" criterion.

**Acceptance criteria**
- [ ] A retried request with the same idempotency key never creates a second Transaction, under concurrent load, not just sequential.
- [ ] The REST payloads match Chunk 10 §2's documented request/response shapes.
- [ ] *(added 2026-09-21)* A suspended, rejected, closed or (for LIVE) unverified tenant can never create a transaction, including under a concurrent suspension.

---

## Chunk 5 — Multi-Tenant Row-Level Security Layer

**Branch:** `chunk-05-rls-tenant-isolation`
**Depends on:** Chunk 0
**Spec reference:** Chunk 1 §2 (shared-schema RLS) · ADR "Consequences and Risks" (the one part of this stack JHipster does not provide)

This chunk is cross-cutting and touches every tenant-scoped entity generated in Chunk 0 — treat it as infrastructure, not a feature.

**Deliverables**
- Postgres RLS policies on every tenant-scoped table (`tenant_id` column, policy restricting rows to the session's current tenant).
- A servlet filter/interceptor that resolves the authenticated caller's tenant and issues `SET LOCAL app.current_tenant` at the start of each transaction.
- Connection-pool safety: proof that tenant context never leaks across pooled connections between requests (this is the exact edge case the spec calls out in Chunk 1's edge-case list, and the ADR flags as JHipster's one real gap).

**Documentation to update**
- `docs/security/multi-tenancy.md`: the RLS policy design, the filter's request lifecycle, and the pooled-connection leakage risk plus how it's mitigated — this doc is the direct answer to the open question the ADR raised.

**Tests required**
- Integration test: two tenants' data present in the same table, confirm tenant A's session can never read or write tenant B's rows, including via a raw JPQL query that "forgot" to filter by tenant.
- Stress test: rapid interleaved requests from different tenants sharing a connection pool, confirm no cross-tenant leakage under load.

**Acceptance criteria**
- [ ] No code path — generated or hand-written — can read another tenant's row, even with a missing `WHERE tenant_id = ?` clause, because the database itself enforces it.
- [ ] The pooled-connection leakage test passes under concurrent load, not just single-threaded.

---

## Chunk 6 — Double-Entry Ledger & Wallets

**Branch:** `chunk-06-ledger-wallet-engine`
**Depends on:** Chunk 4, Chunk 5
**Spec reference:** Chunk 6 (Multi-Currency Tenant Wallets, Double-Entry Bookkeeping)

**Deliverables**
- Journal-entry posting service: every balance-affecting event (hold, release, dispute win/loss, fee) creates one balanced `JournalEntry` with two or more `JournalLine`s — never a direct mutation of a balance field.
- `TenantWallet` snapshot maintenance kept in sync with the ledger, plus a reconciliation job that cross-checks the wallet snapshot against a replay of the ledger.

**Documentation to update**
- `docs/modules/ledger.md`: the chart of accounts, and worked examples of each transaction type's journal entries (mirroring the spec's Chunk 6 worked examples).

**Tests required**
- Unit tests confirming every journal-posting method produces balanced entries (debits equal credits) for every transaction type in the spec.
- Integration test for the reconciliation job catching a deliberately introduced mismatch between wallet snapshot and ledger replay.

**Acceptance criteria**
- [ ] Every journal entry the system can produce balances to zero.
- [ ] The reconciliation job detects and reports a wallet/ledger mismatch rather than silently trusting the snapshot.

---

## Chunk 7 — Fee Engine & Payouts

**Branch:** `chunk-07-fee-payout-engine`
**Depends on:** Chunk 6
**Spec reference:** Chunk 7 (Fee Calculation Engine, Payout Schedules, Cross-Border Clearing)

**Deliverables**
- Fee resolution: fixed + percentage + cap, tenant config overriding country-method benchmark overriding platform default, fail-closed when nothing resolves (per the spec's explicit "unpriced transaction is a revenue-integrity risk, not a convenience" stance).
- MERCHANT vs. CUSTOMER fee-bearer handling, gated per corridor by card-network surcharge rules.
- Scheduled payout execution per `PayoutSchedule` frequency, with per-tenant `SettlementBatch` isolation so one tenant's bank rejection never blocks another's run.
- Cross-border clearing: FX conversion locked at batch-creation time (reusing Chunk 2's ForexRate lock).
- *(added 2026-09-21)* Designated settlement account precondition (spec Chunk 2 §1.1 `ACTIVE` state definition): a tenant may not be activated or receive payouts without a designated settlement account. Implemented as an additional guard on the `ACTIVE` transition in the tenant lifecycle (in `extended`), not by editing generated code.

**Documentation to update**
- `docs/modules/fees-payouts.md`: the fee resolution hierarchy worked through an example, and the payout batch state machine.

**Tests required**
- Unit tests for the fee-resolution hierarchy, including the fail-closed case when no tier matches.
- Integration test: one tenant's payout batch fails (simulated bank rejection), confirm other tenants' batches in the same run complete unaffected and the failed batch's wallet debit is atomically reversed.

**Acceptance criteria**
- [ ] An unpriced transaction is rejected at checkout-session creation, never silently charged at zero fee.
- [ ] A failed payout never leaves funds "in limbo" between debited-from-tenant and confirmed-received-by-bank.
- [ ] *(added 2026-09-21)* A tenant without a designated settlement account cannot become `ACTIVE` or be paid out.

---

## Chunk 8 — Dispute & Chargeback Lifecycle

**Branch:** `chunk-08-dispute-lifecycle`
**Depends on:** Chunk 6
**Spec reference:** Chunk 8 (Dispute, Chargeback & Evidence Lifecycle Management)

**Deliverables**
- Dispute state machine (OPEN → EVIDENCE_SUBMITTED → UNDER_REVIEW → WON/LOST → CLOSED) per Figure 8.1, with an escrow hold journal entry on OPEN and a resolution journal entry on WON/LOST (built on Chunk 6's ledger).
- Evidence upload with checksum verification (reject a payload whose declared `sha256Checksum` doesn't match the uploaded file, synchronously).
- Deadline enforcement: evidence submitted after `dueDate` is rejected with a specific error rather than silently accepted.
- Negative-balance handling: a chargeback exceeding available balance becomes a tracked negative balance with the three escalating mitigation triggers from the spec (settlement interception, backup-instrument debit, payout freeze).

**Documentation to update**
- `docs/modules/disputes.md`: the state machine, and the negative-balance mitigation ladder as a first-class documented behavior, not an error case.

**Tests required**
- Unit tests for every dispute state transition and the two rejection paths (late evidence, checksum mismatch).
- Integration test: dispute opened against a tenant with insufficient available balance, confirm the negative-balance state and first mitigation trigger fire correctly.

**Acceptance criteria**
- [ ] Every dispute resolution (WON or LOST) produces the correct balanced journal entry from Chunk 6, matching the spec's worked examples.
- [ ] A negative available balance is a valid, queryable, non-error state.

---

## Chunk 9 — AML, Audit Trail & Compliance

**Branch:** `chunk-09-aml-audit-compliance`
**Depends on:** Chunk 4
**Spec reference:** Chunk 9 (Security, Compliance, AML velocity engines, hash-chained audit trails)

**Deliverables**
- AML rule engine per the decision tree in the spec (sanctions/PEP check, velocity check, geolocation/proxy check, card-testing detection, cumulative risk score → ALLOW/CHALLENGE/REJECT).
- Hash-chained `AuditLogEntry` writer: every entry's hash depends on the previous entry's hash, and a verification routine that walks the chain and detects tampering.
- *(added 2026-09-21)* KYC/KYB evidence from Chunk 1 (which signals fired, score, decision, reviewer) persisted as tamper-evident audit-log entries; KYC rejections filed to the internal compliance dashboard; the Tier-2 manual-review queue with its 24-hour target (spec Chunk 2 §2, Figure 2.2).
- Encryption-key rotation tracking hooks for the KMS-backed envelope encryption described in the spec (the KMS integration itself may be stubbed/mocked in this chunk if no KMS is provisioned yet — document that explicitly if so).

**Documentation to update**
- `docs/security/aml-audit.md`: the AML decision tree as implemented, and how to run the hash-chain verification routine.

**Tests required**
- Unit tests for each AML rule and the cumulative scoring bands (ALLOW < 30, CHALLENGE 30–75, REJECT > 75, per the spec's decision tree).
- Integration test that tampers with one AuditLogEntry mid-chain and confirms the verification routine detects it.

**Acceptance criteria**
- [ ] All four AML rules (sanctions, velocity, geolocation, card-testing) are independently testable and independently triggerable.
- [ ] Tampering with any single audit log entry is detectable by the chain-verification routine.
- [ ] *(added 2026-09-21)* Every KYC decision made in Chunk 1's workflow has a corresponding hash-chained audit entry, and manual-review applications appear in a queue with a due time.

---

## Chunk 10 — Webhooks & API Integration Layer

**Branch:** `chunk-10-webhook-api-integration`
**Depends on:** Chunk 4, Chunk 9
**Spec reference:** Chunk 10 (Integration Strategy, Secure Webhook Delivery, Architectural Roadmap)

**Deliverables**
- Outbound webhook dispatcher: HMAC-SHA256 signing, replay-attack timestamp guard, retry with backoff, delivery-attempt logging (`WebhookDeliveryAttempt`).
- The three integration models' supporting infrastructure (Hosted Checkout redirect, Drop-in SDK hosted-fields endpoint, Direct Server API) to the extent each needs distinct backend support.
- *(added 2026-09-21)* Onboarding/status notifications to the merchant by email and webhook (spec Chunk 2 Figure 2.2 step 5); public `pk_` keys and the public `key_id` handle for the checkout SDK (spec Chunk 2 §4.1); the white-label configuration (branding, custom-domain SSL/DNS verification state, CDN/CSP asset isolation; spec Chunk 2 §5).
- A written migration runbook translating the spec's Chunk 10 three-phase roadmap (extract Transaction/Routing behind Kafka, then isolate the ledger, then the webhook dispatcher) into concrete follow-up chunks for when those triggers are actually hit — this is documentation, not code, since the ADR's decision is not to build the microservices split prematurely.

**Documentation to update**
- `docs/api/webhooks.md`: the signing scheme with the exact verification code sample from the spec, kept accurate to the real implementation.
- `docs/roadmap/microservices-migration.md`: the runbook described above.

**Tests required**
- Unit test for the HMAC signing/verification round trip, including the replay-guard timestamp rejection.
- Integration test: a merchant endpoint that times out, confirm retry-with-backoff behavior and that it doesn't block other tenants' webhook delivery.

**Acceptance criteria**
- [ ] A tampered webhook payload fails signature verification.
- [ ] A slow or failing merchant webhook endpoint never blocks delivery to other tenants (matches the spec's stated migration trigger for extracting this into its own worker).

---

## Chunk 11 — Observability, Rate Limiting & Hardening

**Branch:** `chunk-11-observability-hardening`
**Depends on:** Chunks 4–10
**Spec reference:** Non-Functional Commitments (availability, durability, latency, auditability, recoverability) from the spec's Executive Summary

**Deliverables**
- Structured logging and metrics for every module, with the P95 latency budget from the spec's Executive Summary made measurable (dashboards or equivalent).
- Rate limiting on public API endpoints, per tenant and per API key.
- A documented incident-response / recoverability runbook for at least the failure modes explicitly named across the spec's per-chunk edge-case sections (RLS pool leakage, Redis outage fail-closed behavior, KMS outage, webhook endpoint failure).

**Documentation to update**
- `docs/operations/observability.md` and `docs/operations/incident-runbook.md`.

**Tests required**
- Integration test proving rate limiting actually rejects over-limit traffic per tenant without affecting other tenants.
- A load test establishing a baseline P95 latency figure against the spec's committed budget.

**Acceptance criteria**
- [ ] Every named failure mode in the spec's edge-case sections has a corresponding entry in the incident runbook.
- [ ] Measured P95 latency for the synchronous checkout path (Chunk 5, Figure 5.2, steps 1–5) meets the spec's committed budget.

---

## Chunk 12 — End-to-End Requirements Validation

**Branch:** `chunk-12-requirements-validation`
**Depends on:** all previous chunks

This is the project's completion gate, not a feature chunk. Nothing after this chunk — the project is either done or it names what's missing.

**Deliverables**
- A requirements-traceability matrix: every numbered requirement, edge case, and acceptance criterion from the spec (all 10 chunks) and this task breakdown, mapped to the chunk/branch/test that satisfies it.
- Full end-to-end scenario tests exercising a transaction from checkout through settlement, dispute, and payout, crossing every module.
- A final pass by the requirements-validator subagent (see the kickoff prompt) against the full traceability matrix, not just the most recent chunk.

**Documentation to update**
- `docs/REQUIREMENTS_TRACEABILITY.md` (new) — the matrix itself, kept as a living record, not a one-time snapshot.

**Tests required**
- The full end-to-end scenario suite described above, run and green.

**Acceptance criteria**
- [ ] Every row in the traceability matrix has a non-empty "satisfied by" column pointing at real code and a real test.
- [ ] Nothing in the matrix is marked "satisfied" without the validator subagent's sign-off recorded against it.
- [ ] Only once every row is satisfied does the project get marked complete — a partial matrix means the project is not done, whatever else works.

---

## Module → Chunk Quick Reference

| Module (from the ADR) | Chunks |
| --- | --- |
| Tenant | 1, 1b |
| Catalog & FX | 2 |
| Routing | 3 |
| Transaction | 4 |
| (cross-cutting) Multi-tenancy | 5 |
| Settlement (ledger, wallets, fees, payouts) | 6, 7 |
| Settlement (disputes) | 8 |
| Audit & AML | 9 |
| Integration | 10 |
| (cross-cutting) Operations | 11 |
| (gate) Validation | 12 |
