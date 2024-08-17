import React from 'react';
import '../styles/PostItem.css'; // 스타일 임포트

const PostItem = ({ title, content }: { title: string, content: string }) => {
    return (
        <div className="post-card">
            <h2 className="post-title">{title}</h2>
            <p className="post-content">{content}</p>
        </div>
    );
};

export default PostItem;
