import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './AskQuestion.css';

const API_BASE = 'http://localhost:8080/api';

function getToken() {
  return localStorage.getItem('token');
}

async function getBackendError(response) {
  const text = await response.text();

  if (!text) {
    return `Request failed with status ${response.status}`;
  }

  try {
    const data = JSON.parse(text);

    if (data.message) return data.message;
    if (data.error) return data.error;

    return text;
  } catch {
    return text;
  }
}

export default function AskQuestion() {
  const navigate = useNavigate();

  const [title, setTitle] = useState('');
  const [body, setBody] = useState('');
  const [tagsInput, setTagsInput] = useState('');

  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    const token = getToken();

    if (!token) {
      navigate('/login');
    }
  }, [navigate]);

  function parseTags(input) {
    return input
      .split(',')
      .map((tag) => tag.trim().toLowerCase())
      .filter((tag) => tag.length > 0);
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');

    const cleanTitle = title.trim();
    const cleanBody = body.trim();
    const tags = parseTags(tagsInput);

    if (!cleanTitle) {
      setError('Title cannot be blank');
      return;
    }

    if (!cleanBody) {
      setError('Body cannot be blank');
      return;
    }

    if (tags.length === 0) {
      setError('Please enter at least one tag');
      return;
    }

    const token = getToken();

    if (!token) {
      navigate('/login');
      return;
    }

    setIsSubmitting(true);

    try {
      const response = await fetch(`${API_BASE}/questions`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          title: cleanTitle,
          body: cleanBody,
          tags: tags,
        }),
      });

      if (!response.ok) {
        const message = await getBackendError(response);
        throw new Error(message);
      }

      const createdQuestion = await response.json();

      navigate(`/questions/${createdQuestion.questionId}`);
    } catch (err) {
      setError(err.message || 'Failed to create question');
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main className="ask-page">
      <section className="ask-container">
        <div className="ask-header">
          <h1>Ask a public question</h1>
          <p>
            Write a clear title, explain your problem, and add tags so others can find it.
          </p>
        </div>

        <form className="ask-form" onSubmit={handleSubmit}>
          {error && <div className="ask-error">{error}</div>}

          <div className="ask-card">
            <label htmlFor="title">Title</label>
            <p className="ask-help">
              Be specific and imagine you are asking another developer.
            </p>
            <input
              id="title"
              type="text"
              placeholder="How do I fix this Spring Boot error?"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
            />
          </div>

          <div className="ask-card">
            <label htmlFor="body">Body</label>
            <p className="ask-help">
              Include the error, what you tried, and what you expected to happen.
            </p>
            <textarea
              id="body"
              placeholder="I am getting this error when running my app..."
              value={body}
              onChange={(e) => setBody(e.target.value)}
              rows={10}
            />
          </div>

          <div className="ask-card">
            <label htmlFor="tags">Tags</label>
            <p className="ask-help">
              Separate tags with commas. Example: spring-boot, java, postgresql
            </p>
            <input
              id="tags"
              type="text"
              placeholder="spring-boot, java, postgresql"
              value={tagsInput}
              onChange={(e) => setTagsInput(e.target.value)}
            />
          </div>

          <div className="ask-actions">
            <button
              type="button"
              className="btn-cancel"
              onClick={() => navigate('/')}
              disabled={isSubmitting}
            >
              Cancel
            </button>

            <button
              type="submit"
              className="btn-post-question"
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Posting...' : 'Post your question'}
            </button>
          </div>
        </form>
      </section>
    </main>
  );
}