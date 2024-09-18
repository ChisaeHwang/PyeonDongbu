import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import '../../styles/PostDetailPage.css';

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
    images: string[];
    tagNames: string[];
    payments: PaymentDTO[];
    recruitmentPostDetailsRes: RecruitmentPostDetailsRes;
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

    useEffect(() => {
        const fetchPostDetail = async () => {
            setLoading(true);
            try {
                const response = await axios.get<ApiResponse<RecruitmentPostRes>>(`http://localhost:8080/api/recruitment/posts/${postId}`);
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
        return date.toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric' });
    };

    const getPaymentString = (payments: PaymentDTO[]) => {
        if (payments.length === 0) return '정보 없음';
        const payment = payments[0];
        if (payment.type === 'MONTHLY_SALARY') {
            return `월 ${payment.amount.toLocaleString()}원`;
        } else if (payment.type === 'PER_HOUR') {
            return `시간당 ${payment.amount.toLocaleString()}원`;
        }
        return '정보 없음';
    };

    return (
        <div className="post-detail-page">
            <div className="post-header">
                <div className="author-info">
                    <img src="/path/to/default/profile/image.jpg" alt={post.memberName} className="author-image" />
                    <div className="author-details">
                        <h2 className="author-name">{post.memberName}</h2>
                        <span className="post-date">{formatDate(post.createdAt)}</span>
                    </div>
                </div>
                <h1 className="post-title">{post.title}</h1>
                <div className="post-meta">
                    <span className="post-views">조회수: {post.viewCount}</span>
                </div>
            </div>
            <div className="post-content">
                {post.images.length > 0 && (
                    <div className="post-image">
                        <img src={post.images[0]} alt={post.title} />
                    </div>
                )}
                <div className="content-text">
                    <p>{post.content}</p>
                </div>
            </div>
            <div className="post-details">
                <div className="detail-item subscribers">
                    <span className="detail-label">최대 구독자 수</span>
                    <span className="detail-value">{post.recruitmentPostDetailsRes.maxSubs}</span>
                </div>
                <div className="detail-item payment">
                    <span className="detail-label">페이</span>
                    <span className="detail-value">{getPaymentString(post.payments)}</span>
                </div>
                <div className="detail-item workload">
                    <span className="detail-label">주간 작업량</span>
                    <span className="detail-value">{post.recruitmentPostDetailsRes.weeklyWorkload}</span>
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
            <div className="post-remarks">
                <h3>비고</h3>
                <p>{post.recruitmentPostDetailsRes.remarks}</p>
            </div>
        </div>
    );
};

export default PostDetailPage;