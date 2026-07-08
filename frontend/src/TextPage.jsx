import { useLocation } from 'react-router-dom';
import './TextPage.css';

const PAGE_TITLES = {
  '/help': 'Help',
  '/chat': 'Chat',
  '/stack-internal': 'Stack Internal',
  '/data-licensing': 'Stack Data Licensing',
  '/ads': 'Stack Ads',
  '/about': 'About',
  '/press': 'Press',
  '/work-here': 'Work Here',
  '/legal': 'Legal',
  '/privacy-policy': 'Privacy Policy',
  '/terms-of-service': 'Terms of Service',
  '/contact': 'Contact Us',
  '/cookie-settings': 'Cookie Settings',
  '/cookie-policy': 'Cookie Policy',
  '/technology': 'Technology',
  '/culture-recreation': 'Culture & Recreation',
  '/life-arts': 'Life & Arts',
  '/science': 'Science',
  '/professional': 'Professional',
  '/business': 'Business',
  '/api': 'API',
  '/data': 'Data',
  '/blog': 'Blog',
  '/facebook': 'Facebook',
  '/twitter': 'Twitter',
  '/linkedin': 'LinkedIn',
  '/instagram': 'Instagram',
};

export default function TextPage() {
  const location = useLocation();
  const title = PAGE_TITLES[location.pathname] || 'Page';

  return (
    <main className="text-page">
      <section className="text-page-container">
        <h1>{title}</h1>

        <div className="text-page-card">
          <p>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer nec odio.
            Praesent libero. Sed cursus ante dapibus diam. Sed nisi. Nulla quis sem
            at nibh elementum imperdiet.
          </p>

          <p>
            Duis sagittis ipsum. Praesent mauris. Fusce nec tellus sed augue semper
            porta. Mauris massa. Vestibulum lacinia arcu eget nulla. Class aptent
            taciti sociosqu ad litora torquent per conubia nostra.
          </p>

          <p>
            Curabitur sodales ligula in libero. Sed dignissim lacinia nunc. Curabitur
            tortor. Pellentesque nibh. Aenean quam. In scelerisque sem at dolor.
          </p>
        </div>
      </section>
    </main>
  );
}