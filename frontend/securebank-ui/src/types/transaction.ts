export type TransactionType = 'DEBIT' | 'CREDIT';

export interface Transaction {
  transactionId: string;
  accountId: string;
  transactionType: TransactionType;
  amount: number;
  currency: string;
  description: string;
  reference: string;
  createdAt: string;
}

export interface CreateTransactionPayload {
  accountId: string;
  transactionType: TransactionType;
  amount: number;
  currency?: string;
  description: string;
  reference?: string;
}

export interface TransactionFilterParams {
  accountId?: string;
  transactionType?: TransactionType;
  minAmount?: number;
  maxAmount?: number;
  startDate?: string;
  endDate?: string;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
}
