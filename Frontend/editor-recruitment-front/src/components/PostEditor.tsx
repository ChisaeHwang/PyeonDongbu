import React, { useState } from 'react';
import axios from 'axios';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import '../styles/PostEditor.css';

// 타입 정의
interface PostEditorProps {
    onSubmit: (title: string, content: string, images: string[], tagNames: string[], payments: any[], recruitmentPostDetailsReq: any) => void;
}

// PaymentType enum을 프론트엔드에서 사용
const paymentTypes = [
    { label: '분당', value: 'PER_HOUR' },
    { label: '건당', value: 'PER_PROJECT' },
    { label: '월급', value: 'MONTHLY_SALARY' }
];

// 미리 지정된 태그 목록
const predefinedTags = [
    '게임', '모션그래픽', '브이로그', 
    '색보정', '애프터이펙트', '영상편집', '유튜브', '프리미어프로'
];

const categories = ['구인', '구직'];

const PostEditor: React.FC<PostEditorProps> = ({ onSubmit }) => {
    const [title, setTitle] = useState('');
    const [category, setCategory] = useState('구인'); // 기본값을 구인으로 설정
    const [content, setContent] = useState('');
    const [imageUrl, setImageUrl] = useState<string[]>([]);
    const [tagNames, setTagNames] = useState<string[]>([]);
    const [payments, setPayments] = useState<any[]>([]);
    const [paymentType, setPaymentType] = useState('');
    const [paymentAmount, setPaymentAmount] = useState('');

    // recruitmentPostDetailsReq에 필요한 필드
    const [maxSubs, setMaxSubs] = useState<number | null>(null);
    const [videoTypes, setVideoTypes] = useState<string[]>([]);
    const [skills, setSkills] = useState<string[]>([]);
    const [remarks, setRemarks] = useState('');

    // 카테고리를 선택하면 다른 카테고리와 중복되지 않도록 하여 태그에 추가
    const handleCategoryChange = (selectedCategory: string) => {
        setCategory(selectedCategory); // 하나의 카테고리만 선택되도록
        // 구인, 구직 태그를 기존 태그에서 제거하고 선택된 카테고리를 태그에 추가
        const updatedTags = tagNames.filter(tag => tag !== '구인' && tag !== '구직');
        setTagNames([...updatedTags, selectedCategory]); // 새로운 카테고리 태그 추가
    };

    // 이미지 업로드 핸들러
    const handleImageUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];
        if (file) {
            const formData = new FormData();
            formData.append('image', file);

            const response = await axios.post('http://localhost:8080/api/upload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            setImageUrl([...imageUrl, response.data.imageUrl]); // 백엔드에서 반환된 이미지 URL
        }
    };

    // 태그 선택/취소 핸들러
    const handleTagClick = (tag: string) => {
        if (tagNames.includes(tag)) {
            setTagNames(tagNames.filter(t => t !== tag));  // 선택 해제
        } else {
            setTagNames([...tagNames, tag]);  // 선택
        }
    };

    // 결제 정보 추가 핸들러
    const handleAddPayment = () => {
        if (!paymentAmount || isNaN(Number(paymentAmount)) || paymentAmount.length > 8) {
            alert('올바른 금액을 입력하세요 (최대 8자리 숫자).');
            return;
        }
        setPayments([...payments, { type: paymentType, amount: paymentAmount }]);
        setPaymentAmount(''); // 입력 필드 초기화
    };

    // 결제 타입 버튼 클릭 핸들러
    const handlePaymentTypeClick = (type: string) => {
        if (paymentType === type) {
            setPaymentType(''); // 이미 선택된 타입을 클릭하면 취소
        } else {
            setPaymentType(type);
        }
    };

    // 결제 정보 삭제 핸들러
    const handleDeletePayment = (index: number) => {
        const updatedPayments = payments.filter((_, i) => i !== index);
        setPayments(updatedPayments);
    };

    const handleSubmit = () => {
        const recruitmentPostDetailsReq = {
            maxSubs,
            videoTypes,
            skills,
            remarks,
        };

        onSubmit(title, content, imageUrl, tagNames, payments, recruitmentPostDetailsReq); // 폼 데이터 전달
    };

    // 미리보기에서 카테고리 태그를 제외한 나머지 태그를 표시하기 위해 필터링
    const tagsWithoutCategory = tagNames.filter(tag => tag !== '구인' && tag !== '구직');

    return (
        <div className="post-editor-container">
            <div className="editor-section">
                <h2>게시글 작성</h2>
                
                {/* 제목 입력 필드 */}
                <input
                    type="text"
                    placeholder="제목을 입력하세요"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    className="title-input"
                />

                {/* 카테고리 선택 버튼 (구인, 구직 중 하나만 선택 가능) */}
                <div className="button-group">
                    {categories.map((cat) => (
                        <button
                            key={cat}
                            className={`category-button ${category === cat ? 'active' : ''}`}
                            onClick={() => handleCategoryChange(cat)} // 하나만 선택되도록
                        >
                            {cat}
                        </button>
                    ))}
                </div>

                {/* 미리 정의된 태그 버튼 */}
                <div className="button-group">
                    {predefinedTags.map((tag) => (
                        <button
                            key={tag}
                            className={`tag-button ${tagNames.includes(tag) ? 'active' : ''}`}
                            onClick={() => handleTagClick(tag)}
                        >
                            {tag}
                        </button>
                    ))}
                </div>

                {/* 이미지 업로드 */}
                <input type="file" onChange={handleImageUpload} className="image-upload" />
                {imageUrl.map((url, index) => (
                    <img key={index} src={url} alt={`Uploaded ${index}`} className="image-preview" />
                ))}

                {/* Quill Editor */}
                <ReactQuill 
                    value={content} 
                    onChange={setContent} 
                    placeholder="여기에 게시글 내용을 작성하세요..." 
                    className="quill-editor"
                />

                {/* 결제 타입 버튼 */}
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

                {/* 결제 정보 입력 필드 */}
                <div className="payment-section">
                    <input
                        type="text"
                        placeholder="금액을 입력하세요 (최대 8자리)"
                        value={paymentAmount}
                        onChange={(e) => setPaymentAmount(e.target.value)}
                        className="payment-amount-input"
                    />
                    <button onClick={handleAddPayment} className="add-payment-button">결제 정보 추가</button>
                </div>

                {/* 결제 정보 리스트 */}
                <div className="payment-list">
                    {payments.map((payment, index) => (
                        <div key={index} className="payment-item">
                            {paymentTypes.find(type => type.value === payment.type)?.label}: {payment.amount}원
                            <button onClick={() => handleDeletePayment(index)}>삭제</button>
                        </div>
                    ))}
                </div>

                {/* recruitmentPostDetailsReq 입력 필드 */}
                <div className="details-section">
                    <h3>세부 사항</h3>
                    <input
                        type="number"
                        placeholder="최대 구독자 수"
                        value={maxSubs || ''}
                        onChange={(e) => setMaxSubs(Number(e.target.value))}
                        className="details-input"
                    />

                    <input
                        type="text"
                        placeholder="영상 유형 (콤마로 구분)"
                        value={videoTypes.join(', ')}
                        onChange={(e) => setVideoTypes(e.target.value.split(',').map(v => v.trim()))}
                        className="details-input"
                    />

                    <input
                        type="text"
                        placeholder="기술 (콤마로 구분)"
                        value={skills.join(', ')}
                        onChange={(e) => setSkills(e.target.value.split(',').map(v => v.trim()))}
                        className="details-input"
                    />

                    <textarea
                        placeholder="비고"
                        value={remarks}
                        onChange={(e) => setRemarks(e.target.value)}
                        className="details-textarea"
                    />
                </div>

                <button className="submit-button" onClick={handleSubmit}>작성 완료</button>
            </div>

            {/* 미리보기 섹션 */}
            <div className="preview-section">
                <h2>미리보기</h2>
                <h3>{title}</h3>
                {imageUrl.map((url, index) => (
                    <img key={index} src={url} alt={`미리보기 이미지 ${index}`} className="image-preview" />
                ))}
                
                {/* 카테고리와 태그 분리 */}
                <div>
                    <strong>카테고리:</strong> {category}  {/* 카테고리는 별도로 표시 */}
                </div>
                <div>
                    <strong>태그:</strong> {tagsWithoutCategory.join(', ')}  {/* 카테고리 제외한 태그만 표시 */}
                </div>
                
                <div>
                    <strong>결제 정보:</strong>
                    <ul>
                        {payments.map((payment, index) => (
                            <li key={index}>{paymentTypes.find(type => type.value === payment.type)?.label} - {payment.amount}원</li>
                        ))}
                    </ul>
                </div>

                {/* 미리보기 섹션에 세부 사항 표시 */}
                <div>
                    <strong>최대 구독자 수:</strong> {maxSubs || 'N/A'} <br />
                    <strong>영상 유형:</strong> {videoTypes.join(', ') || 'N/A'} <br />
                    <strong>기술:</strong> {skills.join(', ') || 'N/A'} <br />
                    <strong>비고:</strong> {remarks || 'N/A'}
                </div>
                <p dangerouslySetInnerHTML={{ __html: content }} />
            </div>
        </div>
    );
};

export default PostEditor;
