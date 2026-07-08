import { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import './QuestionDetail.css';

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

function getToken() {
  return localStorage.getItem('token');
}
function getUsername() {
  return localStorage.getItem('username') ?? '';
}

// ─── Icons ───

function ArrowUpIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
      <polyline points="18 15 12 9 6 15" />
    </svg>
  );
}

function ArrowDownIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
      <polyline points="6 9 12 15 18 9" />
    </svg>
  );
}

function BookmarkIcon({ filled }) {
  return (
      <svg
          viewBox="0 0 24 24"
          fill={filled ? "var(--color-primary)" : "none"}
          stroke={filled ? "var(--color-primary)" : "currentColor"}
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
          style={{ width: '20px', height: '20px' }}
      >
        <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z" />
      </svg>
  );
}

function CheckIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
      <polyline points="20 6 9 17 4 12" />
    </svg>
  );
}

function CommentIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
    </svg>
  );
}

function BackIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <polyline points="15 18 9 12 15 6" />
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

async function authFetch(url, options = {}) {
  const token = getToken();
  return fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers,
    },
  });
}

// ─── Vote Button Group ───

function VoteButtons({ score, userVote, onUpvote, onDownvote, vertical = true }) {
  return (
    <div className={`vote-buttons ${vertical ? 'vote-buttons--vertical' : ''}`}>
      <button
        className={`vote-btn ${userVote === 'UPVOTE' ? 'vote-btn--active-up' : ''}`}
        onClick={onUpvote}
        aria-label="Upvote"
      >
        <ArrowUpIcon />
      </button>
      <span className="vote-score">{score ?? 0}</span>
      <button
        className={`vote-btn ${userVote === 'DOWNVOTE' ? 'vote-btn--active-down' : ''}`}
        onClick={onDownvote}
        aria-label="Downvote"
      >
        <ArrowDownIcon />
      </button>
    </div>
  );
}

// ─── Comment List ───

function CommentList({ comments, onAddComment, targetId, type }) {
  const [showForm, setShowForm] = useState(false);
  const [body, setBody] = useState('');
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmitComment(e) {
    e.preventDefault();
    if (!body.trim()) return;
    setSubmitting(true);
    try {
      const endpoint = type === 'question'
        ? `${API_BASE}/comments/question`
        : `${API_BASE}/comments/answer`;
      const payload = type === 'question'
        ? { body: body.trim(), questionId: targetId }
        : { body: body.trim(), answerId: targetId };

      const res = await authFetch(endpoint, {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      if (!res.ok) throw new Error('Failed to post comment');
      const newComment = await res.json();
      onAddComment(newComment);
      setBody('');
      setShowForm(false);
    } catch (err) {
      alert(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="comment-section">
      {comments.length > 0 && (
        <ul className="comment-list">
          {comments.map((c) => (
            <li key={c.commentId} className="comment-item">
              <span className="comment-body">{c.body}</span>
              <span className="comment-meta">
                — <strong>{c.authorName}</strong> {formatDate(c.createdAt)}
              </span>
            </li>
          ))}
        </ul>
      )}
      {showForm ? (
        <form className="comment-form" onSubmit={handleSubmitComment}>
          <textarea
            className="comment-textarea"
            value={body}
            onChange={(e) => setBody(e.target.value)}
            placeholder="Add a comment…"
            rows={2}
            autoFocus
          />
          <div className="comment-form-actions">
            <button type="submit" className="btn-comment-submit" disabled={submitting || !body.trim()}>
              {submitting ? 'Posting…' : 'Post Comment'}
            </button>
            <button type="button" className="btn-comment-cancel" onClick={() => setShowForm(false)}>
              Cancel
            </button>
          </div>
        </form>
      ) : (
        <button className="btn-add-comment" onClick={() => setShowForm(true)}>
          <CommentIcon /> Add a comment
        </button>
      )}
    </div>
  );
}

// ─── Answer Card ───

function AnswerCard({ answer, questionAuthorName, onVote, onAccept }) {
  const [comments, setComments] = useState([]);
  const [commentsLoaded, setCommentsLoaded] = useState(false);
  const [showComments, setShowComments] = useState(false);

  async function loadComments() {
    if (commentsLoaded) {
      setShowComments((s) => !s);
      return;
    }
    try {
      const res = await fetch(`${API_BASE}/comments/answer/${answer.answerId}`);  // not a real endpoint, handled via question
      if (res.ok) {
        const data = await res.json();
        setComments(data);
      }
    } catch (_) { }
    setCommentsLoaded(true);
    setShowComments(true);
  }

  const currentUsername = getUsername();
  const isAccepted = answer.isAccepted || answer.accepted;
  const canAccept = questionAuthorName === currentUsername;

  return (
    <div className={`answer-card ${isAccepted ? 'answer-card--accepted' : ''}`}>
      {isAccepted && (
        <div className="accepted-banner">
          <CheckIcon /> Accepted Answer
        </div>
      )}

      <div className="answer-layout">
        {/* Vote Column */}
        <div className="answer-vote-col">
          <VoteButtons
            score={answer.voteCount ?? 0}
            userVote={answer.userVote}
            onUpvote={() => onVote(answer.answerId, 'UPVOTE')}
            onDownvote={() => onVote(answer.answerId, 'DOWNVOTE')}
          />
          {canAccept && (
              <button
                  className={`btn-accept ${isAccepted ? 'btn-accept--active' : ''}`}
                  onClick={() => onAccept(answer.answerId)}
                  title={isAccepted ? "Unmark as accepted answer" : "Mark as accepted answer"}
              >
                <CheckIcon />
              </button>
          )}
        </div>

        {/* Content */}
        <div className="answer-content">
          <p className="answer-body">{answer.body}</p>

          <div className="answer-footer">
            <div className="answer-meta">
              <span className="meta-item"><ClockIcon /> answered {formatDate(answer.createdAt)}</span>
            </div>
            <div className="answer-author">
              <UserIcon />
              <span>{answer.username ?? answer.displayName ?? 'Unknown'}</span>
            </div>
          </div>

          <button className="btn-toggle-comments" onClick={loadComments}>
            <CommentIcon />
            {showComments ? 'Hide comments' : `${answer.commentCount ?? 0} comments`}
          </button>

          {showComments && (
            <CommentList
              comments={comments}
              onAddComment={(c) => setComments((prev) => [...prev, c])}
              targetId={answer.answerId}
              type="answer"
            />
          )}
        </div>
      </div>
    </div>
  );
}

// ─── Skeleton ───

function DetailSkeleton() {
  return (
    <div className="detail-skeleton">
      <div className="skeleton-block" style={{ height: 28, width: '70%', marginBottom: 16 }} />
      <div className="skeleton-block" style={{ height: 14, width: '90%', marginBottom: 8 }} />
      <div className="skeleton-block" style={{ height: 14, width: '80%', marginBottom: 8 }} />
      <div className="skeleton-block" style={{ height: 14, width: '60%', marginBottom: 32 }} />
      <div className="skeleton-block" style={{ height: 14, width: '40%' }} />
    </div>
  );
}

// ─── Main Page ───

export default function QuestionDetail() {
  const { id: questionId } = useParams();
  const navigate = useNavigate();
  const [question, setQuestion] = useState(null);
  const [answers, setAnswers] = useState([]);
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [isBookmarked, setIsBookmarked] = useState(false);

  // Answer form
  const [answerBody, setAnswerBody] = useState('');
  const [submittingAnswer, setSubmittingAnswer] = useState(false);
  const [answerError, setAnswerError] = useState(null);

  const loadData = useCallback(async () => {
    setLoading(true);
    setError(null);

    const token = getToken();
    const headers = token ? { 'Authorization': `Bearer ${token}` } : {};
    
    try {
      const [qRes, aRes, cRes] = await Promise.all([
        fetch(`${API_BASE}/questions/${questionId}`),
        fetch(`${API_BASE}/questions/${questionId}/answers`),
        fetch(`${API_BASE}/comments/question/${questionId}`),
      ]);

      if (!qRes.ok) throw new Error('Question not found');
      const qData = await qRes.json();
      const aData = await aRes.json();
      const cData = await cRes.json();
      setQuestion(qData);

      const normalizedAnswers = Array.isArray(aData) ? aData.map(ans => ({
        ...ans,
        isAccepted: ans.isAccepted !== undefined ? ans.isAccepted : ans.accepted
      })) : [];

      const sortedAnswers = [...normalizedAnswers].sort((x, y) => {
        const xAcc = x.isAccepted ? 1 : 0;
        const yAcc = y.isAccepted ? 1 : 0;
        if (xAcc !== yAcc) return yAcc - xAcc;
        return new Date(x.createdAt) - new Date(y.createdAt);
      });

      setAnswers(sortedAnswers);
      setComments(Array.isArray(cData) ? cData : []);

      if (token) {
        const bRes = await fetch(`${API_BASE}/users/bookmarks`, { headers });
        if (bRes.ok) {
          const bData = await bRes.json();
          const found = bData.some(b => String(b.questionId) === String(questionId));
          setIsBookmarked(found);
        }
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, [questionId]);

  useEffect(() => { loadData(); }, [loadData]);

  async function handleToggleBookmark() {
    const token = getToken();
    if (!token) {
      navigate('/login');
      return;
    }

    const method = isBookmarked ? 'DELETE' : 'POST';
    try {
      const res = await fetch(`${API_BASE}/questions/${questionId}/bookmark`, {
        method: method,
        headers: { 'Authorization': `Bearer ${token}` }
      });

      if (res.ok) {
        setIsBookmarked(!isBookmarked);
      } else {
        alert("Failed to update bookmark status");
      }
    } catch (_) {
      alert("Network communication error updating bookmark status");
    }
  }

  async function handleVoteQuestion(voteType) {
    try {
      await authFetch(`${API_BASE}/questions/${questionId}/vote`, {
        method: 'POST',
        body: JSON.stringify({ voteType }),
      });
      loadData();
    } catch (_) { }
  }

  async function handleVoteAnswer(answerId, voteType) {
    try {
      await authFetch(`${API_BASE}/answers/${answerId}/vote`, {
        method: 'POST',
        body: JSON.stringify({ voteType }),
      });
      loadData();
    } catch (_) { }
  }

  async function handleAcceptAnswer(answerId) {
    try {
      const res = await authFetch(`${API_BASE}/answers/${answerId}/accept`, { method: 'PUT' });
      if (!res.ok) throw new Error('Could not accept answer');
      loadData();
    } catch (err) {
      alert(err.message);
    }
  }

  async function handleSubmitAnswer(e) {
    e.preventDefault();
    if (!answerBody.trim()) return;
    setSubmittingAnswer(true);
    setAnswerError(null);
    try {
      const res = await authFetch(`${API_BASE}/questions/${questionId}/answers`, {
        method: 'POST',
        body: JSON.stringify({ body: answerBody.trim() }),
      });
      if (!res.ok) throw new Error('Failed to post answer');
      setAnswerBody('');
      loadData();
    } catch (err) {
      setAnswerError(err.message);
    } finally {
      setSubmittingAnswer(false);
    }
  }

  if (loading) {
    return (
      <div className="detail-page">
        <div className="detail-container">
          <button className="btn-back" onClick={() => navigate(-1)}><BackIcon /> All Questions</button>
          <DetailSkeleton />
        </div>
      </div>
    );
  }

  if (error || !question) {
    return (
      <div className="detail-page">
        <div className="detail-container">
          <button className="btn-back" onClick={() => navigate(-1)}><BackIcon /> All Questions</button>
          <div className="error-state" role="alert">
            <p className="error-title">Could not load question</p>
            <p className="error-detail">{error}</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="detail-page">
      <div className="detail-container">

        {/* Back nav */}
        <button className="btn-back" onClick={() => navigate(-1)}>
          <BackIcon /> Back
        </button>

        {/* ─── Question Section ─── */}
        <section className="question-section">
          <div className="question-header">
            <h1 className="question-detail-title">
              {question.title}
              {(question.isClosed || question.closed) && (
                <span className="badge badge--closed">Closed</span>
              )}
            </h1>
            <div className="question-header-meta">
              <span className="meta-item"><ClockIcon /> asked {formatDate(question.createdAt)}</span>
              <span className="meta-item"><UserIcon /> {question.authorName ?? 'Unknown'}</span>
            </div>
          </div>

          <div className="question-body-layout">
            {/* Vote column */}
            <div className="question-vote-col">
              <VoteButtons
                score={question.voteCount ?? 0}
                userVote={question.userVote}
                onUpvote={() => handleVoteQuestion('UPVOTE')}
                onDownvote={() => handleVoteQuestion('DOWNVOTE')}
              />
              <button
                  className={`bookmark-btn ${isBookmarked ? 'bookmark-btn--active' : ''}`}
                  onClick={handleToggleBookmark}
                  style={{ background: 'transparent', border: 'none', cursor: 'pointer', marginTop: '12px' }}
                  title={isBookmarked ? "Remove from bookmarks" : "Save this question"}
              >
                <BookmarkIcon filled={isBookmarked} />
              </button>
            </div>

            {/* Body */}
            <div className="question-body-content">
              <p className="question-body-text">{question.body}</p>

              <CommentList
                comments={comments}
                onAddComment={(c) => setComments((prev) => [...prev, c])}
                targetId={questionId}
                type="question"
              />
            </div>
          </div>
        </section>

        {/* ─── Answers Section ─── */}
        <section className="answers-section">
          <h2 className="section-heading">
            {answers.length} {answers.length === 1 ? 'Answer' : 'Answers'}
          </h2>

          {answers.length === 0 ? (
            <div className="empty-state">
              <p className="empty-title">No answers yet</p>
              <p className="empty-detail">Be the first to answer this question.</p>
            </div>
          ) : (
            <div className="answers-list">
              {answers.map((a) => (
                <AnswerCard
                  key={a.answerId}
                  answer={a}
                  questionAuthorName={question.authorName}
                  onVote={handleVoteAnswer}
                  onAccept={handleAcceptAnswer}
                />
              ))}
            </div>
          )}
        </section>

        {/* ─── Post Answer Form ─── */}
        <section className="post-answer-section">
          <h2 className="section-heading">Your Answer</h2>
          <form onSubmit={handleSubmitAnswer} className="answer-form">
            <textarea
              id="answer-body"
              className={`answer-textarea ${answerError ? 'answer-textarea--error' : ''}`}
              value={answerBody}
              onChange={(e) => setAnswerBody(e.target.value)}
              placeholder="Write your answer here…"
              rows={7}
            />
            {answerError && <p className="answer-error">{answerError}</p>}
            <button
              type="submit"
              className="btn-post-answer"
              disabled={submittingAnswer || !answerBody.trim()}
            >
              {submittingAnswer ? 'Posting…' : 'Post Your Answer'}
            </button>
          </form>
        </section>

      </div>
    </div>
  );
}
