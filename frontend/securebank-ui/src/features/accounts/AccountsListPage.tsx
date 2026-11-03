import React, { useEffect, useState, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { PlusCircle, Search, CreditCard, Edit3, Eye, RefreshCw } from 'lucide-react';
import { accountService } from '../../services/accountService';
import { Account } from '../../types/account';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import { Alert } from '../../components/common/Alert';
import { CreateAccountModal } from './CreateAccountModal';
import { StatusUpdateModal } from './StatusUpdateModal';

export const AccountsListPage: React.FC = () => {
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [statusModalAccount, setStatusModalAccount] = useState<Account | null>(null);

  const loadAccounts = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      let data: Account[];
      if (searchQuery.trim()) {
        data = await accountService.searchAccounts(searchQuery.trim());
      } else {
        data = await accountService.listAccounts(undefined, statusFilter || undefined);
      }
      setAccounts(data);
    } catch {
      setError('Unable to load accounts. Ensure Account Service (:8081) is accessible.');
    } finally {
      setIsLoading(false);
    }
  }, [searchQuery, statusFilter]);

  useEffect(() => {
    loadAccounts();
  }, [loadAccounts]);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    loadAccounts();
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-slate-900">Accounts Directory</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Manage customer accounts, search by name, and update status
          </p>
        </div>
        <Button
          variant="primary"
          onClick={() => setIsCreateModalOpen(true)}
          className="self-start sm:self-auto"
        >
          <PlusCircle className="w-4 h-4 mr-2" />
          Open Account
        </Button>
      </div>

      {error && <Alert type="error" message={error} onClose={() => setError(null)} />}

      {/* Filter and Search Bar */}
      <Card>
        <form onSubmit={handleSearchSubmit} className="flex flex-col md:flex-row gap-3 items-center">
          <div className="relative flex-1 w-full">
            <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search by customer name (e.g. Aarav, Priya)..."
              className="w-full pl-9 pr-4 py-2 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 bg-white"
            />
          </div>

          <div className="w-full md:w-48">
            <select
              value={statusFilter}
              onChange={(e) => {
                setStatusFilter(e.target.value);
                setSearchQuery('');
              }}
              className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 bg-white"
            >
              <option value="">All Statuses</option>
              <option value="ACTIVE">ACTIVE</option>
              <option value="FROZEN">FROZEN</option>
              <option value="CLOSED">CLOSED</option>
            </select>
          </div>

          <div className="flex gap-2 w-full md:w-auto">
            <Button type="submit" variant="primary" size="md" className="flex-1 md:flex-initial">
              Search
            </Button>
            <Button
              type="button"
              variant="outline"
              size="md"
              onClick={() => {
                setSearchQuery('');
                setStatusFilter('');
              }}
              title="Reset Filters"
            >
              <RefreshCw className="w-4 h-4" />
            </Button>
          </div>
        </form>
      </Card>

      {/* Accounts Table */}
      <Card>
        {isLoading ? (
          <div className="py-12 text-center text-sm text-slate-400">Loading accounts directory...</div>
        ) : accounts.length === 0 ? (
          <div className="py-12 text-center">
            <CreditCard className="w-12 h-12 text-slate-300 mx-auto mb-3" />
            <h3 className="text-base font-semibold text-slate-800">No accounts found</h3>
            <p className="text-xs text-slate-500 mt-1 max-w-sm mx-auto">
              No customer accounts matched your search criteria or the database is currently empty.
            </p>
          </div>
        ) : (
          <div className="overflow-x-auto -mx-6">
            <table className="w-full text-left border-collapse text-sm">
              <thead>
                <tr className="border-b border-slate-200 bg-slate-50/75 text-xs font-semibold text-slate-500 uppercase tracking-wider">
                  <th className="py-3 px-6">Account Number</th>
                  <th className="py-3 px-6">Customer Name</th>
                  <th className="py-3 px-6">Type</th>
                  <th className="py-3 px-6">Balance</th>
                  <th className="py-3 px-6">Status</th>
                  <th className="py-3 px-6">Created Date</th>
                  <th className="py-3 px-6 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {accounts.map((acc) => (
                  <tr key={acc.accountId} className="hover:bg-slate-50/50 transition-colors">
                    <td className="py-4 px-6 font-mono font-medium text-slate-900">
                      <Link
                        to={`/accounts/${acc.accountId}`}
                        className="text-sky-600 hover:text-sky-800 hover:underline flex items-center gap-1.5"
                      >
                        {acc.accountNumber}
                      </Link>
                    </td>
                    <td className="py-4 px-6">
                      <div className="font-medium text-slate-900">{acc.customerName}</div>
                      <div className="text-xs text-slate-400 font-mono">{acc.userId}</div>
                    </td>
                    <td className="py-4 px-6">
                      <Badge variant={acc.accountType.toLowerCase() as 'savings' | 'current'}>
                        {acc.accountType}
                      </Badge>
                    </td>
                    <td className="py-4 px-6 font-semibold text-slate-900">
                      ₹{Number(acc.balance).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                    </td>
                    <td className="py-4 px-6">
                      <Badge variant={acc.status.toLowerCase() as 'active' | 'frozen' | 'closed'}>
                        {acc.status}
                      </Badge>
                    </td>
                    <td className="py-4 px-6 text-xs text-slate-500">
                      {new Date(acc.createdAt).toLocaleDateString()}
                    </td>
                    <td className="py-4 px-6 text-right">
                      <div className="flex items-center justify-end gap-2">
                        <Link
                          to={`/accounts/${acc.accountId}`}
                          className="p-1.5 text-slate-400 hover:text-sky-600 hover:bg-sky-50 rounded-lg transition-colors"
                          title="View Details"
                        >
                          <Eye className="w-4 h-4" />
                        </Link>
                        <button
                          type="button"
                          onClick={() => setStatusModalAccount(acc)}
                          className="p-1.5 text-slate-400 hover:text-amber-600 hover:bg-amber-50 rounded-lg transition-colors"
                          title="Update Status"
                        >
                          <Edit3 className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Card>

      {/* Modals */}
      <CreateAccountModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        onAccountCreated={(newAcc) => {
          setAccounts((prev) => [newAcc, ...prev]);
        }}
      />

      <StatusUpdateModal
        isOpen={!!statusModalAccount}
        onClose={() => setStatusModalAccount(null)}
        account={statusModalAccount}
        onStatusUpdated={(updated) => {
          setAccounts((prev) =>
            prev.map((acc) => (acc.accountId === updated.accountId ? updated : acc))
          );
        }}
      />
    </div>
  );
};
