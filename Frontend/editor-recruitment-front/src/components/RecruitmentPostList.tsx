import React, { useState, useEffect, useCallback } from 'react';
import { debounce } from 'lodash';
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
    weeklyWorkload: string; // 추가된 필드
}

interface RecruitmentPost {
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

interface RecruitmentPostItemProps {
    post: RecruitmentPost;
    variant: 'main' | 'jobs';
}

const RecruitmentPostItem: React.FC<RecruitmentPostItemProps> = ({ post, variant }) => {
    const navigate = useNavigate();

    const handleClick = () => {
        navigate(`/post/${post.id}`);
    };

    const getPaymentString = (payments: PaymentDTO[]) => {
        if (payments.length === 0) return '정보 없음';
        const payment = payments[0];
        if (payment.type === 'MONTHLY_SALARY') {
            return `월 ${payment.amount.toLocaleString()}원`;
        } else if (payment.type === 'PER_HOUR') {
            return `분당 ${payment.amount.toLocaleString()}원`;
        }
        return '정보 없음';
    };

    const truncateContent = (content: string, maxLength: number) => {
        const textContent = extractTextFromHTML(content);
        if (textContent.length <= maxLength) return textContent;
        return textContent.slice(0, maxLength) + '...';
    };

    return (
        <div className="recruitment-post-item" onClick={handleClick}>
            <div className="post-image">
                {post.images && post.images.length > 0 && (
                    <img 
                        src={post.images[0]} 
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
                        {truncateContent(post.content, 20)}
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
                            <span className="detail-value">{getPaymentString(post.payments)}</span>
                        </div>
                        <div className="detail-item subscribers">
                            <span className="detail-label">구독자 수</span>
                            <span className="detail-value">{post.recruitmentPostDetailsRes.maxSubs.toLocaleString()}명</span>
                        </div>
                        <div className="detail-item workload">
                            <span className="detail-label">작업 갯수</span>
                            <span className="detail-value">{post.recruitmentPostDetailsRes.weeklyWorkload}</span>
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
}

const RecruitmentPostList: React.FC<RecruitmentPostListProps> = ({ skills, videoTypes, tagNames, sliderSettings, variant }) => {
    const [posts, setPosts] = useState<RecruitmentPost[]>([]);
    const [filteredPosts, setFilteredPosts] = useState<RecruitmentPost[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const fetchPosts = useCallback(async () => {
        setLoading(true);
        setError(null);
        try {
            const response = await fetch(`http://localhost:8080/api/recruitment/posts/search/by-details?skills=${skills.join(',')}&videoTypes=${videoTypes.join(',')}&tagNames=${tagNames.join(',')}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            });
            if (!response.ok) {
                throw new Error('서버 응답이 실패했습니다');
            }
            const data = await response.json();
            setPosts(data.data);
            setFilteredPosts(data.data);
        } catch (error) {
            console.error('구인 게시글을 불러오는데 실패했습니다.', error);
            setError('게시글을 불러오는데 실패했습니다. 다시 시도해주세요.');
        } finally {
            setLoading(false);
        }
    }, [skills, videoTypes, tagNames]);

    useEffect(() => {
        fetchPosts();
    }, [fetchPosts]);

    useEffect(() => {
        const filterPosts = () => {
            const filtered = posts.filter(post => 
                (!skills.length || post.recruitmentPostDetailsRes.skills.some(skill => skills.includes(skill))) &&
                (!videoTypes.length || post.recruitmentPostDetailsRes.videoTypes.some(type => videoTypes.includes(type))) &&
                (!tagNames.length || post.tagNames.some(tag => tagNames.includes(tag)))
            );
            setFilteredPosts(filtered);
        };

        const debouncedFilterPosts = debounce(filterPosts, 300);
        debouncedFilterPosts();

        return () => {
            debouncedFilterPosts.cancel();
        };
    }, [posts, skills, videoTypes, tagNames]);

    if (loading) {
        return <div className="loading">로딩 중...</div>;
    }

    if (error) {
        return <div className="error-message">{error}</div>;
    }

    return (
        <div className="recruitment-post-list-container">
            {filteredPosts.length > 0 ? (
                sliderSettings ? (
                    <Slider {...sliderSettings}>
                        {filteredPosts.map((post) => (
                            <div key={post.id}>
                                <RecruitmentPostItem post={post} variant={variant} />
                            </div>
                        ))}
                    </Slider>
                ) : (
                    <div className="recruitment-post-list">
                        {filteredPosts.map((post) => (
                            <RecruitmentPostItem key={post.id} post={post} variant={variant} />
                        ))}
                    </div>
                )
            ) : (
                <div className="no-posts">게시글이 없습니다.</div>
            )}
        </div>
    );
};

export default RecruitmentPostList;