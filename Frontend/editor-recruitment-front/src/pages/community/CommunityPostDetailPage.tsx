import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import DOMPurify from 'dompurify';
import '../../styles/CommunityPostDetailPage.css';

interface CommunityPost {
    id: number;
    title: string;
    content: string;
    memberName: string;
    createdAt: string;
    modifiedAt: string;
    viewCount: number;
    isAuthor: boolean;
}

interface ApiResponse {
    code: string;
    message: string;
    data: CommunityPost;
}

const CommunityPostDetailPage: React.FC = () => {
    const { postId } = useParams<{ postId: string }>();
    const [post, setPost] = useState<CommunityPost | null>(null);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchPostDetail = async () => {
            setLoading(true);
            try {
                const accessToken = sessionStorage.getItem('access-token');
                const headers: Record<string, string> = {
                    'Content-Type': 'application/json',
                };
                
                if (accessToken) {
                    headers['Authorization'] = `Bearer ${accessToken}`;
                }

                const response = await axios.get<ApiResponse>(
                    `http://localhost:8080/api/community/posts/${postId}`,
                    {
                        headers,
                        withCredentials: true
                    }
                );
                setPost(response.data.data);
            } catch (error) {
                console.error('게시글을 불러오는데 실패했습니다.', error);
            } finally {
                setLoading(false);
            }
        };

        fetchPostDetail();
    }, [postId]);

    const handleEdit = () => {
        navigate(`/community/edit/${postId}`);
    };

    const handleDelete = async () => {
        if (window.confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
            try {
                const accessToken = sessionStorage.getItem('access-token');
                await axios.delete(`http://localhost:8080/api/community/posts/${postId}`, {
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                        'Content-Type': 'application/json',
                    },
                    withCredentials: true
                });
                navigate('/community');
            } catch (error) {
                console.error('게시글 삭제 중 오류가 발생했습니다.', error);
            }
        }
    };

    if (loading) {
        return <div className="loading">로딩 중...</div>;
    }

    if (!post) {
        return <div className="error">게시글을 찾을 수 없습니다.</div>;
    }

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' });
    };

    const createMarkup = (html: string) => {
        return { __html: DOMPurify.sanitize(html) };
    };

    return (
        <div className="post-detail-page community-post-detail-page">
            <div className="post-header">
                <div className="author-info">
                    <div className="author-details">
                        <h2 className="author-name">{post.memberName}</h2>
                        <span className="post-date">{formatDate(post.createdAt)}</span>
                    </div>
                </div>
                {post.isAuthor && (
                    <div className="post-actions">
                        <button onClick={handleEdit} className="action-button edit-button">수정</button>
                        <button onClick={handleDelete} className="action-button delete-button">삭제</button>
                    </div>
                )}
            </div>
            <h1 className="post-title">{post.title}</h1>
            <div className="post-meta">
                <span className="post-views">조회수: {post.viewCount}</span>
            </div>
            <div className="post-content">
                <div className="content-text" dangerouslySetInnerHTML={createMarkup(post.content)} />
            </div>
        </div>
    );
};

export default CommunityPostDetailPage;
