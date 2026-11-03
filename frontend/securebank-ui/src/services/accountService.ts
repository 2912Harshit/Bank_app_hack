import { request } from './apiClient';
import { Account, CreateAccountPayload, UpdateAccountStatusPayload } from '../types/account';

const ACCOUNT_API_BASE = import.meta.env.VITE_ACCOUNT_SERVICE_URL || 'http://localhost:8081/api';

export const accountService = {
  async listAccounts(userId?: string, status?: string): Promise<Account[]> {
    const params = new URLSearchParams();
    if (userId) params.append('userId', userId);
    if (status) params.append('status', status);
    const queryString = params.toString() ? `?${params.toString()}` : '';
    return request<Account[]>(`${ACCOUNT_API_BASE}/accounts${queryString}`);
  },

  async getAccountById(accountId: string): Promise<Account> {
    return request<Account>(`${ACCOUNT_API_BASE}/accounts/${accountId}`);
  },

  async searchAccounts(name: string): Promise<Account[]> {
    const params = new URLSearchParams();
    if (name) params.append('name', name);
    return request<Account[]>(`${ACCOUNT_API_BASE}/accounts/search?${params.toString()}`);
  },

  async createAccount(payload: CreateAccountPayload): Promise<Account> {
    return request<Account>(`${ACCOUNT_API_BASE}/accounts`, {
      method: 'POST',
      body: JSON.stringify(payload)
    });
  },

  async updateAccountStatus(accountId: string, payload: UpdateAccountStatusPayload): Promise<Account> {
    return request<Account>(`${ACCOUNT_API_BASE}/accounts/${accountId}/status`, {
      method: 'PATCH',
      body: JSON.stringify(payload)
    });
  }
};

