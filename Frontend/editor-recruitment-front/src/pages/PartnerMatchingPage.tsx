import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/PartnerMatchingPage.css';

interface MatchedPartner {
    id: number;
    name: string;
    title: string;
    description: string;
    subscribers: string;
    payment: string;
    workload: string;
    imageUrl: string;
}

const MatchedPartnerItem: React.FC<{ partner: MatchedPartner }> = ({ partner }) => {
    const navigate = useNavigate();

    const handleClick = () => {
        // 여기서 실제 게시글 ID를 사용해야 합니다. 지금은 임시로 partner.id를 사용합니다.
        navigate(`/recruitment-post/${partner.id}`);
    };

    return (
        <div className="matched-partner-item" onClick={handleClick}>
            <div className="partner-image">
                <img src={partner.imageUrl} alt={partner.name} />
            </div>
            <div className="partner-content">
                <h3 className="partner-title">{partner.title}</h3>
                <p className="partner-description">{partner.description}</p>
                <div className="partner-details">
                    <div className="detail-item subscribers">
                        <span className="detail-label">구독자 수</span>
                        <span className="detail-value">{partner.subscribers}</span>
                    </div>
                    <div className="detail-item payment">
                        <span className="detail-label">페이</span>
                        <span className="detail-value">{partner.payment}</span>
                    </div>
                    <div className="detail-item workload">
                        <span className="detail-label">작업 갯수</span>
                        <span className="detail-value">{partner.workload}</span>
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
    const partnersPerPage = 10;

    useEffect(() => {
        setLoading(true);
        // 더미 데이터를 사용하여 매칭된 파트너 목록을 생성합니다.
        const dummyPartners: MatchedPartner[] = Array.from({ length: 30 }, (_, i) => ({
            id: i + 1,
            name: `파트너 ${i + 1}`,
            title: `매칭된 파트너 ${i + 1}의 제목`,
            description: `이 파트너는 당신의 요구사항과 잘 맞습니다. 다양한 경험과 기술을 보유하고 있어 훌륭한 협업이 가능할 것 같습니다.`,
            subscribers: ['10만', '50만', '100만 이상'][Math.floor(Math.random() * 3)],
            payment: [`월 ${Math.floor(Math.random() * 5 + 3)}00만원`, `시간당 ${Math.floor(Math.random() * 3 + 2)}만원`][Math.floor(Math.random() * 2)],
            workload: `주 ${Math.floor(Math.random() * 4 + 2)}회`,
            imageUrl: `https://picsum.photos/200/200?random=${i}`, // 랜덤 이미지 URL
        }));

        setMatchedPartners(dummyPartners);
        setLoading(false);
    }, []);

    const indexOfLastPartner = currentPage * partnersPerPage;
    const indexOfFirstPartner = indexOfLastPartner - partnersPerPage;
    const currentPartners = matchedPartners.slice(indexOfFirstPartner, indexOfLastPartner);

    const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

    if (loading) {
        return <div className="loading">로딩 중...</div>;
    }

    return (
        <div className="partner-matching-page">
            <h1 className="matching-title">현재 가장 적합하다고 판단되는 파트너들입니다!</h1>
            <p className="matching-subtitle">마이페이지의 정보를 참고해서 가장 매칭확률이 높은 분들을 간추려봤어요.</p>
            
            <div className="matched-partners-list">
                {currentPartners.map((partner) => (
                    <MatchedPartnerItem key={partner.id} partner={partner} />
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