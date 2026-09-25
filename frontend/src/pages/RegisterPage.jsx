import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/useAuth';
import Input from '../components/ui/Input';
import Button from '../components/ui/Button';
import ThemeToggle from '../components/ui/ThemeToggle';
import './AuthPages.css';

export const RegisterPage = () => {
  const { register } = useAuth();
  const navigate = useNavigate();

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [fieldErrors, setFieldErrors] = useState({});
  const [generalError, setGeneralError] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const validate = () => {
    const errors = {};

    if (!name.trim()) {
      errors.name = 'Name is required';
    } else if (name.trim().length < 2) {
      errors.name = 'Name must be at least 2 characters';
    }

    if (!email.trim()) {
      errors.email = 'Email is required';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())) {
      errors.email = 'Please enter a valid email address';
    }

    if (!password) {
      errors.password = 'Password is required';
    } else if (password.length < 8) {
      errors.password = 'Password must be at least 8 characters';
    }

    if (!confirmPassword) {
      errors.confirmPassword = 'Confirm your password';
    } else if (password !== confirmPassword) {
      errors.confirmPassword = 'Passwords do not match';
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
      await register({
        name: name.trim(),
        email: email.trim(),
        password,
      });
      navigate('/', { replace: true });
    } catch (err) {
      if (err.code === 'EMAIL_ALREADY_EXISTS') {
        setFieldErrors((prev) => ({
          ...prev,
          email: 'An account with this email already exists',
        }));
      } else if (err.fieldErrors && Object.keys(err.fieldErrors).length > 0) {
        setFieldErrors(err.fieldErrors);
      } else {
        setGeneralError(err.message || 'Registration failed. Please check your details and try again.');
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
            id="register-name"
            label="Name"
            type="text"
            value={name}
            onChange={(e) => {
              setName(e.target.value);
              if (fieldErrors.name) setFieldErrors((prev) => ({ ...prev, name: null }));
            }}
            error={fieldErrors.name}
            placeholder="Your name"
            autoComplete="name"
            autoFocus
            disabled={isSubmitting}
            required
          />

          <Input
            id="register-email"
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
            disabled={isSubmitting}
            required
          />

          <Input
            id="register-password"
            label="Password"
            type="password"
            value={password}
            onChange={(e) => {
              setPassword(e.target.value);
              if (fieldErrors.password) setFieldErrors((prev) => ({ ...prev, password: null }));
            }}
            error={fieldErrors.password}
            placeholder="At least 8 characters"
            autoComplete="new-password"
            disabled={isSubmitting}
            required
          />

          <Input
            id="register-confirm-password"
            label="Confirm Password"
            type="password"
            value={confirmPassword}
            onChange={(e) => {
              setConfirmPassword(e.target.value);
              if (fieldErrors.confirmPassword) setFieldErrors((prev) => ({ ...prev, confirmPassword: null }));
            }}
            error={fieldErrors.confirmPassword}
            placeholder="Re-enter password"
            autoComplete="new-password"
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
            Create account
          </Button>
        </form>

        <footer className="lifeos-auth-footer">
          <span>Already have an account?</span>
          <Link to="/login" className="lifeos-auth-link">
            Sign in
          </Link>
        </footer>
      </div>
    </div>
  );
};

export default RegisterPage;
