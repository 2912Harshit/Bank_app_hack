import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { Layout } from '../components/layout/Layout';
import { DashboardPage } from '../features/dashboard/DashboardPage';
import { AccountsListPage } from '../features/accounts/AccountsListPage';
import { AccountDetailPage } from '../features/accounts/AccountDetailPage';
import { TransactionsListPage } from '../features/transactions/TransactionsListPage';

export const AppRoutes: React.FC = () => {
  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        <Route index element={<DashboardPage />} />
        <Route path="accounts" element={<AccountsListPage />} />
        <Route path="accounts/:accountId" element={<AccountDetailPage />} />
        <Route path="transactions" element={<TransactionsListPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  );
};
