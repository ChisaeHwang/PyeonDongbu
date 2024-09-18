import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/MyPostsPage.css';

interface Post {
    id: number;
    title: string;
    content: string;
    createdAt: string;
    viewCount: number;
}

const MyPostsPage: React.FC = () => {
    const [posts, setPosts] = useState<Post[]>([]);
    const [currentPage, setCurrentPage] = useState(1);
    const postsPerPage = 10;
    const navigate = useNavigate();

    useEffect(() => {
        // TODO: API로 사용자의 게시글 데이터를 가져오는 로직 구현
        // 임시 데이터
        const tempPosts: Post[] = Array.from({ length: 25 }, (_, i) => ({
            id: i + 1,
            title: `게시글 제목 ${i + 1}`,
            content: `게시글 내용 ${i + 1}...`,
            createdAt: new Date(2023, 4, 15 - i).toISOString(),
            viewCount: Math.floor(Math.random() * 100) + 1
        }));
        setPosts(tempPosts);
    }, []);

    const indexOfLastPost = currentPage * postsPerPage;
    const indexOfFirstPost = indexOfLastPost - postsPerPage;
    const currentPosts = posts.slice(indexOfFirstPost, indexOfLastPost);

    const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

    const handlePostClick = (postId: number) => {
        navigate(`/post/${postId}`);
    };

    return (
        <div className="my-posts-page">
            <h1>내 게시글 목록</h1>
            <div className="post-list">
                {currentPosts.map((post) => (
                    <div key={post.id} className="post-item" onClick={() => handlePostClick(post.id)}>
                        <h3>{post.title}</h3>
                        <p>{post.content}</p>
                        <div className="post-info">
                            <span className="post-date">{new Date(post.createdAt).toLocaleDateString()}</span>
                            <span className="post-views">조회수: {post.viewCount}</span>
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