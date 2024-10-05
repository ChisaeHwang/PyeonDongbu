import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../../styles/CommunityPage.css';
import { AiOutlineSearch } from 'react-icons/ai';
import { extractTextFromHTML } from '../../utils/TextExtractor';

interface CommunityPost {
    id: number;
    title: string;
    content: string;
    memberName: string;
    createdAt: string;
    modifiedAt: string;
    viewCount: number;
}

interface ApiResponse {
    code: string;
    message: string;
    data: CommunityPost[];
}

const CommunityPage: React.FC = () => {
    const [posts, setPosts] = useState<CommunityPost[]>([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedCategory, setSelectedCategory] = useState('all');
    const searchInputRef = useRef<HTMLInputElement>(null);
    const navigate = useNavigate();
    const [popularPosts, setPopularPosts] = useState<CommunityPost[]>([]);

    useEffect(() => {
        const fetchPosts = async () => {
            try {
                let url = 'http://localhost:8080/api/community/posts';
                let response;

                if (selectedCategory === 'all') {
                    response = await axios.get<ApiResponse>(url);
                } else {
                    url = `${url}/search/by-tags?tagNames=${selectedCategory}`;
                    response = await axios.get<ApiResponse>(url);
                }

                setPosts(response.data.data);
                const sortedPosts = [...response.data.data].sort((a, b) => b.viewCount - a.viewCount);
                setPopularPosts(sortedPosts.slice(0, 5));
            } catch (error) {
                console.error('게시글을 불러오는 데 실패했습니다:', error);
            }
        };

        fetchPosts();
    }, [selectedCategory]);

    const categories = ['편집', '유튜버', '썸네일러', '모델링'];

    const handleSearch = () => {
        // TO-DO 백엔드 검색 로직 추가 필요
        console.log('Searching for:', searchTerm);
    };

    const handleKeyPress = (event: React.KeyboardEvent<HTMLInputElement>) => {
        if (event.key === 'Enter') {
            handleSearch();
        }
    };

    const handleSearchIconClick = () => {
        if (searchInputRef.current) {
            searchInputRef.current.focus();
        }
    };

    const handlePostClick = (postId: number) => {
        navigate(`/community/post/${postId}`);
    };

    const handleCreatePost = () => {
        navigate('/community/create');
    };

    return (
        <div className="community-page">
            <div className="content-wrapper">
                <div className="popular-posts">
                    <h3>현재 인기 게시글</h3>
                    {popularPosts.map((post) => (
                        <div key={post.id} className="popular-post-item" onClick={() => handlePostClick(post.id)}>
                            <h4>{post.title}</h4>
                            <p>{extractTextFromHTML(post.content).slice(0, 15)}...</p>
                            <span className="popular-post-author">{post.memberName}</span>
                        </div>
                    ))}
                </div>
                <div className="main-content">
                    <div className="community-search-bar">
                        <AiOutlineSearch className="search-icon" onClick={handleSearchIconClick} />
                        <input
                            ref={searchInputRef}
                            type="text"
                            placeholder="검색어를 입력하세요"
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            onKeyPress={handleKeyPress}
                            className="search-input"
                        />
                    </div>
                    <div className="category-bar">
                        <div className="category-buttons">
                            <button
                                className={selectedCategory === 'all' ? 'active' : ''}
                                onClick={() => setSelectedCategory('all')}
                            >
                                전체
                            </button>
                            {categories.map((category) => (
                                <button
                                    key={category}
                                    className={selectedCategory === category ? 'active' : ''}
                                    onClick={() => setSelectedCategory(category)}
                                >
                                    {category}
                                </button>
                            ))}
                        </div>
                        <button className="create-post-button" onClick={handleCreatePost}>
                            게시글 작성
                        </button>
                    </div>
                    <div className="post-list">
                        {posts.map((post) => (
                            <div 
                                key={post.id} 
                                className="post-item" 
                                onClick={() => handlePostClick(post.id)}
                            >
                                <h3>{post.title}</h3>
                                <p>{extractTextFromHTML(post.content)}</p>
                                <div className="post-info">
                                    <span className="post-author">{post.memberName}</span>
                                    <span className="post-date">{new Date(post.createdAt).toLocaleDateString()}</span>
                                    <span className="post-views">조회수: {post.viewCount}</span>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CommunityPage;