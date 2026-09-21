# Payment Gateway Platform — Technical Specification & System Architecture Blueprint

> **Document Classification:** Internal Engineering — Production-Grade Blueprint
> **Version:** 1.0
> **Status:** Master Specification (10 Modular Chunks)

---

## Table of Contents

1. [Chunk 1: Executive Overview, Modular Architecture & Global Foundations](#chunk-1-executive-overview-modular-architecture--global-foundations)
2. [Chunk 2: Tenant Lifecycle Management, Onboarding, Automated KYC, RBAC & Credential Management](#chunk-2-tenant-lifecycle-management-onboarding-automated-kyc-rbac--credential-management)
3. [Chunk 3: Country-Specific Payment Method Matrix & Regulatory Localization](#chunk-3-country-specific-payment-method-matrix--regulatory-localization)
4. [Chunk 4: Gateway Adapter Architecture, Regional Integrations & Dynamic Smart Routing Engine](#chunk-4-gateway-adapter-architecture-regional-integrations--dynamic-smart-routing-engine)
5. [Chunk 5: End-to-End Transaction Processing Engine, State Machine & Tokenization](#chunk-5-end-to-end-transaction-processing-engine-state-machine--tokenization)
6. [Chunk 6: Multi-Currency Tenant Wallets, Double-Entry Accounting & Ledger Mechanics](#chunk-6-multi-currency-tenant-wallets-double-entry-accounting--ledger-mechanics)
7. [Chunk 7: Fee Calculation Engine, Payout Schedules & Cross-Border Clearing](#chunk-7-fee-calculation-engine-payout-schedules--cross-border-clearing)
8. [Chunk 8: Dispute, Chargeback & Evidence Lifecycle Management](#chunk-8-dispute-chargeback--evidence-lifecycle-management)
9. [Chunk 9: Security, Compliance (PCI-DSS Scoping), AML Risk Engine & Immutable Audit Logging](#chunk-9-security-compliance-pci-dss-scoping-aml-risk-engine--immutable-audit-logging)
10. [Chunk 10: Integration Strategy, RESTful API Specs, Secure Webhooks & Migration Roadmap](#chunk-10-integration-strategy-restful-api-specs-secure-webhooks--migration-roadmap)

---

## Chunk 1: Executive Overview, Modular Architecture & Global Foundations

### 1. Executive Summary & Product Vision

#### 1.1 Objective

The platform is an enterprise-grade, multi-tenant payment gateway delivered as a Software-as-a-Service (SaaS). It serves corporate tenants (e.g., merchants, digital platforms, marketplace operators, multi-national retailers) across diverse operating jurisdictions by providing unified payment collection, intelligent transaction routing, multi-currency ledger management, compliance screening, and automated payout settlement.

#### 1.2 Core Value Propositions

- **Single Unified API for Fragmented Payment Rails:** Abstract regional fragmentation across emerging and mature markets (Cards, Mobile Money like M-Pesa, Instant Bank Transfers like NIP, USSD, and QR) behind a standard integration contract.
- **Intelligent Routing & Failover:** Dynamically evaluate routing criteria (currency, transaction volume, transaction cost, regional acquirer performance) with automatic failover to eliminate payment drop-offs.
- **Strict Multi-Tenant Isolation:** Built-in shared-schema isolation using tenant keys and PostgreSQL Row-Level Security (RLS) to enforce data security while maintaining operational simplicity and low infrastructure overhead.
- **Audit-Proof Financial Ledger:** Multi-currency double-entry ledger architecture ensuring zero financial drift between gateway settlements, tenant balances, holds, and external banking payouts.

---

### 2. High-Level Architectural Blueprint

#### 2.1 The Modular Monolith Strategy

The system is intentionally constructed as a **Modular Monolith** rather than a distributed microservices cluster for initial launch.

```
                                  [ Client Layer ]
              (Web Dashboard / Checkout Widget / Merchant API Consumer)
                                         │
                                         │ HTTPS / TLS 1.3
                                         ▼
                      ┌──────────────────────────────────────┐
                      │    API Gateway & Security Filter     │
                      │  (Rate Limiting, WAF, JWT, HMAC)     │
                      └──────────────────┬───────────────────┘
                                         │
 ┌───────────────────────────────────────┴───────────────────────────────────────┐
 │                           MODULAR MONOLITH CORE                              │
 │                                                                              │
 │  ┌───────────────────────┐  ┌───────────────────────┐  ┌──────────────────┐  │
 │  │    Tenant Module      │  │  Catalog & FX Module  │  │  Routing Engine  │  │
 │  │ (Onboarding, KYC, RLS)│  │ (Country, Currency)   │  │ (Rules, Adapters)│  │
 │  └───────────┬───────────┘  └───────────┬───────────┘  └─────────┬────────┘  │
 │              │                          │                        │           │
 │              │         Internal In-Memory Event Bus              │           │
 │              │        (Spring ApplicationEvent / Guava)          │           │
 │              ▼                          ▼                        ▼           │
 │  ┌───────────────────────┐  ┌───────────────────────┐  ┌──────────────────┐  │
 │  │  Transaction Engine   │  │   Settlement Module   │  │  Audit & AML     │  │
 │  │(State Machine, Tokens)│  │(Ledger, Wallets, Fees)│  │(Immutable Logs)  │  │
 │  └───────────────────────┘  └───────────────────────┘  └──────────────────┘  │
 └───────────────────────────────────────┬──────────────────────────────────────┘
                                         │
                  ┌──────────────────────┴──────────────────────┐
                  ▼                                             ▼
       ┌─────────────────────┐                       ┌─────────────────────┐
       │   PostgreSQL DB     │                       │     Redis Cache     │
       │ (Shared Schema RLS) │                       │(Idempotency/Routing)│
       └─────────────────────┘                       └─────────────────────┘
```

**Why Modular Monolith for Startup Phase:**

1. **Single Deployment Artifact:** Greatly simplifies CI/CD pipelines, container management (Docker on ECS/Compute), and cross-module debugging.
2. **ACID Transactions by Default:** Crucial for financial workflows. Moving money between a tenant's fee account, escrow wallet, and processing pipeline can occur inside an ACID database transaction without requiring complex two-phase commits (2PC) or the SAGA pattern across network boundaries.
3. **Clear Boundary Enforcements:** Modules communicate strictly via well-defined internal interfaces (service layer DTOs) and in-memory event buses. Modules maintain zero direct repository access into neighboring domains. This guarantees that any single module (e.g., Transaction Engine or Routing) can be carved out into an independent microservice later when load dictates.

#### 2.2 Data Isolation: Shared Database with Tenant Identifiers

```
+-------------------------------------------------------------------------------+
|                             PostgreSQL Database                               |
|                                                                               |
|  Table: transactions                                                          |
|  +----+-----------+------------+--------+----------+-----------------------+  |
|  | id | tenant_id | reference  | amount | currency | status                |  |
|  +----+-----------+------------+--------+----------+-----------------------+  |
|  | 1  | TENANT_A  | TXN_98765  | 500.00 | USD      | SUCCESSFUL            |  |
|  | 2  | TENANT_B  | TXN_12345  | 25000  | NGN      | SUCCESSFUL            |  |
|  +----+-----------+------------+--------+----------+-----------------------+  |
|                                                                               |
|  Row-Level Security (RLS) Policy:                                             |
|  CREATE POLICY tenant_isolation_policy ON transactions                        |
|  USING (tenant_id = current_setting('app.current_tenant_id'));                |
+-------------------------------------------------------------------------------+
```

- **Tenant Key Injection:** Every operational database table contains an indexed `tenant_id` foreign key referencing `CorporateTenant.id`.
- **Execution Context:** Incoming requests pass through an authentication filter where the `tenant_id` is extracted from the authenticated JWT or API Key and placed inside a thread-local execution context (e.g., `TenantContextHolder`).
- **PostgreSQL Row-Level Security (RLS):** For every session/transaction checked out of the database connection pool, the application sets a session variable:

```sql
SET LOCAL app.current_tenant_id = 'c128f642-e1e3-4d69-952d-bc48398e404b';
```

Database policies natively block queries from returning or updating rows belonging to other tenants, preventing data leaks even in the event of an application logic bug.

---

### 3. Global Geography, Multi-Currency & FX Engine

To support global transactions seamlessly, the system abstracts geography and currency into master catalogs, decoupling them from transaction logic.

#### 3.1 Catalog Entities & Schema

```
  ┌────────────────────────┐                   ┌────────────────────────┐
  │        Country         │                   │        Currency        │
  ├────────────────────────┤                   ├────────────────────────┤
  │ * id                   │                   │ * id                   │
  │   iso2 (e.g. "NG")     │                   │   code (e.g. "USD")    │
  │   iso3 (e.g. "NGA")    │                   │   name                 │
  │   name                 │                   │   symbol ("$")         │
  │   default_currency_id  ├─────────┐         │   decimal_places (2)   │
  │   is_active            │         │         │   is_active            │
  └───────────┬────────────┘         │         └───────────┬────────────┘
              │                      │                     │
              │                      └──────────┬──────────┘
              │ 1                               │ 1
              ▼ *                               ▼ *
  ┌─────────────────────────────────────────────────────────────┐
  │                    CountryPaymentMethod                     │
  ├─────────────────────────────────────────────────────────────┤
  │ * id                                                        │
  │ * country_id (FK -> Country)                                │
  │ * payment_method_id (FK -> PaymentMethod)                   │
  │ * allowed_currency_id (FK -> Currency)                      │
  │   min_transaction_amount                                    │
  │   max_transaction_amount                                    │
  │   supports_recurring                                        │
  │   supports_refunds                                          │
  │   is_active                                                 │
  └─────────────────────────────────────────────────────────────┘
```

**Specifications:**

- **Precision Handling:** Currencies define their standard ISO decimal places (e.g., JPY = 0, USD = 2, KWD = 3, crypto/specialty = 4). Financial calculations throughout the application use arbitrary-precision numbers (`BigDecimal` in Java / `Numeric(18, 4)` in PostgreSQL) to completely prevent floating-point rounding errors.
- **Country-Currency Binding:** While a country has a default currency (e.g., NG -> NGN), transactions can process secondary currencies if an explicit `CountryPaymentMethod` mapping permits it (e.g., processing a USD card transaction within a Nigerian entity).

#### 3.2 Real-Time Forex (FX) Rate Engine

For cross-border payments where payment collection occurs in one currency and merchant settlement occurs in another, the FX Rate Engine manages conversions and margins.

```
+-------------------------------------------------------------------------------------+
|                                Forex Rate Lifecycle                                 |
|                                                                                     |
|   [ Third-Party FX Provider ]                                                       |
|   (OANDA / XE / Central Bank API)                                                   |
|                │                                                                    |
|                ▼ Scheduled Cron (Every 15-60 min)                                   |
|   [ FX Ingestion Service ]                                                          |
|                │                                                                    |
|                ├─► Write Base Interbank Rate (e.g., 1 USD = 1,550 NGN)              |
|                ├─► Apply Tenant / Platform Margin (+1.5% markup)                    |
|                └─► Persist to ForexRate Table with TTL                              |
|                                                                                     |
|   During Transaction Processing:                                                    |
|   [ Lock FX Quote ] ──► Guarantee rate for a 15-minute checkout window              |
|                     ──► Store locked rate on Transaction record (exchange_rate)     |
+-------------------------------------------------------------------------------------+
```

**ForexRate Entity Specification:**

| Field | Description |
|-------|-------------|
| `base_currency` | The benchmark currency (e.g., USD) |
| `target_currency` | The target currency (e.g., KES) |
| `rate` | Raw interbank mid-market exchange rate |
| `tenant_markup_percent` | Platform markup applied over market rate (e.g., 1.50%) |
| `effective_at` | Exact timestamp when the rate becomes valid |
| `expires_at` | Validity window after which a fresh rate must be fetched or locked |

#### Summary of Chunk 1 Deliverables

- Established architectural principles (Modular Monolith, Shared-DB with RLS isolation).
- Standardized decimal handling and currency structures.
- Designed the foundational geographic, currency, and Forex models.

---

## Chunk 2: Tenant Lifecycle Management, Onboarding, Automated KYC, RBAC & Credential Management

### 1. Tenant Lifecycle & Operational States

Every corporate merchant using the gateway operates under a strictly partitioned `CorporateTenant` aggregate. The lifecycle transition ensures that no tenant can initiate live transactions without completing required identity, legal, and operational verification.

#### 1.1 Tenant Lifecycle State Machine

```
      ┌────────────────────────────────────────────────────────┐
      │                   SELF-SERVE SIGNUP                    │
      │  (POST /api/v1/auth/register-tenant)                   │
      └───────────────────────────┬────────────────────────────┘
                                  │
                                  ▼
      ┌────────────────────────────────────────────────────────┐
      │              PENDING_VERIFICATION (Sandbox)           │
      │  • Test API keys issued automatically                  │
      │  • Test transactions allowed in Sandbox                │
      │  • Live processing strictly blocked                    │
      └─────────────┬────────────────────────────┬─────────────┘
                    │                            │
      [KYC Submitted & Approved]         [Sanctions/Fraud Flag]
                    │                            │
                    ▼                            ▼
      ┌───────────────────────────┐  ┌─────────────────────────┐
      │          ACTIVE           │  │        SUSPENDED        │
      │ • Live API keys generated │  │ • All processing halted │
      │ • Full routing enabled    │  │ • Funds held in escrow  │
      │ • Payouts allowed         │  └───────────┬─────────────┘
      └─────────────┬─────────────┘              │
                    │                   [Admin Remediation]
            [Voluntary Offboarding]              │
                    │                            ▼
                    ▼                ┌─────────────────────────┐
      ┌───────────────────────────┐  │         ACTIVE          │
      │        DEACTIVATED        │  │   (Re-instated access)  │
      │ • Keys permanently revoked│  └─────────────────────────┘
      │ • Final payout settled    │
      └───────────────────────────┘
```

**State Definitions:**

| State | Description |
|-------|-------------|
| `PENDING_VERIFICATION` | Default state upon registration. Grants immediate access to sandbox/test credentials and the tenant dashboard. Live payment endpoints reject requests from this tenant with HTTP 403 Forbidden (`TENANT_NOT_VERIFIED`). |
| `ACTIVE` | The tenant has cleared business verification (KYC/KYB), designated a settlement account, and passed sanction screenings. Full capabilities (card acquiring, mobile money, payouts) are unlocked. |
| `SUSPENDED` | Triggered by automated AML/fraud alarms, high dispute velocity (e.g., chargeback ratio > 1%), or administrative intervention. Incoming transaction requests fail, and automated payouts are frozen. |
| `DEACTIVATED` | Tenant contract terminated. API keys revoked; lingering settlement balances are cleared via manual final reconciliation. |

---

### 2. Automated Merchant Onboarding & KYB/KYC Verification Workflow

Corporate tenants onboarding onto the platform must satisfy regulatory Know Your Customer (KYC) and Know Your Business (KYB) compliance depending on their operating jurisdiction (e.g., CAC verification in Nigeria, Registrar of Companies in Kenya, Companies House in the UK).

```
Merchant User            Tenant Service           KYC/AML Provider          Compliance Team
      │                         │                         │                         │
      │── 1. Submit KYB Docs ──►│                         │                         │
      │   (Reg No, Directors,   │                         │                         │
      │    Utility, Tax ID)     │                         │                         │
      │                         │── 2. Sanction/PEP Check─►│                         │
      │                         │   & Business Lookup     │                         │
      │                         │                         │                         │
      │                         │◄── 3. Check Result ─────│                         │
      │                         │   (Score: Low Risk)     │                         │
      │                         │                         │                         │
      │                         ├─► [Risk Score < Threshold]                        │
      │                         │   │                                               │
      │                         │   ├─► Auto-Approve ──► Set Status: ACTIVE         │
      │                         │   │                                               │
      │                         │   └─► [Ambiguous / High Risk Score]               │
      │                         │       │                                           │
      │                         │       └─► Push to Review Queue ──────────────────►│
      │                         │                                                   │
      │                         │◄── 4. Manual Approval / Rejection ────────────────│
      │                         │                                                   │
      │◄── 5. Status Notification
      │   (Email / Webhook)     │
```

#### 2.1 Verification Document Payload Specification

```json
{
  "businessRegistrationNumber": "RC-1928374",
  "legalBusinessName": "Apex Technologies Global Limited",
  "taxIdentificationNumber": "TIN-83920194",
  "operatingJurisdiction": "NG",
  "directors": [
    {
      "fullName": "Sarah Jane Doe",
      "dateOfBirth": "1984-06-15",
      "nationality": "NG",
      "identificationType": "NATIONAL_ID",
      "identificationNumber": "NIN-99281726354",
      "verificationDocUrl": "https://vault.gateway.internal/docs/dir_1_id.pdf"
    }
  ],
  "proofOfAddressDocUrl": "https://vault.gateway.internal/docs/utility_bill_q3.pdf",
  "certificateOfIncorporationUrl": "https://vault.gateway.internal/docs/cac_cert.pdf"
}
```

#### 2.2 Automated Scoring & Decision Logic

1. **Sanctions & PEP (Politically Exposed Persons) Screening:** Every listed director and the beneficial corporate owner are checked against global watchlists (OFAC, EU Sanctions, UN Lists) via third-party compliance APIs (e.g., ComplyAdvantage, Smile Identity).
2. **Registry Cross-Reference:** The registration number is queried directly against government registries via regional adapters to ensure the business is active and not dissolved.
3. **Threshold Actions:**
   - **Score 0–20 (Clean):** Instant transition to `KYCStatus = APPROVED` and `TenantStatus = ACTIVE`. Live API keys provisioned automatically.
   - **Score 21–60 (Medium Risk / Document Mismatch):** Flagged for Tier-2 manual human compliance review within 24 hours.
   - **Score 61–100 (Critical / Sanction Hit):** Immediate rejection (`KYCStatus = REJECTED`), automated filing to the internal compliance dashboard, and notification sent.

---

### 3. Role-Based Access Control (RBAC) & User Management

To enforce separation of duties, the platform manages administrative and tenant users through strict RBAC boundaries.

```
                    ┌───────────────────────────────┐
                    │       CorporateTenant         │
                    └───────────────┬───────────────┘
                                    │ 1
                                    ▼ *
                    ┌───────────────────────────────┐
                    │           AppUser             │
                    ├───────────────────────────────┤
                    │ • id: UUID                    │
                    │ • tenant_id: UUID (FK)        │
                    │ • email: String               │
                    │ • password_hash: String       │
                    │ • role: UserRoleEnum          │
                    │ • mfa_secret: String          │
                    │ • mfa_enabled: Boolean        │
                    └───────────────────────────────┘
```

#### 3.1 Standard Predefined Tenant Roles

| Role Name | Scope & Capabilities | Access Restrictions |
|-----------|---------------------|---------------------|
| `ROLE_TENANT_ADMIN` | Complete administrative control over the merchant account. Invite team members, update white-label settings, change webhook URLs, trigger key rotations, and configure fee allocations. | Cannot alter global platform configurations or view other tenants' telemetry. |
| `ROLE_FINANCE` | View multi-currency wallet balances, generate financial reconciliations, initiate or view payout requests, download settlement batches, and handle dispute evidence uploads. | Cannot roll API keys, change webhook endpoints, or modify developer routing rules. |
| `ROLE_DEVELOPER` | Access live and sandbox API keys, view transaction raw request/response payloads, simulate webhook deliveries, configure smart routing rules, and view API latency logs. | Read-only access to ledger balances; cannot initiate payouts or alter settlement bank credentials. |
| `ROLE_SUPPORT` | Read-only visibility across transactions, refunds, and dispute statuses. Can initiate partial/full customer refunds (subject to tenant approval limits). | Cannot view raw API secrets, download banking payout batches, or alter user permissions. |

---

### 4. Multi-Tenant API Credential Architecture & Key Rotation

Tenants integrate with the gateway using scoped API Keys and Secret Keys. Security best practices dictate that secrets must never be stored in plaintext in the database.

#### 4.1 Key Anatomy & Storage Scheme

```
Public Key:  pk_live_83b1c67d89ef4847b2c... (Visible in web dashboards, used for checkout SDKs)
Secret Key:  sk_live_21f98d6c7b...           (Used strictly server-to-server for financial operations)
```

**Storage Strategy:**

```
┌────────────────────────────────────────────────────────────────────────┐
│ TenantApiKey Entity                                                    │
├───────────────────┬────────────────────────────────────────────────────┤
│ key_id            │ "key_live_abc123" (Public handle)                  │
│ key_hash          │ SHA-256(raw_secret + pepper)                       │
│ key_prefix        │ "sk_live_21f9" (First 8 chars for merchant lookup)  │
│ environment       │ LIVE / TEST                                        │
│ is_active         │ true                                               │
│ created_at        │ 2026-09-13T17:00:00Z                               │
└───────────────────┴────────────────────────────────────────────────────┘
```

- **One-Way Salting/Hashing:** The full secret key is presented to the merchant exactly once upon generation. The database persists only a salted SHA-256 hash (`key_hash`) along with a short visual identifier prefix (`key_prefix`).
- **Environment Separation:** Keys are strictly isolated via `is_live`:
  - Keys starting with `pk_test_` / `sk_test_` only authenticate against the mock/sandbox routing engine and can never hit live acquirers or deduct real funds.
  - Keys starting with `pk_live_` / `sk_live_` only execute if `CorporateTenant.status == ACTIVE`.

#### 4.2 Zero-Downtime Key Rotation Sequence

```
Merchant Dashboard          Tenant Service         Database              Gateway Cache (Redis)
        │                         │                    │                           │
        │── 1. Request Rotation ─►│                    │                           │
        │                         │── 2. Create Key B ─►│                           │
        │                         │   (Status: ACTIVE) │                           │
        │                         │                    │── 3. Warm Cache ─────────►│
        │                         │                    │    (Key A & B both valid) │
        │◄── 4. Return Secret B ──│                    │                           │
        │   (Starts 48hr window)  │                    │                           │
        │                         │                    │                           │
        │=== Merchant updates their backend services to use Secret B ===           │
        │                         │                    │                           │
        │── 5. Revoke Key A ─────►│                    │                           │
        │   (or 48hr auto-expire) │── 6. Invalidate A ─►│                           │
        │                         │                    │── 7. Evict A from Cache ──►│
```

1. **Dual-Active Window:** When a tenant triggers key rotation, the system provisions a new key pair (Key B) while leaving Key A operational for a configurable grace period (e.g., 24 to 48 hours).
2. **Seamless Rollout:** The merchant updates their distributed application servers with Key B without incurring failed API transactions.
3. **Revocation:** Once migration is complete, the tenant explicitly clicks "Revoke Old Key" in the dashboard, or the grace period lapses, changing `Key A.is_active` to `false` and invalidating Redis authorization caches instantly.

---

### 5. Multi-Language & White-Label Customization Engine

To deliver localized and branded experiences, each tenant defines localization preferences and checkout customizations.

#### 5.1 White-Label Configuration Schema

The `CorporateTenant.whiteLabelConfig` JSON blob provides strict branding controls loaded dynamically by the client checkout SDK:

```json
{
  "branding": {
    "brandName": "SwiftMarket Africa",
    "logoUrl": "https://cdn.gateway.io/tenants/t-8491/logo.png",
    "primaryColor": "#1E40AF",
    "secondaryColor": "#F3F4F6",
    "borderRadius": "8px",
    "fontFamily": "Inter, sans-serif"
  },
  "localization": {
    "defaultLocale": "fr_SN",
    "supportedLocales": ["en_US", "fr_SN", "pt_AO"],
    "fallbackLocale": "en_US"
  },
  "customDomain": {
    "checkoutDomain": "pay.swiftmarket.com",
    "sslCertificateStatus": "PROVISIONED",
    "dnsVerified": true
  },
  "receiptCustomization": {
    "supportEmail": "billing@swiftmarket.com",
    "supportPhone": "+221 33 800 0000",
    "customFooterNote": "Merci pour votre achat sur SwiftMarket!"
  }
}
```

#### 5.2 Dynamic Checkout Rendering Engine

1. **CNAME & Tenant Resolution:** When a customer visits `pay.swiftmarket.com`, the API Gateway matches the custom domain against registered tenant configs.
2. **Localization Hydration:** The payment page identifies the user's browser language. If matched against `supportedLocales`, the UI strings (prompts, buttons, error validations) are hydrated instantly in that language (e.g., French for Francophone West Africa, English for East Africa).
3. **Asset Isolation:** All logos and CSS attributes are served through a hardened Content Delivery Network (CDN) with strict Content Security Policy (CSP) headers to avoid cross-site scripting (XSS) via customized properties.

#### Summary of Chunk 2 Deliverables

- Fully documented the tenant lifecycle state machine and transitions.
- Detailed the automated and manual KYB/KYC verification flows with third-party registry verification.
- Established the complete RBAC permission matrix for merchant organizations.
- Designed the dual-key, zero-downtime API credential rotation mechanism.
- Specified the white-label branding, localized checkout, and custom domain routing architecture.

---

## Chunk 3: Country-Specific Payment Method Matrix & Regulatory Localization

### 1. Regional Payment Infrastructure & Rail Diversity

Operating a multi-country payment platform requires abstracting fundamentally different clearing and settlement rails behind a normalized transaction domain. Payment rails vary dramatically across target jurisdictions:

```
┌──────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                   REGIONAL PAYMENT LANDSCAPE                                     │
├──────────────────────┬────────────────────────┬─────────────────────────┬────────────────────────┤
│ Region / Jurisdiction│ Dominant Rail Types    │ Typical Processing Rails│ Regulatory Body / Rule │
├──────────────────────┼────────────────────────┼─────────────────────────┼────────────────────────┤
│ Nigeria (NG)         │ Instant Account-to-Acc,│ NIBSS Instant Payments  │ Central Bank of        │
│                      │ Cards, USSD            │ (NIP), Interswitch,     │ Nigeria (CBN),         │
│                      │                        │ Verve, Mastercard, Visa │ NDPR Compliance        │
├──────────────────────┼────────────────────────┼─────────────────────────┼────────────────────────┤
│ Kenya (KE)           │ Mobile Money Wallets,  │ Safaricom M-Pesa (Daraja│ Central Bank of        │
│                      │ Cards                  │ API), Airtel Money,     │ Kenya (CBK),           │
│                      │                        │ Pesalink, Visa/Mastercard│ Kenya Data Protection │
├──────────────────────┼────────────────────────┼─────────────────────────┼────────────────────────┤
│ South Africa (ZA)    │ Cards, Instant EFT     │ BankservAfrica, Ozow,   │ SARB, PASA,            │
│                      │                        │ Capitec Pay, Visa/MC    │ POPIA Compliance       │
├──────────────────────┼────────────────────────┼─────────────────────────┼────────────────────────┤
│ Ghana (GH)           │ Mobile Money, Cards,   │ GhIPSS (GhQR, GIP),     │ Bank of Ghana (BoG),   │
│                      │ USSD                   │ MTN MoMo, Telecel Cash  │ Data Protection Act    │
├──────────────────────┼────────────────────────┼─────────────────────────┼────────────────────────┤
│ Pan-Europe (SEPA/GB) │ Credit Transfer, Direct│ SEPA Instant, Bacs,     │ ECB, FCA, PSD2 / PSR,  │
│                      │ Debit, Cards           │ Faster Payments, Cards  │ GDPR, Strong Auth (SCA)│
├──────────────────────┼────────────────────────┼─────────────────────────┼────────────────────────┤
│ United States (US)   │ Cards, ACH Transfers   │ FedNow, RTP, Nacha ACH, │ FinCEN, OCC,           │
│                      │                        │ Card Brand Networks     │ NACHA Rules, State MTL │
└──────────────────────┴────────────────────────┴─────────────────────────┴────────────────────────┘
```

---

### 2. Country-to-Payment Method Mapping Matrix

To prevent invalid transaction attempts, the system decouples payment methods from geographic jurisdictions using an explicit availability matrix (`CountryPaymentMethod`).

```
                              ┌─────────────────────────┐
                              │         Country         │
                              │ (iso2, iso3, currency)  │
                              └────────────┬────────────┘
                                           │ 1
                                           │
                                           ▼ *
                              ┌─────────────────────────┐
                              │  CountryPaymentMethod   │
                              │ ─────────────────────── │
                              │ * min_amount            │
                              │ * max_amount            │
                              │ * supports_recurring    │
                              │ * supports_refunds      │
                              │ * requires_customer_kyc │
                              │ * is_active             │
                              └───────┬─────────┬───────┘
                                      │ *       │ *
            ┌─────────────────────────┘         └─────────────────────────┐
            ▼ 1                                                           ▼ 1
┌────────────────────────┐                                     ┌────────────────────────┐
│     PaymentMethod      │                                     │        Currency        │
│ ────────────────────── │                                     │ ────────────────────── │
│ * code: "MPESA_EXPRESS"│                                     │ * code: "KES"          │
│ * category: MOB_MONEY  │                                     │ * decimal_places: 2    │
└────────────────────────┘                                     └────────────────────────┘
```

#### 2.1 Standard Payment Method Catalog

```sql
-- Seed examples for Master Payment Methods
INSERT INTO payment_method (id, code, name, category, is_active) VALUES
('pm-001', 'CARD_GLOBAL',       'International / Domestic Card', 'CARD',          true),
('pm-002', 'MPESA_STK_PUSH',     'M-Pesa STK Express',            'MOBILE_MONEY',  true),
('pm-003', 'MTN_MOMO_COLLECT',   'MTN Mobile Money Collections',  'MOBILE_MONEY',  true),
('pm-004', 'AIRTEL_MONEY_PAY',   'Airtel Money Push',             'MOBILE_MONEY',  true),
('pm-005', 'NG_DIRECT_BANK_NIP', 'Nigeria Instant Bank Transfer', 'BANK_TRANSFER', true),
('pm-006', 'NG_USSD_PAY',        'Bank USSD String Checkout',     'USSD',          true),
('pm-007', 'ZA_OZOW_EFT',        'Ozow Instant EFT',              'BANK_TRANSFER', true),
('pm-008', 'EU_SEPA_INSTANT',    'SEPA Instant Credit Transfer',  'BANK_TRANSFER', true),
('pm-009', 'US_ACH_DIRECT_DEBIT','ACH Electronic Bank Transfer',  'BANK_TRANSFER', true);
```

#### 2.2 Concrete Country-Method Availability Matrix

| Country (ISO2) | Payment Method Code | Currency | Min Txn Amount | Max Txn Amount | Supports Recurring | Supports Instant Refund |
|----------------|---------------------|----------|----------------|----------------|-------------------|------------------------|
| KE | `MPESA_STK_PUSH` | KES | 10.00 | 250,000.00 | Yes (M-Pesa Ratiba) | Yes |
| KE | `CARD_GLOBAL` | KES | 100.00 | 1,000,000.00 | Yes | Yes |
| NG | `NG_DIRECT_BANK_NIP` | NGN | 100.00 | 50,000,000.00 | No | No (Requires Payout) |
| NG | `CARD_GLOBAL` | NGN | 100.00 | 10,000,000.00 | Yes (Tokenized) | Yes |
| NG | `NG_USSD_PAY` | NGN | 100.00 | 100,000.00 | No | No |
| GH | `MTN_MOMO_COLLECT` | GHS | 1.00 | 20,000.00 | Yes | Yes |
| ZA | `ZA_OZOW_EFT` | ZAR | 10.00 | 500,000.00 | No | No |
| DE | `EU_SEPA_INSTANT` | EUR | 1.00 | 100,000.00 | Yes (SEPA Mandate) | Yes |

---

### 3. Dynamic Checkout Rail Discovery Algorithm

When a tenant's application or consumer browser requests a payment session, the system runs a filtering pipeline to return only valid rails based on the shopper's location, currency, amount, and the tenant's subscription tier.

**Incoming Request:**

```json
{
  "tenantId": "t-10029",
  "amount": 4500.00,
  "currency": "KES",
  "customerCountry": "KE",
  "customerIp": "102.219.208.1"
}
```

```
                                │
                                ▼
         ┌──────────────────────────────────────────────┐
         │ Step 1: Resolve Target Country & Currency    │
         │ - Validate KES is active in country KE       │
         └──────────────────────┬───────────────────────┘
                                │
                                ▼
         ┌──────────────────────────────────────────────┐
         │ Step 2: Query Active CountryPaymentMethods   │
         │ - Select all where country_id = 'KE'         │
         │   AND allowed_currency = 'KES'               │
         │   AND is_active = true                       │
         └──────────────────────┬───────────────────────┘
                                │
                                ▼
         ┌──────────────────────────────────────────────┐
         │ Step 3: Filter by Tenant Allowed Methods     │
         │ - Check if Tenant has disabled specific     │
         │   methods (e.g., opted out of Mobile Money)  │
         └──────────────────────┬───────────────────────┘
                                │
                                ▼
         ┌──────────────────────────────────────────────┐
         │ Step 4: Validate Transaction Amount Bounds   │
         │ - Filter where amount BETWEEN min AND max    │
         │   (4500.00 is valid for M-Pesa & Cards)      │
         └──────────────────────┬───────────────────────┘
                                │
                                ▼
         ┌──────────────────────────────────────────────┐
         │ Output: Available Rails Payload              │
         │ -> [MPESA_STK_PUSH, CARD_GLOBAL]             │
         └──────────────────────────────────────────────┘
```

---

### 4. Regulatory Localization & Compliance Rules

Each jurisdiction imposes distinct requirements on authentication, data residency, identity collection, and transaction surcharges.

#### 4.1 Authentication Protocols & Customer Verification

- **Nigeria (CBN 3D-Secure 2.0 & Tokenization):** All domestic card processing requires two-factor authentication (3DS / OTP verification). Card tokenization must comply with CBN guidelines regarding sensitive card data storage.
- **Kenya (M-Pesa STK Authentication):** Payment is pushed to the customer's phone via an unencrypted or encrypted USSD/SIM ToolKit push prompt; the customer enters their secret M-Pesa PIN directly on their handset. No PIN data ever crosses the SaaS gateway.
- **European Union (PSD2 / 3DS2 SCA):** Strong Customer Authentication (SCA) is mandatory for electronic payments within the European Economic Area, requiring two of three elements: Knowledge (password/PIN), Possession (phone/hardware token), and Inherence (biometrics). Exemption engines (e.g., low-value payments < €30, Transaction Risk Analysis) can bypass SCA if the acquirer supports it.

#### 4.2 Data Residency & Cross-Border Sovereign Data Handling

To adhere to national data protection mandates (such as Nigeria's NDPR/NDPA, Kenya's Data Protection Act 2019, and EU GDPR):

```
                        ┌────────────────────────────────────────┐
                        │        Ingress Traffic Router          │
                        └───────────────────┬────────────────────┘
                                            │
                    ┌───────────────────────┴───────────────────────┐
                    ▼                                               ▼
     [ Nigerian Traffic (NG) ]                       [ Rest of the World (Global) ]
     ├─► Customer PII & NIN                         ├─► Global Tenant DB
     ├─► Encrypted at Rest                          ├─► Shared Monolith Instance
     ├─► Localized Read Replica                     └─► Multi-region Redundancy
     └─► Resident audit storage
```

- **PII Masking:** Direct personal identifiers (National Identification Numbers, Kenyan ID numbers, Social Security Numbers, full card PANs) are encrypted at rest using AES-GCM-256 with tenant-isolated or country-isolated data keys managed in a Hardware Security Module (HSM).
- **Cross-Border Transfer Restrictions:** Financial audit trails containing sensitive resident identity records can be routed to localized database instances or storage buckets where sovereign borders mandate in-country persistence.

---

### 5. CountryPaymentMethod Configuration Schema

The availability matrix entity is configured with fine-grained controls:

```sql
CREATE TABLE country_payment_method (
    id VARCHAR(64) PRIMARY KEY,
    country_iso2 VARCHAR(2) NOT NULL REFERENCES country(iso2),
    payment_method_code VARCHAR(50) NOT NULL REFERENCES payment_method(code),
    currency_code VARCHAR(3) NOT NULL REFERENCES currency(code),
    min_transaction_amount NUMERIC(18, 4) NOT NULL DEFAULT 1.0000,
    max_transaction_amount NUMERIC(18, 4) NOT NULL,
    supports_recurring BOOLEAN NOT NULL DEFAULT false,
    supports_refunds BOOLEAN NOT NULL DEFAULT true,
    requires_customer_phone BOOLEAN NOT NULL DEFAULT false,
    requires_customer_email BOOLEAN NOT NULL DEFAULT true,
    settlement_cycle_days INT NOT NULL DEFAULT 1, -- e.g., T+1, T+2
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_country_method_currency UNIQUE (country_iso2, payment_method_code, currency_code)
);

CREATE INDEX idx_cpm_lookup ON country_payment_method(country_iso2, currency_code, is_active);
```

#### Summary of Chunk 3 Deliverables

- Mapped the multi-country payment landscape spanning Cards, Mobile Money, Instant EFT, and USSD.
- Formalized the `CountryPaymentMethod` availability matrix decoupling currencies and countries from rail logic.
- Defined the step-by-step dynamic rail discovery algorithm for checkout sessions.
- Documented regulatory authentication mandates (3DS2, M-Pesa STK prompts, SCA exemptions) and data protection localization frameworks.

---

## Chunk 4: Gateway Adapter Architecture, Regional Integrations & Dynamic Smart Routing Engine

### 1. Adapter Design Pattern & Normalization Strategy

To prevent downstream third-party payment provider changes (e.g., Paystack, Flutterwave, Stripe, Adyen, Safaricom Daraja) from bleeding into the core business logic, the platform implements the **Adapter Design Pattern** mediated by an **Abstract Factory**.

```
                               ┌─────────────────────────┐
                               │   Transaction Service   │
                               └────────────┬────────────┘
                                            │ Executes normalized contract
                                            ▼
                               ┌─────────────────────────┐
                               │   PaymentGatewayPort    │
                               │       (Interface)       │
                               └────────────┬────────────┘
                                            │
                                            ▼ Instantiated via
                               ┌─────────────────────────┐
                               │  GatewayAdapterFactory  │
                               └────────────┬────────────┘
                                            │
        ┌───────────────────┬───────────────┴───────────────┬───────────────────┐
        ▼                   ▼                               ▼                   ▼
┌───────────────┐   ┌───────────────┐               ┌───────────────┐   ┌───────────────┐
│PaystackAdapter│   │FlutterwaveAdp │               │ StripeAdapter │   │ DarajaAdapter │
│ (Cards, NIP)  │   │(MoMo, Barter) │               │(Global Cards) │   │ (M-Pesa KE)   │
└───────┬───────┘   └───────┬───────┘               └───────┬───────┘   └───────┬───────┘
        │                   │                               │                   │
        ▼                   ▼                               ▼                   ▼
[Paystack API]      [Flutterwave API]               [Stripe API]        [Safaricom API]
```

#### 1.1 Normalized Gateway Adapter Interface (`PaymentGatewayPort`)

Every external provider integration implements a strict interface contract. Core processing services interact exclusively with this abstraction:

```java
public interface PaymentGatewayPort {
    
    /**
     * Initializes or authorizes a payment transaction.
     */
    GatewayAuthorizationResponse authorize(GatewayAuthorizationRequest request);

    /**
     * Captures a previously pre-authorized charge.
     */
    GatewayCaptureResponse capture(String gatewayTransactionId, BigDecimal amount);

    /**
     * Queries transaction status directly from the provider (Reconciliation/Polling).
     */
    GatewayStatusResponse queryStatus(String gatewayTransactionId);

    /**
     * Initiates a full or partial refund to the customer.
     */
    GatewayRefundResponse refund(GatewayRefundRequest request);

    /**
     * Decodes and validates inbound webhooks from this provider.
     */
    NormalizedWebhookEvent parseWebhook(String rawPayload, Map<String, String> headers);
}
```

#### 1.2 Data Normalization Pipeline

Downstream providers use vastly disparate naming conventions and formats (e.g., amounts in cents vs. standard decimals, HTTP webhooks with different signature algorithms). The adapter standardizes all external models into canonical domain objects:

```
[ Downstream Provider ]                                  [ Core Domain ]
Paystack:   { "status": "success", "amount": 50000 }  ──►  TransactionStatus: SUCCESSFUL
Stripe:     { "status": "succeeded", "amount": 500 }  ──►  Amount: 5.00
Flutterwave:{ "status": "successful", "id": 98124 }   ──►  GatewayTxId: "98124"
```

---

### 2. Dynamic Smart Routing Engine

The Routing Engine decides which gateway adapter processes a transaction to maximize authorization rates and minimize interchange fees.

```
                       ┌───────────────────────────────┐
                       │   Incoming Transaction DTO    │
                       │ (Amount, Currency, Country,   │
                       │  Bin/Card Brand, Tenant ID)   │
                       └───────────────┬───────────────┘
                                       │
                                       ▼
                       ┌───────────────────────────────┐
                       │     Routing Rule Evaluator    │
                       └───────────────┬───────────────┘
                                       │
                ┌──────────────────────┴──────────────────────┐
                ▼                                             ▼
  ┌───────────────────────────┐                 ┌───────────────────────────┐
  │   Tenant Custom Rules     │                 │   Platform Global Rules   │
  │ (Higher Priority, 1-100)  │                 │ (Default Fallback, 101+)  │
  └─────────────┬─────────────┘                 └─────────────┬─────────────┘
                │                                             │
                └──────────────────────┬──────────────────────┘
                                       │
                                       ▼ Selected Route
                        ┌──────────────────────────────┐
                        │ Primary Adapter: Paystack    │
                        │ Fallback Adapter: Flutterwave│
                        │ Max Retries: 2               │
                        └──────────────────────────────┘
```

#### 2.1 Routing Rule Schema & Criteria Evaluation

Routing rules evaluate incoming transaction context using prioritized criteria matching:

```sql
CREATE TABLE routing_rule (
    id VARCHAR(64) PRIMARY KEY,
    tenant_id VARCHAR(64) REFERENCES corporate_tenant(id), -- NULL if global platform rule
    rule_name VARCHAR(100) NOT NULL,
    priority INT NOT NULL,                                 -- Lower number = Higher evaluation priority
    country_iso2 VARCHAR(2) REFERENCES country(iso2),
    currency_code VARCHAR(3) REFERENCES currency(code),
    payment_method_code VARCHAR(50) REFERENCES payment_method(code),
    card_brand VARCHAR(20),                                -- e.g., 'VISA', 'MASTERCARD', 'VERVE'
    min_amount NUMERIC(18, 4),
    max_amount NUMERIC(18, 4),
    primary_adapter_id VARCHAR(64) NOT NULL REFERENCES gateway_adapter(id),
    fallback_adapter_id VARCHAR(64) REFERENCES gateway_adapter(id),
    max_retry_attempts INT NOT NULL DEFAULT 1,
    is_active BOOLEAN NOT NULL DEFAULT true
);
```

**Rule Evaluation Order:**

1. **Tenant-Specific Overrides:** If a tenant configured custom routing for a specific card brand or currency, those take precedence (`tenant_id IS NOT NULL`).
2. **Specific Criteria Matching:** Rules with matching Country + Currency + PaymentMethod + Amount Range.
3. **Card Brand Specialization:** Domestic Verve cards in Nigeria route to a domestic switch (e.g., Paystack/Interswitch); international Mastercard/Visa route to global acquirers (e.g., Stripe/Adyen).
4. **Global Fallback:** Default catch-all rules route remaining volume to the platform's primary acquirer.

---

### 3. Acquirer Health Check, Circuit Breakers & Failover Architecture

To protect conversion rates, the platform integrates **Resilience4j circuit breakers** and rolling window health checks across all adapters.

```
       Incoming Charge
              │
              ▼
    [ Circuit Breaker Check ]
              │
       Is Primary Open?
         /          \
      NO (Healthy)   YES (Degraded/Down)
       /              \
      ▼                ▼
[ Primary Route ]    [ Failover Route ]
(e.g., Paystack)     (e.g., Flutterwave)
      │
 Does it timeout/fail with 5xx?
      │
     YES ────────────► Trip circuit breaker, increment error rate,
                       retry seamlessly on Failover Route
```

#### 3.1 Failure Classification & Retry Semantics

The platform distinguishes between retriable and terminal failures before dispatching to a secondary adapter:

| Error Category | HTTP Code / Provider Code | Retriable? | Action |
|----------------|---------------------------|------------|--------|
| Gateway Timeout | `504 Gateway Timeout`, `SocketTimeout` | **YES** | Immediately route to Fallback Adapter with the same idempotency key. |
| Provider Server Error | `500 Internal Error`, `502 Bad Gateway` | **YES** | Route to Fallback Adapter; record provider failure metric. |
| Insufficient Funds | `402 Payment Required`, `INSUFFICIENT_FUNDS` | **NO** | Terminal failure; immediately report to customer (fail fast). |
| Stolen / Lost Card | `403 Forbidden`, `FRAUD_PICKUP_CARD` | **NO** | Terminal failure; trigger fraud logging. |
| Incorrect 3DS / PIN | `INVALID_CREDENTIALS`, `PIN_ERROR` | **NO** | Terminal failure; prompt customer for re-entry. |

#### 3.2 Sliding Window Circuit Breaker Settings

- **Failure Rate Threshold:** 50% failures over a rolling window of 100 transactions trips the primary adapter to `OPEN`.
- **Slow Call Threshold:** Any call taking > 8000 ms is marked slow; 60% slow calls trips the breaker.
- **Wait Duration in Open State:** 30 seconds, after which it enters `HALF_OPEN` allowing 10 probe transactions through.
- **Automatic Eviction:** While `OPEN`, all traffic routes to the secondary fallback adapter with zero delay.

---

### 4. Gateway Credential Vault & Tenant Integration Models

Tenants can either use the **Platform Master Acquirer Accounts** (the platform handles merchant-of-record processing) or configure **Bring-Your-Own-Credentials (BYOC)**.

```
                             ┌───────────────────────────────┐
                             │       GatewayAdapter          │
                             ├───────────────────────────────┤
                             │ • id: UUID                    │
                             │ • provider_name: String       │
                             │ • credentials_config: TextBlob│ (Encrypted Vault)
                             │ • is_tenant_override: Boolean │
                             │ • tenant_id: UUID (Nullable)  │
                             └───────────────────────────────┘
```

#### 4.1 AES-256 Envelope Encryption for Stored Provider Credentials

All credentials stored in `credentials_config` (API Secret Keys, Webhook Secrets, Public Keys) are encrypted before writing to PostgreSQL:

- **Key Encryption Key (KEK):** Stored in AWS KMS or HashiCorp Vault.
- **Data Encryption Key (DEK):** Generated per corporate tenant, used to encrypt credentials via AES-GCM-256 with an authenticated authentication tag (AEAD). Plaintext secrets are never logged or exposed via administrative APIs.

#### Summary of Chunk 4 Deliverables

- Standardized external gateway APIs into a unified `PaymentGatewayPort` contract.
- Designed the dynamic smart routing algorithm matching transactions by country, currency, brand, and tenant rules.
- Engineered automated failover mechanisms utilizing circuit breakers and failure classifications.
- Documented secure credential storage for Platform-owned and BYOC processing models.

---

## Chunk 5: End-to-End Transaction Processing Engine, State Machine & Tokenization

### 1. Transaction Processing Architecture & State Machine

The Transaction Processing Engine orchestrates payment initiation, risk scoring, adapter dispatch, asynchronous webhooks, and ledger updates.

#### 1.1 Strict Transaction State Machine

To guarantee financial consistency and prevent illegal state jumps, transaction state transitions are strictly governed:

```
                            ┌────────────────┐
                            │   INITIATED    │
                            └───────┬────────┘
                                    │
                                    ▼
                            ┌────────────────┐
                            │    PENDING     │◄──────────────┐
                            └───────┬────────┘               │
                                    │                        │ (3DS Redirect /
                 ┌──────────────────┼──────────────────┐     │  STK Push Wait)
                 │                  │                  │     │
                 ▼                  ▼                  ▼     │
         ┌──────────────┐   ┌──────────────┐   ┌──────────────┐
         │  SUCCESSFUL  │   │    FAILED    │   │  CANCELLED   │
         └───────┬──────┘   └──────────────┘   └──────────────┘
                 │
        ┌────────┴────────┐
        ▼                 ▼
 ┌──────────────┐  ┌──────────────┐
 │PARTIALLY_REF.│  │   REFUNDED   │
 └──────┬───────┘  └──────────────┘
        │                 ▲
        └─────────────────┘ (Subsequent refund)
```

**State Transition Rules:**

- `INITIATED → PENDING`: Request validated, idempotency key locked in Redis, fee and FX calculations completed.
- `PENDING → SUCCESSFUL`: Synchronous capture approval OR verified asynchronous webhook signature confirmed from downstream acquirer.
- `PENDING → FAILED`: Terminal declined code received, maximum retry/failover attempts exhausted, or transaction authorization timed out.
- `PENDING → CANCELLED`: Customer explicitly abandoned the checkout session or failed 3DS verification challenge window.
- `SUCCESSFUL → REFUNDED / PARTIALLY_REFUNDED`: Funds returned to source rail upon merchant or dispute trigger.

---

### 2. End-to-End Sequence Diagram (Card 3DS & Mobile Money STK)

```
Shopper / Browser       Tenant Backend       Gateway Engine          Acquirer Adapter       Payment Rail (Bank/Telco)
       │                      │                     │                       │                       │
       │── 1. Init Checkout ─►│                     │                       │                       │
       │                      │── 2. POST /charges ─►│                      │                       │
       │                      │  (Idempotency-Key)  │                       │                       │
       │                      │                     │── 3. Evaluate Route ─►│                       │
       │                      │                     │── 4. Tokenize Card ──►│                       │
       │                      │                     │── 5. Dispatch Charge ─►│                       │
       │                      │                     │                       │── 6. Push 3DS/STK ────►│
       │                      │                     │                       │                        │
       │◄─────────────────────┴── 7. Redirect URL / Prompt Trigger ─────────┴────────────────────────┤
       │                                                                                             │
       │── 8. Complete PIN / OTP Authentication on Handset or Bank Page ────────────────────────────►│
       │                                                                                             │
       │                                            │                       │◄── 9. Payment Event ───│
       │                                            │◄── 10. Webhook Ingest─│    (Success)          │
       │                                            │    (Verify HMAC)      │                       │
       │                                            │                       │                       │
       │                                            ├── 11. Transition: SUCCESSFUL                  │
       │                                            ├── 12. Credit Tenant Wallet (Ledger)           │
       │                                            │                                               │
       │◄── 13. UI Redirect / Confirmation ─────────┤                                               │
       │                                            │                                               │
       │                      │◄── 14. Signed Webhook Notification (HMAC-SHA256)                    │
       │                      │   (POST tenant.com/webhook)                                         │
```

---

### 3. Distributed Idempotency Engine

To prevent accidental double-billing due to network disconnects, browser refreshes, or automated merchant retries, every mutating transaction endpoint enforces strict distributed idempotency.

```
Incoming Request (Header: Idempotency-Key: "idemp_abc123")
                          │
                          ▼
             [ Query Redis by Composite Key ]
         ("idemp:" + tenant_id + ":" + idempotency_key)
                          │
           ┌──────────────┴──────────────┐
           ▼                             ▼
      Key Exists?                   Key Not Found?
           │                             │
    ┌──────┴──────┐                      │
    ▼             ▼                      ▼
Status:        Status:            [ Set Redis Key ]
"IN_FLIGHT"    "COMPLETED"        Status: "IN_FLIGHT"
    │             │               TTL: 120 seconds
    │             ▼                      │
    │        Return Cached               ▼
    │        HTTP Response        Proceed with Transaction
    ▼                             Processing Pipeline
Reject with 409 Conflict                 │
("Transaction in progress")              ▼
                                  [ On Completion ]
                                  Update Redis Key:
                                  Status: "COMPLETED",
                                  Payload: JSON(Response),
                                  TTL: 86400 seconds (24h)
```

---

### 4. Payment Tokenization & PCI-DSS Scope Elimination

To protect cardholder data and maintain PCI-DSS SAQ A-EP / SAQ A compliance, raw Primary Account Numbers (PANs), CVVs, and expiration dates never hit platform databases.

```
Customer Browser                 Gateway Hosted Fields / SDK                Tokenizer Vault
       │                                     │                                     │
       │── 1. Enter PAN & CVV into iframe ──►│                                     │
       │                                     │── 2. Direct HTTPS POST Card Data ──►│
       │                                     │      (Isolated Micro-Vault)         │
       │                                     │                                     │
       │                                     │◄── 3. Return One-Time Token ────────│
       │                                     │      "tok_live_9b283748201a"        │
       │                                     │                                     │
       │◄── 4. Inject Token into Form ───────┘                                     │
       │                                                                           │
       ▼                                                                           │
Submit Order to SaaS Gateway                                                       │
Payload: { "paymentToken": "tok_live_9b283748201a", "amount": 1000 }               │
       │                                                                           │
       └──── Gateway Engine resolves token with Acquirer without handling PAN ────►│
```

#### 4.1 Payment Token Storage Spec

In the Transaction entity, card data is stored exclusively as:

- `payment_token`: Deterministic, encrypted reference (`tok_live_83910...`).
- `card_brand`: `VISA`, `MASTERCARD`, `VERVE`, etc.
- `card_last_four`: `4242` (Masked for customer receipts).
- `card_expiry_month_year`: `12/28` (For recurring eligibility checks).
- **CVV/CVC:** Stored in volatile memory only during the authorization flight; never written to disk or logs.

---

### 5. Webhook Ingestion & Outbound Dispatch Architecture

#### 5.1 Inbound Acquirer Webhooks

1. Acquirer calls `POST /api/v1/webhooks/adapters/{providerName}`.
2. Ingestor checks provider signature (e.g., `x-paystack-signature`, `stripe-signature`) against the provider's secret key.
3. If valid, payload is pushed to an in-memory queue/event bus to decouple external ingestion from internal database processing, returning `200 OK` within 200 ms.

#### 5.2 Outbound Tenant Webhook Delivery Engine

Once a transaction transitions to `SUCCESSFUL` or `FAILED`, the gateway notifies the merchant:

```
[ Transaction Engine ] ──► [ Webhook Dispatcher ]
                                   │
                                   ├─► 1. Sign Payload: HMAC-SHA256(payload, tenant.webhook_secret)
                                   ├─► 2. Add Header: "X-Gateway-Signature: sha256=..."
                                   ├─► 3. Add Header: "X-Gateway-Timestamp: 1773489438"
                                   │
                                   ▼ POST to tenant.webhook_url
                     ┌───────────────────────────┐
                     │ Tenant Endpoint Response? │
                     └─────────────┬─────────────┘
                                   │
                    ┌──────────────┴──────────────┐
                    ▼                             ▼
              HTTP 200 / 204                HTTP 5xx / Timeout
                    │                             │
              Mark Delivered               Exponential Backoff Retry
                                           • Retry 1: +5 minutes
                                           • Retry 2: +30 minutes
                                           • Retry 3: +2 hours
                                           • Retry 4: +24 hours
                                           • Dead Letter Queue (DLQ)
```

#### Summary of Chunk 5 Deliverables

- Mapped the transactional state machine and transition guards.
- Documented end-to-end execution flows for Card 3DS2 and Mobile Money STK push.
- Built the distributed Redis idempotency mechanism to eliminate double-charges.
- Outlined PCI-DSS tokenization boundaries and card masking standards.
- Engineered bidirectional webhook processing with HMAC-SHA256 signing and exponential backoff retries.

---

## Chunk 6: Multi-Currency Tenant Wallets, Double-Entry Accounting & Ledger Mechanics

### 1. Multi-Currency Financial Ledger Principles

To guarantee zero data drift, financial auditability, and mathematically verifiable account balances, the platform enforces an immutable **Double-Entry Bookkeeping Ledger**.

#### 1.1 Core Ledger Commandments

- **Immutability:** Ledger rows are strictly append-only. Rows are never subjected to SQL `UPDATE` or `DELETE` statements. Errors are corrected exclusively through offsetting reversing entries.
- **Balanced Transactions:** Every financial event consists of at least one debit entry and at least one credit entry. The sum of all debits must strictly equal the sum of all credits:

$$\sum \text{Debits} - \sum \text{Credits} = 0$$

- **Currency Purity:** Multi-currency ledgers do not balance across different currency codes directly. A single journal entry must balance within its designated currency, or route through explicit FX clearing intermediary accounts.
- **Isolation of Balances:** Balances are derived projections of immutable journal entries, with cached aggregate snapshots kept for low-latency transaction checks.

---

### 2. Platform Account Chart & Account Hierarchy

The system defines platform-level and tenant-level accounts across the standard financial categories:

```
                                  [ Global Chart of Accounts ]
                                                │
         ┌──────────────────┬───────────────────┼───────────────────┬──────────────────┐
         ▼                  ▼                   ▼                   ▼                  ▼
    1000: ASSETS      2000: LIABILITIES   3000: EQUITY        4000: REVENUE      5000: EXPENSES
         │                  │                                       │                  │
         ├─ Acquirer Settl. ├─ Tenant Avail.                        ├─ Platform Fee    └─ Acquirer
         │  Clearing        │  Wallet                               │  Revenue            Interchange
         │  (Paystack/Stripe│  (Payable to Merch)                   │                     Processing Fees
         │   Receivables)   │                                       │
         │                  ├─ Tenant Locked                        │
         └─ Bank Operating  │  Escrow                               │
            Accounts        │  (Dispute Holds)                      │
                            │                                       │
                            └─ Undistributed                        │
                               Settlement                           │
                               Payables                             │
```

#### 2.1 Account Definitions

| Account Code | Account Name | Type | Currency | Description |
|--------------|--------------|------|----------|-------------|
| `1100_{CUR}_{ADAPTER}` | Acquirer Clearing Account | Asset | Fixed (Per Rail) | Tracks un-settled funds held by downstream acquirers (e.g., Paystack NGN, Stripe USD). |
| `1200_{CUR}_{BANK}` | Bank Settlement Cash Account | Asset | Fixed (Per Bank) | Direct physical fiat balances in platform operating/custodial bank accounts. |
| `2100_{TENANT_ID}_{CUR}` | Tenant Available Balance | Liability | Merchant Specific | Liquid funds owned by the merchant, available for immediate on-demand or scheduled payout. |
| `2200_{TENANT_ID}_{CUR}` | Tenant Locked Dispute Hold | Liability | Merchant Specific | Merchant funds frozen in escrow pending active chargeback or dispute resolution. |
| `4100_{CUR}_FEE` | Platform Processing Revenue | Revenue | Multi-Currency | Gateway fees earned from transactions (fixed fees, volume commissions). |
| `5100_{CUR}_{ADAPTER}` | Gateway Processing Expense | Expense | Multi-Currency | Interchange and direct gateway processing charges billed by external card rails and telcos. |

---

### 3. Double-Entry Journal Entry Workflow for Key Operations

#### 3.1 Successful Payment Collection (Customer Pays $100.00 USD, 2.5% + $0.30 Fee)

A customer makes a successful $100.00 card payment. The platform fee is $2.80 ($2.50 variable + $0.30 fixed). The merchant net proceeds equal $97.20.

**Transaction Event:** `Txn_US_998124`
**Amount:** $100.00 USD | **Fee:** $2.80 USD | **Net:** $97.20 USD

```
Journal Entry:
┌──────────────────────────────┬──────────────────┬─────────────┬──────────────┐
│ Account                      │ Account Type     │ Debit ($)   │ Credit ($)   │
├──────────────────────────────┼──────────────────┼─────────────┼──────────────┤
│ 1100_USD_STRIPE (Acquirer)   │ Asset            │ 100.00      │              │
│ 2100_TENANT_A_USD (Merchant) │ Liability        │             │ 97.20        │
│ 4100_USD_FEE (Platform Rev)  │ Revenue          │             │ 2.80         │
├──────────────────────────────┴──────────────────┼─────────────┼──────────────┤
│ Total Verification:                             │ 100.00      │ 100.00       │
└─────────────────────────────────────────────────┴─────────────┴──────────────┘
```

#### 3.2 Dispute / Chargeback Hold Placed ($100.00 Disputed)

A customer files a dispute. The full original amount of $100.00 is locked from the merchant's available balance into escrow:

```
Journal Entry:
┌──────────────────────────────┬──────────────────┬─────────────┬──────────────┐
│ Account                      │ Account Type     │ Debit ($)   │ Credit ($)   │
├──────────────────────────────┼──────────────────┼─────────────┼──────────────┤
│ 2100_TENANT_A_USD (Available)│ Liability        │ 100.00      │              │
│ 2200_TENANT_A_USD (Hold)     │ Liability        │             │ 100.00       │
├──────────────────────────────┴──────────────────┼─────────────┼──────────────┤
│ Total Verification:                             │ 100.00      │ 100.00       │
└─────────────────────────────────────────────────┴─────────────┴──────────────┘
```

> **Note:** The merchant's net equity remains unaffected, but their withdrawable liquid balance drops by $100.00.

#### 3.3 Dispute Resolved: Merchant Wins

The issuing bank rules in favor of the merchant. Escrowed funds are returned to available balance:

```
Journal Entry:
┌──────────────────────────────┬──────────────────┬─────────────┬──────────────┐
│ Account                      │ Account Type     │ Debit ($)   │ Credit ($)   │
├──────────────────────────────┼──────────────────┼─────────────┼──────────────┤
│ 2200_TENANT_A_USD (Hold)     │ Liability        │ 100.00      │              │
│ 2100_TENANT_A_USD (Available)│ Liability        │             │ 100.00       │
├──────────────────────────────┴──────────────────┼─────────────┴──────────────┤
│ Total Verification:                             │ 100.00      │ 100.00       │
└─────────────────────────────────────────────────┴─────────────┴──────────────┘
```

#### 3.4 Dispute Resolved: Merchant Loses (Chargeback Forfeited + Dispute Surcharge)

The cardholder wins. Funds are transferred back to the acquirer for credit to the customer, and a $15.00 dispute processing fee is assessed against the merchant:

```
Journal Entry:
┌──────────────────────────────┬──────────────────┬─────────────┬──────────────┐
│ Account                      │ Account Type     │ Debit ($)   │ Credit ($)   │
├──────────────────────────────┼──────────────────┼─────────────┼──────────────┤
│ 2200_TENANT_A_USD (Hold)     │ Liability        │ 100.00      │              │
│ 2100_TENANT_A_USD (Available)│ Liability        │ 15.00       │              │
│ 1100_USD_STRIPE (Acquirer)   │ Asset            │             │ 100.00       │
│ 4100_USD_FEE (Platform Rev)  │ Revenue          │             │ 15.00        │
├──────────────────────────────┴──────────────────┼─────────────┼──────────────┤
│ Total Verification:                             │ 115.00      │ 115.00       │
└─────────────────────────────────────────────────┴─────────────┴──────────────┘
```

---

### 4. Wallet Architecture & Schema Design

To balance the computational overhead of scanning historical ledger lines with sub-millisecond response times, the system utilizes a **Snapshot State Model with Pessimistic Locking**.

```
                           ┌──────────────────────────────┐
                           │         TenantWallet         │
                           ├──────────────────────────────┤
                           │ * id: UUID                   │
                           │ * tenant_id: UUID (FK)       │
                           │ * currency_code: String (3)  │
                           │   ledger_balance: Numeric    │
                           │   available_balance: Numeric │
                           │   locked_balance: Numeric    │
                           │   version: Long (Optimistic) │
                           │   updated_at: Instant        │
                           └──────────────┬───────────────┘
                                          │ 1
                                          ▼ *
                           ┌──────────────────────────────┐
                           │      WalletTransaction       │
                           ├──────────────────────────────┤
                           │ * id: UUID                   │
                           │ * wallet_id: UUID (FK)       │
                           │ * journal_batch_id: UUID     │
                           │ * entry_type: CREDIT / DEBIT │
                           │ * balance_bucket: AVAIL/HOLD │
                           │ * amount: Numeric(18, 4)     │
                           │ * running_balance: Numeric   │
                           │ * reference: String (Unique) │
                           │ * created_at: Instant        │
                           └──────────────────────────────┘
```

#### 4.1 Safe Balance Mutation Engine (PostgreSQL Concurrency Guard)

When a transaction updates a wallet balance, raw SQL arithmetic and row-level locking are used to eliminate race conditions under concurrent webhook delivery:

```sql
-- Atomic wallet lock and update during charge credit
BEGIN;

-- Select wallet row with exclusive lock
SELECT id, available_balance, locked_balance, ledger_balance 
FROM tenant_wallet 
WHERE tenant_id = 't-98127' AND currency_code = 'KES' 
FOR UPDATE;

-- Insert Immutable Audit Line
INSERT INTO wallet_transaction (
    id, wallet_id, journal_batch_id, entry_type, balance_bucket, 
    amount, running_balance, reference, created_at
) VALUES (
    gen_random_uuid(), 'w-10293', 'jb-55412', 'CREDIT', 'AVAILABLE', 
    4875.0000, 154875.0000, 'TXN_REF_KE_102', CURRENT_TIMESTAMP
);

-- Update Wallet Cached Balances
UPDATE tenant_wallet 
SET 
    available_balance = available_balance + 4875.0000,
    ledger_balance = ledger_balance + 4875.0000,
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'w-10293';

COMMIT;
```

---

### 5. Automated Reconciliation Engine

The reconciliation worker reconciles physical cash flows from external bank/processor settlement files (MT940, CAMT.053, CSV, and Stripe Balance APIs) with internal ledger states.

```
       [ Acquirer / Bank End-of-Day File ]
       (MT940 / CSV / Settlement Export)
                        │
                        ▼
       ┌─────────────────────────────────┐
       │   Reconciliation Job Runner     │
       │   (Nightly Automated Cron)      │
       └────────────────┬────────────────┘
                        │
                        ▼
       ┌─────────────────────────────────┐
       │   Three-Way Match Verification  │
       │ 1. Internal Transaction Record  │
       │ 2. Internal Ledger Journal Entry│
       │ 3. External Bank Settlement Line│
       └────────────────┬────────────────┘
                        │
           ┌────────────┴────────────┐
           ▼                         ▼
      [ Fully Matched ]         [ Discrepancy Found ]
           │                         │
      Set Status: RECONCILED    Log Reconciliation Exception:
      Mark Settlement Batch:    • Status: MISMATCHED_AMOUNT
      READY_FOR_PAYOUT          • Status: UNMATCHED_EXTERNAL_TX
                                • Alert Finance Team
```

#### 5.1 Reconciliation Mismatch Handlers

- **Amount Discrepancy:** If the acquirer deducted unexpected interchange fees, a journal entry posts the difference to `5100_{CUR}_{ADAPTER}` (Gateway Processing Expense) to balance ledger totals.
- **Missing External Capture:** If a transaction is marked `SUCCESSFUL` internally but omitted from the settlement file, the transaction transitions to `RECONCILIATION_FLAGGED` and is held from merchant payout until resolved.

#### Summary of Chunk 6 Deliverables

- Implemented the strict Double-Entry Bookkeeping Ledger architecture ($\sum \text{Debits} = \sum \text{Credits}$).
- Defined the hierarchical Chart of Accounts spanning assets, liabilities, revenue, and fee expenses.
- Provided journal entry flows for sales, dispute holds, won disputes, and lost chargebacks.
- Designed the high-performance PostgreSQL snapshotting model with `FOR UPDATE` concurrency control.
- Structured the 3-way automated bank reconciliation engine and exception handling.

---

## Chunk 7: Fee Calculation Engine, Payout Schedules & Cross-Border Clearing

### 1. Fee Calculation Engine & Pricing Architecture

The pricing engine allows SaaS operators to monetize the platform via dynamic transaction fees while giving tenants granular control over how charges are absorbed or passed on to consumers.

#### 1.1 Mathematical Formulation of Fees

Every charge processed by the gateway evaluates fixed fees, variable percentages, and regulatory or commercial fee caps:

$$\text{Calculated Fee} = \min\left( (\text{Gross Amount} \times \text{Percentage Fee}) + \text{Fixed Fee}, \; \text{Cap Amount} \right)$$

$$\text{Net Merchant Proceeds} = \text{Gross Amount} - \text{Calculated Fee}$$

**Example:** A transaction of 5,000.00 NGN processed via local card with a 1.5% variable fee, 100.00 NGN fixed fee, and a 2,000.00 NGN cap:

$$\text{Calculated Fee} = \min\left( (5000 \times 0.015) + 100, \; 2000 \right) = \min(75 + 100, 2000) = 175.00 \text{ NGN}$$

$$\text{Net Merchant Proceeds} = 5000 - 175 = 4825.00 \text{ NGN}$$

#### 1.2 Multi-Tiered Fee Configuration Hierarchy

Fees are evaluated using an inheritance model where granular configurations override broader defaults:

```
                      ┌────────────────────────────────────────┐
                      │ Level 1: Global Platform Default Fees  │
                      │ (e.g., 2.9% + $0.30 for International) │
                      └───────────────────┬────────────────────┘
                                          │ Overridden by
                                          ▼
                      ┌────────────────────────────────────────┐
                      │ Level 2: Country-Method Benchmark Fees │
                      │ (e.g., KE M-Pesa: 1.2% Flat, No Cap)   │
                      └───────────────────┬────────────────────┘
                                          │ Overridden by
                                          ▼
                      ┌────────────────────────────────────────┐
                      │ Level 3: Tenant Custom Negotiated Plan │
                      │ (e.g., High-volume merchant: 0.8% flat)│
                      └────────────────────────────────────────┘
```

**Fee Configuration Schema:**

```sql
CREATE TABLE tenant_fee_config (
    id VARCHAR(64) PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL REFERENCES corporate_tenant(id),
    country_payment_method_id VARCHAR(64) NOT NULL REFERENCES country_payment_method(id),
    fixed_fee NUMERIC(18, 4) NOT NULL DEFAULT 0.0000,
    percentage_fee NUMERIC(5, 4) NOT NULL DEFAULT 0.0000, -- e.g., 0.0150 for 1.5%
    cap_amount NUMERIC(18, 4),                           -- NULL = uncapped
    fee_bearer VARCHAR(20) NOT NULL DEFAULT 'MERCHANT',  -- 'MERCHANT' or 'CUSTOMER'
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_tenant_fee_method UNIQUE (tenant_id, country_payment_method_id)
);
```

**Fee Bearer Model:**

- `MERCHANT`: Customer pays the sticker price (e.g., 100.00), and the platform deducts fees (2.80) at settlement. Merchant receives 97.20.
- `CUSTOMER`: The fee is surcharged onto the checkout invoice. The customer is charged 102.80, the merchant receives the full 100.00, and the platform retains 2.80.

---

### 2. Settlement Cycles & Automated Payout Engine

Payout processing settles accrued merchant ledger balances from platform custodial accounts into designated commercial bank accounts or corporate mobile money paybills.

#### 2.1 Settlement Timing Windows

Settlement cadences are enforced via cron-triggered orchestrators based on tenant tiering:

| Frequency Mode | Execution Timing | Cut-Off Window | Use Case |
|----------------|------------------|----------------|----------|
| `DAILY (T+1)` | Nightly at 02:00:00 UTC | Previous day transactions ending 23:59:59 UTC | Standard enterprise merchants |
| `WEEKLY` | Every Monday at 04:00:00 UTC | Preceding week (Mon 00:00 through Sun 23:59) | Low-volume or medium-risk merchants |
| `MONTHLY` | 1st business day of each month | Prior calendar month | Specialized subscription SaaS |
| `MANUAL` | On-demand via API / Dashboard | Instantaneous (subject to velocity limits) | VIP marketplace platforms |

#### 2.2 Payout Execution Workflow

```
[ Scheduled Payout Trigger ]
            │
            ▼
┌───────────────────────────────────────┐
│ 1. Scan Eligible Tenants              │
│ - Check PayoutSchedule.is_active = true│
│ - Current time >= next_payout_date    │
└───────────────────┬───────────────────┘
                    │
                    ▼
┌───────────────────────────────────────┐
│ 2. Evaluate Wallet Balances           │
│ - Lock TenantWallet row (FOR UPDATE)  │
│ - Verify Available Balance >= Threshold│
└───────────────────┬───────────────────┘
                    │
                    ▼
┌───────────────────────────────────────┐
│ 3. Create SettlementBatch & Lock Funds│
│ - Debit Available Balance             │
│ - Credit Undistributed Settlements    │
│ - Set Batch Status: PROCESSING        │
└───────────────────┬───────────────────┘
                    │
                    ▼
┌───────────────────────────────────────┐
│ 4. Dispatch to Banking Payout Rail    │
│ (NIP / Pesalink / SEPA / ACH API)     │
└───────────────────┬───────────────────┘
                    │
         ┌──────────┴──────────┐
         ▼                     ▼
   [ Bank 200 OK ]      [ Bank Rejection ]
         │                     │
  Status: PAID          Status: FAILED
  Emit Webhook          Rollback Wallet Ledger
  Generate MT103/PDF    Alert Operations Desk
```

---

### 3. Cross-Border Remittance & Clearing Pipeline

When a merchant collects payments in localized currencies (e.g., KES via M-Pesa or NGN via Bank Transfer) but requires final settlement in an international hard currency (e.g., USD via SWIFT/ACH), the Cross-Border Clearing Pipeline coordinates conversion and disbursement.

```
       [ Local Collection ]
  Customer pays 150,000.00 KES
               │
               ▼
  [ Local KES Tenant Wallet ]
  Ledger balance increases by 150,000 KES
               │
               ▼ Payout Trigger (Cross-Border Rule)
  ┌────────────────────────────────────────────────────────┐
  │ Foreign Exchange (FX) Conversion Engine                │
  │ • Target Settlement Currency: USD                      │
  │ • Fetch Interbank Base Rate: 1 USD = 130.00 KES        │
  │ • Apply Platform FX Spread (1.5%): 1 USD = 131.95 KES  │
  │ • Converted Settlement: $1,136.79 USD                  │
  │ • Platform FX Margin Revenue: $17.05 USD               │
  └────────────────────────────┬───────────────────────────┘
                               │
                               ▼
  ┌────────────────────────────────────────────────────────┐
  │ Double-Entry Cross-Border Balancing                    │
  │ • Debit: KES Wallet (150,000.00 KES)                   │
  │ • Credit: FX Clearing Intermediary (150,000.00 KES)    │
  │ • Debit: FX Clearing USD Node ($1,136.79 USD)          │
  │ • Credit: USD Outbound Settlement Clearing ($1,136.79) │
  └────────────────────────────┬───────────────────────────┘
                               │
                               ▼
  [ International Wire Dispatch ]
  SWIFT MT103 / Fedwire / Local Partner Rail transfer initiated
```

---

### 4. Minimum Payout Thresholds & Escrow Reserves

To safeguard platform liquidity against sudden chargeback spikes or operational defaults:

- **Threshold Controls:** Payouts do not execute if available funds fail to meet the `PayoutSchedule.threshold_amount` (e.g., minimum 50.00 USD or 10,000.00 NGN) to prevent payout fees from exceeding the disbursement amount.
- **Rolling Reserves:** High-risk merchant verticals can be configured with an automated rolling reserve policy (e.g., 10% of gross daily sales held in the `locked_balance` bucket for 90 to 180 days before releasing into `available_balance`).

#### Summary of Chunk 7 Deliverables

- Standardized arbitrary-precision fee calculations across fixed, percentage, and capped tiers.
- Formulated the merchant vs. customer fee-bearing mechanisms.
- Engineered the automated payout execution lifecycle and scheduling state machine.
- Outlined cross-border multi-currency clearing workflows, spread captures, and international wire dispatches.

---

## Chunk 8: Dispute, Chargeback & Evidence Lifecycle Management

### 1. Chargeback & Dispute Lifecycle Architecture

When a cardholder or account holder files an inquiry, claim, or chargeback with their issuing bank, the gateway isolates the funds, notifies the tenant, and manages dispute resolution.

#### 1.1 Dispute State Machine

```
                              [ Inbound Dispute Webhook ]
                             (from Card Brand / Processor)
                                          │
                                          ▼
                              ┌───────────────────────┐
                              │         OPEN          │
                              │ • Funds auto-locked   │
                              │ • SLA clock starts    │
                              └───────────┬───────────┘
                                          │
                     ┌────────────────────┴────────────────────┐
                     │                                         │
        [ Evidence Uploaded via API/UI ]              [ SLA Window Expires / ]
                     │                                [ Merchant Concedes    ]
                     ▼                                         │
         ┌───────────────────────┐                             │
         │   EVIDENCE_SUBMITTED  │                             │
         └───────────┬───────────┘                             │
                     │                                         │
                     ▼                                         │
         ┌───────────────────────┐                             │
         │     UNDER_REVIEW      │                             │
         │  (Issuer / Network)   │                             │
         └───────────┬───────────┘                             │
                     │                                         │
           ┌─────────┴─────────┐                               │
           │                   │                               │
    [ Issuer Rules ]    [ Issuer Rules ]                       │
      for Merchant        for Customer                         │
           │                   │                               │
           ▼                   ▼                               ▼
  ┌─────────────────┐ ┌─────────────────┐            ┌───────────────────┐
  │       WON       │ │      LOST       │            │      CLOSED       │
  │ • Escrow funds  │ │ • Escrow debited│            │ • Forfeited funds │
  │   unlocked      │ │ • Chargeback fee│            │ • Fee debited     │
  │   to Available  │ │   applied       │            └───────────────────┘
  └─────────────────┘ └─────────────────┘
```

**State Definitions:**

| State | Description |
|-------|-------------|
| `OPEN` | Initiated via acquirer webhook. Transaction funds equal to the disputed amount are automatically moved from the tenant's `available_balance` to `locked_balance`. A dispute deadline (`due_date`) is set based on card network rules (typically 7–14 days). |
| `EVIDENCE_SUBMITTED` | Merchant has compiled and submitted representation evidence (e.g., proof of delivery, logs, signed invoices). Evidence is bundled and dispatched to the upstream acquirer. |
| `UNDER_REVIEW` | The acquirer and card network (Visa, Mastercard, Verve) arbitration committees are evaluating counter-evidence. |
| `WON` | Network resolves dispute in the merchant's favor. The escrow hold is released, restoring funds to `available_balance`. |
| `LOST` | Cardholder claim is upheld. Escrowed funds are permanently transferred to the acquirer for customer refund, and a non-refundable dispute fee is billed to the tenant. |
| `CLOSED` | Merchant accepted the dispute without contest, or the counter-evidence submission deadline lapsed. |

---

### 2. Evidence Submission & Representation Data Schema

Representment requires formatted, tamper-proof documentation matching card network specifications (Visa Resolve Online - VROL, Mastercard MasterCom).

```sql
CREATE TABLE dispute (
    id VARCHAR(64) PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL REFERENCES corporate_tenant(id),
    transaction_id VARCHAR(64) NOT NULL REFERENCES transaction(id),
    case_reference VARCHAR(100) NOT NULL UNIQUE,       -- Processor dispute identifier
    amount NUMERIC(18, 4) NOT NULL,
    currency_code VARCHAR(3) NOT NULL REFERENCES currency(code),
    reason_code VARCHAR(50) NOT NULL,                  -- e.g., '10.4_FRAUD', '13.1_MERCHANDISE_NOT_RECEIVED'
    reason_description TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    due_date TIMESTAMP WITH TIME ZONE NOT NULL,        -- Strict counter-evidence submission deadline
    evidence_submitted_at TIMESTAMP WITH TIME ZONE,
    resolved_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE dispute_evidence (
    id VARCHAR(64) PRIMARY KEY,
    dispute_id VARCHAR(64) NOT NULL REFERENCES dispute(id),
    evidence_type VARCHAR(50) NOT NULL,                -- 'PROOF_OF_DELIVERY', 'CUSTOMER_COMMUNICATION', 'REFUND_POLICY'
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,                   -- 'application/pdf', 'image/jpeg'
    sha256_checksum VARCHAR(64) NOT NULL,              -- Integrity checksum
    uploaded_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
```

#### 2.1 Evidence Upload Payload Contract

When merchants represent disputes programmatically via API (`POST /api/v1/disputes/{caseReference}/evidence`):

```json
{
  "submissionNotes": "Customer signed for delivery on tracking package #FDX-991204 at registered billing address.",
  "evidenceFiles": [
    {
      "evidenceType": "PROOF_OF_DELIVERY",
      "documentUrl": "https://storage.gateway.io/tenants/t-1002/disputes/pod_991204.pdf",
      "checksum": "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
    },
    {
      "evidenceType": "CUSTOMER_COMMUNICATION",
      "documentUrl": "https://storage.gateway.io/tenants/t-1002/disputes/email_thread.pdf",
      "checksum": "5f4dcc3b5aa765d61d8327deb882cf99"
    }
  ]
}
```

---

### 3. Financial Hold Mechanics & Edge-Case Protection

A major risk in multi-tenant payment platforms is **negative wallet balances** caused by chargebacks when a merchant has already withdrawn all available funds.

```
       Dispute Ingested ($500.00)
                   │
                   ▼
┌──────────────────────────────────────┐
│ Evaluate Available Tenant Balance    │
└──────────────────┬───────────────────┘
                   │
       ┌───────────┴───────────┐
       ▼                       ▼
[ Balance >= $500.00 ]   [ Balance < $500.00 ]
       │                       │
Lock full $500.00 into   Lock remaining liquid funds
locked_balance bucket    (e.g., $150.00); set remaining
                         amount ($350.00) as:
                         NEGATIVE AVAILABLE BALANCE
                               │
                               ▼
                         Automated Mitigation Triggers:
                         1. Intercept incoming transaction
                            settlements until balance >= 0.
                         2. If unresolved within 48 hours,
                            trigger auto-debit on linked
                            backup corporate direct debit/card.
                         3. Freeze automated outgoing payouts.
```

---

### 4. Acquirer Dispute Ingestion Sequence

```
Card Network / Acquirer       Gateway Dispute Worker      Tenant Wallet (Ledger)    Merchant Webhook
           │                            │                            │                     │
           │── 1. Webhook: Chargeback ─►│                            │                     │
           │   (case: "dsp_8812")       │                            │                     │
           │                            ├── 2. Create Dispute Row    │                     │
           │                            │      (Status: OPEN)        │                     │
           │                            │                            │                     │
           │                            ├── 3. Place Escrow Hold ───►│                     │
           │                            │      (Debit Avail,         │                     │
           │                            │       Credit Locked)       │                     │
           │                            │                            │                     │
           │                            ├── 4. Send Event ────────────────────────────────►│
           │                            │   "dispute.created"        │                     │
           │                            │   (SLA: due_date)          │                     │
           │                            │                            │                     │
           │                            │◄── 5. Submit Representation Evidence ────────────│
           │                            │                            │                     │
           │── 6. Transmit Evidence ───►│                            │                     │
           │   to Acquirer Representment│                            │                     │
```

#### Summary of Chunk 8 Deliverables

- Mapped complete dispute and chargeback lifecycle states, transitions, and network representation timelines.
- Designed the database schema and data models for disputes, reason codes, and checksummed evidence files.
- Established financial hold safeguards, negative balance handling, and clawback mechanisms for overdrawn tenant accounts.
- Outlined webhook ingestion sequences and automated representment workflows.

---

## Chunk 9: Security, Compliance (PCI-DSS Scoping), AML Risk Engine & Immutable Audit Logging

### 1. Compliance Boundaries & PCI-DSS Scope Reduction

Handling payment card data requires adherence to the Payment Card Industry Data Security Standard (PCI-DSS v4.0). The primary architectural goal of this platform is to reduce the Cardholder Data Environment (CDE) scope to the absolute minimum, keeping the core SaaS monolith operating strictly within PCI-DSS SAQ A-EP or SAQ A boundaries.

#### 1.1 Architectural Boundary Isolation

```
 ┌────────────────────────────────────────────────────────────────────────┐
 │                      OUT OF SCOPE (Zero Card Data)                     │
 │                                                                        │
 │   ┌──────────────────────┐              ┌──────────────────────────┐   │
 │   │  Merchant Dashboard  │              │  Customer Application    │   │
 │   └──────────┬───────────┘              └────────────┬─────────────┘   │
 └──────────────┼───────────────────────────────────────┼─────────────────┘
                │                                       │
                │ HTTPS / REST                          │ Browser TLS 1.3
                ▼                                       ▼
 ┌────────────────────────────────────────────────────────────────────────┐
 │                     SAQ A-EP SCOPE (Tokenized Only)                    │
 │                                                                        │
 │   ┌────────────────────────────────────────────────────────────────┐   │
 │   │                   MODULAR MONOLITH PLATFORM                    │   │
 │   │  • Stores: Masked PANs (411111******1111), Token References    │   │
 │   │  • Processes: Non-sensitive payment metadata, wallet balances  │   │
 │   │  • Rejects: Any payload containing raw PAN, CVV, or Track Data  │   │
 │   └───────────────────────────────┬────────────────────────────────┘   │
 └───────────────────────────────────┼────────────────────────────────────┘
                                     │
               Secure Direct Post    │ Hosted Form Redirection
                                     ▼
 ┌────────────────────────────────────────────────────────────────────────┐
 │                     FULL CDE SCOPE (SAQ D / Level 1)                   │
 │                                                                        │
 │   ┌────────────────────────────────────────────────────────────────┐   │
 │   │            ISOLATED TOKENIZER & DIRECT ACQUIRER SDK            │   │
 │   │  • Hosted Fields / Iframes (Secure Elements)                   │   │
 │   │  • Direct transient transit of PAN/CVV to Acquirer HSM        │   │
 │   │  • Raw card data NEVER enters database persistence or disk logs│   │
 │   └────────────────────────────────────────────────────────────────┘   │
 └────────────────────────────────────────────────────────────────────────┘
```

**Core PCI-DSS Control Implementations:**

- **Zero Persistence Policy:** Primary Account Numbers (PAN), Card Verification Values (CVV/CVC), and PIN blocks are never stored, logged, or cached anywhere within the application database or file storage.
- **Payload Inspection Filters:** An incoming HTTP gateway filter scans all request bodies using regular expressions for Luhn-valid 13–19 digit sequences. If detected outside the dedicated tokenizer proxy endpoint, the request is terminated immediately with `400 Bad Request (CARD_DATA_DETECTED_IN_UNSECURED_CHANNEL)`.
- **Transport Encryption:** All endpoints enforce TLS 1.3 with mandatory HTTP Strict Transport Security (HSTS) with a `max-age` of `31,536,000` seconds. Insecure cipher suites are rejected at the edge load balancer.

---

### 2. Anti-Money Laundering (AML) & Transaction Risk Engine

Every payment and payout transaction undergoes synchronous and asynchronous fraud evaluation to block illicit flows, money laundering, and card testing loops.

```
Incoming Transaction Request
             │
             ▼
┌────────────────────────────────────────┐
│     Rule 1: Sanctions & PEP Check      │──► Match Found? ──► Terminate: STATUS_BLOCKED
└──────────────────┬─────────────────────┘                     (Emit Sanction Alert)
                   │ Clear
                   ▼
┌────────────────────────────────────────┐
│     Rule 2: Velocity & Velocity Spikes │──► Exceeds Limit? ─► Flag: TIER_REVIEW
└──────────────────┬─────────────────────┘                     (Step up to 3DS / Hold)
                   │ Clear
                   ▼
┌────────────────────────────────────────┐
│   Rule 3: Geolocation & Proxy Check    │──► TOR / High Risk IP? ─► Increment Risk Score
└──────────────────┬─────────────────────┘
                   │ Clear
                   ▼
┌────────────────────────────────────────┐
│   Rule 4: Card Testing / BIN Velocity  │──► > 5 failures/min? ──► Terminate: STATUS_BLOCKED
└──────────────────┬─────────────────────┘
                   │
                   ▼
       [ Calculate Total Risk Score ]
                (0 to 100)
                   │
    ┌──────────────┼──────────────┐
    ▼              ▼              ▼
 Score < 30   Score 30-75    Score > 75
    │              │              │
    ▼              ▼              ▼
 [ ALLOW ]    [ CHALLENGE ]    [ REJECT ]
 Auto-pass    Mandatory 3DS/   Auto-decline,
 to Acquirer  Biometric step   record in AMLCheck
```

#### 2.1 AML Risk Rule Evaluator Specifications

| Rule Identifier | Evaluation Vector | Threshold Condition | Dynamic Action |
|-----------------|-------------------|---------------------|----------------|
| `AML_VEL_001` | Single Customer Card Velocity | > 4 attempts per 10 minutes on same fingerprint | Force 3DS2 Challenge; block on 5th attempt |
| `AML_GEO_002` | IP vs Card Issuing Country | IP country $\neq$ Issuer country on transactions > $500 | Increment risk score by +35 points |
| `AML_AMT_003` | Smurfing / Structuring Detection | Multiple payments just below reporting threshold (e.g., $9,900) | Hold funds, trigger regulatory Suspicious Activity Report (SAR) |
| `AML_DEV_004` | Device Fingerprint Anonymity | Tor exit node, commercial VPN, or headless browser detected | Increment risk score by +40 points |

#### 2.2 AML Data Model (`AMLCheck` Entity)

```sql
CREATE TABLE aml_check (
    id VARCHAR(64) PRIMARY KEY,
    transaction_id VARCHAR(64) NOT NULL UNIQUE REFERENCES transaction(id),
    risk_score INT NOT NULL CHECK (risk_score BETWEEN 0 AND 100),
    risk_level VARCHAR(20) NOT NULL, -- 'LOW', 'MEDIUM', 'HIGH', 'CRITICAL'
    triggered_rules JSONB NOT NULL DEFAULT '[]',
    customer_ip VARCHAR(45) NOT NULL,
    ip_country VARCHAR(2),
    is_proxy_or_vpn BOOLEAN NOT NULL DEFAULT false,
    is_blocked BOOLEAN NOT NULL DEFAULT false,
    checked_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_aml_risk_level ON aml_check(risk_level, is_blocked);
```

---

### 3. Immutable Compliance Audit Logging Architecture

To ensure auditability for regulatory inquiries and internal forensics, the platform maintains a cryptographically verified, append-only Audit Log system.

```
Administrative Event / Configuration Mutation / Financial Action
                               │
                               ▼
                ┌───────────────────────────────┐
                │   Audit Interceptor Module    │
                │ • Capture actor identity      │
                │ • Capture origin IP and agent │
                │ • Snapshot before & after JSON│
                └──────────────┬────────────────┘
                               │
                               ▼
                ┌───────────────────────────────┐
                │ Cryptographic Chain Hasher    │
                │ Current Hash = SHA-256(       │
                │   Prev_Hash + Payload + Salt  │
                │ )                             │
                └──────────────┬────────────────┘
                               │
            ┌──────────────────┴──────────────────┐
            ▼                                     ▼
┌───────────────────────────────┐   ┌───────────────────────────┐
│ Primary Database (PostgreSQL) │   │ WORM Storage (AWS S3)     │
│ • Append-only table           │   │ • Object Lock Enabled     │
│ • Strict DENY UPDATE/DELETE   │   │ • Retain for 7 Years      │
└───────────────────────────────┘   └───────────────────────────┘
```

#### 3.1 PostgreSQL Immutability Enforcement via Triggers

To prevent any database administrator or compromised application process from tampering with historical audit rows, a PostgreSQL database trigger blocks modifications:

```sql
CREATE TABLE audit_log (
    id VARCHAR(64) PRIMARY KEY,
    tenant_id VARCHAR(64) REFERENCES corporate_tenant(id),
    action VARCHAR(100) NOT NULL,              -- e.g., 'API_KEY_ROTATED', 'PAYOUT_CONFIG_ALTERED'
    entity_name VARCHAR(100) NOT NULL,         -- 'CorporateTenant', 'RoutingRule'
    entity_id VARCHAR(64) NOT NULL,
    performed_by VARCHAR(150) NOT NULL,        -- 'user:admin@merchant.com' or 'system:payout_worker'
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    change_details JSONB NOT NULL,             -- { "previous": {...}, "current": {...} }
    previous_log_hash VARCHAR(64) NOT NULL,    -- Blockchain-style tamper-evident link
    log_hash VARCHAR(64) NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Trigger function to prohibit updates and deletions
CREATE OR REPLACE FUNCTION enforce_audit_log_immutability()
RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'Security Violation: Audit log entries are strictly immutable and cannot be updated or deleted.';
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_protect_audit_log
BEFORE UPDATE OR DELETE ON audit_log
FOR EACH ROW EXECUTE FUNCTION enforce_audit_log_immutability();
```

---

### 4. Key Management & Data Encryption At Rest (Envelope Encryption)

All sensitive merchant configurations, third-party provider keys, and customer tokens use **Envelope Encryption** powered by an external Key Management Service (AWS KMS, Google Cloud KMS, or HashiCorp Vault).

```
         ┌──────────────────────────────────────┐
         │     Key Management Service (KMS)     │
         │  Holds Master Key Encryption Key     │
         │  (KEK) - Never leaves the HSM        │
         └──────────────────┬───────────────────┘
                            │
               GenerateDataKey(KeySpec=AES_256)
                            │
                            ▼
 ┌──────────────────────────────────────────────────────┐
 │ Application Memory (Volatile)                        │
 │ • Plaintext DEK (Used to encrypt/decrypt credentials)│
 │ • Destroyed immediately after cryptographic operation│
 └──────────────────────────┬───────────────────────────┘
                            │
                            ▼
 ┌──────────────────────────────────────────────────────┐
 │ PostgreSQL Database Storage                          │
 │ • Encrypted Credentials Data (Ciphertext)            │
 │ • Encrypted Data Encryption Key (EDEK)               │
 │ • Initialization Vector (IV / Nonce)                 │
 │ • Authentication Tag (GCM mode verification)         │
 └──────────────────────────────────────────────────────┘
```

- **Algorithm:** AES-256-GCM (Galois/Counter Mode) providing both confidentiality and authenticated integrity.
- **Key Scoping:** Each corporate tenant possesses a unique, dynamically derived data key. Even if an attacker executes an arbitrary SQL dump, decrypting sensitive rows across distinct tenants requires access to individual KMS cryptographic contexts.

#### Summary of Chunk 9 Deliverables

- Established the PCI-DSS SAQ A-EP operational boundaries, zero card data persistence policy, and automated PAN regex sanitizers.
- Engineered the multi-rule AML risk engine evaluating velocity, geolocations, proxy signatures, and smurfing indicators.
- Designed the immutable, cryptographically chained audit logging system backed by PostgreSQL mutation-prevention triggers and WORM storage.
- Specified the envelope encryption architecture for securing sensitive credentials and adapter configurations using AES-256-GCM.

---

## Chunk 10: Integration Strategy, RESTful API Specs, Secure Webhooks & Migration Roadmap

### 1. Developer Integration Experience & Integration Models

The platform provides three primary integration touchpoints designed to fit different merchant architectures:

```
┌──────────────────────────────────────────────────────────────────────────────────┐
│                            DEVELOPER INTEGRATION MODELS                          │
├────────────────────┬─────────────────────────────┬───────────────────────────────┤
│ Integration Model  │ Target Audience             │ PCI Scope & Complexity        │
├────────────────────┼─────────────────────────────┼───────────────────────────────┤
│ **Hosted Checkout**│ Low-code platforms, Shopify/│ **SAQ A**                     │
│                    │ WooCommerce plugins         │ Minimal setup. Customer is    │
│                    │                             │ redirected to platform URL.   │
├────────────────────┼─────────────────────────────┼───────────────────────────────┤
│ **Drop-in SDK /**  │ Custom web & mobile apps    │ **SAQ A-EP**                  │
│ **Hosted Fields**  │ wanting native look & feel  │ Card elements rendered inside │
│                    │                             │ sandboxed iframes.            │
├────────────────────┼─────────────────────────────┼───────────────────────────────┤
│ **Direct Server API**│ Mobile money, USSD, direct │ **Out of PCI Scope**          │
│                    │ bank transfer, internal apps│ Server-to-server operations   │
│                    │                             │ without raw card handling.    │
└────────────────────┴─────────────────────────────┴───────────────────────────────┘
```

---

### 2. Core RESTful API Specifications

All endpoints use standard JSON payloads over HTTPS and require authentication via Bearer tokens (`sk_live_...` or `sk_test_...`).

#### 2.1 Standard Headers

```
Authorization: Bearer sk_live_71f98d6c7b01928374a...
Content-Type: application/json
Idempotency-Key: idemp_99120481-2291-4cf1
X-Gateway-Version: 2026-09-01
```

#### 2.2 Payment Initiation Endpoint

**`POST /api/v1/charges`**

**Request Payload**

```json
{
  "amount": 15000.00,
  "currency": "KES",
  "country": "KE",
  "paymentMethodCode": "MPESA_STK_PUSH",
  "tenantReference": "INV-2026-00918",
  "customer": {
    "email": "customer@example.co.ke",
    "phoneNumber": "+254712345678",
    "name": "Jane Wanjiku"
  },
  "metadata": {
    "orderId": "ord_88129",
    "fulfillmentCenter": "Nairobi-HQ"
  }
}
```

**Response Payload (201 Created)**

```json
{
  "reference": "txn_ke_9817263541",
  "tenantReference": "INV-2026-00918",
  "status": "PENDING",
  "amount": 15000.00,
  "feeAmount": 180.00,
  "netAmount": 14820.00,
  "currency": "KES",
  "actionRequired": {
    "type": "STK_PUSH_TRIGGERED",
    "instruction": "Please enter your M-Pesa PIN on your mobile handset to complete the charge."
  },
  "createdAt": "2026-09-13T17:45:00Z"
}
```

#### 2.3 Card Authorization & Capture Endpoints

- `POST /api/v1/charges/authorize`: Authorizes card funds (holds authorization for 7 days).
- `POST /api/v1/charges/{reference}/capture`: Captures previously authorized funds (supports partial capture).
- `POST /api/v1/charges/{reference}/refund`: Initiates a full or partial refund.

**Refund Request Payload** (`POST /api/v1/charges/txn_ke_9817263541/refund`)

```json
{
  "amount": 5000.00,
  "reason": "CUSTOMER_REQUEST_RETURN",
  "tenantRefundReference": "REF-00192"
}
```

**Response (200 OK)**

```json
{
  "refundReference": "ref_8829104",
  "originalTransactionReference": "txn_ke_9817263541",
  "amountRefunded": 5000.00,
  "currency": "KES",
  "status": "REFUNDED",
  "createdAt": "2026-09-13T17:46:15Z"
}
```

---

### 3. Secure Webhook Delivery Engine & Signature Verification

The gateway sends webhook events to notify merchant systems when asynchronous transactions conclude (e.g., card 3DS challenge completion, M-Pesa push acceptance, bank transfers).

```
 ┌────────────────────────────────────────────────────────┐
 │                   Gateway Dispatcher                   │
 └───────────────────────────┬────────────────────────────┘
                             │
            Compute HMAC-SHA256(payload, secret)
                             │
                             ▼
 ┌────────────────────────────────────────────────────────┐
 │ Outbound HTTP POST Request                             │
 │ Header: X-Gateway-Signature: 8f9b2d...                │
 │ Header: X-Gateway-Timestamp: 1773491178                │
 │ Header: User-Agent: PaymentGateway-Webhook/2.0         │
 └───────────────────────────┬────────────────────────────┘
                             │
                             ▼
 ┌────────────────────────────────────────────────────────┐
 │ Merchant Webhook Listener Endpoint                     │
 │ 1. Reject if timestamp > 300 seconds old (Replay Guard)│
 │ 2. Compute local HMAC-SHA256 and compare constant-time │
 │ 3. Return 200 OK immediately; process in worker thread │
 └────────────────────────────────────────────────────────┘
```

#### 3.1 Merchant Signature Verification Code Sample (Node.js)

```javascript
const crypto = require('crypto');

function verifyWebhookSignature(payloadBuffer, signatureHeader, timestampHeader, secret) {
    // 1. Replay attack guard: verify timestamp within 5 minutes
    const currentTime = Math.floor(Date.now() / 1000);
    if (Math.abs(currentTime - parseInt(timestampHeader, 10)) > 300) {
        throw new Error("Webhook timestamp expired or out of bounds.");
    }

    // 2. Compute signature
    const signedPayload = `${timestampHeader}.${payloadBuffer.toString('utf8')}`;
    const expectedSignature = crypto
        .createHmac('sha256', secret)
        .update(signedPayload)
        .digest('hex');

    // 3. Constant-time comparison to prevent timing attacks
    const isValid = crypto.timingSafeEqual(
        Buffer.from(signatureHeader, 'utf8'),
        Buffer.from(expectedSignature, 'utf8')
    );

    if (!isValid) {
        throw new Error("Invalid HMAC signature.");
    }
    return true;
}
```

---

### 4. Architectural Roadmap: Modular Monolith to Microservices

While the platform launches as a modular monolith to maximize velocity and ACID integrity, the internal module boundaries allow seamless decomposition into autonomous microservices when scaling demands dictate.

```
+-----------------------------------------------------------------------------------------+
|                                    DECOMPOSITION PLAN                                   |
|                                                                                         |
| PHASE 1: Launch               PHASE 2: Scale Separation       PHASE 3: Full Microservices|
| (Current Modular Monolith)    (Extract High-Throughput Rails) (Domain Autonomous Engines)|
|                                                                                         |
| +-------------------------+   +-------------------------+    +-----------------------+  |
| |   Modular Monolith      |   | Monolith (Tenants, KYC, |    | Tenant & Auth Service |  |
| |                         |   | Payouts, Settlement)    |    +-----------------------+  |
| | - Tenant & RBAC         |   +------------┬------------+    +-----------------------+  |
| | - Smart Routing         |                │                 | Smart Routing Service |  |
| | - Adapters              |        Kafka Event Bus           +-----------------------+  |
| | - Ledger & Wallets      |                │                 +-----------------------+  |
| | - Webhooks & Audit      |   +------------┴------------+    | Transaction Processor |  |
| +------------┬------------+   | Transact. Engine Worker |    +-----------------------+  |
|              │                | (High-velocity card/MoMo|    +-----------------------+  |
|     PostgreSQL + Redis        |  ingest & tokenization) │    | Wallet & Ledger Engine|  |
|                               +-------------------------+    +-----------------------+  |
+-----------------------------------------------------------------------------------------+
```

#### 4.1 Step-by-Step Deconstruction Triggers

1. **Step 1: Extract Transaction & Routing Engine**
   - **Trigger:** When transaction throughput exceeds 1,000 writes/sec and starts starving DB pool connections needed by tenant dashboard users.
   - **Action:** Decouple Transaction and `GatewayAdapter` execution into an independent service. The modular monolith's in-memory bus is replaced with Apache Kafka.
2. **Step 2: Isolate the Double-Entry Ledger Engine**
   - **Trigger:** Financial auditing requires strictly partitioned access; ledger writes need horizontal scaling independent of transaction ingest.
   - **Action:** Extract `TenantWallet` and `WalletTransaction` into a dedicated Ledger Service using a dedicated database instance, receiving settlement messages asynchronously over Kafka topics (`transaction.settled`).
3. **Step 3: Webhook & Outbound Event Dispatcher Worker**
   - **Trigger:** Merchant webhook endpoints timing out or responding slowly begins blocking web threads.
   - **Action:** Offload all outbound notifications to a lightweight serverless/worker pool consuming from a dedicated queue with exponential backoff handlers.

---

### Blueprint Overview

This completes the 10-chunk technical architecture series:

| Domain | Coverage |
|--------|----------|
| **Architecture & DB** | Modular Monolith blueprint, PostgreSQL RLS shared-schema data isolation, JDL data models. |
| **Global Domain** | Multi-country matrix, multi-currency support, dynamic FX conversion rates. |
| **Onboarding & Operations** | Automated KYC/KYB screening, RBAC permissions, zero-downtime key rotation. |
| **Routing & Payment Rails** | Acquirer adapters, circuit breakers, failover algorithms, card/mobile money execution lifecycles. |
| **Ledger & Settlement** | Immutable double-entry bookkeeping, atomic balance snapshots, dynamic fee structures, scheduled automated payouts. |
| **Security & Compliance** | SAQ A-EP tokenization, AML velocity engines, hash-chained audit trails, AES-256 envelope encryption. |
| **Integration Strategy** | REST API contracts, signed HMAC webhook delivery, and a phased microservices migration roadmap. |

---

*End of Master Technical Specification*