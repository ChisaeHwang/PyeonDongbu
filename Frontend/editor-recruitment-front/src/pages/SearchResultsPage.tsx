import React, { useState, useEffect, useCallback } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import '../styles/SearchResultsPage.css';
import { extractTextFromHTML } from '../utils/TextExtractor';
import api from '../api/axios';
import { useToast } from '../hooks/useToast';

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

interface PageInfo {
    content: SearchResult[];
    totalPages: number;
    totalElements: number;
    size: number;
    number: number;
}

const SearchResultsPage: React.FC = () => {
    const [searchResults, setSearchResults] = useState<PageInfo>({
        content: [],
        totalPages: 0,
        totalElements: 0,
        size: 10,
        number: 0
    });
    const [loading, setLoading] = useState(true);
    const [currentPage, setCurrentPage] = useState(0);
    const location = useLocation();
    const navigate = useNavigate();
    const { showErrorToast } = useToast();

    const fetchSearchResults = useCallback(async (query: string, page: number) => {
        setLoading(true);
        try {
            const response = await api.get('/api/recruitment/posts/search/by-details', {
                params: {
                    title: query,
                    page: page,
                    size: 10
                }
            });
            setSearchResults(response.data.data);
            setCurrentPage(page);
        } catch (error) {
            console.error('검색 결과를 가져오는데 실패했습니다:', error);
            showErrorToast('검색 결과를 가져오는데 실패했습니다.');
        } finally {
            setLoading(false);
        }
    }, [showErrorToast]);

    useEffect(() => {
        const searchQuery = new URLSearchParams(location.search).get('query');
        if (searchQuery) {
            fetchSearchResults(searchQuery, currentPage);
        }
    }, [location.search, currentPage, fetchSearchResults]);

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

    const handlePageChange = (newPage: number) => {
        const searchQuery = new URLSearchParams(location.search).get('query');
        if (searchQuery) {
            fetchSearchResults(searchQuery, newPage);
        }
    };

    if (loading) {
        return <div className="loading">검색 중...</div>;
    }

    return (
        <div className="search-results-page">
            <h1 className="search-results-title">검색 결과</h1>
            <div className="search-results-list">
                {searchResults.content.length > 0 ? (
                    searchResults.content.map((post) => (
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
            {searchResults.totalPages > 1 && (
                <div className="pagination">
                    <button 
                        onClick={() => handlePageChange(currentPage - 1)} 
                        disabled={currentPage === 0}
                        className="pagination-arrow prev"
                        aria-label="이전 페이지"
                    >
                        &lt;
                    </button>
                    <span className="pagination-info">{currentPage + 1} / {searchResults.totalPages}</span>
                    <button 
                        onClick={() => handlePageChange(currentPage + 1)} 
                        disabled={currentPage === searchResults.totalPages - 1}
                        className="pagination-arrow next"
                        aria-label="다음 페이지"
                    >
                        &gt;
                    </button>
                </div>
            )}
        </div>
    );
};

export default SearchResultsPage;
