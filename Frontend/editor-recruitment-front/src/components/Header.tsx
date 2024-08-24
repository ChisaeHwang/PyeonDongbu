import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/Header.css'; // 스타일 임포트

// 로그인 상태를 확인하는 함수 (JWT 토큰 확인)
const checkLoginStatus = () => {
    return sessionStorage.getItem('access-token') !== null; // 토큰 확인
};

const Header = () => {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [showDropdown, setShowDropdown] = useState(false); // 드롭다운 상태
    const navigate = useNavigate(); // useNavigate 훅

    // 컴포넌트가 마운트되면 로그인 상태 확인
    useEffect(() => {
        setIsLoggedIn(checkLoginStatus());
    }, []);

    const toggleDropdown = () => {
        setShowDropdown(!showDropdown); // 드롭다운 토글
    };

    const handleLogin = () => {
        window.location.href = `http://localhost:8080/auth/google`;
    };

    const handleLogout = () => {
        sessionStorage.removeItem('access-token'); // 로그아웃 시 토큰 제거
        setIsLoggedIn(false);
        window.location.reload(); // 새로고침
    };

    const handlePostPage = () => {
        if (isLoggedIn) {
            navigate('/posts'); // 로그인이 되어 있으면 게시글 페이지로 이동
        } else {
            alert('로그인이 필요합니다.');
            navigate('/'); 
        }
    };

    return (
        <header className="header">
            <h1 className="header-title" onClick={() => navigate('/')}>PDB</h1>
            <div className="header-actions">
                {isLoggedIn ? (
                    <>
                        <button className="post-button" onClick={handlePostPage}>
                            구인 | 구직 하러가기
                        </button>
                        <div className="profile-wrap" onClick={toggleDropdown}>
                            <img src="https://ifh.cc/g/q2ZvDd.jpg" alt="Profile" className="profile-image" />
                            {showDropdown && (
                                <div className="dropdown-menu">
                                    <button onClick={() => navigate('/profile')}>프로필</button>
                                    <button onClick={handleLogout}>로그아웃</button>
                                </div>
                            )}
                        </div>
                    </>
                ) : (
                    <button className="google-login-button" onClick={handleLogin}>Google Login</button>
                )}
            </div>
        </header>
    );
};

export default Header;
