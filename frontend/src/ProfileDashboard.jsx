import { useState, useEffect } from 'react';

const API_BASE = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api') + '/users';

export default function ProfileDashboard() {
    const [bookmarks, setBookmarks] = useState([]);
    const [badges, setBadges] = useState([]);
    const username = localStorage.getItem('username');

    useEffect(() => {
        const headers = { 'Authorization': `Bearer ${localStorage.getItem('token')}` };

        fetch(`${API_BASE}/bookmarks`, { headers })
            .then(res => res.json()).then(data => setBookmarks(data)).catch(() => {});

        fetch(`${API_BASE}/badges`, { headers })
            .then(res => res.json()).then(data => setBadges(data)).catch(() => {});
    }, []);

    return (
        <div style={{ minHeight: '100dvh', background: '#0e0f13', padding: '32px 24px', fontFamily: 'Inter, sans-serif' }}>
            <div style={{ maxWidth: '860px', margin: '0 auto', color: '#edeef0' }}>
                <h2 style={{ color: '#f48225', marginBottom: '4px' }}>Welcome back, {username}!</h2>
                <p style={{ color: '#8b8fa7', marginBottom: '32px' }}>Track your saved items and platform recognition metrics here.</p>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '32px' }}>
                    <section style={{ background: '#1c1e28', border: '1px solid #2d303e', borderRadius: '8px', padding: '20px' }}>
                        <h4 style={{ borderBottom: '1px solid #2d303e', paddingBottom: '8px', color: '#0a95ff' }}>Your Bookmarks ({bookmarks.length})</h4>
                        {bookmarks.length === 0 ? <p style={{ fontSize: '13px', color: '#8b8fa7', marginTop: '12px' }}>No bookmarked questions yet.</p> : (
                            <ul style={{ paddingLeft: '16px', marginTop: '12px', display: 'flex', flexDirection: 'column', gap: '8px' }}>
                                {bookmarks.map((b, i) => (
                                    <li key={i}><a href={`/questions/${b.questionId}`} style={{ color: '#edeef0', textDecoration: 'underline' }}>{b.questionTitle}</a></li>
                                ))}
                            </ul>
                        )}
                    </section>

                    <section style={{ background: '#1c1e28', border: '1px solid #2d303e', borderRadius: '8px', padding: '20px' }}>
                        <h4 style={{ borderBottom: '1px solid #2d303e', paddingBottom: '8px', color: '#3dd68c' }}>Earned Badges ({badges.length})</h4>
                        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '10px', marginTop: '12px' }}>
                            {badges.length === 0 ? <p style={{ fontSize: '13px', color: '#8b8fa7' }}>Participate on the platform to earn reward badges.</p> : badges.map((badge, idx) => (
                                <div key={idx} style={{ background: '#222430', border: '1px solid #2d303e', padding: '6px 12px', borderRadius: '6px', fontSize: '12px' }}>
                                    <span style={{ fontWeight: 'bold', color: '#ffd700', marginRight: '6px' }}>●</span>
                                    <strong>{badge.badgeName}</strong>
                                    <div style={{ fontSize: '10px', color: '#8b8fa7', marginTop: '2px' }}>{badge.description}</div>
                                </div>
                            ))}
                        </div>
                    </section>
                </div>
            </div>
        </div>
    );
}