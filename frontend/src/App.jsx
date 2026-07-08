import { useState } from 'react';
import { Routes, Route, useNavigate, useLocation } from 'react-router-dom';
import LoginPage from './LoginPage';
import Home from './Home';
import QuestionDetail from './QuestionDetail';
import AppNavbar from './Navbar';
import AskQuestion from './AskQuestion';
import ProfileDashboard from './ProfileDashboard';
import LeftSidebar from './LeftSidebar';
import UsersPage from './UsersPage';
import Footer from './Footer';
import TextPage from './TextPage';
import './App.css';
import TagsPage from './TagsPage';

// Pages where the sidebar should be hidden (e.g. login)
const NO_SIDEBAR_ROUTES = ['/login'];

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(() => !!localStorage.getItem('token'));
  const navigate = useNavigate();
  const location = useLocation();

  const hideSidebar = NO_SIDEBAR_ROUTES.includes(location.pathname);

  function handleLogout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    setIsLoggedIn(false);
    navigate('/');
  }

  return (
    <div className="app-shell">
      <AppNavbar isLoggedIn={isLoggedIn} onLogoutClick={handleLogout} />

      <div className="app-body">
        {!hideSidebar && (
          <LeftSidebar isLoggedIn={isLoggedIn} />
        )}

        <main className="app-content">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<LoginPage onLoginSuccess={() => { setIsLoggedIn(true); navigate('/'); }} />} />
            <Route path="/questions/:id" element={<QuestionDetail />} />
            <Route path="/questions/ask" element={<AskQuestion />} />
            <Route path="/profile" element={<ProfileDashboard />} />
            <Route path="/users" element={<UsersPage />} />
            <Route path="/tags" element={<TagsPage />} />

            <Route path="/help" element={<TextPage />} />
            <Route path="/chat" element={<TextPage />} />
            <Route path="/stack-internal" element={<TextPage />} />
            <Route path="/data-licensing" element={<TextPage />} />
            <Route path="/ads" element={<TextPage />} />
            <Route path="/about" element={<TextPage />} />
            <Route path="/press" element={<TextPage />} />
            <Route path="/work-here" element={<TextPage />} />
            <Route path="/legal" element={<TextPage />} />
            <Route path="/privacy-policy" element={<TextPage />} />
            <Route path="/terms-of-service" element={<TextPage />} />
            <Route path="/contact" element={<TextPage />} />
            <Route path="/cookie-settings" element={<TextPage />} />
            <Route path="/cookie-policy" element={<TextPage />} />
            <Route path="/technology" element={<TextPage />} />
            <Route path="/culture-recreation" element={<TextPage />} />
            <Route path="/life-arts" element={<TextPage />} />
            <Route path="/science" element={<TextPage />} />
            <Route path="/professional" element={<TextPage />} />
            <Route path="/business" element={<TextPage />} />
            <Route path="/api" element={<TextPage />} />
            <Route path="/data" element={<TextPage />} />
            <Route path="/blog" element={<TextPage />} />
            <Route path="/facebook" element={<TextPage />} />
            <Route path="/twitter" element={<TextPage />} />
            <Route path="/linkedin" element={<TextPage />} />
            <Route path="/instagram" element={<TextPage />} />
          </Routes>
        </main>
      </div>

      <Footer />
    </div>
  );
}

export default App;
