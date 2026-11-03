import React, { useEffect, useState, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, Edit3, ArrowLeftRight, CreditCard } from 'lucide-react';
import { accountService } from '../../services/accountService';
import { transactionService } from '../../services/transactionService';
import { Account } from '../../types/account';
import { Transaction } from '../../types/transaction';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import { Alert } from '../../components/common/Alert';
import { StatusUpdateModal } from './StatusUpdateModal';
import { CreateTransactionModal } from '../transactions/CreateTransactionModal';

export const AccountDetailPage: React.FC = () => {
  const { accountId } = useParams<{ accountId: string }>();
  const [account, setAccount] = useState<Account | null>(null);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [isStatusModalOpen, setIsStatusModalOpen] = useState(false);
  const [isTxnModalOpen, setIsTxnModalOpen] = useState(false);

  const loadAccountDetails = useCallback(async () => {
    if (!accountId) return;
    setIsLoading(true);
    setError(null);
    try {
      const accData = await accountService.getAccountById(accountId);
      setAccount(accData);

      // Fetch transaction history from Transaction Service
      const txnData = await transactionService.getAccountTransactions(accountId);
      setTransactions(txnData);
    } catch {
      setError('Failed to load account details. Ensure both backend services are running.');
    } finally {
      setIsLoading(false);
    }
  }, [accountId]);

  useEffect(() => {
    loadAccountDetails();
  }, [loadAccountDetails]);

  if (isLoading) {
    return <div className="py-12 text-center text-sm text-slate-400">Loading account details...</div>;
  }

  if (!account) {
    return (
      <div className="py-12 text-center space-y-4">
        <CreditCard className="w-12 h-12 text-slate-300 mx-auto" />
        <h2 className="text-lg font-bold text-slate-800">Account Not Found</h2>
        <p className="text-sm text-slate-500">The requested account ID does not exist in accounts_db.</p>
        <Link to="/accounts">
          <Button variant="outline">Back to Accounts</Button>
        </Link>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Top Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <Link
            to="/accounts"
            className="p-2 text-slate-500 hover:text-slate-800 hover:bg-slate-200/60 rounded-lg transition-colors"
            title="Back to Accounts"
          >
            <ArrowLeft className="w-5 h-5" />
          </Link>
          <div>
            <div className="flex items-center gap-3">
              <h1 className="text-2xl font-bold tracking-tight text-slate-900 font-mono">
                {account.accountNumber}
              </h1>
              <Badge variant={account.status.toLowerCase() as 'active' | 'frozen' | 'closed'}>
                {account.status}
              </Badge>
            </div>
            <p className="text-xs text-slate-500 mt-0.5">
              Account ID: <span className="font-mono text-slate-700">{account.accountId}</span> • Owned by {account.customerName}
            </p>
          </div>
        </div>

        <div className="flex items-center gap-3">
          <Button
            variant="outline"
            onClick={() => setIsStatusModalOpen(true)}
          >
            <Edit3 className="w-4 h-4 mr-1.5" />
            Change Status
          </Button>
          <Button
            variant="primary"
            onClick={() => setIsTxnModalOpen(true)}
          >
            <ArrowLeftRight className="w-4 h-4 mr-1.5" />
            Record Transaction
          </Button>
        </div>
      </div>

      {error && <Alert type="error" message={error} onClose={() => setError(null)} />}

      {/* Account Details Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card className="border-t-4 border-t-sky-500">
          <div className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Available Balance</div>
          <div className="text-2xl font-bold text-slate-900 mt-1">
            ₹{Number(account.balance).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
          </div>
          <div className="text-xs text-slate-400 mt-1">Currency: {account.currency}</div>
        </Card>

        <Card className="border-t-4 border-t-purple-500">
          <div className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Account Type</div>
          <div className="text-lg font-bold text-slate-900 mt-1">{account.accountType}</div>
          <div className="text-xs text-slate-400 mt-1">Standard banking product</div>
        </Card>

        <Card className="border-t-4 border-t-slate-500">
          <div className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Account Holder</div>
          <div className="text-lg font-bold text-slate-900 mt-1">{account.customerName}</div>
          <div className="text-xs text-slate-400 font-mono mt-1">{account.userId}</div>
        </Card>

        <Card className="border-t-4 border-t-emerald-500">
          <div className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Created At</div>
          <div className="text-lg font-bold text-slate-900 mt-1">
            {new Date(account.createdAt).toLocaleDateString()}
          </div>
          <div className="text-xs text-slate-400 mt-1">
            {new Date(account.createdAt).toLocaleTimeString()}
          </div>
        </Card>
      </div>

      {/* Account Transaction History */}
      <Card
        title="Transaction History"
        subtitle={`Audit records fetched from Transaction Service for account ${account.accountId}`}
        action={
          <Button
            variant="ghost"
            size="sm"
            onClick={loadAccountDetails}
          >
            Refresh
          </Button>
        }
      >
        {transactions.length === 0 ? (
          <div className="py-12 text-center">
            <ArrowLeftRight className="w-10 h-10 text-slate-300 mx-auto mb-2" />
            <h4 className="text-sm font-semibold text-slate-700">No transactions recorded for this account</h4>
            <p className="text-xs text-slate-500 mt-1">Click "Record Transaction" above to create an entry.</p>
          </div>
        ) : (
          <div className="overflow-x-auto -mx-6">
            <table className="w-full text-left border-collapse text-sm">
              <thead>
                <tr className="border-b border-slate-200 bg-slate-50/75 text-xs font-semibold text-slate-500 uppercase tracking-wider">
                  <th className="py-3 px-6">Reference</th>
                  <th className="py-3 px-6">Type</th>
                  <th className="py-3 px-6">Amount</th>
                  <th className="py-3 px-6">Description</th>
                  <th className="py-3 px-6">Date</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {transactions.map((txn) => {
                  const isCredit = txn.transactionType === 'CREDIT';
                  return (
                    <tr key={txn.transactionId} className="hover:bg-slate-50/50">
                      <td className="py-4 px-6 font-mono text-xs text-slate-800">
                        {txn.reference}
                      </td>
                      <td className="py-4 px-6">
                        <Badge variant={txn.transactionType.toLowerCase() as 'debit' | 'credit'}>
                          {txn.transactionType}
                        </Badge>
                      </td>
                      <td className="py-4 px-6 font-bold">
                        <span className={isCredit ? 'text-emerald-700' : 'text-slate-900'}>
                          {isCredit ? '+' : '-'}₹{Number(txn.amount).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                        </span>
                      </td>
                      <td className="py-4 px-6 text-slate-700">{txn.description}</td>
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

      {/* Modals */}
      <StatusUpdateModal
        isOpen={isStatusModalOpen}
        onClose={() => setIsStatusModalOpen(false)}
        account={account}
        onStatusUpdated={(updated) => setAccount(updated)}
      />

      <CreateTransactionModal
        isOpen={isTxnModalOpen}
        onClose={() => setIsTxnModalOpen(false)}
        accounts={[account]}
        defaultAccountId={account.accountId}
        onTransactionCreated={(newTxn) => {
          setTransactions((prev) => [newTxn, ...prev]);
        }}
      />
    </div>
  );
};
