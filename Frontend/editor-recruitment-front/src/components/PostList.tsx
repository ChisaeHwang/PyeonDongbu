// src/components/PostList.tsx
import React from 'react';
import PostItem from './PostItem';
import '../styles/PostList.css'; // 스타일 임포트

const PostList = ({ posts }: { posts: { id: number, title: string, content: string }[] }) => {
    return (
        <div className="post-list">
            {posts.map((post) => (
                <PostItem key={post.id} title={post.title} content={post.content} />
            ))}
        </div>
    );
};

export default PostList;
