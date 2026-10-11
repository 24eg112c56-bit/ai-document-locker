import { createContext, useContext, useState, useEffect } from 'react';
import api from '../services/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('doclocker_user');
    return saved ? JSON.parse(saved) : null;
  });
  const [token, setToken] = useState(() => localStorage.getItem('doclocker_token'));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const verifyUser = async () => {
      if (token) {
        try {
          const res = await api.get('/auth/me');
          setUser(res.data);
          localStorage.setItem('doclocker_user', JSON.stringify(res.data));
        } catch {
          logout();
        }
      }
      setLoading(false);
    };

    const handleExpired = () => {
      setUser(null);
      setToken(null);
    };

    window.addEventListener('auth-expired', handleExpired);
    verifyUser();

    return () => window.removeEventListener('auth-expired', handleExpired);
  }, [token]);

  const login = async (email, password) => {
    const res = await api.post('/auth/login', { email, password });
    const { token: newToken, user: newUser } = res.data;
    localStorage.setItem('doclocker_token', newToken);
    localStorage.setItem('doclocker_user', JSON.stringify(newUser));
    setToken(newToken);
    setUser(newUser);
    return newUser;
  };

  const register = async (fullName, email, password) => {
    const res = await api.post('/auth/register', { fullName, email, password });
    const { token: newToken, user: newUser } = res.data;
    localStorage.setItem('doclocker_token', newToken);
    localStorage.setItem('doclocker_user', JSON.stringify(newUser));
    setToken(newToken);
    setUser(newUser);
    return newUser;
  };

  const logout = () => {
    localStorage.removeItem('doclocker_token');
    localStorage.removeItem('doclocker_user');
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout, isAuthenticated: !!token }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
