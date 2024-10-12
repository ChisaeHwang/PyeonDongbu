import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import DOMPurify from 'dompurify';
import '../../styles/PostDetailPage.css';
import { useToast } from '../../hooks/useToast';

interface PaymentDTO {
    type: string;
    amount: number;
}

interface RecruitmentPostDetailsRes {
    maxSubs: number;
    videoTypes: string[];
    skills: string[];
    remarks: string;
    weeklyWorkload: string;
}

interface RecruitmentPostRes {
    id: number;
    title: string;
    content: string;
    memberName: string;
    createdAt: string;
    modifiedAt: string;
    viewCount: number;
    imageUrl: string;
    tagNames: string[];
    payment: PaymentDTO;
    recruitmentPostDetailsRes: RecruitmentPostDetailsRes;
    isAuthor: boolean; 
}

interface ApiResponse<T> {
    data: T;
    status: number;
    message: string;
}

const PostDetailPage: React.FC = () => {
    const { postId } = useParams<{ postId: string }>();
    const [post, setPost] = useState<RecruitmentPostRes | null>(null);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();
    const { showSuccessToast, showErrorToast } = useToast();
    const [isAuthor, setIsAuthor] = useState(true);  // 임시로 모든 사용자를 작성자로 설정

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

                const response = await axios.get<ApiResponse<RecruitmentPostRes>>(
                    `http://localhost:8080/api/recruitment/posts/${postId}`,
                    {
                        headers,
                        withCredentials: true
                    }
                );
                setPost(response.data.data);
                setIsAuthor(response.data.data.isAuthor);
            } catch (error) {
                console.error('게시글을 불러오는데 실패했습니다.', error);
                showErrorToast('게시글을 불러오는데 실패했습니다.');
            } finally {
                setLoading(false);
            }
        };

        fetchPostDetail();
    }, [postId, showErrorToast]);

    if (loading) {
        return <div className="loading">로딩 중...</div>;
    }

    if (!post) {
        return <div className="error">게시글을 찾을 수 없습니다.</div>;
    }

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric' });
    };

    const getPaymentString = (payment: PaymentDTO) => {
        if (!payment) return '정보 없음';
        const formattedAmount = payment.amount.toLocaleString('ko-KR', { maximumFractionDigits: 0 });
        switch (payment.type) {
            case 'MONTHLY_SALARY':
                return `월 ${formattedAmount}원`;
            case 'PER_HOUR':
                return `분당 ${formattedAmount}원`;
            case 'PER_PROJECT':
                return `건당 ${formattedAmount}원`;
            case 'NEGOTIABLE':
                return '협의 가능';
            default:
                return '정보 없음';
        }
    };

    const handleEdit = () => {
        navigate(`/post/edit/${postId}`);
    };

    const handleDelete = async () => {
        if (window.confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
            try {
                const accessToken = sessionStorage.getItem('access-token');
                await axios.delete(`http://localhost:8080/api/recruitment/posts/${postId}`, {
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                        'Content-Type': 'application/json',
                    },
                    withCredentials: true
                });
                showSuccessToast('게시글이 성공적으로 삭제되었습니다.');
                navigate('/');
            } catch (error) {
                console.error('게시글 삭제 중 오류가 발생했습니다.', error);
                showErrorToast('게시글 삭제 중 오류가 발생했습니다.');
            }
        }
    };

    return (
        <div className="post-detail-page">
            <div className="post-header">
                <div className="author-info">
                    <div className="author-details">
                        <h2 className="author-name">{post.memberName}</h2>
                        <span className="post-date">{formatDate(post.createdAt)}</span>
                    </div>
                </div>
                {isAuthor && (
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
                <div className="content-text" dangerouslySetInnerHTML={{ __html: DOMPurify.sanitize(post.content) }} />
            </div>
            <div className="post-details">
                <div className="detail-item subscribers">
                    <span className="detail-label">최대 구독자 수</span>
                    <span className="detail-value">{post.recruitmentPostDetailsRes.maxSubs}</span>
                </div>
                <div className="detail-item payment">
                    <span className="detail-label">페이</span>
                    <span className="detail-value">{getPaymentString(post.payment)}</span>
                </div>
                <div className="detail-item workload">
                    <span className="detail-label">주간 작업량</span>
                    <span className="detail-value">{post.recruitmentPostDetailsRes.weeklyWorkload} 개</span>
                </div>
            </div>
            <div className="post-skills">
                <h3>필요한 기술</h3>
                {post.recruitmentPostDetailsRes.skills.map((skill, index) => (
                    <span key={index} className="skill">{skill}</span>
                ))}
            </div>
            <div className="post-video-types">
                <h3>영상 유형</h3>
                {post.recruitmentPostDetailsRes.videoTypes.map((type, index) => (
                    <span key={index} className="video-type">{type}</span>
                ))}
            </div>
        </div>
    );
};

export default PostDetailPage;
