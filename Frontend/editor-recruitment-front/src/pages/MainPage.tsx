import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import Header from '../components/Header';
import SearchBar from '../components/SearchBar';
import CategoryTabs from '../components/CategoryTabs';
import PostList from '../components/PostList';
import '../styles/MainPage.css';

const MainPage = () => {
    const [searchQuery, setSearchQuery] = useState('');
    const [selectedCategory, setSelectedCategory] = useState('구인');
    const [posts, setPosts] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        // fetchPosts 함수를 useEffect 내부에 정의
        const fetchPosts = async () => {
            try {
                const response = await axios.get('http://localhost:8080/api/recruitment/posts/search/by-details', {
                    params: {
                        title: searchQuery, // 검색어가 있을 경우 사용
                        tagNames: selectedCategory // tagNames를 배열로 전달
                    }
                });
                setPosts(response.data.data); // API 응답 데이터를 상태에 저장
            } catch (error) {
                console.error('게시글을 불러오는데 실패했습니다.', error);
            }
        };
        
        
        
                

        fetchPosts();
    }, [searchQuery, selectedCategory]); // 검색어 또는 카테고리 변경 시 API 호출

    const handleSearch = (query: string) => setSearchQuery(query);
    const handleCategorySelect = (category: string) => setSelectedCategory(category);

    const handleCreatePost = () => {
        navigate('/create-post');
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
            <PostList posts={posts} />
        </div>
    );
};

export default MainPage;
