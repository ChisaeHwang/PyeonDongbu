import React, { useState, useCallback, useRef } from 'react';
import axios from 'axios';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import '../styles/PostEditor.css';
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
import { formatNumber } from '../utils/FormatNumber';
import Modal from './Modal';

enum PaymentType {
    PER_HOUR = 'PER_HOUR',
    PER_PROJECT = 'PER_PROJECT',
    MONTHLY_SALARY = 'MONTHLY_SALARY',
    NEGOTIABLE = 'NEGOTIABLE'
}

interface Payment {
    type: PaymentType;
    amount: number;
}

interface PostEditorProps {
    onSubmit?: (
        title: string,
        content: string,
        imageUrl: string,
        tagNames: string[],
        payment: Payment,
        recruitmentPostDetailsReq: any
    ) => void;
    initialData?: RecruitmentPostRes;
}

interface RecruitmentPostRes {
    title: string;
    content: string;
    imageUrl: string;
    tagNames: string[];
    payment: Payment;
    recruitmentPostDetailsRes: {
        maxSubs: number;
        videoTypes: string[];
        skills: string[];
        weeklyWorkload: string;
    };
}

const paymentTypes = [
    { label: '분당', value: PaymentType.PER_HOUR },
    { label: '건당', value: PaymentType.PER_PROJECT },
    { label: '월급', value: PaymentType.MONTHLY_SALARY },
    { label: '협의', value: PaymentType.NEGOTIABLE }
];

const recruitmentTypes = ['구인', '구직'];

const weeklyWorkOptions = [
    { value: '1', label: '1~2개' },
    { value: '3', label: '3~4개' },
    { value: '5', label: '5~6개' },
    { value: '7', label: '7개 이상' }
];

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

const PostEditor: React.FC<PostEditorProps> = ({ onSubmit, initialData }) => {
    const [title, setTitle] = useState(initialData?.title || '');
    const [recruitmentType, setRecruitmentType] = useState('구인');
    const [content, setContent] = useState(initialData?.content || '');
    const [payment, setPayment] = useState<Payment>(initialData?.payment || { type: PaymentType.PER_HOUR, amount: 0 });
    const [paymentType, setPaymentType] = useState<PaymentType>(initialData?.payment?.type || PaymentType.PER_HOUR);
    const [paymentAmount, setPaymentAmount] = useState(initialData?.payment?.amount.toString() || '');
    const [maxSubs, setMaxSubs] = useState<number | null>(initialData?.recruitmentPostDetailsRes?.maxSubs || null);
    const [maxSubsInput, setMaxSubsInput] = useState('');
    const [weeklyWorkload, setWeeklyWorkload] = useState(initialData?.recruitmentPostDetailsRes?.weeklyWorkload || '');
    const [selectedVideoGenres, setSelectedVideoGenres] = useState<string[]>(initialData?.recruitmentPostDetailsRes?.videoTypes || []);
    const [selectedSkills, setSelectedSkills] = useState<string[]>(initialData?.recruitmentPostDetailsRes?.skills || []);
    const [isNegotiable, setIsNegotiable] = useState(initialData?.payment?.type === PaymentType.NEGOTIABLE || false);
  
    const quillRef = useRef<ReactQuill>(null);
    const [showModal, setShowModal] = useState(false);
    const [imageUrl, setImageUrl] = useState<string | null>(initialData?.imageUrl || null);

    const handleRecruitmentTypeChange = (selectedType: string) => {
        setRecruitmentType(selectedType);
    };

    const handleImageUpload = useCallback(() => {
        const input = document.createElement('input');
        input.setAttribute('type', 'file');
        input.setAttribute('accept', 'image/*');
        input.click();

        input.onchange = async () => {
            const file = input.files?.[0];
            if (file) {
                const formData = new FormData();
                formData.append('file', file);  

                try {
                    const response = await axios.post('http://localhost:8080/upload', formData, {
                        headers: {
                            'Content-Type': 'multipart/form-data',
                        },
                    });

                    const imageUrl = response.data.replace('Uploaded: ', '').trim();
                    const quill = quillRef.current?.getEditor();
                    if (quill) {
                        const range = quill.getSelection(true);
                        quill.insertEmbed(range.index, 'image', imageUrl);
                    }
                } catch (error) {
                    console.error('이미지 업로드 실패:', error);
                }
            }
        };
    }, []);

    const handleRepresentativeImageUpload = useCallback(() => {
        const input = document.createElement('input');
        input.setAttribute('type', 'file');
        input.setAttribute('accept', 'image/*');
        input.click();

        input.onchange = async () => {
            const file = input.files?.[0];
            if (file) {
                const formData = new FormData();
                formData.append('file', file);  

                try {
                    const response = await axios.post('http://localhost:8080/upload', formData, {
                        headers: {
                            'Content-Type': 'multipart/form-data',
                        },
                    });

                    const imageUrl = response.data.replace('Uploaded: ', '').trim();
                    setImageUrl(imageUrl);
                } catch (error) {
                    console.error('대표 이미지 업로드 실패:', error);
                }
            }
        };
    }, []);

    const handlePaymentTypeClick = (type: PaymentType) => {
        setPaymentType(type);
        if (type === PaymentType.NEGOTIABLE) {
            setPaymentAmount('협의');
            setIsNegotiable(true);
            setPayment({ type: PaymentType.NEGOTIABLE, amount: 0 });
        } else {
            setPaymentAmount('');
            setIsNegotiable(false);
            setPayment(prev => ({ ...prev, type }));
        }
    };

    const handleMaxSubsChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value.replace(/[^0-9]/g, '');
        const numValue = parseInt(value, 10);
        
        if (numValue > 10000000) {
            setMaxSubsInput('10000000');
            setMaxSubs(10000000);
        } else {
            setMaxSubsInput(value);
            setMaxSubs(numValue || null);
        }
    };

    const handlePaymentAmountChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (!isNegotiable) {
            const numericValue = e.target.value.replace(/[^0-9]/g, '');
            setPaymentAmount(numericValue);
            setPayment(prev => ({ ...prev, amount: parseInt(numericValue) || 0 }));
        }
    };

    const handleFilterOptionClick = (option: FilterOption) => {
        if (option.type === 'skill') {
            setSelectedSkills(prev => 
                prev.includes(option.name) ? prev.filter(skill => skill !== option.name) : [...prev, option.name]
            );
        } else {
            setSelectedVideoGenres(prev => 
                prev.includes(option.name) ? prev.filter(genre => genre !== option.name) : [...prev, option.name]
            );
        }
    };

    const handleSubmit = async () => {
        if (!imageUrl) {
            alert('대표 이미지를 선택해주세요.');
            return;
        }

        const recruitmentPostDetailsReq = {
            maxSubs,
            videoTypes: selectedVideoGenres,
            skills: selectedSkills,
            weeklyWorkload,
        };

        const postData = {
            title,
            content,
            imageUrl, 
            tagNames: [recruitmentType],
            payment,
            recruitmentPostDetailsReq,
        };

        if (onSubmit) {
            onSubmit(postData.title, postData.content, postData.imageUrl, postData.tagNames, postData.payment, postData.recruitmentPostDetailsReq);
        }
    };

    const modules = {
        toolbar: {
            container: [
                [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
                ['bold', 'italic', 'underline', 'strike'],
                [{ 'list': 'ordered'}, { 'list': 'bullet' }],
                ['link', 'image'],
            ],
            handlers: {
                image: handleImageUpload
            }
        }
    };

    return (
        <div className="post-editor-container">
            <div className="editor-section">
                <h2>게시글 작성</h2>
                
                <div className="title-input-container">
                    <input
                        type="text"
                        placeholder="제목을 입력하세요"
                        value={title}
                        onChange={(e) => setTitle(e.target.value.slice(0, 25))}
                        className="title-input"
                        maxLength={25}
                    />
                    <span className="title-char-count">{title.length}/25</span>
                </div>

                <h3>구인/구직</h3>
                <div className="button-group">
                    {recruitmentTypes.map((type) => (
                        <button
                            key={type}
                            className={`recruitment-type-button ${recruitmentType === type ? 'active' : ''}`}
                            onClick={() => handleRecruitmentTypeChange(type)}
                        >
                            {type}
                        </button>
                    ))}
                </div>

                <h3>선호하는 영상 장르</h3>
                <div className="button-group">
                    {filterOptions.filter(option => option.type === 'videoGenre').map((option) => (
                        <button
                            key={option.name}
                            className={`filter-button ${selectedVideoGenres.includes(option.name) ? 'active' : ''}`}
                            onClick={() => handleFilterOptionClick(option)}
                        >
                            <option.icon />
                            {option.name}
                        </button>
                    ))}
                </div>

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

                <div className="details-section">
                    <div className="details-row">
                        <div className="details-item">
                            <label htmlFor="maxSubs">최대 구독자 수</label>
                            <div className="input-with-unit">
                                <input
                                    id="maxSubs"
                                    type="text"
                                    placeholder="최대 구독자 수 입력"
                                    value={formatNumber(maxSubsInput)}
                                    onChange={handleMaxSubsChange}
                                    className="details-input"
                                />
                                <span className="input-unit">명</span>
                            </div>
                        </div>
                        <div className="details-item">
                            <label htmlFor="weeklyWorkload">주간 작업량</label>
                            <select
                                id="weeklyWorkload"
                                value={weeklyWorkload}
                                onChange={(e) => setWeeklyWorkload(e.target.value)}
                                className="details-select"
                            >
                                <option value="">선택하세요</option>
                                {weeklyWorkOptions.map(option => (
                                    <option key={option.value} value={option.value}>{option.label}</option>
                                ))}
                            </select>
                        </div>
                    </div>
                </div>

                <ReactQuill 
                    ref={quillRef}
                    value={content} 
                    onChange={setContent} 
                    modules={modules}
                    placeholder="여기에 게시글 내용을 작성하세요..." 
                    className="quill-editor"
                />

                <div className="payment-container">
                <h3>결제 유형</h3>
                    <div className="payment-group">
                        {paymentTypes.map((type) => (
                            <button
                                key={type.value}
                                className={`payment-type-button ${paymentType === type.value ? 'active' : ''}`}
                                onClick={() => handlePaymentTypeClick(type.value)}
                            >
                                {type.label}
                            </button>
                        ))}
                    </div>

                    <div className="payment-section">
                        <div className="input-with-unit">
                            <input
                                type="text"
                                placeholder="금액을 입력하세요 (최대 8자리)"
                                value={isNegotiable ? '협의' : formatNumber(paymentAmount)}
                                onChange={handlePaymentAmountChange}
                                className="payment-amount-input"
                                maxLength={11}  // 쉼표를 고려하여 최대 길이를 11로 변경
                                disabled={isNegotiable}
                            />
                            <span className="input-unit">원</span>
                        </div>
                    </div>
                </div>

                <div className="button-container">
                    <button className="submit-button" onClick={() => setShowModal(true)}>
                        {recruitmentType} 하러가기
                    </button>
                </div>
            </div>

            <div className="preview-section">
                <h2>미리보기</h2>
                <div className="preview-content">
                    <h3>{title}</h3>
                    <div dangerouslySetInnerHTML={{ __html: content }} />
                </div>
            </div>

            {showModal && (
            <Modal onClose={() => setShowModal(false)}>
                <h2 className="post-editor-modal-title">대표 이미지 설정</h2>
                <div className="post-editor-representative-image-container" onClick={handleRepresentativeImageUpload}>
                    {imageUrl ? (
                        <img src={imageUrl} alt="대표 이미지" className="post-editor-representative-image" />
                    ) : (
                        <div className="post-editor-image-placeholder">
                            <span>+</span>
                            <p>대표 이미지 설정하기</p>
                        </div>
                    )}
                </div>
                <button className="post-editor-submit-button" onClick={handleSubmit}>
                    작성 완료하기
                </button>
            </Modal>
            )}      
        </div>
    );
};

export default PostEditor;