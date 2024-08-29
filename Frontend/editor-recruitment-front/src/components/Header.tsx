import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/Header.css';
import { AiOutlineSearch } from 'react-icons/ai';
import GoogleLogo from '../assets/GoogleLogo';

const checkLoginStatus = () => {
    return sessionStorage.getItem('access-token') !== null;
};

const Header = () => {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [showDropdown, setShowDropdown] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');
    const [isFixed, setIsFixed] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        setIsLoggedIn(checkLoginStatus());

        const handleScroll = () => {
            if (window.scrollY > 50) {
                setIsFixed(true);
            } else {
                setIsFixed(false);
            }
        };

        window.addEventListener('scroll', handleScroll);

        return () => {
            window.removeEventListener('scroll', handleScroll);
        };
    }, []);

    const toggleDropdown = () => {
        setShowDropdown(!showDropdown);
    };

    const handleLogin = () => {
        window.location.href = `http://localhost:8080/auth/google`;
    };

    const handleLogout = () => {
        sessionStorage.removeItem('access-token');
        setIsLoggedIn(false);
        navigate('/');
    };

    const handlePostPage = () => {
        if (isLoggedIn) {
            navigate('/posts');
        } else {
            alert('로그인이 필요합니다.');
            navigate('/auth/google');
        }
    };

    const handleSearch = () => {
        if (searchQuery.trim()) {
            navigate(`/search?query=${searchQuery}`);
        }
    };

    const handleKeyPress = (event: React.KeyboardEvent<HTMLInputElement>) => {
        if (event.key === 'Enter') {
            handleSearch();
        }
    };

    return (
        <header className={`header ${isFixed ? 'fixed' : ''}`}>
            <div className="left-section">
                <img src="https://ifh.cc/g/PDRy1k.png" alt="Logo" className="logo" />
                <h1 className="header-title" onClick={() => navigate('/')}>PDB</h1>
                <nav className="nav-menu">
                    <span onClick={() => navigate('/jobs')}>작업 찾기</span>
                    <span onClick={() => navigate('/workers')}>작업자 찾기</span>
                    <span onClick={() => navigate('/community')}>커뮤니티</span>
                </nav>
            </div>
            <div className="right-section">
                <div className="search-bar">
                    <AiOutlineSearch className="search-icon" onClick={handleSearch} />
                    <input
                        type="text"
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        onKeyPress={handleKeyPress}
                        placeholder="파트너를 찾아보세요"
                        className="search-input"
                    />
                </div>
                {isLoggedIn ? (
                    <>
                        <button className="match-button" onClick={() => navigate('/match')}>파트너 매칭하기</button>
                        <button className="post-button" onClick={handlePostPage}>
                            구인 | 구직 하기
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
                    <button className="google-login-button" onClick={handleLogin}>
                        <div className="google-icon">
                            <GoogleLogo/>
                        </div>
                        <div className="google-login-content">
                            구글 로그인
                        </div>
                    </button>
                )}
            </div>
        </header>
    );
};

export default Header;