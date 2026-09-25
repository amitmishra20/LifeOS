import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/useAuth';
import Input from '../components/ui/Input';
import Button from '../components/ui/Button';
import ThemeToggle from '../components/ui/ThemeToggle';
import './AuthPages.css';

export const LoginPage = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [fieldErrors, setFieldErrors] = useState({});
  const [generalError, setGeneralError] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Return to the page they were trying to access, or default to '/'
  const from = location.state?.from?.pathname || '/';

  const validate = () => {
    const errors = {};
    if (!email.trim()) {
      errors.email = 'Email is required';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())) {
      errors.email = 'Please enter a valid email address';
    }

    if (!password) {
      errors.password = 'Password is required';
    }

    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setGeneralError(null);

    if (!validate()) return;

    setIsSubmitting(true);
    try {
      await login({ email: email.trim(), password });
      navigate(from, { replace: true });
    } catch (err) {
      if (err.fieldErrors && Object.keys(err.fieldErrors).length > 0) {
        setFieldErrors(err.fieldErrors);
      } else {
        setGeneralError(err.message || 'Invalid email or password. Please try again.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="lifeos-auth-wrapper">
      <div className="lifeos-auth-topbar">
        <ThemeToggle />
      </div>

      <div className="lifeos-auth-card">
        <header className="lifeos-auth-header">
          <div className="lifeos-auth-logo" aria-hidden="true">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M11 20A7 7 0 0 1 4 13C4 7 9 3 17 3c1 5-1 11-6 17z" />
              <path d="M7 17l8-8" />
            </svg>
          </div>
          <h1 className="lifeos-auth-brand-name">LifeOS</h1>
          <p className="lifeos-auth-slogan">&ldquo;Your life, on your terms.&rdquo;</p>
        </header>

        {generalError && (
          <div className="lifeos-auth-error-banner" role="alert">
            <svg className="lifeos-auth-error-banner__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="12" cy="12" r="10" />
              <line x1="12" y1="8" x2="12" y2="12" />
              <line x1="12" y1="16" x2="12.01" y2="16" />
            </svg>
            <span>{generalError}</span>
          </div>
        )}

        <form className="lifeos-auth-form" onSubmit={handleSubmit} noValidate>
          <Input
            id="login-email"
            label="Email"
            type="email"
            value={email}
            onChange={(e) => {
              setEmail(e.target.value);
              if (fieldErrors.email) setFieldErrors((prev) => ({ ...prev, email: null }));
            }}
            error={fieldErrors.email}
            placeholder="you@example.com"
            autoComplete="email"
            autoFocus
            disabled={isSubmitting}
            required
          />

          <Input
            id="login-password"
            label="Password"
            type="password"
            value={password}
            onChange={(e) => {
              setPassword(e.target.value);
              if (fieldErrors.password) setFieldErrors((prev) => ({ ...prev, password: null }));
            }}
            error={fieldErrors.password}
            placeholder="••••••••"
            autoComplete="current-password"
            disabled={isSubmitting}
            required
          />

          <Button
            type="submit"
            variant="primary"
            size="md"
            className="lifeos-auth-submit-btn"
            isLoading={isSubmitting}
          >
            Sign in
          </Button>
        </form>

        <footer className="lifeos-auth-footer">
          <span>Don&apos;t have an account?</span>
          <Link to="/register" className="lifeos-auth-link">
            Create account
          </Link>
        </footer>
      </div>
    </div>
  );
};

export default LoginPage;
