import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import '../styles/MyProfile.css';
import { formatNumber } from '../utils/FormatNumber';
import { useToast } from '../hooks/useToast';
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

// Role 타입 정의
type Role = 'CLIENT' | 'EDITOR' | 'ETC_WORKER' | 'GUEST';

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
    const [role, setRole] = useState<Role>('GUEST');
    const [selectedVideoTypes, setSelectedVideoTypes] = useState<string[]>([]);
    const [selectedSkills, setSelectedSkills] = useState<string[]>([]);
    const [editedChannels, setEditedChannels] = useState('');
    const [currentChannels, setCurrentChannels] = useState('');
    const [maxSubs, setMaxSubs] = useState('');
    const [introduction, setIntroduction] = useState('');
    const [imageUrl, setImageUrl] = useState('https://ifh.cc/g/q2ZvDd.jpg');
    const [isEditing, setIsEditing] = useState(false);
    const { showSuccessToast, showErrorToast } = useToast();

    const handleImageUpload = useCallback(() => {
        console.log('Image upload button clicked'); // 디버깅용 로그
        const input = document.createElement('input');
        input.setAttribute('type', 'file');
        input.setAttribute('accept', 'image/*');
        input.click();

        input.onchange = async (event) => {
            console.log('File selected'); // 디버깅용 로그
            const file = (event.target as HTMLInputElement).files?.[0];
            if (file) {
                console.log('File:', file); // 디버깅용 로그
                const formData = new FormData();
                formData.append('file', file);  

                try {
                    const accessToken = sessionStorage.getItem('access-token');
                    console.log('Access token:', accessToken); // 디버깅용 로그
                    const response = await axios.post('http://localhost:8080/upload', formData, {
                        headers: {
                            'Content-Type': 'multipart/form-data',
                            Authorization: `Bearer ${accessToken}`,
                        },
                        withCredentials: true,
                    });

                    console.log('Upload response:', response); // 디버깅용 로그
                    const imageUrl = response.data.replace('Uploaded: ', '').trim();
                    setImageUrl(imageUrl);
                    console.log('New image URL:', imageUrl); // 디버깅용 로그
                } catch (error) {
                    console.error('이미지 업로드 실패:', error);
                    showErrorToast('프로필 이미지 업로드에 실패했습니다.');
                }
            }
        };
    }, [showErrorToast]);

    useEffect(() => {
        fetchProfileData();
    }, []);

    const fetchProfileData = async () => {
        try {
            const accessToken = sessionStorage.getItem('access-token');
            const response = await axios.get('http://localhost:8080/api/member', {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                },
                withCredentials: true,
            });
            const data = response.data.data;
            setNickname(data.nickname);
            setImageUrl(data.imageUrl);
            setRole(data.role);
            setSelectedVideoTypes(data.memberDetailsRes.videoTypes);
            setSelectedSkills(data.memberDetailsRes.skills);
            setEditedChannels(data.memberDetailsRes.editedChannels.join(', '));
            setCurrentChannels(data.memberDetailsRes.currentChannels.join(', '));
            setMaxSubs(data.memberDetailsRes.maxSubs.toString());
            setIntroduction(data.memberDetailsRes.remarks);
        } catch (error) {
            console.error('프로필 정보를 가져오는 데 실패했습니다:', error);
        }
    };

    const handleSaveProfile = async () => {
        try {
            const accessToken = sessionStorage.getItem('access-token');
            const profileData = {
                nickname,
                imageUrl,
                role,
                memberDetails: {
                    maxSubs: parseInt(maxSubs),
                    videoTypes: selectedVideoTypes,
                    editedChannels: editedChannels.split(',').map(channel => channel.trim()),
                    currentChannels: currentChannels.split(',').map(channel => channel.trim()),
                    skills: selectedSkills,
                    remarks: introduction,
                },
            };
            await axios.put('http://localhost:8080/api/member', profileData, {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                },
                withCredentials: true,
            });
            showSuccessToast('프로필이 성공적으로 저장되었습니다.');
            setIsEditing(false);
            // 페이지 새로고침
            window.location.reload();
        } catch (error) {
            console.error('프로필 저장 중 오류가 발생했습니다:', error);
            showErrorToast('프로필 저장에 실패했습니다.');
        }
    };

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

    const handleRoleChange = (newRole: 'CLIENT' | 'WORKER') => {
        if (newRole === 'CLIENT') {
            setRole('CLIENT');
        } else {
            // 작업자로 설정할 때는 기존 역할이 EDITOR나 ETC_WORKER면 그대로 유지, 아니면 EDITOR로 설정
            setRole(prevRole => (prevRole === 'EDITOR' || prevRole === 'ETC_WORKER') ? prevRole : 'EDITOR');
        }
    };

    const isWorker = (role: Role) => role === 'EDITOR' || role === 'ETC_WORKER';

    return (
        <div className="profile-container">
            <div className="profile-header">
                <h2 className="page-title">마이 페이지</h2>
                <button 
                    className="save-profile-button"
                    onClick={isEditing ? handleSaveProfile : () => setIsEditing(true)}
                >
                    {isEditing ? '저장하기' : '수정하기'}
                </button>
            </div>
            
            <div className="profile-row">
                <div className="profile-image-container">
                    <img src={imageUrl} alt="Profile" className="profile-image" />
                    <button 
                        className="upload-button" 
                        onClick={handleImageUpload}
                        disabled={!isEditing}
                    >
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
                            disabled={!isEditing}
                        />
                        <span className="char-count">{nickname.length}/8</span>
                    </div>
                </div>
            </div>

            <div className="profile-section">
                <h3>역할</h3>
                <div className="profile-button-group">
                    <button
                        className={`profile-role-button ${role === 'CLIENT' ? 'active' : ''}`}
                        onClick={() => handleRoleChange('CLIENT')}
                        disabled={!isEditing}
                    >
                        클라이언트
                    </button>
                    <button
                        className={`profile-role-button ${isWorker(role) ? 'active' : ''}`}
                        onClick={() => handleRoleChange('WORKER')}
                        disabled={!isEditing}
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
                            disabled={!isEditing}
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
                            disabled={!isEditing}
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
                        disabled={!isEditing}
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
                        disabled={!isEditing}
                    />
                    <span className="char-count">{currentChannels.length}/48</span>
                </div>
            </div>

            <div className="profile-section">
                <h3>최고 구독자 수</h3>
                <div className="input-with-unit">
                    <input
                        type="text"
                        value={isEditing ? maxSubs : formatNumber(maxSubs)}
                        onChange={(e) => setMaxSubs(e.target.value.replace(/[^0-9]/g, ''))}
                        placeholder="최고 구독자 수를 입력하세요 (최대 1,000만 명)"
                        disabled={!isEditing}
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
                    disabled={!isEditing}
                />
                <span className="char-count">{introduction.length}/100</span>
            </div>
        </div>
    );
};

export default MyProfile;