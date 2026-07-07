import { useState } from 'react';
import { Routes, Route, useNavigate } from 'react-router-dom';
import LoginPage from './LoginPage';
import Home from './Home';
import QuestionDetail from './QuestionDetail';
import AppNavbar from './Navbar';
import AskQuestion from './AskQuestion';
import ProfileDashboard from './ProfileDashboard';

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(() => !!localStorage.getItem('token'));
  const navigate = useNavigate();

  return (
    <>
      <AppNavbar 
        isLoggedIn={isLoggedIn}
        onLogoutClick={() => {
          localStorage.removeItem('token');
          localStorage.removeItem('username');
          setIsLoggedIn(false);
          navigate('/');
        }}
      />
      
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={ <LoginPage onLoginSuccess={() => { setIsLoggedIn(true); navigate('/'); }} />} />
        <Route path="/questions/:id" element={<QuestionDetail />} />
        <Route path="/questions/ask" element={<AskQuestion />} />
        <Route path="/profile" element={<ProfileDashboard />} />
      </Routes>
    </>
  );
}

export default App;
