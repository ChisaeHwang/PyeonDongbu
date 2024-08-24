import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/Header.css'; // 스타일 임포트
import { AiOutlineSearch } from 'react-icons/ai'; // 돋보기 아이콘 임포트

// 로그인 상태를 확인하는 함수 (JWT 토큰 확인)
const checkLoginStatus = () => {
    return sessionStorage.getItem('access-token') !== null; // 토큰 확인
};

const Header = () => {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [showDropdown, setShowDropdown] = useState(false); // 드롭다운 상태
    const [searchQuery, setSearchQuery] = useState(''); // 검색어 상태
    const navigate = useNavigate(); // useNavigate 훅

    // 컴포넌트가 마운트되면 로그인 상태 확인
    useEffect(() => {
        setIsLoggedIn(checkLoginStatus());
    }, []);

    const toggleDropdown = () => {
        setShowDropdown(!showDropdown); // 드롭다운 토글
    };

    const handleLogin = () => {
        navigate('/auth/google'); // 페이지 이동
    };

    const handleLogout = () => {
        sessionStorage.removeItem('token'); // 로그아웃 시 토큰 제거
        setIsLoggedIn(false);
        navigate('/'); // 메인 페이지로 이동
    };

    const handlePostPage = () => {
        if (isLoggedIn) {
            navigate('/posts'); // 로그인이 되어 있으면 게시글 페이지로 이동
        } else {
            alert('로그인이 필요합니다.');
            navigate('/auth/google'); // 로그인 페이지로 이동
        }
    };

    const handleSearch = () => {
        if (searchQuery.trim()) {
            navigate(`/search?query=${searchQuery}`); // 검색 페이지로 이동
        }
    };

    const handleKeyPress = (event: React.KeyboardEvent<HTMLInputElement>) => {
        if (event.key === 'Enter') {
            handleSearch(); // 엔터 키로 검색 실행
        }
    };    

    return (
        <header className="header">
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
                        <img src="https://upload.wikimedia.org/wikipedia/commons/5/53/Google_%22G%22_Logo.svg" alt="Google Icon" className="google-icon" />
                        구글 로그인
                    </button>
                )}
            </div>
        </header>
    );
};

export default Header;
