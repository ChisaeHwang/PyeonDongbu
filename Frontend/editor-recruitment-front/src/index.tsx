import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import App from './App';
import { GoogleOAuthProvider } from '@react-oauth/google';
import './index.css';

const clientId = process.env.REACT_APP_GOOGLE_CLIENT_ID;

if (!clientId) {
  throw new Error("REACT_APP_GOOGLE_CLIENT_ID is not defined in the environment variables.");
}

const rootElement = document.getElementById('root');
if (!rootElement) throw new Error('Failed to find the root element');

const root = createRoot(rootElement);

root.render(
  <StrictMode>
    <GoogleOAuthProvider clientId={clientId}> 
      <App />
    </GoogleOAuthProvider>
  </StrictMode>
);