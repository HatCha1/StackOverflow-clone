import { useState } from 'react';
import './FakeAd.css';

function CloseIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <line x1="18" y1="6" x2="6" y2="18" />
      <line x1="6" y1="6" x2="18" y2="18" />
    </svg>
  );
}

export function InlineFakeAd() {
  const [hidden, setHidden] = useState(false);
  if (hidden) return null;

  return (
    <article className="fake-ad fake-ad--inline" onClick={(e) => e.preventDefault()}>
      <div className="fake-ad-top">
        <span>Sponsored</span>
        <button
          className="fake-ad-close"
          onClick={(e) => {
            e.stopPropagation();
            setHidden(true);
          }}
          aria-label="Close ad"
        >
          <CloseIcon />
        </button>
      </div>

      <div className="fake-ad-content">
        <div className="fake-ad-emoji">🐛</div>
        <div>
          <h3>Bug Spray for JavaScript</h3>
          <p>
            Remove 99.9% of bugs by adding one more console.log. Results may vary.
          </p>
          <span className="fake-ad-link">Learn nothing</span>
        </div>
      </div>
    </article>
  );
}

export function SidebarFakeAd() {
  const [hidden, setHidden] = useState(false);
  if (hidden) return null;

  return (
    <aside className="fake-ad fake-ad--sidebar" onClick={(e) => e.preventDefault()}>
      <div className="fake-ad-top">
        <span>Ad</span>
        <button
          className="fake-ad-close"
          onClick={(e) => {
            e.stopPropagation();
            setHidden(true);
          }}
          aria-label="Close ad"
        >
          <CloseIcon />
        </button>
      </div>

      <div className="fake-ad-vertical">
        <div className="fake-ad-emoji fake-ad-emoji--large">☕</div>
        <h3>Compile Faster Coffee</h3>
        <p>Turns errors into different errors since 2026.</p>
        <span className="fake-ad-button">Fake Buy</span>
      </div>
    </aside>
  );
}

export function BottomFakeAd() {
  const [hidden, setHidden] = useState(false);
  if (hidden) return null;

  return (
    <div className="fake-ad-bottom-wrap">
      <div className="fake-ad fake-ad--bottom" onClick={(e) => e.preventDefault()}>
        <div className="fake-ad-bottom-main">
          <div className="fake-ad-emoji">🦆</div>
          <div>
            <div className="fake-ad-bottom-title">Rubber Duck Debugging Pro</div>
            <div className="fake-ad-bottom-text">
              Talk to the duck. Blame the duck. Ship the code.
            </div>
          </div>
        </div>

        <button
          className="fake-ad-close fake-ad-close--bottom"
          onClick={(e) => {
            e.stopPropagation();
            setHidden(true);
          }}
          aria-label="Close ad"
        >
          <CloseIcon />
        </button>
      </div>
    </div>
  );
}