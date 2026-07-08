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
import './App.css';

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
          </Routes>
        </main>
      </div>
    </div>
  );
}

export default App;
