import { Link } from 'react-router-dom';
import './Footer.css';

function FooterLogo() {
  return (
    <svg viewBox="0 0 40 48" className="footer-logo" aria-hidden="true">
      <path
        d="M32 42v-9h4v13H4V33h4v9h24z"
        fill="currentColor"
      />
      <path
        d="M28.5 0l-3.1 2.3 11.5 15.4 3.1-2.3L28.5 0zM33.8 22.2l-2.4-3.1-14.4 11 2.4 3.1 14.4-11zM38 14.4L24.9 6.9l-2.1 3.3 13.1 7.5 2.1-3.3zM11 37h16v-4H11v4z"
        fill="currentColor"
      />
    </svg>
  );
}

export default function Footer() {
  return (
    <footer className="site-footer">
      <div className="footer-inner">
        <div className="footer-brand">
          <FooterLogo />
        </div>

        <div className="footer-columns">
          <div className="footer-column">
            <h4>Stack Overflow</h4>
            <Link to="/">Questions</Link>
            <Link to="/help">Help</Link>
            <Link to="/chat">Chat</Link>
          </div>

          <div className="footer-column">
            <h4>Business</h4>
            <Link to="/stack-internal">Stack Internal</Link>
            <Link to="/data-licensing">Stack Data Licensing</Link>
            <Link to="/ads">Stack Ads</Link>
          </div>

          <div className="footer-column">
            <h4>Company</h4>
            <Link to="/about">About</Link>
            <Link to="/press">Press</Link>
            <Link to="/work-here">Work Here</Link>
            <Link to="/legal">Legal</Link>
            <Link to="/privacy-policy">Privacy Policy</Link>
            <Link to="/terms-of-service">Terms of Service</Link>
            <Link to="/contact">Contact Us</Link>
            <Link to="/cookie-settings">Cookie Settings</Link>
            <Link to="/cookie-policy">Cookie Policy</Link>
          </div>

          <div className="footer-column">
            <h4>Stack Exchange Network</h4>
            <Link to="/technology">Technology</Link>
            <Link to="/culture-recreation">Culture & recreation</Link>
            <Link to="/life-arts">Life & arts</Link>
            <Link to="/science">Science</Link>
            <Link to="/professional">Professional</Link>
            <Link to="/business">Business</Link>

            <div className="footer-column-spacer" />

            <Link to="/api">API</Link>
            <Link to="/data">Data</Link>
          </div>
        </div>

        <div className="footer-bottom">
          <div className="footer-socials">
            <Link to="/blog">Blog</Link>
            <Link to="/facebook">Facebook</Link>
            <Link to="/twitter">Twitter</Link>
            <Link to="/linkedin">LinkedIn</Link>
            <Link to="/instagram">Instagram</Link>
          </div>

          <p>
            Site design / logo © 2026 Stack Exchange Inc; user contributions licensed under CC BY-SA.
          </p>
        </div>
      </div>
    </footer>
  );
}