import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import '../styles/Header.css';
import { AiOutlineSearch } from 'react-icons/ai';
import GoogleLogo from '../assets/GoogleLogo';
import useAxiosInterceptor from '../hooks/useAxiosInterceptor';
import { useToast } from '../hooks/useToast';
import 'react-toastify/dist/ReactToastify.css';
import api from '../api/axios';
import { getApiBaseUrl } from '../utils/env';
import { getAccessToken, clearAccessToken } from '../utils/auth';

const checkLoginStatus = () => {
    return getAccessToken() !== null;
};

interface UserInfo {
    nickname: string;
    imageUrl: string;
    role: string;
}

interface ApiResponse {
    code: string;
    message: string;
    data: {
        nickname: string;
        imageUrl: string;
        role: string;
        memberDetailsRes: {
            maxSubs: number;
            videoTypes: string[];
            editedChannels: string[];
            currentChannels: string[];
            portfolio: string;
            skills: string[];
            remarks: string;
        };
    };
}

const Header = () => {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [showDropdown, setShowDropdown] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');
    const [isFixed, setIsFixed] = useState(false);
    const [userInfo, setUserInfo] = useState<UserInfo>({
        nickname: '',
        imageUrl: '',
        role: ''
    });
    const navigate = useNavigate();
    const location = useLocation();
    const { showSuccessToast } = useToast();

    useAxiosInterceptor();

    useEffect(() => {
        const loggedIn = checkLoginStatus();
        setIsLoggedIn(loggedIn);

        if (loggedIn) {
            fetchUserInfo();
        }

        const handleScroll = () => {
            setIsFixed(window.scrollY > 50);
        };

        window.addEventListener('scroll', handleScroll);

        return () => {
            window.removeEventListener('scroll', handleScroll);
        };
    }, [location]); 

    const fetchUserInfo = async () => {
        try {
            const response = await api.get<ApiResponse>('/api/member');
            const { nickname, imageUrl, role } = response.data.data;
            setUserInfo({ nickname, imageUrl, role });
        } catch (error) {
            console.error('Failed to fetch user info:', error);
        }
    };

    const toggleDropdown = () => {
        setShowDropdown(!showDropdown);
    };

    const handleLogin = () => {
        window.location.href = `${getApiBaseUrl()}/auth/google`;
    };

    const handleLogout = () => {
        clearAccessToken();
        setIsLoggedIn(false);
        setUserInfo({ nickname: '', imageUrl: '', role: '' });
        showSuccessToast('로그아웃되었습니다.');
        navigate('/', { replace: true });
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
            navigate(`/search?query=${encodeURIComponent(searchQuery)}`);
        }
    };

    const handleKeyPress = (event: React.KeyboardEvent<HTMLInputElement>) => {
        if (event.key === 'Enter') {
            handleSearch();
        }
    };

    return (
        <>
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
                                <img src={userInfo.imageUrl} alt="Profile" className="profile-image" />
                                {showDropdown && (
                                    <div className="dropdown-menu">
                                        <div className="user-info">
                                            <img src={userInfo.imageUrl} alt="Profile" className="dropdown-profile-image" />
                                            <div className="user-details">
                                                <span className="user-name">{userInfo.nickname}</span>
                                                <span className="user-position">{userInfo.role}</span>
                                            </div>
                                        </div>
                                        <div className="dropdown-divider"></div>
                                        <button onClick={() => navigate('/mypage')}>마이 페이지</button>
                                        <button onClick={() => navigate('/myposts')}>게시글 목록</button>
                                        <div className="dropdown-divider"></div>
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
        </>
    );
};

export default Header;
