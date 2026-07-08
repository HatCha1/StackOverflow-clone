import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './UsersPage.css';

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

function formatDate(d) {
  if (!d) return '';
  return new Date(d).toLocaleDateString('en-US', { month: 'short', year: 'numeric' });
}

function UserCard({ user }) {
  const initials = (user.displayName || user.username).slice(0, 2).toUpperCase();

  return (
    <article className="user-card">
      <div className="user-card-avatar">{initials}</div>
      <div className="user-card-body">
        <div className="user-card-name">{user.displayName || user.username}</div>
        <div className="user-card-username">@{user.username}</div>
        {user.bio && <p className="user-card-bio">{user.bio.length > 80 ? user.bio.slice(0, 80) + '…' : user.bio}</p>}
        <div className="user-card-footer">
          <span className="user-card-rep">
            <strong>{user.reputation ?? 0}</strong> reputation
          </span>
          <span className="user-card-joined">member since {formatDate(user.createdAt)}</span>
        </div>
      </div>
    </article>
  );
}

function UserCardSkeleton() {
  return (
    <div className="user-card user-card--skeleton">
      <div className="user-card-avatar skeleton-pulse" />
      <div className="user-card-body" style={{ gap: '8px' }}>
        <div className="skeleton-line skeleton-pulse" style={{ width: '60%', height: '14px' }} />
        <div className="skeleton-line skeleton-pulse" style={{ width: '40%', height: '12px' }} />
        <div className="skeleton-line skeleton-pulse" style={{ width: '80%', height: '12px' }} />
      </div>
    </div>
  );
}

export default function UsersPage() {
  const [users, setUsers] = useState([]);
  const [filtered, setFiltered] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function load() {
      setLoading(true);
      try {
        const res = await fetch(`${API_BASE}/users`);
        if (!res.ok) throw new Error(`Server error: ${res.status}`);
        const data = await res.json();
        setUsers(data);
        setFiltered(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }
    load();
  }, []);

  useEffect(() => {
    const q = search.toLowerCase();
    setFiltered(
      users.filter(
        (u) =>
          u.username.toLowerCase().includes(q) ||
          (u.displayName || '').toLowerCase().includes(q) ||
          (u.bio || '').toLowerCase().includes(q)
      )
    );
  }, [search, users]);

  return (
    <div className="users-page">
      {/* Header */}
      <div className="users-header">
        <div>
          <h1 className="users-title">Users</h1>
          <p className="users-subtitle">
            {loading ? 'Loading…' : `${filtered.length} user${filtered.length !== 1 ? 's' : ''}`}
          </p>
        </div>
        <input
          type="search"
          className="users-search"
          placeholder="Filter by name or username…"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          aria-label="Search users"
        />
      </div>

      {/* Error */}
      {error && (
        <div className="users-error">Could not load users: {error}</div>
      )}

      {/* Grid */}
      <div className="users-grid">
        {loading
          ? Array.from({ length: 12 }).map((_, i) => <UserCardSkeleton key={i} />)
          : filtered.length === 0
            ? <p className="users-empty">No users match your search.</p>
            : filtered.map((u) => <UserCard key={u.userId} user={u} />)
        }
      </div>
    </div>
  );
}
