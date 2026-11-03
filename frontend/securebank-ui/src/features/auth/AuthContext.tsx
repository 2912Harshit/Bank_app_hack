import React, { createContext, useContext, useState, useEffect } from 'react';
import { DemoUser, DEMO_USERS } from '../../types/user';

interface AuthContextType {
  currentUser: DemoUser;
  selectUser: (userId: string) => void;
  availableUsers: DemoUser[];
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [currentUser, setCurrentUser] = useState<DemoUser>(() => {
    const saved = localStorage.getItem('securebank_demo_user');
    if (saved) {
      const found = DEMO_USERS.find(u => u.userId === saved);
      if (found) return found;
    }
    return DEMO_USERS[0];
  });

  const selectUser = (userId: string) => {
    const user = DEMO_USERS.find(u => u.userId === userId);
    if (user) {
      setCurrentUser(user);
      localStorage.setItem('securebank_demo_user', user.userId);
    }
  };

  useEffect(() => {
    localStorage.setItem('securebank_demo_user', currentUser.userId);
  }, [currentUser]);

  return (
    <AuthContext.Provider value={{ currentUser, selectUser, availableUsers: DEMO_USERS }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

