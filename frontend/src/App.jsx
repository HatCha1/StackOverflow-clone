import { useState } from 'react';
import LoginPage from './LoginPage';
import Home from './Home';
import AppNavbar from './Navbar';

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(() => !!localStorage.getItem('token'));

  if (isLoggedIn) {
    return (
      <>
        {/* Navbar renders once here — automatically on every authenticated page */}
        <AppNavbar onLogout={() => setIsLoggedIn(false)} />
        <Home />
      </>
    );
  }

  return <LoginPage onLoginSuccess={() => setIsLoggedIn(true)} />;
}

export default App;
