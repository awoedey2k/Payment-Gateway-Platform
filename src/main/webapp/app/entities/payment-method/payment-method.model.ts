import { PaymentMethodCategory } from 'app/entities/enumerations/payment-method-category.model';

export interface IPaymentMethod {
  id: number;
  code?: string | null;
  displayName?: string | null;
  category?: keyof typeof PaymentMethodCategory | null;
}

export type NewPaymentMethod = Omit<IPaymentMethod, 'id'> & { id: null };
