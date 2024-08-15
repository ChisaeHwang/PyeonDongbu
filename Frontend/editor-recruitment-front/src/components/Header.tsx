import React from 'react';

type HeaderProps = {
    onGoogleLogin: () => void; // 함수 타입 정의
};

const Header: React.FC<HeaderProps> = ({ onGoogleLogin }) => {
    return (
        <header className="header">
            <h1 className="header-title">My App</h1>
            <button className="google-login-button" onClick={onGoogleLogin}>
                Google 로그인
            </button>
        </header>
    );
};

export default Header;
