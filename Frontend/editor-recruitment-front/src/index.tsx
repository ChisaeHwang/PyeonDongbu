import { StrictMode } from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import { GoogleOAuthProvider } from '@react-oauth/google'; // GoogleOAuthProvider 가져오기
import 'index.css';

const clientId = process.env.REACT_APP_GOOGLE_CLIENT_ID;

if (!clientId) {
  throw new Error("REACT_APP_GOOGLE_CLIENT_ID is not defined in the environment variables.");
}


ReactDOM.render(
  <StrictMode>
    <GoogleOAuthProvider clientId={clientId}> 
      <App />
    </GoogleOAuthProvider>
  </StrictMode>,
  document.getElementById('root')
);
