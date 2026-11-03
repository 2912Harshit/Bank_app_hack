import React from 'react';

export const App: React.FC = () => {
  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-slate-900 text-white p-6">
      <div className="max-w-md w-full bg-slate-800 p-8 rounded-xl shadow-lg border border-slate-700 text-center">
        <h1 className="text-3xl font-bold tracking-tight text-sky-400">SecureBank</h1>
        <p className="mt-2 text-slate-300">
          Proactive Shift-Left Defense Target Application
        </p>
        <div className="mt-6 p-4 bg-slate-950/60 rounded-lg text-left text-sm text-slate-400 border border-slate-800 space-y-1">
          <div><strong className="text-sky-300">Account Service:</strong> http://localhost:8081/api</div>
          <div><strong className="text-sky-300">Transaction Service:</strong> http://localhost:8082/api</div>
        </div>
        <div className="mt-6">
          <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-emerald-500/10 text-emerald-400 border border-emerald-500/30">
            Frontend Foundation Ready
          </span>
        </div>
      </div>
    </div>
  );
};

export default App;

