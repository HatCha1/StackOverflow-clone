import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import { Link } from 'react-router-dom';

function AppNavbar({ isLoggedIn, onLogoutClick }) {
    return (
        <Navbar bg="dark" data-bs-theme="dark">
            <Container>
                <Navbar.Brand as={Link} to="/">Stackoverflow</Navbar.Brand>
                <Nav className="me-auto">
                    <Nav.Link as={Link} to="/">Home</Nav.Link>
                    {isLoggedIn ? (
                        <>
                            <Nav.Link as={Link} to="/profile">My Profile & Badges</Nav.Link>
                            <Nav.Link href="#logout" onClick={(e) => { e.preventDefault(); onLogoutClick(); }}>Logout</Nav.Link>
                        </>
                    ) : (
                        <Nav.Link as={Link} to="/login">Login / Register</Nav.Link>
                    )}
                </Nav>
            </Container>
        </Navbar>
    );
}

export default AppNavbar;