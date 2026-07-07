import { useState, useEffect } from 'react';
import './Home.css';

const API_BASE = 'http://localhost:8080/api';

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

import { useNavigate } from 'react-router-dom';

export default function Home() {
  const navigate = useNavigate();
  const [questions, setQuestions] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const username = localStorage.getItem('username') ?? 'User';

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

    fetchQuestions();
  }, []);


  return (
    <div className="home-page">
      {/* ─── Main Content ─── */}
      <main className="home-main">
        <div className="home-container">

          {/* Page header */}
          <div className="page-header">
            <div>
              <h1 className="page-title">All Questions</h1>
              <p className="page-subtitle">
                {isLoading ? 'Loading…' : error ? '' : `${questions.length} question${questions.length !== 1 ? 's' : ''}`}
              </p>
            </div>
            {/* Ask Question button — visible but disabled until the form is built */}
            <button
              className="btn-ask"
              onClick={() => navigate('/questions/ask')}
            >
              Ask Question
            </button>
          </div>

          {/* Loading state */}
          {isLoading && (
            <div className="questions-list" aria-busy="true" aria-label="Loading questions">
              {Array.from({ length: 5 }).map((_, i) => (
                <QuestionSkeleton key={i} />
              ))}
            </div>
          )}

          {/* Error state */}
          {!isLoading && error && (
            <div className="error-state" role="alert">
              <p className="error-title">Could not load questions</p>
              <p className="error-detail">{error}</p>
            </div>
          )}

          {/* Empty state */}
          {!isLoading && !error && questions.length === 0 && (
            <div className="empty-state">
              <p className="empty-title">No questions yet</p>
              <p className="empty-detail">Be the first to ask a question!</p>
            </div>
          )}

          {/* Questions list */}
          {!isLoading && !error && questions.length > 0 && (
            <div className="question-list">
              {questions.map((q) => (
                <QuestionCard
                  key={q.questionId}
                  question={q}
                  onClick={() => navigate(`/questions/${q.questionId}`)}
                />
              ))}
            </div>
          )}

        </div>
      </main>
    </div>
  );
}
