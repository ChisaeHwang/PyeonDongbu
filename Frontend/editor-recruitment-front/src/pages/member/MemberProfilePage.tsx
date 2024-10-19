import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../../styles/MyProfile.css';
import { formatNumber } from '../../utils/FormatNumber';
import premierProIcon from '../../assets/premierProIcon';
import photoshopIcon from '../../assets/photoshopIcon';
import afterEffectsIcon from '../../assets/afterEffectsIcon';
import illustratorIcon from '../../assets/illustratorIcon';
import chatIcon from '../../assets/chatIcon';
import radioIcon from '../../assets/radioIcon';
import gameIcon from '../../assets/gameIcon';
import foodIcon from '../../assets/foodIcon';
import educationIcon from '../../assets/educationIcon';
import reviewIcon from '../../assets/reviewIcon';

interface MemberDetails {
    maxSubs: number;
    weeklyWorkload: number;
    videoTypes: string[];
    editedChannels: string[];
    currentChannels: string[];
    portfolio: string;
    skills: string[];
    remarks: string;
}

interface MemberProfile {
    nickname: string;
    imageUrl: string;
    role: string;
    memberDetailsRes: MemberDetails;
}

const filterOptions = [
    { name: '프리미어 프로', icon: premierProIcon, type: 'skill' },
    { name: '포토샵', icon: photoshopIcon, type: 'skill' },
    { name: '에프터 이펙트', icon: afterEffectsIcon, type: 'skill' },
    { name: '어도비 일러스트', icon: illustratorIcon, type: 'skill' },
    { name: '저스트 채팅', icon: chatIcon, type: 'videoGenre' },
    { name: '보이는 라디오', icon: radioIcon, type: 'videoGenre' },
    { name: '게임', icon: gameIcon, type: 'videoGenre' },
    { name: '먹방', icon: foodIcon, type: 'videoGenre' },
    { name: '교육/강의', icon: educationIcon, type: 'videoGenre' },
    { name: '리뷰/정보', icon: reviewIcon, type: 'videoGenre' },
];

const MemberProfilePage: React.FC = () => {
    const { nickname } = useParams<{ nickname: string }>();
    const [profile, setProfile] = useState<MemberProfile | null>(null);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchMemberProfile = async () => {
            setLoading(true);
            try {
                const response = await axios.get(`http://localhost:8080/api/member/profile/${nickname}`);
                setProfile(response.data.data);
            } catch (error) {
                console.error('프로필을 불러오는데 실패했습니다.', error);
                navigate('/not-found');
            } finally {
                setLoading(false);
            }
        };

        fetchMemberProfile();
    }, [nickname, navigate]);

    if (loading) {
        return <div className="loading">로딩 중...</div>;
    }

    if (!profile) {
        return <div className="error">프로필을 찾을 수 없습니다.</div>;
    }

    return (
        <div className="profile-container">
            <h2 className="page-title">{profile.nickname}의 프로필</h2>
            
            <div className="profile-row">
                <div className="profile-image-container">
                    <img src={profile.imageUrl} alt={profile.nickname} className="profile-image" />
                </div>
                <div className="nickname-container">
                    <label htmlFor="nickname">닉네임</label>
                    <input
                        id="nickname"
                        type="text"
                        value={profile.nickname}
                        readOnly
                    />
                </div>
            </div>

            <div className="profile-section">
                <h3>역할</h3>
                <div className="profile-button-group">
                    <button
                        className={`profile-role-button ${profile.role === 'CLIENT' ? 'active' : ''}`}
                        disabled
                    >
                        클라이언트
                    </button>
                    <button
                        className={`profile-role-button ${profile.role !== 'CLIENT' ? 'active' : ''}`}
                        disabled
                    >
                        작업자
                    </button>
                </div>
            </div>

            <div className="profile-section">
                <h3>선호하는 영상 타입</h3>
                <div className="profile-button-group">
                    {filterOptions.filter(option => option.type === 'videoGenre').map((option) => (
                        <button
                            key={option.name}
                            className={`profile-filter-button ${profile.memberDetailsRes.videoTypes.includes(option.name) ? 'active' : ''}`}
                            disabled
                        >
                            <option.icon />
                            {option.name}
                        </button>
                    ))}
                </div>
            </div>

            <div className="profile-section">
                <h3>사용하는 기술</h3>
                <div className="profile-button-group">
                    {filterOptions.filter(option => option.type === 'skill').map((option) => (
                        <button
                            key={option.name}
                            className={`profile-filter-button ${profile.memberDetailsRes.skills.includes(option.name) ? 'active' : ''}`}
                            disabled
                        >
                            <option.icon />
                            {option.name}
                        </button>
                    ))}
                </div>
            </div>

            <div className="profile-section">
                <h3>편집했던 채널</h3>
                <input
                    type="text"
                    value={profile.memberDetailsRes.editedChannels.join(', ')}
                    readOnly
                />
            </div>

            <div className="profile-section">
                <h3>작업 중인 채널</h3>
                <input
                    type="text"
                    value={profile.memberDetailsRes.currentChannels.join(', ')}
                    readOnly
                />
            </div>

            <div className="profile-section">
                <h3>최고 구독자 수</h3>
                <div className="input-with-unit">
                    <input
                        type="text"
                        value={formatNumber(profile.memberDetailsRes.maxSubs.toString())}
                        readOnly
                    />
                    <span className="input-unit">명</span>
                </div>
            </div>

            <div className="profile-section">
                <h3>자기 소개</h3>
                <textarea
                    value={profile.memberDetailsRes.remarks}
                    readOnly
                />
            </div>
        </div>
    );
};

export default MemberProfilePage;
