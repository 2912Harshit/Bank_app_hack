import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { ShieldCheck, LayoutDashboard, CreditCard, ArrowLeftRight, User } from 'lucide-react';
import { useAuth } from '../../features/auth/AuthContext';

export const Navbar: React.FC = () => {
  const { currentUser, selectUser, availableUsers } = useAuth();
  const location = useLocation();

  const navLinks = [
    { to: '/', label: 'Dashboard', icon: LayoutDashboard },
    { to: '/accounts', label: 'Accounts', icon: CreditCard },
    { to: '/transactions', label: 'Transactions', icon: ArrowLeftRight }
  ];

  return (
    <header className="bg-slate-900 border-b border-slate-800 text-white sticky top-0 z-40">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Brand Logo */}
          <div className="flex items-center gap-8">
            <Link to="/" className="flex items-center gap-2.5 font-bold text-xl tracking-tight text-white hover:text-sky-400 transition-colors">
              <div className="w-9 h-9 rounded-lg bg-sky-500/20 border border-sky-400/40 flex items-center justify-center text-sky-400">
                <ShieldCheck className="w-5 h-5" />
              </div>
              <span>Secure<span className="text-sky-400">Bank</span></span>
            </Link>

            {/* Nav Links */}
            <nav className="hidden md:flex items-center gap-1">
              {navLinks.map(({ to, label, icon: Icon }) => {
                const isActive = location.pathname === to || (to !== '/' && location.pathname.startsWith(to));
                return (
                  <Link
                    key={to}
                    to={to}
                    className={`flex items-center gap-2 px-3.5 py-2 rounded-lg text-sm font-medium transition-colors ${
                      isActive
                        ? 'bg-sky-500/15 text-sky-400 border border-sky-500/30'
                        : 'text-slate-300 hover:text-white hover:bg-slate-800'
                    }`}
                  >
                    <Icon className="w-4 h-4" />
                    {label}
                  </Link>
                );
              })}
            </nav>
          </div>

          {/* User Selector Dropdown */}
          <div className="flex items-center gap-3">
            <div className="hidden sm:flex flex-col text-right">
              <span className="text-xs font-semibold text-slate-200">{currentUser.customerName}</span>
              <span className="text-[11px] text-slate-400 font-mono">{currentUser.userId}</span>
            </div>
            <div className="relative flex items-center">
              <div className="w-9 h-9 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center text-sky-400 mr-2">
                <User className="w-4 h-4" />
              </div>
              <select
                value={currentUser.userId}
                onChange={(e) => selectUser(e.target.value)}
                aria-label="Switch Demo User"
                className="bg-slate-800 text-slate-200 border border-slate-700 text-xs rounded-lg px-2.5 py-1.5 focus:outline-none focus:ring-2 focus:ring-sky-500 cursor-pointer"
              >
                {availableUsers.map((u) => (
                  <option key={u.userId} value={u.userId}>
                    {u.customerName} ({u.userId})
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};
