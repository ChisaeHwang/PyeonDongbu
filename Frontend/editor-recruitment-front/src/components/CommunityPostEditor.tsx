import React, { useState, useRef } from 'react';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import '../styles/CommunityPostEditor.css';

interface CommunityPostEditorProps {
    onSubmit: (title: string, content: string, tag: string) => void;
}

const CommunityPostEditor: React.FC<CommunityPostEditorProps> = ({ onSubmit }) => {
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [tag, setTag] = useState('');
    const [tagError, setTagError] = useState(false);
    const quillRef = useRef<ReactQuill>(null);

    const handleSubmit = () => {
        if (!tag) {
            setTagError(true);
            return;
        }
        onSubmit(title, content, tag);
    };

    const handleTagSelect = (selectedTag: string) => {
        setTag(selectedTag);
        setTagError(false);
    };

    const tags = ['편집', '유튜버', '썸네일러', '모델링'];

    const modules = {
        toolbar: {
            container: [
                [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
                ['bold', 'italic', 'underline', 'strike'],
                [{ 'list': 'ordered'}, { 'list': 'bullet' }],
                ['link'],
            ],
        }
    };

    return (
        <div className="community-post-editor-container">
            <div className="editor-section">
                <h2>커뮤니티 게시글 작성</h2>
                <div className="title-input-container">
                    <input
                        type="text"
                        placeholder="제목을 입력하세요"
                        value={title}
                        onChange={(e) => setTitle(e.target.value.slice(0, 25))}
                        className="community-post-editor__title-input"
                        maxLength={25}
                    />
                    <span className="title-char-count">{title.length}/25</span>
                </div>
                <ReactQuill 
                    ref={quillRef}
                    value={content} 
                    onChange={setContent} 
                    modules={modules}
                    placeholder="여기에 게시글 내용을 작성하세요..." 
                    className="community-post-editor__content-input"
                />
                <div className="community-post-editor__tag-selection">
                    <h3>태그 선택</h3>
                    <div className="community-post-editor__tag-buttons">
                        {tags.map((tagOption) => (
                            <button
                                key={tagOption}
                                className={`community-post-editor__tag-button ${tag === tagOption ? 'active' : ''}`}
                                onClick={() => handleTagSelect(tagOption)}
                            >
                                {tagOption}
                            </button>
                        ))}
                    </div>
                    {tagError && <p className="tag-error-message">태그를 선택해 주세요.</p>}
                </div>
                <div className="button-container">
                    <button onClick={handleSubmit} className="community-post-editor__submit-button">
                        게시글 작성
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

export default CommunityPostEditor;