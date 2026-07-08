import { NavLink, useLocation } from 'react-router-dom';
import './LeftSidebar.css';

function QuestionsIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
    </svg>
  );
}

function TagIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/>
      <line x1="7" y1="7" x2="7.01" y2="7"/>
    </svg>
  );
}

function BookmarkIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
    </svg>
  );
}

function UsersIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
      <circle cx="9" cy="7" r="4"/>
      <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
      <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
    </svg>
  );
}

function AskIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
      <line x1="12" y1="5" x2="12" y2="19"/>
      <line x1="5" y1="12" x2="19" y2="12"/>
    </svg>
  );
}

const NAV_ITEMS = [
  { to: '/',          label: 'Questions',  Icon: QuestionsIcon, exact: true },
  { to: '/tags',      label: 'Tags',       Icon: TagIcon },
  { to: '/users',     label: 'Users',      Icon: UsersIcon },
];

const AUTH_ITEMS = [
  { to: '/profile',   label: 'Bookmarks',  Icon: BookmarkIcon },
];

export default function LeftSidebar({ isLoggedIn }) {
  return (
    <aside className="left-sidebar">
      <nav className="left-sidebar-nav">
        <ul className="left-sidebar-list">
          {NAV_ITEMS.map(({ to, label, Icon, exact }) => (
            <li key={to}>
              <NavLink
                to={to}
                end={exact}
                className={({ isActive }) =>
                  `left-sidebar-link ${isActive ? 'left-sidebar-link--active' : ''}`
                }
              >
                <Icon />
                {label}
              </NavLink>
            </li>
          ))}
        </ul>

        {isLoggedIn && (
          <>
            <div className="left-sidebar-section-label">My Account</div>
            <ul className="left-sidebar-list">
              {AUTH_ITEMS.map(({ to, label, Icon }) => (
                <li key={to}>
                  <NavLink
                    to={to}
                    className={({ isActive }) =>
                      `left-sidebar-link ${isActive ? 'left-sidebar-link--active' : ''}`
                    }
                  >
                    <Icon />
                    {label}
                  </NavLink>
                </li>
              ))}
            </ul>
          </>
        )}
      </nav>

      {isLoggedIn && (
        <NavLink to="/questions/ask" className="left-sidebar-ask-btn">
          <AskIcon />
          Ask Question
        </NavLink>
      )}
    </aside>
  );
}
