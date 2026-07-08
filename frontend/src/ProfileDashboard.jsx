import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './ProfileDashboard.css';

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

function getToken() { return localStorage.getItem('token'); }
function getUsername() { return localStorage.getItem('username') ?? ''; }

function authFetch(url, opts = {}) {
  const token = getToken();
  if (!token) return Promise.reject(new Error('Not logged in'));
  return fetch(url, {
    ...opts,
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
      ...(opts.headers || {}),
    },
  });
}

function formatDate(d) {
  if (!d) return '';
  return new Date(d).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
}

// ─── Tabs ───

const TABS = [
  { key: 'overview', label: 'Overview' },
  { key: 'settings', label: 'Settings' },
  { key: 'reputation', label: 'Reputation' },
];

export default function ProfileDashboard() {
  const navigate = useNavigate();
  const [tab, setTab] = useState('overview');
  const [user, setUser] = useState(null);
  const [bookmarks, setBookmarks] = useState([]);
  const [badges, setBadges] = useState([]);
  const [repHistory, setRepHistory] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!getToken()) { navigate('/login'); return; }
    loadProfile();
  }, []);

  async function loadProfile() {
    setLoading(true);
    try {
      const headers = { 'Authorization': `Bearer ${getToken()}`, 'Content-Type': 'application/json' };

      const [userRes, bookmarkRes, badgeRes, repRes] = await Promise.allSettled([
        fetch(`${API_BASE}/users/me`, { headers }),
        fetch(`${API_BASE}/users/bookmarks`, { headers }),
        fetch(`${API_BASE}/users/badges`, { headers }),
        fetch(`${API_BASE}/users/reputation-history`, { headers }),
      ]);

      if (userRes.status === 'fulfilled' && userRes.value.ok) setUser(await userRes.value.json());
      if (bookmarkRes.status === 'fulfilled' && bookmarkRes.value.ok) setBookmarks(await bookmarkRes.value.json());
      if (badgeRes.status === 'fulfilled' && badgeRes.value.ok) setBadges(await badgeRes.value.json());
      if (repRes.status === 'fulfilled' && repRes.value.ok) setRepHistory(await repRes.value.json());
    } catch (_) { }
    setLoading(false);
  }

  if (loading) {
    return (
      <div className="profile-page">
        <div className="profile-container">
          <p style={{ color: '#8b8fa7' }}>Loading profile…</p>
        </div>
      </div>
    );
  }

  const displayName = user?.displayName || getUsername();
  const initials = displayName.slice(0, 2).toUpperCase();

  return (
    <div className="profile-page">
      <div className="profile-container">

        {/* ─── Header ─── */}
        <div className="profile-header">
          <div className="profile-avatar">{initials}</div>
          <div className="profile-info">
            <h1>{displayName}</h1>
            <p className="profile-username">@{user?.username || getUsername()}</p>
            {user?.bio && <p className="profile-bio">{user.bio}</p>}
          </div>
          <div className="profile-stats">
            <div className="stat-badge">
              <span className="stat-number">{user?.reputation ?? 0}</span>
              <span className="stat-label">Reputation</span>
            </div>
            <div className="stat-badge">
              <span className="stat-number">{badges.length}</span>
              <span className="stat-label">Badges</span>
            </div>
            <div className="stat-badge">
              <span className="stat-number">{bookmarks.length}</span>
              <span className="stat-label">Bookmarks</span>
            </div>
          </div>
        </div>

        {/* ─── Tabs ─── */}
        <div className="profile-tabs">
          {TABS.map((t) => (
            <button
              key={t.key}
              className={`profile-tab ${tab === t.key ? 'profile-tab--active' : ''}`}
              onClick={() => setTab(t.key)}
            >
              {t.label}
            </button>
          ))}
        </div>

        {/* ─── Tab Content ─── */}
        {tab === 'overview' && (
          <OverviewTab bookmarks={bookmarks} badges={badges} />
        )}
        {tab === 'settings' && (
          <SettingsTab user={user} onProfileUpdated={loadProfile} navigate={navigate} />
        )}
        {tab === 'reputation' && (
          <ReputationTab history={repHistory} />
        )}
      </div>
    </div>
  );
}

// ─────────────────────────────────────
// Overview Tab
// ─────────────────────────────────────

function OverviewTab({ bookmarks, badges }) {
  return (
    <div className="profile-grid">
      {/* Bookmarks */}
      <div className="profile-card">
        <h3>Bookmarks ({bookmarks.length})</h3>
        {bookmarks.length === 0 ? (
          <p className="profile-empty">No bookmarked questions yet.</p>
        ) : (
          <ul className="bookmark-list">
            {bookmarks.map((b, i) => (
              <li key={i} className="bookmark-item">
                <a href={`/questions/${b.questionId}`}>{b.questionTitle}</a>
              </li>
            ))}
          </ul>
        )}
      </div>

      {/* Badges */}
      <div className="profile-card">
        <h3>Earned Badges ({badges.length})</h3>
        {badges.length === 0 ? (
          <p className="profile-empty">Participate on the platform to earn reward badges.</p>
        ) : (
          <div className="badge-grid">
            {badges.map((badge, idx) => {
              const typeClass = badge.badgeType === 'GOLD' ? 'badge-chip--gold'
                : badge.badgeType === 'SILVER' ? 'badge-chip--silver'
                : 'badge-chip--bronze';
              return (
                <div key={idx} className={`badge-chip ${typeClass}`}>
                  <span className="badge-dot">●</span>
                  <strong>{badge.badgeName}</strong>
                  {badge.description && <div className="badge-description">{badge.description}</div>}
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}

// ─────────────────────────────────────
// Settings Tab
// ─────────────────────────────────────

function SettingsTab({ user, onProfileUpdated, navigate }) {
  // Edit profile state
  const [displayName, setDisplayName] = useState(user?.displayName || '');
  const [bio, setBio] = useState(user?.bio || '');
  const [profileMsg, setProfileMsg] = useState(null);
  const [savingProfile, setSavingProfile] = useState(false);

  // Change password state
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [passwordMsg, setPasswordMsg] = useState(null);
  const [savingPassword, setSavingPassword] = useState(false);

  // Delete account state
  const [deleteConfirm, setDeleteConfirm] = useState('');
  const [deleting, setDeleting] = useState(false);

  async function handleSaveProfile(e) {
    e.preventDefault();
    setSavingProfile(true);
    setProfileMsg(null);
    try {
      const res = await authFetch(`${API_BASE}/users/me`, {
        method: 'PUT',
        body: JSON.stringify({ displayName: displayName.trim() || null, bio: bio.trim() || null }),
      });
      if (!res.ok) {
        const data = await res.json().catch(() => null);
        throw new Error(data?.message || 'Failed to update profile');
      }
      setProfileMsg({ type: 'success', text: 'Profile updated successfully!' });
      onProfileUpdated();
    } catch (err) {
      setProfileMsg({ type: 'error', text: err.message });
    } finally {
      setSavingProfile(false);
    }
  }

  async function handleChangePassword(e) {
    e.preventDefault();
    setSavingPassword(true);
    setPasswordMsg(null);
    try {
      const res = await authFetch(`${API_BASE}/users/me/password`, {
        method: 'PATCH',
        body: JSON.stringify({ currentPassword, newPassword }),
      });
      if (!res.ok) {
        const data = await res.json().catch(() => null);
        throw new Error(data?.message || 'Failed to change password');
      }
      setPasswordMsg({ type: 'success', text: 'Password changed successfully!' });
      setCurrentPassword('');
      setNewPassword('');
    } catch (err) {
      setPasswordMsg({ type: 'error', text: err.message });
    } finally {
      setSavingPassword(false);
    }
  }

  async function handleDeleteAccount() {
    if (deleteConfirm !== 'delete my account') {
      alert('Please type "delete my account" exactly to confirm.');
      return;
    }
    setDeleting(true);
    try {
      const res = await authFetch(`${API_BASE}/users/me`, {
        method: 'DELETE',
        body: JSON.stringify({
          usernameOrEmail: user?.username || getUsername(),
          confirmationPhrase: 'delete my account',
        }),
      });
      if (!res.ok) {
        const data = await res.json().catch(() => null);
        throw new Error(data?.message || 'Failed to delete account');
      }
      localStorage.removeItem('token');
      localStorage.removeItem('username');
      alert('Your account has been deleted.');
      navigate('/');
      window.location.reload();
    } catch (err) {
      alert(err.message);
    } finally {
      setDeleting(false);
    }
  }

  return (
    <div>
      {/* ─── Edit Profile ─── */}
      <div className="profile-card">
        <h3>Edit Profile</h3>
        {profileMsg && (
          <div className={`profile-message profile-message--${profileMsg.type}`}>{profileMsg.text}</div>
        )}
        <form onSubmit={handleSaveProfile}>
          <div className="profile-form-group">
            <label htmlFor="displayName">Display Name</label>
            <input
              id="displayName"
              className="profile-input"
              value={displayName}
              onChange={(e) => setDisplayName(e.target.value)}
              placeholder="How others see your name"
              maxLength={100}
            />
          </div>
          <div className="profile-form-group">
            <label htmlFor="bio">Bio</label>
            <textarea
              id="bio"
              className="profile-textarea"
              value={bio}
              onChange={(e) => setBio(e.target.value)}
              placeholder="Tell others about yourself…"
              maxLength={500}
            />
          </div>
          <button type="submit" className="profile-btn profile-btn--primary" disabled={savingProfile}>
            {savingProfile ? 'Saving…' : 'Save Changes'}
          </button>
        </form>
      </div>

      {/* ─── Change Password ─── */}
      <div className="profile-card">
        <h3>Change Password</h3>
        {passwordMsg && (
          <div className={`profile-message profile-message--${passwordMsg.type}`}>{passwordMsg.text}</div>
        )}
        <form onSubmit={handleChangePassword}>
          <div className="profile-form-group">
            <label htmlFor="currentPassword">Current Password</label>
            <input
              id="currentPassword"
              type="password"
              className="profile-input"
              value={currentPassword}
              onChange={(e) => setCurrentPassword(e.target.value)}
              placeholder="Enter your current password"
            />
          </div>
          <div className="profile-form-group">
            <label htmlFor="newPassword">New Password</label>
            <input
              id="newPassword"
              type="password"
              className="profile-input"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              placeholder="Min 8 characters"
              minLength={8}
            />
          </div>
          <button
            type="submit"
            className="profile-btn profile-btn--primary"
            disabled={savingPassword || !currentPassword || !newPassword}
          >
            {savingPassword ? 'Changing…' : 'Change Password'}
          </button>
        </form>
      </div>

      {/* ─── Danger Zone ─── */}
      <div className="profile-card danger-zone">
        <h3>⚠ Danger Zone</h3>
        <p className="danger-description">
          Permanently delete your account and all associated data. This action <strong>cannot be undone</strong>.
          <br />
          Type <code style={{ color: '#d93025', background: '#1a1012', padding: '2px 6px', borderRadius: '3px' }}>delete my account</code> to confirm.
        </p>
        <input
          className="danger-confirm-input"
          value={deleteConfirm}
          onChange={(e) => setDeleteConfirm(e.target.value)}
          placeholder='Type "delete my account"'
        />
        <button
          className="profile-btn profile-btn--danger"
          disabled={deleting || deleteConfirm !== 'delete my account'}
          onClick={handleDeleteAccount}
        >
          {deleting ? 'Deleting…' : 'Delete My Account'}
        </button>
      </div>
    </div>
  );
}

// ─────────────────────────────────────
// Reputation Tab
// ─────────────────────────────────────

function ReputationTab({ history }) {
  if (!history || history.length === 0) {
    return (
      <div className="profile-card">
        <h3>Reputation History</h3>
        <p className="profile-empty">No reputation changes yet. Ask questions, post answers, and receive votes to build your reputation!</p>
      </div>
    );
  }

  return (
    <div className="profile-card">
      <h3>Reputation History</h3>
      <div className="rep-list">
        {history.map((r) => (
          <div key={r.reputationId} className="rep-item">
            <span className={`rep-change ${r.reputationChange >= 0 ? 'rep-change--positive' : 'rep-change--negative'}`}>
              {r.reputationChange >= 0 ? '+' : ''}{r.reputationChange}
            </span>
            <span className="rep-reason">{r.reason}</span>
            {r.sourceUsername && (
              <span style={{ color: '#8b8fa7', fontSize: '12px' }}>by {r.sourceUsername}</span>
            )}
            <span className="rep-date">{formatDate(r.createdAt)}</span>
          </div>
        ))}
      </div>
    </div>
  );
}