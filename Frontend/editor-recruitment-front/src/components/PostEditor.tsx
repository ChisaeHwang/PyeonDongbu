// src/components/PostEditor.tsx
import React, { useState } from 'react';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import '../styles/PostEditor.css';

const categories = ['구인', '구직', '커뮤니티'];

const PostEditor = ({ onSubmit }: { onSubmit: (title: string, category: string, content: string, imageUrl: string) => void }) => {
    const [title, setTitle] = useState('');
    const [category, setCategory] = useState('구인');
    const [content, setContent] = useState('');
    const [image, setImage] = useState<File | null>(null);
    const [imageUrl, setImageUrl] = useState('');

    // 이미지 업로드 핸들러
    const handleImageUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];
        if (file) {
            setImage(file);
            // 백엔드로 이미지 업로드 후 URL 반환
            const formData = new FormData();
            formData.append('image', file);

            // 예시로 백엔드에 이미지 전송 (API 호출)
            const response = await fetch('https://your-backend-url.com/upload', {
                method: 'POST',
                body: formData,
            });
            const data = await response.json();
            setImageUrl(data.imageUrl); // 백엔드에서 반환된 이미지 URL
        }
    };

    const handleSubmit = () => {
        // 게시글 작성 정보 전송
        onSubmit(title, category, content, imageUrl);
    };

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

                {/* 카테고리 선택 */}
                <select value={category} onChange={(e) => setCategory(e.target.value)} className="category-select">
                    {categories.map((cat) => (
                        <option key={cat} value={cat}>
                            {cat}
                        </option>
                    ))}
                </select>

                {/* 이미지 업로드 */}
                <input type="file" onChange={handleImageUpload} className="image-upload" />
                {imageUrl && <img src={imageUrl} alt="Uploaded" className="image-preview" />}

                {/* Quill Editor */}
                <ReactQuill 
                    value={content} 
                    onChange={setContent} 
                    placeholder="여기에 게시글 내용을 작성하세요..." 
                    className="quill-editor"
                />
                
                <button className="submit-button" onClick={handleSubmit}>작성 완료</button>
            </div>

            <div className="preview-section">
                <h2>미리보기</h2>
                <h3>{title}</h3>
                {imageUrl && <img src={imageUrl} alt="미리보기 이미지" className="image-preview" />}
                <p dangerouslySetInnerHTML={{ __html: content }} />
            </div>
        </div>
    );
};

export default PostEditor;
