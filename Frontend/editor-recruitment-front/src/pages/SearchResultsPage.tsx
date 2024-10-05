import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/SearchResultsPage.css';
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

interface SearchResult {
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

const SearchResultsPage: React.FC = () => {
    const [searchResults, setSearchResults] = useState<SearchResult[]>([]);
    const [loading, setLoading] = useState(true);
    const location = useLocation();
    const navigate = useNavigate();

    useEffect(() => {
        const searchQuery = new URLSearchParams(location.search).get('query');
        if (searchQuery) {
            fetchSearchResults(searchQuery);
        }
    }, [location.search]);

    const fetchSearchResults = async (query: string) => {
        setLoading(true);
        try {
            const response = await axios.get(`http://localhost:8080/api/recruitment/posts/search/by-details?title=${encodeURIComponent(query)}`);
            setSearchResults(response.data.data);
        } catch (error) {
            console.error('검색 결과를 가져오는데 실패했습니다:', error);
        } finally {
            setLoading(false);
        }
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

    if (loading) {
        return <div className="loading">검색 중...</div>;
    }

    return (
        <div className="search-results-page">
            <h1 className="search-results-title">검색 결과</h1>
            <div className="search-results-list">
                {searchResults.length > 0 ? (
                    searchResults.map((post) => (
                        <div key={post.id} className="recruitment-post-item" onClick={() => navigate(`/post/${post.id}`)}>
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
                                <p className="post-description">
                                    {truncateContent(post.content, 100)}
                                </p>
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
                            </div>
                        </div>
                    ))
                ) : (
                    <div className="no-results">검색 결과가 없습니다.</div>
                )}
            </div>
        </div>
    );
};

export default SearchResultsPage;