# ADR: Adopting JHipster for the Payment Gateway Platform

2026-09-21 · @Someone

## Context

This ADR evaluates whether to build the Payment Gateway Platform's Spring Boot backend on JHipster (`generator-jhipster`) rather than a hand-rolled setup, given the plan to launch as a modular monolith and later split into microservices under higher transaction volume. The technical specification already assumes a Java/Spring Boot stack, JDL-modeled entities, and six internal modules (Tenant, Catalog & FX, Routing, Transaction, Settlement, Audit & AML) communicating over an in-process event bus, with a documented Chunk 10 roadmap to extract services behind Kafka once throughput or auditing needs justify it.

The decision to make: adopt JHipster for scaffolding and the eventual microservices topology, build everything by hand, or adopt a different generator such as Seed4J.

## Decision Drivers

- Multi-tenancy: the spec commits to a single shared PostgreSQL schema with row-level security (RLS) keyed on tenant, not database-per-tenant or schema-per-tenant.
- Domain modeling: the spec already expresses several data models in JDL, JHipster's own entity-definition language.
- Migration path: a three-phase roadmap (monolith, then extract Transaction/Routing behind Kafka, then full microservices with dedicated Tenant & Auth, Smart Routing, Transaction Processor, and Wallet & Ledger services) is already committed in the spec, not hypothetical.
- Transaction-critical logic: double-entry ledger writes, FX-rate locking, fee-tier resolution, dispute state transitions, and distributed idempotency are all custom business logic no generator produces out of the box.
- Compliance posture: PCI-DSS-relevant scoping (SAQ A-EP tokenization) favors well-audited, widely used auth and security patterns over bespoke implementations.
- Throughput: the platform must sustain high transaction volume as it scales, so whatever is chosen must not add meaningful per-request overhead of its own.

## Options Considered

**Core JHipster (`generator-jhipster`)** — actively maintained, with v9.4.0 shipped September 2026 on Java 21+ and Spring Boot 4.1, and releases roughly every 4 to 8 weeks through the year. Generates a Spring Boot backend from JDL entity definitions, then can regenerate the same entities as independent microservice applications behind a Spring Cloud Gateway with Consul service discovery and Kafka messaging. Follows a single-version support policy: only the latest major release receives fixes, with no long-term-support branch.

**Hand-rolled Spring Boot, no generator** — full control and no forced upgrade cadence, but every piece of plumbing (auth, Liquibase migrations, CRUD layers, Docker/CI setup, and later the gateway, service discovery, and Kafka wiring) is built and maintained from scratch, duplicating well-trodden work JHipster already automates.

**Seed4J** — the hexagonal/domain-first fork of the now-archived JHipster Lite, maintained separately since August 2025 by JHipster Lite's original creator. Built around ports-and-adapters architecture rather than JDL, and organized around incremental step-by-step generation rather than a monolith-first entity scaffold. A different philosophy from what the spec already assumes, and not pursued further here.

## Decision

Adopt core JHipster in a hybrid pattern: JHipster generates and later re-shapes the scaffolding, authentication, JDL-modeled CRUD entities, and, at the point of the Chunk 10 migration, the API gateway, service discovery, and Kafka wiring. The ledger, routing/fee, dispute, and idempotency engines, and the RLS tenant-context layer, remain hand-written Spring services sitting on top of the generated entities — the same code that would exist with or without JHipster.

This keeps the modular monolith's module boundaries (Chunk 1, Section 2.1) intact as the seam JHipster later cuts along when generating each module as its own microservice application.

## What JHipster Generates vs. What Stays Custom

| Module | JHipster provides | Stays hand-written |
| --- | --- | --- |
| Tenant | JDL entities for tenant/config records, CRUD REST + admin UI, Spring Security/JWT auth, Liquibase migrations | Onboarding workflow, KYC/KYB scoring logic, RLS tenant-context filter |
| Catalog & FX | JDL entities for the country/currency/payment-method matrix | ForexRate lookup, rate-lock timing semantics |
| Routing | JDL entities for routing rules | Rule evaluator, acquirer adapter interfaces, circuit breakers, failover |
| Transaction | JDL entities for Transaction, base CRUD | State machine, distributed idempotency, webhook signing and verification |
| Settlement | JDL entities for Ledger, Payout, Dispute | Double-entry journal logic, fee-tier resolution, payout batching, dispute state machine |
| Audit & AML | JDL entities for audit log records, immutable-log storage pattern | Hash-chaining logic, AML velocity and risk scoring |

## Consequences and Risks

The RLS multi-tenancy model the spec commits to has no native JHipster support. A servlet filter or interceptor must be built to resolve the tenant per request and issue a `SET LOCAL` on each transaction, with care that Hibernate's connection pooling doesn't leak tenant context across pooled connections — the same failure mode Chunk 1's edge-case list already names. This is standard Spring, Hibernate, and Postgres work, not a JHipster gap specific to this project, but it is not something the generator hands us.

JHipster's single-version support policy means the team commits to an ongoing upgrade cadence rather than freezing on a stable major for a few years. Generated code is ordinary Spring Boot the team fully owns, so falling behind is recoverable, but staying current is the intended mode of operation, and that carries a real, recurring engineering cost worth budgeting for given the platform's likely PCI-DSS obligations.

The CRUD-scaffold-versus-financial-engine boundary in the table above needs to stay a deliberate discipline. JDL suits config- and reference-data entities well, and forcing transaction-critical, multi-row, invariant-heavy logic — the ledger in particular — into generated CRUD services would be a mistake independent of JHipster. That logic needs hand-written, carefully tested `@Transactional` methods regardless of how the entities were scaffolded.

## Migration Path Alignment

Chunk 10's three-phase roadmap maps onto JHipster's supported flow without requiring a different tool at each phase.

1. Launch monolith — JHipster generates the initial Spring Boot app from the JDL model, with all six modules co-deployed and communicating over Spring's in-process event bus, matching Figure 10.2's Phase 1.
2. Extract Transaction & Routing behind Kafka — JHipster's microservice application type can regenerate these modules as standalone services from the same JDL entities, introducing Kafka as the inter-service bus in place of the in-memory one, matching Phase 2's trigger of throughput exceeding 1,000 writes per second.
3. Full microservices — the remaining modules (Wallet & Ledger, Tenant & Auth, Smart Routing) follow the same regeneration path behind a Spring Cloud Gateway with Consul service discovery, matching Phase 3.

Because module boundaries were already designed with zero cross-module table access (Chunk 1, Section 2.1), each extraction stays what the original spec intended it to be: an infrastructure and deployment change, not a logic rewrite.

## Next Steps

1. Spike the RLS tenant-context filter (request-scoped tenant resolution plus `SET LOCAL` per transaction) against a throwaway JHipster-generated entity to validate the pattern before committing.
2. Convert the JDL fragments already in the spec into a single validated `.jdl` file covering at least the Tenant and Catalog & FX modules, and generate a first monolith from it.
3. Confirm the target Java and Spring Boot versions (JHipster 9.x requires Java 21+ and Spring Boot 4.1) are compatible with the team's deployment environment before scaffolding.
4. Decide whether JHipster's generated Angular, React, or Vue admin UI is wanted for the tenant dashboard mentioned in the spec, or whether the platform stays API-only — JHipster supports a gateway-less, backend-only setup.
