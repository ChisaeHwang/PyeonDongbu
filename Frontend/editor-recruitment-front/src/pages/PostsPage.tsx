import React, { useState, useEffect } from 'react';
import axios from 'axios';
import SearchBar from '../components/SearchBar';
import CategoryTabs from '../components/CategoryTabs';
import PostList from '../components/PostList';
import '../styles/PostsPage.css';

// 게시물의 타입 정의
interface Post {
    id: number;
    title: string;
    description: string;
    category: string;
}

const PostsPage = () => {
    const [posts, setPosts] = useState<Post[]>([]);
    const [searchQuery, setSearchQuery] = useState('');
    const [selectedCategory, setSelectedCategory] = useState('구인');

    useEffect(() => {
        const fetchPosts = async () => {
            try {
                const response = await axios.get('http://localhost:8080/api/recruitment/posts/search/by-details', {
                    params: {
                        title: searchQuery,
                        tagNames: selectedCategory
                    }
                });
                setPosts(response.data.data);
            } catch (error) {
                console.error('게시글을 불러오는데 실패했습니다.', error);
                // 임시 데이터 설정
                setPosts([
                    { id: 1, title: '임시 구인 게시글 1', description: '임시 구인 게시글 설명 1', category: '구인' },
                    { id: 2, title: '임시 구인 게시글 2', description: '임시 구인 게시글 설명 2', category: '구인' },
                    { id: 3, title: '임시 구인 게시글 3', description: '임시 구인 게시글 설명 3', category: '구인' }
                ]);
            }
        };

        fetchPosts();
    }, [searchQuery, selectedCategory]);

    const handleSearch = (query: string) => setSearchQuery(query);
    const handleCategorySelect = (category: string) => setSelectedCategory(category);

    return (
        <div className="posts-page">
            <SearchBar onSearch={handleSearch} />
            <CategoryTabs onSelect={handleCategorySelect} selectedCategory={selectedCategory} />
            <PostList posts={posts.map(post => ({ ...post, content: post.description }))} />
        </div>
    );
};

export default PostsPage;
