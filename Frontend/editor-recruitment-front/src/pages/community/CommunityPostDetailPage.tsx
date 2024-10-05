import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
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

    useEffect(() => {
        const fetchPostDetail = async () => {
            setLoading(true);
            try {
                const response = await axios.get<ApiResponse>(`http://localhost:8080/api/community/posts/${postId}`);
                setPost(response.data.data);
            } catch (error) {
                console.error('게시글을 불러오는데 실패했습니다.', error);
            } finally {
                setLoading(false);
            }
        };

        fetchPostDetail();
    }, [postId]);

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
        <div className="community-post-detail-page">
            <div className="post-header">
                <h1 className="post-title">{post.title}</h1>
                <div className="post-meta">
                    <span className="post-author">{post.memberName}</span>
                    <span className="post-date">{formatDate(post.createdAt)}</span>
                    <span className="post-views">조회수: {post.viewCount}</span>
                </div>
            </div>
            <div className="post-content" dangerouslySetInnerHTML={createMarkup(post.content)} />
        </div>
    );
};

export default CommunityPostDetailPage;