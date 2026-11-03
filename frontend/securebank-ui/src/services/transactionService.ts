import { request } from './apiClient';
import {
  Transaction,
  CreateTransactionPayload,
  TransactionFilterParams
} from '../types/transaction';

const TRANSACTION_API_BASE = import.meta.env.VITE_TRANSACTION_SERVICE_URL || 'http://localhost:8082/api';

export const transactionService = {
  async getTransactionById(transactionId: string): Promise<Transaction> {
    return request<Transaction>(`${TRANSACTION_API_BASE}/transactions/${transactionId}`);
  },

  async getAccountTransactions(accountId: string): Promise<Transaction[]> {
    return request<Transaction[]>(`${TRANSACTION_API_BASE}/transactions/account/${accountId}`);
  },

  async searchTransactions(query: string): Promise<Transaction[]> {
    const params = new URLSearchParams();
    if (query) params.append('query', query);
    return request<Transaction[]>(`${TRANSACTION_API_BASE}/transactions/search?${params.toString()}`);
  },

  async filterTransactions(filterParams: TransactionFilterParams): Promise<Transaction[]> {
    const params = new URLSearchParams();
    if (filterParams.accountId) params.append('accountId', filterParams.accountId);
    if (filterParams.transactionType) params.append('transactionType', filterParams.transactionType);
    if (filterParams.minAmount !== undefined && !isNaN(filterParams.minAmount)) {
      params.append('minAmount', filterParams.minAmount.toString());
    }
    if (filterParams.maxAmount !== undefined && !isNaN(filterParams.maxAmount)) {
      params.append('maxAmount', filterParams.maxAmount.toString());
    }
    if (filterParams.startDate) params.append('startDate', filterParams.startDate);
    if (filterParams.endDate) params.append('endDate', filterParams.endDate);
    if (filterParams.sortBy) params.append('sortBy', filterParams.sortBy);
    if (filterParams.sortDirection) params.append('sortDirection', filterParams.sortDirection);

    return request<Transaction[]>(`${TRANSACTION_API_BASE}/transactions/filter?${params.toString()}`);
  },

  async createTransaction(payload: CreateTransactionPayload): Promise<Transaction> {
    return request<Transaction>(`${TRANSACTION_API_BASE}/transactions`, {
      method: 'POST',
      body: JSON.stringify(payload)
    });
  }
};

