// src/pages/MainPage.tsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import SearchBar from '../components/SearchBar';
import CategoryTabs from '../components/CategoryTabs';
import PostList from '../components/PostList';
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

    // 게시글 작성 버튼을 눌렀을 때 게시글 작성 페이지로 이동
    const handleCreatePost = () => {
        navigate('/create-post'); // 게시글 작성 페이지로 이동
    };

    return (
        <div className="main-page">
            <Header />
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
