import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import SearchBar from '../components/SearchBar';
import CategoryTabs from '../components/CategoryTabs';
import PostList from '../components/PostList';
import { useGoogleLogin } from '@react-oauth/google'; // 구글 로그인 훅 추가
import axios from 'axios'; // API 호출을 위해 axios 사용
import '../styles/MainPage.css';

const samplePosts = [
    { id: 1, title: '구인 공고 1', content: '구인 내용을 여기에 씁니다.' },
    { id: 2, title: '구직 공고 1', content: '구직 내용을 여기에 씁니다.' },
    { id: 3, title: '커뮤니티 글 1', content: '커뮤니티 글 내용을 여기에 씁니다.' }
];

const MainPage = () => {
    const [searchQuery, setSearchQuery] = useState('');
    const [selectedCategory, setSelectedCategory] = useState('구인');
    const navigate = useNavigate(); // 페이지 이동을 위한 네비게이트 훅

    const handleSearch = (query: string) => setSearchQuery(query);
    const handleCategorySelect = (category: string) => setSelectedCategory(category);

    const filteredPosts = samplePosts.filter(post =>
        post.title.includes(searchQuery) && post.title.includes(selectedCategory)
    );

    // 구글 로그인 성공 후 백엔드로 인증 코드 전송
    const handleGoogleLoginSuccess = async (code: string) => {
        try {
            const response = await axios.post('/api/login', { code });

            sessionStorage.setItem('access-token', response.data.data.accessToken);

            document.cookie = `refresh-token=${response.data.data.refreshToken}; path=/`;

        } catch (error) {
            console.error('로그인 중 오류가 발생했습니다:', error);
        }
    };

    const googleLogin = useGoogleLogin({
        onSuccess: (tokenResponse) => handleGoogleLoginSuccess(tokenResponse.code),
        flow: 'auth-code', 
    });

    const handleCreatePost = () => {
        navigate('/create-post'); // 게시글 작성 페이지로 이동
    };

    return (
        <div className="main-page">
            <Header onGoogleLogin={googleLogin} />
            <div className="search-and-category">
                <SearchBar onSearch={handleSearch} />
                <CategoryTabs onSelect={handleCategorySelect} selectedCategory={selectedCategory} />
            </div>
            <div className="create-post-section">
                <button className="create-post-button" onClick={handleCreatePost}>
                    게시글 작성
                </button>
            </div>
            <PostList posts={filteredPosts} />
        </div>
    );
};

export default MainPage;
