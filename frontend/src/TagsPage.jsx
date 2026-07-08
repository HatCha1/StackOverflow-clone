import { useState, useEffect } from 'react';
import './TagsPage.css';

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

export default function TagsPage() {
const [tags, setTags] = useState([]);
const [searchQuery, setSearchQuery] = useState('');
const [sortBy, setSortBy] = useState('popular'); // popular, name, new
const [loading, setLoading] = useState(true);

useEffect(() => {
    async function fetchTags() {
setLoading(true);
try {
    const response = await fetch(`${API_BASE}/tags?sort=${sortBy}&search=${searchQuery}`);
    if (response.ok) {
    const data = await response.json();
setTags(data);
}
} catch (error) {
console.error("Error loading tag collections:", error);
} finally {
  setLoading(false);
  }
}
fetchTags();
}, [sortBy, searchQuery]);

    return (
    <div className="tags-page">
    <h1 className="tags-title">Tags</h1>
    <p className="tags-description">
    A tag is a keyword or label that categorizes your question with other, similar questions. Using
    the right tags makes it easier for others to find and answer your question.
    </p>

    <div className="tags-filter-container">
    <div className="search-wrapper">
    <svg className="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
    <circle cx="11" cy="11" r="8"></circle>
    <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
    </svg>
    <input
    type="text"
    className="tag-search-input"
    placeholder="Filter by tag name"
    value={searchQuery}
    onChange={(e) => setSearchQuery(e.target.value)}
/>
</div>

<div className="tag-tabs">
<button
className={`tag-tab ${sortBy === 'popular' ? 'tag-tab--active' : ''}`}
    onClick={() => setSortBy('popular')}
>
Popular
</button>
<button
className={`tag-tab ${sortBy === 'name' ? 'tag-tab--active' : ''}`}
    onClick={() => setSortBy('name')}
>
Name
</button>
<button
className={`tag-tab ${sortBy === 'new' ? 'tag-tab--active' : ''}`}
    onClick={() => setSortBy('new')}
>
New
</button>
</div>
</div>

{loading ? (
<div className="tags-loading">Loading tags…</div>
) : (
<div className="tags-grid">
{tags.map((tag) => (
<div key={tag.tagId} className="tag-card">
<span className="tag-badge">{tag.tagName}</span>
<p className="tag-card-description">{tag.description}</p>
<div className="tag-card-stats">
<span>{tag.questionCount} questions</span>
<span>{tag.askedTodayCount} asked today</span>
</div>
</div>
))}
</div>
)}
</div>
);
}