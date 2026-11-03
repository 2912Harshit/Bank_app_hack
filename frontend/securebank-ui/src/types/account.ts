export type AccountType = 'SAVINGS' | 'CURRENT';
export type AccountStatus = 'ACTIVE' | 'FROZEN' | 'CLOSED';

export interface Account {
  accountId: string;
  userId: string;
  customerName: string;
  accountNumber: string;
  accountType: AccountType;
  currency: string;
  balance: number;
  status: AccountStatus;
  createdAt: string;
}

export interface CreateAccountPayload {
  userId: string;
  customerName: string;
  accountType: AccountType;
  currency: string;
}

export interface UpdateAccountStatusPayload {
  status: AccountStatus;
}

