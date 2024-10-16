import React, { useState, useEffect, useRef, useCallback, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../../styles/CommunityPage.css';
import { AiOutlineSearch } from 'react-icons/ai';
import { extractTextFromHTML } from '../../utils/TextExtractor';
import { debounce } from 'lodash'; // lodash 라이브러리 사용

interface CommunityPost {
    id: number;
    title: string;
    content: string;
    memberName: string;
    tagNames: string[];
    createdAt: string;
    modifiedAt: string;
    viewCount: number;
    isAuthor: boolean | null;
}

interface PageInfo {
    content: CommunityPost[];
    totalPages: number;
    totalElements: number;
    size: number;
    number: number;
}

interface ApiResponse<T> {
    code: string;
    message: string;
    data: T;
}

const CommunityPage: React.FC = () => {
    const [posts, setPosts] = useState<CommunityPost[]>([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedCategory, setSelectedCategory] = useState('all');
    const searchInputRef = useRef<HTMLInputElement>(null);
    const navigate = useNavigate();
    const [popularPosts, setPopularPosts] = useState<CommunityPost[]>([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const fetchPosts = useCallback(async (search: string = '', page: number = 0, category: string = 'all') => {
        try {
            const url = 'http://localhost:8080/api/community/posts/search/by-tags';
            let params: { search?: string; tagNames?: string; page: number; size: number } = {
                page: page,
                size: 10
            };

            if (search) {
                params.search = search;
            }

            if (category !== 'all') {
                params.tagNames = category;
            }

            const response = await axios.get<ApiResponse<PageInfo>>(url, { params });
            setPosts(response.data.data.content);
            setTotalPages(response.data.data.totalPages);
            setCurrentPage(response.data.data.number);
        } catch (error) {
            console.error('게시글을 불러오는 데 실패했습니다:', error);
        }
    }, []);

    // 인기 게시글을 가져오는 함수
    const fetchPopularPosts = useCallback(async () => {
        try {
            const response = await axios.get<ApiResponse<CommunityPost[]>>('http://localhost:8080/api/community/posts/search/popular', {
                params: { limit: 5 }
            });
            setPopularPosts(response.data.data);
        } catch (error) {
            console.error('인기 게시글을 불러오는데 실패했습니다:', error);
        }
    }, []);

    // 컴포넌트 마운트 시 인기 게시글 가져오기
    useEffect(() => {
        fetchPopularPosts();
    }, [fetchPopularPosts]);

    const debouncedFetchPosts = useMemo(
        () => debounce((search: string, page: number, category: string) => {
            fetchPosts(search, page, category);
        }, 300),
        [fetchPosts]
    );

    useEffect(() => {
        debouncedFetchPosts(searchTerm, 0, selectedCategory);
    }, [debouncedFetchPosts, searchTerm, selectedCategory]);

    const categories = ['편집', '유튜버', '썸네일러', '모델링'];

    const handleSearch = () => {
        setCurrentPage(0);
        debouncedFetchPosts(searchTerm, 0, selectedCategory);
    };

    const handleKeyPress = (event: React.KeyboardEvent<HTMLInputElement>) => {
        if (event.key === 'Enter') {
            handleSearch();
        }
    };

    const handleSearchIconClick = () => {
        handleSearch();
    };

    const handlePostClick = (postId: number) => {
        navigate(`/community/post/${postId}`);
    };

    const handleCreatePost = () => {
        navigate('/community/create');
    };

    const handleCategoryChange = (category: string) => {
        setSelectedCategory(category);
        setSearchTerm('');
        setCurrentPage(0);
        if (searchInputRef.current) {
            searchInputRef.current.value = '';
        }
        debouncedFetchPosts('', 0, category);
    };

    const handlePageChange = (newPage: number) => {
        setCurrentPage(newPage);
        debouncedFetchPosts(searchTerm, newPage, selectedCategory);
    };

    return (
        <div className="community-page">
            <div className="content-wrapper">
                <div className="popular-posts">
                    <h3>현재 인기 게시글</h3>
                    {popularPosts.map((post) => (
                        <div key={post.id} className="popular-post-item" onClick={() => handlePostClick(post.id)}>
                            <h4>{post.title}</h4>
                            <p>{extractTextFromHTML(post.content).slice(0, 50)}...</p>
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
                                onClick={() => handleCategoryChange('all')}
                            >
                                전체
                            </button>
                            {categories.map((category) => (
                                <button
                                    key={category}
                                    className={selectedCategory === category ? 'active' : ''}
                                    onClick={() => handleCategoryChange(category)}
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
                    {totalPages > 1 && (
                        <div className="pagination">
                            <button 
                                onClick={() => handlePageChange(currentPage - 1)} 
                                disabled={currentPage === 0}
                                className="pagination-arrow prev"
                                aria-label="이전 페이지"
                            >
                                &lt;
                            </button>
                            <span className="pagination-info">{currentPage + 1} / {totalPages}</span>
                            <button 
                                onClick={() => handlePageChange(currentPage + 1)} 
                                disabled={currentPage === totalPages - 1}
                                className="pagination-arrow next"
                                aria-label="다음 페이지"
                            >
                                &gt;
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default CommunityPage;
