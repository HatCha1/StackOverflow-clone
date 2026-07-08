import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import './Home.css';

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

// ─── Icons ───

function StackOverflowIcon() {
  return (
    <svg viewBox="0 0 32 37" xmlns="http://www.w3.org/2000/svg">
      <path d="M26 33v-9h4v13H0V24h4v9h22z" fill="rgba(255,255,255,0.9)" />
      <path d="M21.5 0l-2.7 2 9.9 13.3 2.7-2L21.5 0zM26 18.4l-2.1-2.7-12.4 9.5 2.1 2.7L26 18.4zM29.7 11.6L18.4 5.1l-1.8 2.9 11.3 6.5 1.8-2.9zM6 28h14v-4H6v4z" fill="white" />
    </svg>
  );
}

function ViewIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
      <circle cx="12" cy="12" r="3" />
    </svg>
  );
}

function ClockIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <circle cx="12" cy="12" r="10" />
      <polyline points="12 6 12 12 16 14" />
    </svg>
  );
}

function UserIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
      <circle cx="12" cy="7" r="4" />
    </svg>
  );
}

function LogoutIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
      <polyline points="16 17 21 12 16 7" />
      <line x1="21" y1="12" x2="9" y2="12" />
    </svg>
  );
}

// ─── Helpers ───

function formatDate(dateStr) {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  const now = new Date();
  const diffMs = now - date;
  const diffMin = Math.floor(diffMs / 60000);
  const diffHr = Math.floor(diffMin / 60);
  const diffDay = Math.floor(diffHr / 24);

  if (diffMin < 1) return 'just now';
  if (diffMin < 60) return `${diffMin}m ago`;
  if (diffHr < 24) return `${diffHr}h ago`;
  if (diffDay < 30) return `${diffDay}d ago`;
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
}

// ─── Skeleton Loader ───

function QuestionSkeleton() {
  return (
    <div className="question-skeleton">
      <div className="skeleton-stats">
        <div className="skeleton-block skeleton-stat" />
        <div className="skeleton-block skeleton-stat" />
      </div>
      <div className="skeleton-body">
        <div className="skeleton-block skeleton-title" />
        <div className="skeleton-block skeleton-subtitle" />
        <div className="skeleton-block skeleton-meta" />
      </div>
    </div>
  );
}

// ─── Question Card ───

function QuestionCard({ question, onClick }) {
  const isClosed = question.isClosed;

  return (
    <article
      className={`question-card ${isClosed ? 'question-card--closed' : ''}`}
      onClick={onClick}
      style={{ cursor: 'pointer' }}
    >
      {/* Stats column */}
      <div className="question-stats">
        <div className="stat-item">
          <span className="stat-value">{question.voteCount ?? 0}</span>
          <span className="stat-label">votes</span>
        </div>
        <div className="stat-item">
          <span className="stat-value">{question.viewCount ?? 0}</span>
          <span className="stat-label">views</span>
        </div>
      </div>

      {/* Main content */}
      <div className="question-content">
        <h2 className="question-title">
          {question.title}
          {isClosed && <span className="badge badge--closed">Closed</span>}
        </h2>

        {/* Excerpt */}
        <p className="question-excerpt">
          {question.body?.length > 150
            ? question.body.slice(0, 150) + '…'
            : question.body}
        </p>

        {question.tags && question.tags.length > 0 && (
          <div style={{ display: 'flex', gap: '6px', margin: '8px 0' }}>
            {question.tags.map((tag, idx) => (
              <span key={idx} style={{ background: '#222430', color: '#0a95ff', padding: '2px 8px', borderRadius: '4px', fontSize: '11px', fontWeight: '500' }}>
                {tag}
              </span>
            ))}
          </div>
        )}

        {/* Footer row */}
        <div className="question-footer">
          <div className="question-meta">
            <span className="meta-item">
              <ViewIcon />
              {question.viewCount ?? 0} views
            </span>
            <span className="meta-item">
              <ClockIcon />
              {formatDate(question.createdAt)}
            </span>
          </div>
          <div className="question-author">
            <UserIcon />
            <span>{question.authorName ?? 'Unknown'}</span>
          </div>
        </div>
      </div>
    </article>
  );
}

export default function Home() {
  const navigate = useNavigate();
  const [questions, setQuestions] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const token = localStorage.getItem('token');
  const username = localStorage.getItem('username') ?? '';
  const isLoggedIn = !!token;

  const [user, setUser] = useState(null);
  const [badges, setBadges] = useState([]);

  const [currentPage, setCurrentPage] = useState(1);
  const [perPage, setPerPage] = useState(15);

  useEffect(() => {
    async function fetchQuestions() {
      setIsLoading(true);
      setError(null);
      try {
        const res = await fetch(`${API_BASE}/questions`);
        if (!res.ok) throw new Error(`Server error: ${res.status}`);
        const data = await res.json();
        setQuestions(data);
      } catch (err) {
        setError(err.message || 'Failed to load questions.');
      } finally {
        setIsLoading(false);
      }
    }

    async function fetchUserData() {
      if (!token) return;
      const headers = { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' };
      try {
        const [userRes, badgeRes] = await Promise.allSettled([
          fetch(`${API_BASE}/users/me`, { headers }),
          fetch(`${API_BASE}/users/badges`, { headers }),
        ]);
        if (userRes.status === 'fulfilled' && userRes.value.ok) setUser(await userRes.value.json());
        if (badgeRes.status === 'fulfilled' && badgeRes.value.ok) setBadges(await badgeRes.value.json());
      } catch (_) {}
    }

    fetchQuestions();
    fetchUserData();
  }, []);

  const totalPages = Math.ceil(questions.length / perPage);

  const startIndex = (currentPage - 1) * perPage;
  const endIndex = startIndex + perPage;

  const visibleQuestions = questions.slice(startIndex, endIndex);

  function goToPage(page) {
    if (page < 1 || page > totalPages) return;
    setCurrentPage(page);
  }

  function changePerPage(value) {
    setPerPage(value);
    setCurrentPage(1);
  }

  return (
    <div className="home-layout">

          {/* ─── Questions Feed ─── */}
          <div className="home-container">

            {/* Page header */}
            <div className="page-header">
              <div>
                <h1 className="page-title">All Questions</h1>
                <p className="page-subtitle">
                  {isLoading ? 'Loading…' : error ? '' : `${questions.length} question${questions.length !== 1 ? 's' : ''}`}
                </p>
              </div>
              <button className="btn-ask" onClick={() => navigate('/questions/ask')}>
                Ask Question
              </button>
            </div>

            {isLoading && (
              <div className="questions-list" aria-busy="true">
                {Array.from({ length: 5 }).map((_, i) => <QuestionSkeleton key={i} />)}
              </div>
            )}
            {!isLoading && error && (
              <div className="error-state" role="alert">
                <p className="error-title">Could not load questions</p>
                <p className="error-detail">{error}</p>
              </div>
            )}
            {!isLoading && !error && questions.length === 0 && (
              <div className="empty-state">
                <p className="empty-title">No questions yet</p>
                <p className="empty-detail">Be the first to ask a question!</p>
              </div>
            )}
            {!isLoading && !error && questions.length > 0 && (
              <>
                <div className="question-list">
                  {visibleQuestions.map((q) => (
                    <QuestionCard
                      key={q.questionId}
                      question={q}
                      onClick={() => navigate(`/questions/${q.questionId}`)}
                    />
                  ))}
                </div>

                <div className="pagination-row">
                  <div className="pagination-pages">
                    <button
                      className="pagination-btn"
                      disabled={currentPage === 1}
                      onClick={() => goToPage(currentPage - 1)}
                    >
                      Prev
                    </button>

                    {Array.from({ length: totalPages }, (_, i) => i + 1)
                      .filter((page) => {
                        return (
                          page === 1 ||
                          page === totalPages ||
                          Math.abs(page - currentPage) <= 1
                        );
                      })
                      .map((page, index, arr) => (
                        <span key={page} className="pagination-group">
                          {index > 0 && page - arr[index - 1] > 1 && (
                            <span className="pagination-ellipsis">...</span>
                          )}

                          <button
                            className={`pagination-btn ${
                              currentPage === page ? 'pagination-btn--active' : ''
                            }`}
                            onClick={() => goToPage(page)}
                          >
                            {page}
                          </button>
                        </span>
                      ))}

                    <button
                      className="pagination-btn"
                      disabled={currentPage === totalPages}
                      onClick={() => goToPage(currentPage + 1)}
                    >
                      Next
                    </button>
                  </div>

                  <div className="per-page-group">
                    {[15, 30, 50].map((value) => (
                      <button
                        key={value}
                        className={`pagination-btn ${
                          perPage === value ? 'pagination-btn--active' : ''
                        }`}
                        onClick={() => changePerPage(value)}
                      >
                        {value}
                      </button>
                    ))}
                    <span className="per-page-label">per page</span>
                  </div>
                </div>
              </>
            )}
          </div>

          {/* ─── Sidebar (logged-in users only) ─── */}
          {isLoggedIn && (
            <aside className="home-sidebar">
              {/* User card */}
              <div className="sidebar-card">
                <div className="sidebar-avatar">
                  {(user?.displayName || username).slice(0, 2).toUpperCase()}
                </div>
                <div className="sidebar-user-info">
                  <div className="sidebar-display-name">{user?.displayName || username}</div>
                  <div className="sidebar-username">@{username}</div>
                </div>
                <div className="sidebar-reputation">
                  <span className="sidebar-rep-number">{user?.reputation ?? 0}</span>
                  <span className="sidebar-rep-label">reputation</span>
                </div>
                <Link to="/profile" className="sidebar-profile-link">View Profile →</Link>
              </div>

              {/* Badges */}
              <div className="sidebar-card">
                <h4 className="sidebar-card-title">Your Badges</h4>
                {badges.length === 0 ? (
                  <p className="sidebar-empty">No badges yet. Keep contributing!</p>
                ) : (
                  <div className="sidebar-badge-list">
                    {badges.map((b, i) => {
                      const dotColor = b.badgeType === 'GOLD' ? '#ffd700'
                        : b.badgeType === 'SILVER' ? '#c0c0c0'
                        : '#cd7f32';
                      return (
                        <div key={i} className="sidebar-badge">
                          <span style={{ color: dotColor, fontSize: '10px' }}>●</span>
                          <span>{b.badgeName}</span>
                        </div>
                      );
                    })}
                  </div>
                )}
              </div>
            </aside>
          )}

    </div>
  );
}

