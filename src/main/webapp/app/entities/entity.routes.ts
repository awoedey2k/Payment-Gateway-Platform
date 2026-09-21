import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'user-management',
    title: 'UserManagements',
    loadChildren: () => import('./admin/user-management/user-management.routes'),
  },
  {
    path: 'authority',
    title: 'Authorities',
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'corporate-tenant',
    title: 'CorporateTenants',
    loadChildren: () => import('./corporate-tenant/corporate-tenant.routes'),
  },
  {
    path: 'tenant-director',
    title: 'TenantDirectors',
    loadChildren: () => import('./tenant-director/tenant-director.routes'),
  },
  {
    path: 'api-key',
    title: 'ApiKeys',
    loadChildren: () => import('./api-key/api-key.routes'),
  },
  {
    path: 'tenant-domain',
    title: 'TenantDomains',
    loadChildren: () => import('./tenant-domain/tenant-domain.routes'),
  },
  {
    path: 'currency',
    title: 'Currencies',
    loadChildren: () => import('./currency/currency.routes'),
  },
  {
    path: 'country',
    title: 'Countries',
    loadChildren: () => import('./country/country.routes'),
  },
  {
    path: 'payment-method',
    title: 'PaymentMethods',
    loadChildren: () => import('./payment-method/payment-method.routes'),
  },
  {
    path: 'country-payment-method',
    title: 'CountryPaymentMethods',
    loadChildren: () => import('./country-payment-method/country-payment-method.routes'),
  },
  {
    path: 'forex-rate',
    title: 'ForexRates',
    loadChildren: () => import('./forex-rate/forex-rate.routes'),
  },
  {
    path: 'routing-rule',
    title: 'RoutingRules',
    loadChildren: () => import('./routing-rule/routing-rule.routes'),
  },
  {
    path: 'transaction',
    title: 'Transactions',
    loadChildren: () => import('./transaction/transaction.routes'),
  },
  {
    path: 'refund',
    title: 'Refunds',
    loadChildren: () => import('./refund/refund.routes'),
  },
  {
    path: 'tenant-wallet',
    title: 'TenantWallets',
    loadChildren: () => import('./tenant-wallet/tenant-wallet.routes'),
  },
  {
    path: 'ledger-account',
    title: 'LedgerAccounts',
    loadChildren: () => import('./ledger-account/ledger-account.routes'),
  },
  {
    path: 'journal-entry',
    title: 'JournalEntries',
    loadChildren: () => import('./journal-entry/journal-entry.routes'),
  },
  {
    path: 'journal-line',
    title: 'JournalLines',
    loadChildren: () => import('./journal-line/journal-line.routes'),
  },
  {
    path: 'tenant-fee-config',
    title: 'TenantFeeConfigs',
    loadChildren: () => import('./tenant-fee-config/tenant-fee-config.routes'),
  },
  {
    path: 'payout-schedule',
    title: 'PayoutSchedules',
    loadChildren: () => import('./payout-schedule/payout-schedule.routes'),
  },
  {
    path: 'settlement-batch',
    title: 'SettlementBatches',
    loadChildren: () => import('./settlement-batch/settlement-batch.routes'),
  },
  {
    path: 'dispute',
    title: 'Disputes',
    loadChildren: () => import('./dispute/dispute.routes'),
  },
  {
    path: 'dispute-evidence',
    title: 'DisputeEvidences',
    loadChildren: () => import('./dispute-evidence/dispute-evidence.routes'),
  },
  {
    path: 'aml-check',
    title: 'AmlChecks',
    loadChildren: () => import('./aml-check/aml-check.routes'),
  },
  {
    path: 'audit-log-entry',
    title: 'AuditLogEntries',
    loadChildren: () => import('./audit-log-entry/audit-log-entry.routes'),
  },
  {
    path: 'webhook-subscription',
    title: 'WebhookSubscriptions',
    loadChildren: () => import('./webhook-subscription/webhook-subscription.routes'),
  },
  {
    path: 'webhook-delivery-attempt',
    title: 'WebhookDeliveryAttempts',
    loadChildren: () => import('./webhook-delivery-attempt/webhook-delivery-attempt.routes'),
  },
  // jhipster-needle-add-entity-route - JHipster will add entity modules routes here
];

export default routes;
