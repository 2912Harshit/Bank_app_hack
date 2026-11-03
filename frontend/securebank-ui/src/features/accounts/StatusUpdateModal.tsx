import React, { useState } from 'react';
import { Modal } from '../../components/common/Modal';
import { Button } from '../../components/common/Button';
import { Alert } from '../../components/common/Alert';
import { accountService } from '../../services/accountService';
import { Account, AccountStatus } from '../../types/account';
import { ApiError } from '../../services/apiClient';

interface StatusUpdateModalProps {
  isOpen: boolean;
  onClose: () => void;
  account: Account | null;
  onStatusUpdated: (updated: Account) => void;
}

export const StatusUpdateModal: React.FC<StatusUpdateModalProps> = ({
  isOpen,
  onClose,
  account,
  onStatusUpdated
}) => {
  const [selectedStatus, setSelectedStatus] = useState<AccountStatus>('ACTIVE');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  React.useEffect(() => {
    if (account) {
      setSelectedStatus(account.status);
    }
  }, [account, isOpen]);

  if (!account) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setIsLoading(true);

    try {
      const updated = await accountService.updateAccountStatus(account.accountId, {
        status: selectedStatus
      });
      onStatusUpdated(updated);
      onClose();
    } catch (err: unknown) {
      const apiErr = err as ApiError;
      setError(apiErr?.message || 'Failed to update account status.');
    } finally {
      setIsLoading(false);
    }
  };

  const isClosed = account.status === 'CLOSED';

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Update Account Status">
      <form onSubmit={handleSubmit} className="space-y-4">
        {error && <Alert type="error" message={error} onClose={() => setError(null)} />}

        <div className="text-sm text-slate-600">
          Account: <strong className="text-slate-900 font-mono">{account.accountNumber}</strong> ({account.customerName})
        </div>

        {isClosed ? (
          <div className="p-3 bg-rose-50 border border-rose-200 rounded-lg text-xs text-rose-800">
            This account is permanently <strong>CLOSED</strong>. Per banking security compliance, closed accounts cannot be reopened.
          </div>
        ) : (
          <div>
            <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-2">
              Select New Status
            </label>
            <div className="space-y-2">
              <label className="flex items-center gap-3 p-3 border rounded-lg cursor-pointer hover:bg-slate-50 transition-colors">
                <input
                  type="radio"
                  name="status"
                  value="ACTIVE"
                  checked={selectedStatus === 'ACTIVE'}
                  onChange={() => setSelectedStatus('ACTIVE')}
                  className="text-sky-600 focus:ring-sky-500"
                />
                <div>
                  <div className="text-sm font-medium text-slate-900">ACTIVE</div>
                  <div className="text-xs text-slate-500">Normal operations permitted</div>
                </div>
              </label>

              <label className="flex items-center gap-3 p-3 border rounded-lg cursor-pointer hover:bg-slate-50 transition-colors">
                <input
                  type="radio"
                  name="status"
                  value="FROZEN"
                  checked={selectedStatus === 'FROZEN'}
                  onChange={() => setSelectedStatus('FROZEN')}
                  className="text-amber-600 focus:ring-amber-500"
                />
                <div>
                  <div className="text-sm font-medium text-slate-900">FROZEN</div>
                  <div className="text-xs text-slate-500">Temporarily suspends withdrawals and activity</div>
                </div>
              </label>

              <label className="flex items-center gap-3 p-3 border border-rose-200 rounded-lg cursor-pointer hover:bg-rose-50/50 transition-colors">
                <input
                  type="radio"
                  name="status"
                  value="CLOSED"
                  checked={selectedStatus === 'CLOSED'}
                  onChange={() => setSelectedStatus('CLOSED')}
                  className="text-rose-600 focus:ring-rose-500"
                />
                <div>
                  <div className="text-sm font-medium text-rose-900">CLOSED</div>
                  <div className="text-xs text-rose-600">Permanently close this account (cannot be reopened)</div>
                </div>
              </label>
            </div>
          </div>
        )}

        <div className="pt-4 flex items-center justify-end gap-3 border-t border-slate-100">
          <Button type="button" variant="outline" onClick={onClose} disabled={isLoading}>
            Cancel
          </Button>
          {!isClosed && (
            <Button
              type="submit"
              variant={selectedStatus === 'CLOSED' ? 'danger' : 'primary'}
              isLoading={isLoading}
            >
              Confirm Update
            </Button>
          )}
        </div>
      </form>
    </Modal>
  );
};

