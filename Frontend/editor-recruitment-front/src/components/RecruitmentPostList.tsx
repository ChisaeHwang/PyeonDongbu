import React, { useState, useEffect, useCallback } from 'react';
import Slider from 'react-slick';
import { useNavigate } from 'react-router-dom';
import '../styles/RecruitmentPostList.css';
import { extractTextFromHTML } from '../utils/TextExtractor';

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

interface RecruitmentPost {
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
}

interface RecruitmentPostItemProps {
    post: RecruitmentPost;
    variant: 'main' | 'jobs';
}

const RecruitmentPostItem: React.FC<RecruitmentPostItemProps> = ({ post, variant }) => {
    const navigate = useNavigate();

    const handleClick = () => {
        navigate(`/post/${post.id}`);
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

    const truncateContent = (content: string, maxLength: number) => {
        const textContent = extractTextFromHTML(content);
        if (textContent.length <= maxLength) return textContent;
        return textContent.slice(0, maxLength) + '...';
    };

    const formatNumber = (num: number) => {
        return num.toLocaleString('ko-KR', { maximumFractionDigits: 0 });
    };

    return (
        <div className="recruitment-post-item" onClick={handleClick}>
            <div className="post-image">
                {post.imageUrl && (
                    <img 
                        src={post.imageUrl} 
                        alt={`게시글 이미지`} 
                        loading="lazy"
                        onError={(e) => {
                            const target = e.target as HTMLImageElement;
                            target.src = '/path/to/placeholder-image.jpg';
                        }}
                    />
                )}
            </div>
            <div className="post-content">
                <h3 className="post-title">{post.title}</h3>
                {variant === 'jobs' && (
                    <p className="post-description">
                        {truncateContent(post.content, 100)}
                    </p>
                )}
                {variant === 'main' && (
                    <div className="post-remarks">
                        {(post.recruitmentPostDetailsRes.remarks || '').split(',').filter(Boolean).map((remark, index) => (
                            <span key={`${post.id}-${index}`} className="remark">{remark.trim()}</span>
                        ))}
                    </div>
                )}
                {variant === 'jobs' && (
                    <div className="post-details">
                        <div className="detail-item payment">
                            <span className="detail-label">페이</span>
                            <span className="detail-value">{getPaymentString(post.payment)}</span>
                        </div>
                        <div className="detail-item subscribers">
                            <span className="detail-label">구독자 수</span>
                            <span className="detail-value">{formatNumber(post.recruitmentPostDetailsRes.maxSubs)}명</span>
                        </div>
                        <div className="detail-item workload">
                            <span className="detail-label">작업 갯수</span>
                            <span className="detail-value">{post.recruitmentPostDetailsRes.weeklyWorkload}개</span>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

interface RecruitmentPostListProps {
    skills: string[];
    videoTypes: string[];
    tagNames: string[];
    sliderSettings?: any;
    variant: 'main' | 'jobs';
    maxSubs?: string;
    paymentType?: string;
    workload?: string;
}

const RecruitmentPostList: React.FC<RecruitmentPostListProps> = ({ 
    skills, 
    videoTypes, 
    tagNames, 
    sliderSettings, 
    variant,
    maxSubs,
    paymentType,
    workload
}) => {
    const [posts, setPosts] = useState<RecruitmentPost[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const fetchPosts = useCallback(async (page: number) => {
        setLoading(true);
        setError(null);
        try {
            const params = new URLSearchParams({
                skills: skills.join(','),
                videoTypes: videoTypes.join(','),
                tagNames: tagNames.join(','),
                ...(maxSubs && { maxSubs }),
                ...(paymentType && { paymentType }),
                ...(workload && { workload }),
                page: (page).toString(), 
                size: '10'
            });

            const response = await fetch(`http://localhost:8080/api/recruitment/posts/search/by-details?${params}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            });
            if (!response.ok) {
                throw new Error('서버 응답이 실패했습니다');
            }
            const data = await response.json();
            
            setPosts(data.data.content || []);
            setTotalPages(data.data.totalPages || 0);
        } catch (error) {
            console.error('구인 게시글을 불러오는데 실패했습니다.', error);
            setError('게시글을 불러오는데 실패했습니다. 다시 시도해주세요.');
        } finally {
            setLoading(false);
        }
    }, [skills, videoTypes, tagNames, maxSubs, paymentType, workload]);

    useEffect(() => {
        fetchPosts(currentPage);
    }, [fetchPosts, currentPage]);

    useEffect(() => {
    }, [posts]);

    const handlePageChange = (newPage: number) => {
        setCurrentPage(newPage);
    };

    if (loading) {
        return <div className="loading">로딩 중...</div>;
    }

    if (error) {
        return <div className="error-message">{error}</div>;
    }

    return (
        <div className="recruitment-post-list-container">
            {posts.length > 0 ? (
                <>
                    {sliderSettings ? (
                        <Slider {...sliderSettings}>
                            {posts.map((post) => (
                                <div key={post.id}>
                                    <RecruitmentPostItem post={post} variant={variant} />
                                </div>
                            ))}
                        </Slider>
                    ) : (
                        <div className="recruitment-post-list">
                            {posts.map((post) => (
                                <RecruitmentPostItem key={post.id} post={post} variant={variant} />
                            ))}
                        </div>
                    )}
                    {!sliderSettings && variant === 'jobs' && (
                        <div className="pagination">
                            <button 
                                onClick={() => handlePageChange(currentPage - 1)} 
                                disabled={currentPage === 0}
                                className="pagination-arrow prev"
                                aria-label="이전 페이지"
                            >
                                &lt;
                            </button>
                            <span className="pagination-info">{currentPage + 1} / {Math.max(totalPages, 1)}</span>
                            <button 
                                onClick={() => handlePageChange(currentPage + 1)} 
                                disabled={currentPage === Math.max(totalPages - 1, 0)}
                                className="pagination-arrow next"
                                aria-label="다음 페이지"
                            >
                                &gt;
                            </button>
                        </div>
                    )}
                </>
            ) : (
                <div className="no-posts">
                    게시글이 없습니다. (Posts: {posts.length}, TotalPages: {totalPages})
                </div>
            )}
        </div>
    );
};

export default RecruitmentPostList;