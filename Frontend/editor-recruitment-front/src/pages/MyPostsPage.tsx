import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../styles/MyPostsPage.css';
import { extractTextFromHTML } from '../utils/TextExtractor';

interface Post {
    id: number;
    title: string;
    content: string;
    createdAt: string;
    viewCount: number;
    type: 'community' | 'recruitment';
}

const MyPostsPage: React.FC = () => {
    const [posts, setPosts] = useState<Post[]>([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [postsPerPage] = useState(10);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchPosts = async () => {
            const accessToken = sessionStorage.getItem('access-token');
            if (!accessToken) {
                console.error('액세스 토큰이 없습니다.');
                return;
            }

            try {
                const [communityResponse, recruitmentResponse] = await Promise.all([
                    axios.get('http://localhost:8080/api/community/posts/me', {
                        headers: { Authorization: `Bearer ${accessToken}` },
                        withCredentials: true,
                    }),
                    axios.get('http://localhost:8080/api/recruitment/posts/me', {
                        headers: { Authorization: `Bearer ${accessToken}` },
                        withCredentials: true,
                    })
                ]);

                const communityPosts = communityResponse.data.data.map((post: any) => ({ ...post, type: 'community' }));
                const recruitmentPosts = recruitmentResponse.data.data.map((post: any) => ({ ...post, type: 'recruitment' }));
                const allPosts = [...communityPosts, ...recruitmentPosts];
                setPosts(allPosts);
            } catch (error) {
                console.error('게시글을 가져오는 중 오류 발생:', error);
            }
        };

        fetchPosts();
    }, []);

    const indexOfLastPost = currentPage * postsPerPage;
    const indexOfFirstPost = indexOfLastPost - postsPerPage;
    const currentPosts = posts.slice(indexOfFirstPost, indexOfLastPost);

    const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

    const handlePostClick = (post: Post) => {
        if (post.type === 'community') {
            navigate(`/community/post/${post.id}`);
        } else {
            navigate(`/post/${post.id}`);
        }
    };

    const truncateContent = (content: string, maxLength: number) => {
        const textContent = extractTextFromHTML(content);
        if (textContent.length <= maxLength) return textContent;
        return textContent.slice(0, maxLength) + '...';
    };

    return (
        <div className="my-posts-page">
            <h1>내 게시글 목록</h1>
            <div className="post-list">
                {currentPosts.map((post) => (
                    <div key={post.id} className="post-item" onClick={() => handlePostClick(post)}>
                        <h3>{post.title}</h3>
                        <p>{truncateContent(post.content, 100)}</p>
                        <div className="post-info">
                            <span className="post-date">{new Date(post.createdAt).toLocaleDateString()}</span>
                            <span className="post-views">조회수: {post.viewCount}</span>
                            <span className="post-type">{post.type === 'community' ? '커뮤니티' : '구인'}</span>
                        </div>
                    </div>
                ))}
            </div>
            {posts.length > postsPerPage && (
                <div className="pagination">
                    {Array.from({ length: Math.ceil(posts.length / postsPerPage) }, (_, i) => (
                        <button
                            key={i + 1}
                            onClick={() => paginate(i + 1)}
                            className={currentPage === i + 1 ? 'active' : ''}
                        >
                            {i + 1}
                        </button>
                    ))}
                </div>
            )}
        </div>
    );
};

export default MyPostsPage;