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

interface PostEditorProps {
    onSubmit: (title: string, content: string, images: string[], tagNames: string[], payments: any[], recruitmentPostDetailsReq: any) => void;
}

const paymentTypes = [
    { label: '분당', value: 'PER_HOUR' },
    { label: '건당', value: 'PER_PROJECT' },
    { label: '월급', value: 'MONTHLY_SALARY' },
    { label: '협의', value: 'NEGOTIABLE' }
];

const recruitmentTypes = ['구인', '구직'];

const weeklyWorkOptions = [
    { value: '1-2', label: '1~2개' },
    { value: '3-4', label: '3~4개' },
    { value: '5-6', label: '5~6개' },
    { value: '7+', label: '7개 이상' }
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

const PostEditor: React.FC<PostEditorProps> = ({ onSubmit }) => {
    const [title, setTitle] = useState('');
    const [recruitmentType, setRecruitmentType] = useState('구인');
    const [content, setContent] = useState('');
    const [tagNames, setTagNames] = useState<string[]>([]);
    const [payments, setPayments] = useState<Record<string, string>>({});
    const [paymentType, setPaymentType] = useState('');
    const [paymentAmount, setPaymentAmount] = useState('');
    const [maxSubs, setMaxSubs] = useState<number | null>(null);
    const [maxSubsInput, setMaxSubsInput] = useState('');
    const [weeklyWorkCount, setWeeklyWorkCount] = useState('');
    const [selectedVideoGenres, setSelectedVideoGenres] = useState<string[]>([]);
    const [selectedSkills, setSelectedSkills] = useState<string[]>([]);
    const [isNegotiable, setIsNegotiable] = useState(false);
    const quillRef = useRef<ReactQuill>(null);

    const handleRecruitmentTypeChange = (selectedType: string) => {
        setRecruitmentType(selectedType);
        const updatedTags = tagNames.filter(tag => tag !== '구인' && tag !== '구직');
        setTagNames([...updatedTags, selectedType]);
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

    const handlePaymentTypeClick = (type: string) => {
        setPaymentType(type);
        if (type === 'NEGOTIABLE') {
            setPaymentAmount('협의');
            setIsNegotiable(true);
        } else {
            setPaymentAmount('');
            setIsNegotiable(false);
        }
    };

    const handleMaxSubsChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value.replace(/[^0-9]/g, '');
        setMaxSubsInput(value);
        setMaxSubs(value ? Number(value) : null);
    };

    const handlePaymentAmountChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (!isNegotiable) {
            const amount = e.target.value;
            setPaymentAmount(amount);
            if (paymentType) {
                setPayments(prev => ({...prev, [paymentType]: amount}));
            }
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

    const handleSubmit = () => {
        const paymentArray = Object.entries(payments).map(([type, amount]) => ({type, amount}));
        const recruitmentPostDetailsReq = {
            maxSubs,
            weeklyWorkCount,
            videoGenres: selectedVideoGenres,
            skills: selectedSkills,
        };

        onSubmit(title, content, [], tagNames, paymentArray, recruitmentPostDetailsReq);
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
                            <label htmlFor="weeklyWorkCount">주간 작업 갯수</label>
                            <select
                                id="weeklyWorkCount"
                                value={weeklyWorkCount}
                                onChange={(e) => setWeeklyWorkCount(e.target.value)}
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
                                maxLength={8}
                                disabled={isNegotiable}
                            />
                            <span className="input-unit">원</span>
                        </div>
                    </div>
                </div>

                <div className="button-container">
                    <button className="submit-button" onClick={handleSubmit}>
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
        </div>
    );
};

export default PostEditor;