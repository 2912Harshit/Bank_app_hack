import React, { useEffect, useState, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { CreditCard, ArrowLeftRight, TrendingUp, AlertTriangle, PlusCircle, ArrowUpRight, ArrowDownLeft } from 'lucide-react';
import { useAuth } from '../auth/AuthContext';
import { accountService } from '../../services/accountService';
import { transactionService } from '../../services/transactionService';
import { Account } from '../../types/account';
import { Transaction } from '../../types/transaction';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import { Alert } from '../../components/common/Alert';
import { CreateAccountModal } from '../accounts/CreateAccountModal';
import { CreateTransactionModal } from '../transactions/CreateTransactionModal';

export const DashboardPage: React.FC = () => {
  const { currentUser } = useAuth();
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [recentTransactions, setRecentTransactions] = useState<Transaction[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [isAccountModalOpen, setIsAccountModalOpen] = useState(false);
  const [isTxnModalOpen, setIsTxnModalOpen] = useState(false);

  const fetchDashboardData = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const userAccounts = await accountService.listAccounts(currentUser.userId);
      setAccounts(userAccounts);

      // Fetch recent transactions for the user's primary accounts
      if (userAccounts.length > 0) {
        const txns = await transactionService.getAccountTransactions(userAccounts[0].accountId);
        setRecentTransactions(txns.slice(0, 5));
      } else {
        // If user has no accounts, fetch general recent transactions
        const allTxns = await transactionService.searchTransactions('');
        setRecentTransactions(allTxns.slice(0, 5));
      }
    } catch {
      setError('Unable to load dashboard data. Ensure backend services on ports 8081 and 8082 are running.');
    } finally {
      setIsLoading(false);
    }
  }, [currentUser.userId]);

  useEffect(() => {
    fetchDashboardData();
  }, [fetchDashboardData]);

  const totalBalance = accounts.reduce((sum, acc) => sum + Number(acc.balance), 0);
  const activeCount = accounts.filter(acc => acc.status === 'ACTIVE').length;
  const frozenCount = accounts.filter(acc => acc.status === 'FROZEN').length;

  return (
    <div className="space-y-6">
      {/* Top Banner / Hero */}
      <div className="bg-gradient-to-r from-slate-900 via-slate-800 to-sky-950 rounded-2xl p-6 sm:p-8 text-white shadow-md border border-slate-800 flex flex-col md:flex-row md:items-center justify-between gap-6">
        <div>
          <div className="inline-flex items-center gap-2 px-2.5 py-1 rounded-full text-xs font-semibold bg-sky-500/20 text-sky-300 border border-sky-400/30 mb-3">
            SecureBank Portal
          </div>
          <h1 className="text-2xl sm:text-3xl font-bold tracking-tight">
            Welcome back, {currentUser.customerName}
          </h1>
          <p className="mt-1 text-sm text-slate-300">
            User ID: <span className="font-mono text-sky-400">{currentUser.userId}</span> • Role: {currentUser.role}
          </p>
        </div>
        <div className="flex flex-wrap items-center gap-3">
          <Button
            variant="outline"
            className="bg-white/10 text-white border-white/20 hover:bg-white/20"
            onClick={() => setIsAccountModalOpen(true)}
          >
            <PlusCircle className="w-4 h-4 mr-1.5" />
            Open Account
          </Button>
          <Button
            variant="primary"
            onClick={() => setIsTxnModalOpen(true)}
          >
            <ArrowLeftRight className="w-4 h-4 mr-1.5" />
            New Transaction
          </Button>
        </div>
      </div>

      {error && <Alert type="error" message={error} onClose={() => setError(null)} />}

      {/* Metrics Row */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <Card className="border-l-4 border-l-sky-500">
          <div className="flex items-center justify-between">
            <div>
              <div className="text-xs font-medium text-slate-500 uppercase tracking-wider">Total Balance</div>
              <div className="text-2xl font-bold text-slate-900 mt-1">
                ₹{totalBalance.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
              </div>
            </div>
            <div className="w-10 h-10 rounded-xl bg-sky-50 text-sky-600 flex items-center justify-center">
              <TrendingUp className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-3 text-xs text-slate-500">Across {accounts.length} registered account(s)</div>
        </Card>

        <Card className="border-l-4 border-l-emerald-500">
          <div className="flex items-center justify-between">
            <div>
              <div className="text-xs font-medium text-slate-500 uppercase tracking-wider">Total Accounts</div>
              <div className="text-2xl font-bold text-slate-900 mt-1">{accounts.length}</div>
            </div>
            <div className="w-10 h-10 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center">
              <CreditCard className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-3 text-xs text-emerald-600 font-medium">{activeCount} Active Status</div>
        </Card>

        <Card className="border-l-4 border-l-amber-500">
          <div className="flex items-center justify-between">
            <div>
              <div className="text-xs font-medium text-slate-500 uppercase tracking-wider">Frozen / Review</div>
              <div className="text-2xl font-bold text-slate-900 mt-1">{frozenCount}</div>
            </div>
            <div className="w-10 h-10 rounded-xl bg-amber-50 text-amber-600 flex items-center justify-center">
              <AlertTriangle className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-3 text-xs text-slate-500">Temporarily restricted</div>
        </Card>

        <Card className="border-l-4 border-l-purple-500">
          <div className="flex items-center justify-between">
            <div>
              <div className="text-xs font-medium text-slate-500 uppercase tracking-wider">Recent Activity</div>
              <div className="text-2xl font-bold text-slate-900 mt-1">{recentTransactions.length}</div>
            </div>
            <div className="w-10 h-10 rounded-xl bg-purple-50 text-purple-600 flex items-center justify-center">
              <ArrowLeftRight className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-3 text-xs text-slate-500">Audit transaction events</div>
        </Card>
      </div>

      {/* Main Content Split: User's Accounts & Recent Transactions */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* User's Accounts */}
        <Card
          title="Your Accounts"
          subtitle={`Accounts registered to ${currentUser.customerName}`}
          action={
            <Link to="/accounts" className="text-xs font-semibold text-sky-600 hover:text-sky-700">
              View All &rarr;
            </Link>
          }
        >
          {isLoading ? (
            <div className="py-8 text-center text-sm text-slate-400">Loading accounts...</div>
          ) : accounts.length === 0 ? (
            <div className="py-8 text-center">
              <CreditCard className="w-10 h-10 text-slate-300 mx-auto mb-2" />
              <div className="text-sm font-medium text-slate-700">No accounts found for this user</div>
              <p className="text-xs text-slate-500 mt-1">Open a new account to begin banking operations.</p>
              <Button
                variant="primary"
                size="sm"
                className="mt-3"
                onClick={() => setIsAccountModalOpen(true)}
              >
                Open Account
              </Button>
            </div>
          ) : (
            <div className="space-y-3">
              {accounts.map((acc) => (
                <Link
                  key={acc.accountId}
                  to={`/accounts/${acc.accountId}`}
                  className="block p-3.5 rounded-xl border border-slate-200 hover:border-sky-300 hover:bg-sky-50/40 transition-all"
                >
                  <div className="flex items-center justify-between">
                    <div>
                      <div className="font-mono text-sm font-semibold text-slate-900">
                        {acc.accountNumber}
                      </div>
                      <div className="text-xs text-slate-500 mt-0.5">
                        {acc.customerName} • {acc.accountType}
                      </div>
                    </div>
                    <div className="text-right">
                      <div className="text-sm font-bold text-slate-900">
                        ₹{Number(acc.balance).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                      </div>
                      <div className="mt-0.5">
                        <Badge variant={acc.status.toLowerCase() as 'active' | 'frozen' | 'closed'}>
                          {acc.status}
                        </Badge>
                      </div>
                    </div>
                  </div>
                </Link>
              ))}
            </div>
          )}
        </Card>

        {/* Recent Transactions */}
        <Card
          title="Recent Transactions"
          subtitle="Latest debits and credits"
          action={
            <Link to="/transactions" className="text-xs font-semibold text-sky-600 hover:text-sky-700">
              View All &rarr;
            </Link>
          }
        >
          {isLoading ? (
            <div className="py-8 text-center text-sm text-slate-400">Loading transactions...</div>
          ) : recentTransactions.length === 0 ? (
            <div className="py-8 text-center">
              <ArrowLeftRight className="w-10 h-10 text-slate-300 mx-auto mb-2" />
              <div className="text-sm font-medium text-slate-700">No transactions recorded yet</div>
              <p className="text-xs text-slate-500 mt-1">Synthetic transaction records will appear here.</p>
              <Button
                variant="outline"
                size="sm"
                className="mt-3"
                onClick={() => setIsTxnModalOpen(true)}
              >
                Record Transaction
              </Button>
            </div>
          ) : (
            <div className="space-y-3">
              {recentTransactions.map((txn) => {
                const isCredit = txn.transactionType === 'CREDIT';
                return (
                  <div
                    key={txn.transactionId}
                    className="p-3.5 rounded-xl border border-slate-100 bg-slate-50/60 flex items-center justify-between"
                  >
                    <div className="flex items-center gap-3">
                      <div
                        className={`w-9 h-9 rounded-lg flex items-center justify-center ${
                          isCredit
                            ? 'bg-emerald-100 text-emerald-700'
                            : 'bg-rose-100 text-rose-700'
                        }`}
                      >
                        {isCredit ? (
                          <ArrowDownLeft className="w-4 h-4" />
                        ) : (
                          <ArrowUpRight className="w-4 h-4" />
                        )}
                      </div>
                      <div>
                        <div className="text-sm font-medium text-slate-900">{txn.description}</div>
                        <div className="text-[11px] text-slate-500 font-mono">
                          {txn.reference} • {new Date(txn.createdAt).toLocaleDateString()}
                        </div>
                      </div>
                    </div>
                    <div className="text-right">
                      <div
                        className={`text-sm font-bold ${
                          isCredit ? 'text-emerald-700' : 'text-slate-900'
                        }`}
                      >
                        {isCredit ? '+' : '-'}₹{Number(txn.amount).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                      </div>
                      <Badge variant={txn.transactionType.toLowerCase() as 'debit' | 'credit'} className="text-[10px]">
                        {txn.transactionType}
                      </Badge>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </Card>
      </div>

      {/* Modals */}
      <CreateAccountModal
        isOpen={isAccountModalOpen}
        onClose={() => setIsAccountModalOpen(false)}
        onAccountCreated={(newAcc) => {
          setAccounts((prev) => [newAcc, ...prev]);
        }}
      />

      <CreateTransactionModal
        isOpen={isTxnModalOpen}
        onClose={() => setIsTxnModalOpen(false)}
        accounts={accounts}
        onTransactionCreated={(newTxn) => {
          setRecentTransactions((prev) => [newTxn, ...prev]);
        }}
      />
    </div>
  );
};
