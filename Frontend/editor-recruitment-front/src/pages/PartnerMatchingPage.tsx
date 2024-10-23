import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/PartnerMatchingPage.css';
import { extractTextFromHTML } from '../utils/TextExtractor';
import api from '../api/axios';
import { useToast } from '../hooks/useToast';

interface MatchedPartner {
    recruitmentPostRes: {
        id: number;
        title: string;
        content: string;
        memberName: string;
        viewCount: number;
        imageUrl: string;
        payment: { type: string; amount: string };
        recruitmentPostDetailsRes: {
            maxSubs: number;
            videoTypes: string[];
            skills: string[];
            weeklyWorkload: number;
        };
    };
    similarity: number;
}

const getPaymentString = (payment: { type: string; amount: string }) => {
    if (!payment) return '정보 없음';
    const formattedAmount = Number(payment.amount).toLocaleString('ko-KR', { maximumFractionDigits: 0 });
    switch (payment.type) {
        case 'MONTHLY_SALARY':
            return `월급 ${formattedAmount}원`;
        case 'PER_HOUR':
            return `시급 ${formattedAmount}원`;
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

const MatchedPartnerItem: React.FC<{ partner: MatchedPartner }> = ({ partner }) => {
    const navigate = useNavigate();

    if (!partner || !partner.recruitmentPostRes) {
        return null;
    }

    const handleClick = () => {
        navigate(`/post/${partner.recruitmentPostRes.id}`);
    };

    const paymentInfo = getPaymentString(partner.recruitmentPostRes.payment);

    const weeklyWorkload = partner.recruitmentPostRes.recruitmentPostDetailsRes.weeklyWorkload;
    const workloadInfo = weeklyWorkload && weeklyWorkload > 0 ? `주 ${weeklyWorkload}회` : '협의';

    return (
        <div className="matched-partner-item" onClick={handleClick}>
            <div className="partner-image">
                {partner.recruitmentPostRes.imageUrl && (
                    <img 
                        src={partner.recruitmentPostRes.imageUrl} 
                        alt={`${partner.recruitmentPostRes.memberName}의 프로필`}
                        loading="lazy"
                        onError={(e) => {
                            const target = e.target as HTMLImageElement;
                            target.src = '/path/to/placeholder-image.jpg';
                        }}
                    />
                )}
            </div>
            <div className="partner-content">
                <h3 className="partner-title">{partner.recruitmentPostRes.title}</h3>
                <p className="partner-description">
                    {truncateContent(partner.recruitmentPostRes.content, 100)}
                </p>
                <div className="partner-details">
                    <div className="detail-item subscribers">
                        <span className="detail-label">최대 구독자 수</span>
                        <span className="detail-value">
                            {partner.recruitmentPostRes.recruitmentPostDetailsRes.maxSubs?.toLocaleString() || '정보 없음'}명
                        </span>
                    </div>
                    <div className="detail-item payment">
                        <span className="detail-label">페이</span>
                        <span className="detail-value">{paymentInfo}</span>
                    </div>
                    <div className="detail-item workload">
                        <span className="detail-label">주간 작업 갯수</span>
                        <span className="detail-value">{workloadInfo}</span>
                    </div>
                </div>
            </div>
        </div>
    );
};

const PartnerMatchingPage: React.FC = () => {
    const [matchedPartners, setMatchedPartners] = useState<MatchedPartner[]>([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const partnersPerPage = 10;
    const { showErrorToast } = useToast();

    const fetchMatchedPartners = useCallback(async () => {
        setLoading(true);
        setError(null);
        try {
            const response = await api.get('/api/match');

            if (response.data.code === "200" && response.data.message === "success") {
                setMatchedPartners(response.data.data.matchingResults);
            } else {
                throw new Error(response.data.message || '매칭 정보를 가져오는데 실패했습니다.');
            }
        } catch (err) {
            console.error('매칭 정보를 가져오는데 실패했습니다:', err);
            setError('매칭 정보를 가져오는데 실패했습니다. 다시 시도해주세요.');
            showErrorToast('매칭 정보를 가져오는데 실패했습니다.');
        } finally {
            setLoading(false);
        }
    }, [showErrorToast]);

    useEffect(() => {
        fetchMatchedPartners();
    }, [fetchMatchedPartners]);

    const indexOfLastPartner = currentPage * partnersPerPage;
    const indexOfFirstPartner = indexOfLastPartner - partnersPerPage;
    const currentPartners = matchedPartners.slice(indexOfFirstPartner, indexOfLastPartner);

    const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

    if (loading) {
        return <div className="loading">로딩 중...</div>;
    }

    if (error) {
        return <div className="error">{error}</div>;
    }

    return (
        <div className="partner-matching-page">
            <h1 className="matching-title">현재 가장 적합하다고 판단되는 파트너들입니다!</h1>
            <p className="matching-subtitle">마이페이지의 정보를 참고해서 가장 매칭확률이 높은 분들을 간추려봤어요.</p>
            
            <div className="matched-partners-list">
                {currentPartners.map((partner) => (
                    <MatchedPartnerItem key={partner.recruitmentPostRes.id} partner={partner} />
                ))}
            </div>

            <div className="pagination">
                {Array.from({ length: Math.ceil(matchedPartners.length / partnersPerPage) }, (_, i) => (
                    <button key={i} onClick={() => paginate(i + 1)} className={currentPage === i + 1 ? 'active' : ''}>
                        {i + 1}
                    </button>
                ))}
            </div>
        </div>
    );
};

export default PartnerMatchingPage;
