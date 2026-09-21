import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { RoutingRuleScope } from 'app/entities/enumerations/routing-rule-scope.model';

export interface IRoutingRule {
  id: number;
  priority?: number | null;
  scope?: keyof typeof RoutingRuleScope | null;
  countryCode?: string | null;
  currencyCode?: string | null;
  cardBrand?: string | null;
  primaryAdapter?: string | null;
  fallbackAdapter?: string | null;
  maxRetries?: number | null;
  isActive?: boolean | null;
  tenant?: Pick<ICorporateTenant, 'id'> | null;
}

export type NewRoutingRule = Omit<IRoutingRule, 'id'> & { id: null };
