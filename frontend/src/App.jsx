import { useState } from 'react';
import LoginPage from './LoginPage';
import Home from './Home';
import QuestionDetail from './QuestionDetail';
import AppNavbar from './Navbar';

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(() => !!localStorage.getItem('token'));
  const [selectedQuestionId, setSelectedQuestionId] = useState(null);

  if (!isLoggedIn) {
    return <LoginPage onLoginSuccess={() => setIsLoggedIn(true)} />;
  }

  return (
    <>
      <AppNavbar onLogout={() => { setIsLoggedIn(false); setSelectedQuestionId(null); }} />
      {selectedQuestionId ? (
        <QuestionDetail
          questionId={selectedQuestionId}
          onBack={() => setSelectedQuestionId(null)}
        />
      ) : (
        <Home onSelectQuestion={(id) => setSelectedQuestionId(id)} />
      )}
    </>
  );
}

export default App;
