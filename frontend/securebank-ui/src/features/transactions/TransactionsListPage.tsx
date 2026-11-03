import React, { useEffect, useState, useCallback } from 'react';
import { PlusCircle, Search, ArrowLeftRight, RefreshCw, Filter } from 'lucide-react';
import { transactionService } from '../../services/transactionService';
import { accountService } from '../../services/accountService';
import { Transaction, TransactionType } from '../../types/transaction';
import { Account } from '../../types/account';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import { Alert } from '../../components/common/Alert';
import { CreateTransactionModal } from './CreateTransactionModal';

export const TransactionsListPage: React.FC = () => {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Search & Filter state
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedAccountId, setSelectedAccountId] = useState('');
  const [selectedType, setSelectedType] = useState<string>('');
  const [minAmount, setMinAmount] = useState<string>('');
  const [maxAmount, setMaxAmount] = useState<string>('');
  const [sortBy, setSortBy] = useState('created_at');
  const [sortDirection, setSortDirection] = useState<'DESC' | 'ASC'>('DESC');

  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);

  const loadAccounts = async () => {
    try {
      const data = await accountService.listAccounts();
      setAccounts(data);
    } catch {
      // Ignored non-fatal for account dropdown
    }
  };

  const loadTransactions = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      let data: Transaction[];
      if (searchQuery.trim()) {
        data = await transactionService.searchTransactions(searchQuery.trim());
      } else {
        data = await transactionService.filterTransactions({
          accountId: selectedAccountId || undefined,
          transactionType: (selectedType as TransactionType) || undefined,
          minAmount: minAmount ? parseFloat(minAmount) : undefined,
          maxAmount: maxAmount ? parseFloat(maxAmount) : undefined,
          sortBy,
          sortDirection
        });
      }
      setTransactions(data);
    } catch {
      setError('Unable to load transactions. Verify Transaction Service (:8082) is running.');
    } finally {
      setIsLoading(false);
    }
  }, [searchQuery, selectedAccountId, selectedType, minAmount, maxAmount, sortBy, sortDirection]);

  useEffect(() => {
    loadAccounts();
    loadTransactions();
  }, [loadTransactions]);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    loadTransactions();
  };

  const handleResetFilters = () => {
    setSearchQuery('');
    setSelectedAccountId('');
    setSelectedType('');
    setMinAmount('');
    setMaxAmount('');
    setSortBy('created_at');
    setSortDirection('DESC');
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-slate-900">Transaction History</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Audit logs and records across all registered bank accounts
          </p>
        </div>
        <Button
          variant="primary"
          onClick={() => setIsCreateModalOpen(true)}
          className="self-start sm:self-auto"
        >
          <PlusCircle className="w-4 h-4 mr-2" />
          Record Transaction
        </Button>
      </div>

      {error && <Alert type="error" message={error} onClose={() => setError(null)} />}

      {/* Filter and Search Box */}
      <Card>
        <form onSubmit={handleSearchSubmit} className="space-y-3">
          {/* Text Search */}
          <div className="relative w-full">
            <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search by description or reference (e.g. salary, electricity, REF-...)..."
              className="w-full pl-9 pr-4 py-2 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 bg-white"
            />
          </div>

          {/* Filter Row */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3 pt-2">
            <div>
              <label className="block text-[11px] font-semibold text-slate-500 uppercase tracking-wider mb-1">
                Account
              </label>
              <select
                value={selectedAccountId}
                onChange={(e) => {
                  setSelectedAccountId(e.target.value);
                  setSearchQuery('');
                }}
                className="w-full px-2.5 py-1.5 border border-slate-300 rounded-lg text-xs bg-white focus:outline-none focus:ring-2 focus:ring-sky-500"
              >
                <option value="">All Accounts</option>
                {accounts.map((acc) => (
                  <option key={acc.accountId} value={acc.accountId}>
                    {acc.accountNumber} ({acc.customerName})
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-[11px] font-semibold text-slate-500 uppercase tracking-wider mb-1">
                Type
              </label>
              <select
                value={selectedType}
                onChange={(e) => {
                  setSelectedType(e.target.value);
                  setSearchQuery('');
                }}
                className="w-full px-2.5 py-1.5 border border-slate-300 rounded-lg text-xs bg-white focus:outline-none focus:ring-2 focus:ring-sky-500"
              >
                <option value="">All Types</option>
                <option value="DEBIT">DEBIT</option>
                <option value="CREDIT">CREDIT</option>
              </select>
            </div>

            <div>
              <label className="block text-[11px] font-semibold text-slate-500 uppercase tracking-wider mb-1">
                Min Amount (₹)
              </label>
              <input
                type="number"
                step="0.01"
                placeholder="Min"
                value={minAmount}
                onChange={(e) => {
                  setMinAmount(e.target.value);
                  setSearchQuery('');
                }}
                className="w-full px-2.5 py-1.5 border border-slate-300 rounded-lg text-xs bg-white focus:outline-none focus:ring-2 focus:ring-sky-500"
              />
            </div>

            <div>
              <label className="block text-[11px] font-semibold text-slate-500 uppercase tracking-wider mb-1">
                Max Amount (₹)
              </label>
              <input
                type="number"
                step="0.01"
                placeholder="Max"
                value={maxAmount}
                onChange={(e) => {
                  setMaxAmount(e.target.value);
                  setSearchQuery('');
                }}
                className="w-full px-2.5 py-1.5 border border-slate-300 rounded-lg text-xs bg-white focus:outline-none focus:ring-2 focus:ring-sky-500"
              />
            </div>

            <div>
              <label className="block text-[11px] font-semibold text-slate-500 uppercase tracking-wider mb-1">
                Sort By
              </label>
              <div className="flex gap-1.5">
                <select
                  value={sortBy}
                  onChange={(e) => setSortBy(e.target.value)}
                  className="w-full px-2 py-1.5 border border-slate-300 rounded-lg text-xs bg-white focus:outline-none focus:ring-2 focus:ring-sky-500"
                >
                  <option value="created_at">Date</option>
                  <option value="amount">Amount</option>
                  <option value="transaction_type">Type</option>
                  <option value="account_id">Account</option>
                </select>
                <button
                  type="button"
                  onClick={() => setSortDirection((d) => (d === 'DESC' ? 'ASC' : 'DESC'))}
                  className="px-2 py-1.5 border border-slate-300 rounded-lg text-xs font-semibold hover:bg-slate-50 text-slate-700"
                  title="Toggle Ascending / Descending"
                >
                  {sortDirection}
                </button>
              </div>
            </div>
          </div>

          <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-100">
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={handleResetFilters}
            >
              <RefreshCw className="w-3.5 h-3.5 mr-1" />
              Reset
            </Button>
            <Button type="submit" variant="primary" size="sm">
              <Filter className="w-3.5 h-3.5 mr-1" />
              Apply Filters
            </Button>
          </div>
        </form>
      </Card>

      {/* Transactions Table */}
      <Card>
        {isLoading ? (
          <div className="py-12 text-center text-sm text-slate-400">Loading transactions...</div>
        ) : transactions.length === 0 ? (
          <div className="py-12 text-center">
            <ArrowLeftRight className="w-12 h-12 text-slate-300 mx-auto mb-3" />
            <h3 className="text-base font-semibold text-slate-800">No transactions found</h3>
            <p className="text-xs text-slate-500 mt-1 max-w-sm mx-auto">
              No records match your active query or filters.
            </p>
          </div>
        ) : (
          <div className="overflow-x-auto -mx-6">
            <table className="w-full text-left border-collapse text-sm">
              <thead>
                <tr className="border-b border-slate-200 bg-slate-50/75 text-xs font-semibold text-slate-500 uppercase tracking-wider">
                  <th className="py-3 px-6">Transaction ID</th>
                  <th className="py-3 px-6">Account ID</th>
                  <th className="py-3 px-6">Type</th>
                  <th className="py-3 px-6">Amount</th>
                  <th className="py-3 px-6">Description</th>
                  <th className="py-3 px-6">Reference</th>
                  <th className="py-3 px-6">Date</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {transactions.map((txn) => {
                  const isCredit = txn.transactionType === 'CREDIT';
                  return (
                    <tr key={txn.transactionId} className="hover:bg-slate-50/50 transition-colors">
                      <td className="py-4 px-6 font-mono text-xs font-medium text-slate-800">
                        {txn.transactionId}
                      </td>
                      <td className="py-4 px-6 font-mono text-xs text-sky-700">
                        {txn.accountId}
                      </td>
                      <td className="py-4 px-6">
                        <Badge variant={txn.transactionType.toLowerCase() as 'debit' | 'credit'}>
                          {txn.transactionType}
                        </Badge>
                      </td>
                      <td className="py-4 px-6 font-semibold">
                        <span className={isCredit ? 'text-emerald-700' : 'text-slate-900'}>
                          {isCredit ? '+' : '-'}₹{Number(txn.amount).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                        </span>
                      </td>
                      <td className="py-4 px-6 text-slate-700">{txn.description}</td>
                      <td className="py-4 px-6 font-mono text-xs text-slate-500">
                        {txn.reference}
                      </td>
                      <td className="py-4 px-6 text-xs text-slate-500">
                        {new Date(txn.createdAt).toLocaleString()}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </Card>

      {/* Modal */}
      <CreateTransactionModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        accounts={accounts}
        onTransactionCreated={(newTxn) => {
          setTransactions((prev) => [newTxn, ...prev]);
        }}
      />
    </div>
  );
};

