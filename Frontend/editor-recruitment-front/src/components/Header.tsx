import React from 'react';
import GoogleLoginButton from './GoogleLoginButton';
import '../styles/Header.css'; // 스타일 임포트

const Header = () => {
    return (
        <header className="header">
            <h1 className="header-title">My App</h1>
            <GoogleLoginButton />
        </header>
    );
};

export default Header;
