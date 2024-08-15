// src/pages/CreatePostPage.tsx
import React from 'react';
import Header from '../components/Header';
import PostEditor from '../components/PostEditor';

const CreatePostPage = () => {
    const handlePostSubmit = (title: string, category: string, content: string, imageUrl: string) => {
        // 4개의 인자를 받아 처리
        console.log('게시글 작성 완료:', { title, category, content, imageUrl });
        // 여기서 백엔드로 게시글 데이터를 전송할 수 있음
    };

    return (
        <div>
            <Header />
            <h1>새 게시글 작성</h1>
            <PostEditor onSubmit={handlePostSubmit} /> {/* 4개의 인자를 받는 handlePostSubmit 전달 */}
        </div>
    );
};

export default CreatePostPage;
