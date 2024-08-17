import React, { useEffect, useState } from 'react';
import axios from 'axios';
import '../styles/MyProfile.css'; // 스타일 파일 임포트

// 사용자 정보 타입 정의
interface UserInfo {
    nickname: string;
    imageUrl: string;
    role: string;
    maxSubs: number;
    videoTypes: string[];
    editedChannels: string[];
    currentChannels: string[];
    portfolio: string;
    skills: string[];
    remarks: string;
}

const MyProfile = () => {
    const [userInfo, setUserInfo] = useState<UserInfo | null>(null);
    const [error, setError] = useState(false);

    // 백엔드에서 사용자 정보를 가져오는 함수
    const fetchUserInfo = async () => {
        try {
            const response = await axios.get('/api/member'); // 백엔드 API 호출
            setUserInfo(response.data.data);
        } catch (error) {
            console.error('Failed to fetch user info:', error);
            setError(true);

            // 더미 데이터 설정 
            setUserInfo({
                nickname: '치새황',
                imageUrl: 'https://ifh.cc/g/q2ZvDd.jpg',
                role: '편집자',
                maxSubs: 6000000,
                videoTypes: ['브이로그', '게임', '저스트 채팅'],
                editedChannels: ['우왁굳', '김재원'],
                currentChannels: ['무직'],
                portfolio: 'https://example.com/portfolio',
                skills: ['Premiere Pro', 'After Effects', 'DaVinci Resolve'],
                remarks: '여러 클라이언트를 경험했던 영상 편집자입니다',
            });
        }
    };

    useEffect(() => {
        fetchUserInfo();
    }, []);

    if (!userInfo) {
        return <div>Loading...</div>; // 로딩 중 표시
    }

    return (
        <div className='container'>
            <div className="profile-container">
                <div className="profile-info">
                    <img src={userInfo.imageUrl} alt="Profile" className="profile-image" />
                    <h2>{userInfo.nickname}</h2>
                    <p><strong>역할 :</strong> {userInfo.role}</p>
                    <p><strong>구독자 수(총):</strong> {userInfo.maxSubs}</p>
                    <p><strong>포트폴리오:</strong> <a href={userInfo.portfolio} target="_blank" rel="noopener noreferrer">View Portfolio</a></p>
                </div>

                <div className="profile-details">
                    <h3>선호하는 카테고리</h3>
                    <ul>
                        {userInfo.videoTypes.map((type, index) => (
                            <li key={index}>{type}</li>
                        ))}
                    </ul>

                    <h3>편집했던 채널</h3>
                    <ul>
                        {userInfo.editedChannels.map((channel, index) => (
                            <li key={index}>{channel}</li>
                        ))}
                    </ul>

                    <h3>현재 작업 중인 채널</h3>
                    <ul>
                        {userInfo.currentChannels.map((channel, index) => (
                            <li key={index}>{channel}</li>
                        ))}
                    </ul>

                    <h3>기술</h3>
                    <ul>
                        {userInfo.skills.map((skill, index) => (
                            <li key={index}>{skill}</li>
                        ))}
                    </ul>

                    <h3>자기소개</h3>
                    <p>{userInfo.remarks}</p>
                </div>

                {error && <p className="error-message">Failed to fetch data, showing placeholder data.</p>}
            </div>
        </div>
    );
};

export default MyProfile;
