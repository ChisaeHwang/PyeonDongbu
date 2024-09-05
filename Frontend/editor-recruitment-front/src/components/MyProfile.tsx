import React, { useState } from 'react';
import '../styles/MyProfile.css';
import { formatNumber } from '../utils/FormatNumber';
import premierProIcon from '../assets/premierProIcon';
import photoshopIcon from '../assets/photoshopIcon';
import afterEffectsIcon from '../assets/afterEffectsIcon';
import illustratorIcon from '../assets/illustratorIcon';
import chatIcon from '../assets/chatIcon';
import radioIcon from '../assets/radioIcon';
import gameIcon from '../assets/gameIcon';
import foodIcon from '../assets/foodIcon';
import educationIcon from '../assets/educationIcon';
import reviewIcon from '../assets/reviewIcon';
import UploadIcon from '../assets/UploadIcon';

type FilterOption = {
    name: string;
    icon: React.ComponentType; 
    type: 'skill' | 'videoGenre';
};

const filterOptions: FilterOption[] = [
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

const MyProfile = () => {
    const [nickname, setNickname] = useState('');
    const [role, setRole] = useState('');
    const [selectedVideoTypes, setSelectedVideoTypes] = useState<string[]>([]);
    const [selectedSkills, setSelectedSkills] = useState<string[]>([]);
    const [editedChannels, setEditedChannels] = useState('');
    const [currentChannels, setCurrentChannels] = useState('');
    const [maxSubs, setMaxSubs] = useState('');
    const [introduction, setIntroduction] = useState('');

    const handleFilterOptionClick = (option: FilterOption) => {
        if (option.type === 'skill') {
            setSelectedSkills(prev => 
                prev.includes(option.name) ? prev.filter(skill => skill !== option.name) : [...prev, option.name]
            );
        } else {
            setSelectedVideoTypes(prev => 
                prev.includes(option.name) ? prev.filter(genre => genre !== option.name) : [...prev, option.name]
            );
        }
    };

    return (
        <div className="profile-container">
            <h2 className="page-title">마이 페이지</h2>
            
            <div className="profile-row">
                <div className="profile-image-container">
                    <img src="https://ifh.cc/g/q2ZvDd.jpg" alt="Profile" className="profile-image" />
                    <button className="upload-button">
                        <UploadIcon />
                        <span>프로필 사진 업로드</span>
                    </button>
                </div>
                <div className="nickname-container">
                    <label htmlFor="nickname">닉네임</label>
                    <div className="input-with-count">
                        <input
                            id="nickname"
                            type="text"
                            value={nickname}
                            onChange={(e) => setNickname(e.target.value)}
                            placeholder="닉네임 (최대 8자)"
                            maxLength={8}
                        />
                        <span className="char-count">{nickname.length}/8</span>
                    </div>
                </div>
            </div>

            <div className="profile-section">
                <h3>역할</h3>
                <div className="profile-button-group">
                    <button
                        className={`profile-role-button ${role === '클라이언트' ? 'active' : ''}`}
                        onClick={() => setRole('클라이언트')}
                    >
                        클라이언트
                    </button>
                    <button
                        className={`profile-role-button ${role === '작업자' ? 'active' : ''}`}
                        onClick={() => setRole('작업자')}
                    >
                        작업자
                    </button>
                </div>
            </div>

            <div className="profile-section">
                <h3>선호하는 영상 타입</h3>
                <div className="button-group">
                    {filterOptions.filter(option => option.type === 'videoGenre').map((option) => (
                        <button
                            key={option.name}
                            className={`filter-button ${selectedVideoTypes.includes(option.name) ? 'active' : ''}`}
                            onClick={() => handleFilterOptionClick(option)}
                        >
                            <option.icon />
                            {option.name}
                        </button>
                    ))}
                </div>
            </div>

            <div className="profile-section">
                <h3>사용하는 기술</h3>
                <div className="button-group">
                    {filterOptions.filter(option => option.type === 'skill').map((option) => (
                        <button
                            key={option.name}
                            className={`filter-button ${selectedSkills.includes(option.name) ? 'active' : ''}`}
                            onClick={() => handleFilterOptionClick(option)}
                        >
                            <option.icon />
                            {option.name}
                        </button>
                    ))}
                </div>
            </div>

            <div className="profile-section">
                <h3>편집했던 채널</h3>
                <div className="input-with-count">
                    <input
                        type="text"
                        value={editedChannels}
                        onChange={(e) => setEditedChannels(e.target.value.slice(0, 48))}
                        placeholder="편집했던 채널을 입력하세요 (최대 48자)"
                        maxLength={48}
                    />
                    <span className="char-count">{editedChannels.length}/48</span>
                </div>
            </div>

            <div className="profile-section">
                <h3>작업 중인 채널</h3>
                <div className="input-with-count">
                    <input
                        type="text"
                        value={currentChannels}
                        onChange={(e) => setCurrentChannels(e.target.value.slice(0, 48))}
                        placeholder="작업 중인 채널을 입력하세요 (최대 48자)"
                        maxLength={48}
                    />
                    <span className="char-count">{currentChannels.length}/48</span>
                </div>
            </div>

            <div className="profile-section">
                <h3>최고 구독자 수</h3>
                <div className="input-with-unit">
                    <input
                        type="text"
                        value={formatNumber(maxSubs)}
                        onChange={(e) => setMaxSubs(e.target.value.replace(/[^0-9]/g, ''))}
                        placeholder="최고 구독자 수를 입력하세요 (최대 1,000만 명)"
                    />
                    <span className="input-unit">명</span>
                </div>
            </div>

            <div className="profile-section">
                <h3>자기 소개</h3>
                <textarea
                    value={introduction}
                    onChange={(e) => setIntroduction(e.target.value.slice(0, 100))}
                    placeholder="자기 소개를 입력하세요 (최대 100자)"
                    maxLength={100}
                />
                <span className="char-count">{introduction.length}/100</span>
            </div>
        </div>
    );
};

export default MyProfile;