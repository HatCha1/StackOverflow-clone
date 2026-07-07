import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './LoginPage.css';

const API_BASE = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api') + '/users';

// ─── Icon Components ───
function StackOverflowIcon() {
  return (
    <svg viewBox="0 0 32 37" xmlns="http://www.w3.org/2000/svg">
      <path d="M26 33v-9h4v13H0V24h4v9h22z" fill="rgba(255,255,255,0.9)" />
      <path d="M21.5 0l-2.7 2 9.9 13.3 2.7-2L21.5 0zM26 18.4l-2.1-2.7-12.4 9.5 2.1 2.7L26 18.4zM29.7 11.6L18.4 5.1l-1.8 2.9 11.3 6.5 1.8-2.9zM6 28h14v-4H6v4z" fill="white" />
    </svg>
  );
}

function EyeIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
      <circle cx="12" cy="12" r="3" />
    </svg>
  );
}

function EyeOffIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
      <line x1="1" y1="1" x2="23" y2="23" />
    </svg>
  );
}

function ErrorIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <circle cx="12" cy="12" r="10" />
      <line x1="15" y1="9" x2="9" y2="15" />
      <line x1="9" y1="9" x2="15" y2="15" />
    </svg>
  );
}

function SuccessIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
      <polyline points="22 4 12 14.01 9 11.01" />
    </svg>
  );
}

export default function LoginPage({ onLoginSuccess }) {
  const [activeTab, setActiveTab] = useState('login');
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState(null); // { type: 'error' | 'success', text: '' }
  const [shakeError, setShakeError] = useState(false);
  const navigate = useNavigate();

  // Form state
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  function clearForm() {
    setUsername('');
    setEmail('');
    setPassword('');
    setMessage(null);
    setShowPassword(false);
  }

  function handleTabSwitch(tab) {
    setActiveTab(tab);
    clearForm();
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setMessage(null);
    setShakeError(false);

    // Basic client-side validation
    if (!username.trim() || !password.trim()) {
      triggerError('Please fill in all required fields.');
      return;
    }

    if (activeTab === 'register' && !email.trim()) {
      triggerError('Please enter your email address.');
      return;
    }

    setIsLoading(true);

    try {
      if (activeTab === 'login') {
        await handleLogin();
      } else {
        await handleRegister();
      }
    } catch (err) {
      triggerError(err.message || 'Something went wrong. Please try again.');
    } finally {
      setIsLoading(false);
    }
  }

  async function handleLogin() {
    const response = await fetch(`${API_BASE}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || 'Invalid username or password.');
    }

    const data = await response.json();

    // Store JWT token
    localStorage.setItem('token', data.token);
    localStorage.setItem('username', data.username);

    if (onLoginSuccess) {
      onLoginSuccess();
    }

    setMessage({ type: 'success', text: `Welcome back, ${data.username}!` });

    navigate('/')
  }

  async function handleRegister() {
    const response = await fetch(`${API_BASE}/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, email, password }),
    });

    if (!response.ok) {
      const errorData = await response.text();
      throw new Error(errorData || 'Registration failed. Please try again.');
    }

    setMessage({ type: 'success', text: 'Account created successfully! You can now log in.' });

    // Auto-switch to login tab after registration
    setTimeout(() => {
      handleTabSwitch('login');
      setMessage({ type: 'success', text: 'Account created! Please log in.' });
    }, 1500);
  }

  function triggerError(text) {
    setMessage({ type: 'error', text });
    setShakeError(true);
    setTimeout(() => setShakeError(false), 500);
  }

  return (
    <div className="login-page">
      <div className="login-container">
        {/* ─── Brand ─── */}
        <div className="login-brand">
          <div className="login-brand-icon">
            <StackOverflowIcon />
          </div>
          <h1>Stack Overflow</h1>
          <p>Every developer has a tab open to Stack Overflow</p>
        </div>

        {/* ─── Card ─── */}
        <div className="login-card">
          {/* Tab Switcher */}
          <div className="login-tabs" role="tablist">
            <button
              id="tab-login"
              className={`login-tab ${activeTab === 'login' ? 'login-tab--active' : ''}`}
              onClick={() => handleTabSwitch('login')}
              role="tab"
              aria-selected={activeTab === 'login'}
            >
              Log In
            </button>
            <button
              id="tab-register"
              className={`login-tab ${activeTab === 'register' ? 'login-tab--active' : ''}`}
              onClick={() => handleTabSwitch('register')}
              role="tab"
              aria-selected={activeTab === 'register'}
            >
              Sign Up
            </button>
          </div>

          {/* Messages */}
          {message && (
            <div className={`message message--${message.type} ${shakeError ? 'message--shake' : ''}`}>
              {message.type === 'error' ? <ErrorIcon /> : <SuccessIcon />}
              <span>{message.text}</span>
            </div>
          )}

          {/* Form */}
          <form className="login-form" onSubmit={handleSubmit} noValidate>
            {/* Username */}
            <div className="input-group">
              <label htmlFor="input-username">Username</label>
              <div className="input-wrapper">
                <input
                  id="input-username"
                  type="text"
                  placeholder="Enter your username"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  autoComplete="username"
                  autoFocus
                />
              </div>
            </div>

            {/* Email (Register only) */}
            {activeTab === 'register' && (
              <div className="input-group">
                <label htmlFor="input-email">Email</label>
                <div className="input-wrapper">
                  <input
                    id="input-email"
                    type="email"
                    placeholder="you@example.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    autoComplete="email"
                  />
                </div>
              </div>
            )}

            {/* Password */}
            <div className="input-group">
              <label htmlFor="input-password">Password</label>
              <div className="input-wrapper">
                <input
                  id="input-password"
                  type={showPassword ? 'text' : 'password'}
                  placeholder="Enter your password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  autoComplete={activeTab === 'login' ? 'current-password' : 'new-password'}
                />
                <button
                  type="button"
                  className="password-toggle"
                  onClick={() => setShowPassword(!showPassword)}
                  aria-label={showPassword ? 'Hide password' : 'Show password'}
                >
                  {showPassword ? <EyeOffIcon /> : <EyeIcon />}
                </button>
              </div>
            </div>

            {/* Submit */}
            <button
              id="btn-submit"
              type="submit"
              className="btn-submit"
              disabled={isLoading}
            >
              <span className="btn-content">
                {isLoading && <span className="spinner" />}
                {isLoading
                  ? (activeTab === 'login' ? 'Logging in...' : 'Creating account...')
                  : (activeTab === 'login' ? 'Log In' : 'Create Account')
                }
              </span>
            </button>
          </form>
        </div>

        {/* ─── Footer ─── */}
        <div className="login-footer">
          <p>
            By continuing, you agree to our{' '}
            <a href="#terms">Terms of Service</a> and{' '}
            <a href="#privacy">Privacy Policy</a>.
          </p>
        </div>
      </div>
    </div>
  );
}
