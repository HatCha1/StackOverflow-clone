import { Link, useNavigate } from 'react-router-dom';
import './Navbar.css';

function StackIcon() {
  return (
    <svg width="28" height="28" viewBox="0 0 28 28" fill="none" aria-hidden="true">
      <rect x="4" y="18" width="20" height="3" rx="1.5" fill="#f48225"/>
      <rect x="4" y="13" width="20" height="3" rx="1.5" fill="#f48225" opacity="0.75"/>
      <rect x="4" y="8" width="20" height="3" rx="1.5" fill="#f48225" opacity="0.45"/>
    </svg>
  );
}

function UserIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
      <circle cx="12" cy="7" r="4"/>
    </svg>
  );
}

function AskIcon() {
  return (
    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
      <line x1="12" y1="5" x2="12" y2="19"/>
      <line x1="5" y1="12" x2="19" y2="12"/>
    </svg>
  );
}

function AppNavbar({ isLoggedIn, onLogoutClick }) {
  const navigate = useNavigate();
  const username = localStorage.getItem('username');

  return (
    <header className="navbar">
      <div className="navbar-inner">

        {/* ─── Brand / Logo ─── */}
        <Link to="/" className="navbar-brand" aria-label="Go to homepage">
          <StackIcon />
          <span className="navbar-brand-text">
            stack<span className="navbar-brand-accent">overflow</span>
          </span>
        </Link>

        {/* ─── Right side actions ─── */}
        <nav className="navbar-actions">
          {isLoggedIn ? (
            <>
              <Link to="/profile" className="navbar-avatar" title={`Profile: ${username}`}>
                <UserIcon />
                <span className="navbar-username">{username}</span>
              </Link>
              <button className="navbar-btn navbar-btn--logout" onClick={onLogoutClick}>
                Sign out
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="navbar-btn navbar-btn--secondary">Log in</Link>
              <Link to="/login?register=1" className="navbar-btn navbar-btn--primary">Sign up</Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}

export default AppNavbar;