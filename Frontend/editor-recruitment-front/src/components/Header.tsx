import React from 'react';
import '../styles/GoogleLoginButton.css'; // 스타일 임포트


const handleLogin = () => {
    window.location.href = `http://localhost:8080/auth/google`;
  };

const Header = () => {
    return (
        <header className="header">
            <h1 className="header-title">My App</h1>
            <button className="google-login-button" onClick={handleLogin}>Google Login</button>
        </header>
    );
};

export default Header;
