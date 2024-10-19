import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/ProfileSetupModal.css';

interface ProfileSetupModalProps {
  isOpen: boolean;
  onClose: () => void;
}

const ProfileSetupModal: React.FC<ProfileSetupModalProps> = ({ isOpen, onClose }) => {
  const navigate = useNavigate();

  if (!isOpen) return null;

  const handleSetupProfile = () => {
    navigate('/mypage');
    onClose();
  };

  return (
    <div className="profile-setup-modal-overlay">
      <div className="profile-setup-modal-content">
        <h2>프로필 설정</h2>
        <p>프로필을 설정하시면 매칭 기능을 사용하실 수 있습니다.</p>
        <div className="profile-setup-modal-buttons">
          <button onClick={handleSetupProfile} className="profile-setup-button primary-button">프로필 설정하기</button>
          <button onClick={onClose} className="profile-setup-button secondary-button">나중에 하기</button>
        </div>
      </div>
    </div>
  );
};

export default ProfileSetupModal;