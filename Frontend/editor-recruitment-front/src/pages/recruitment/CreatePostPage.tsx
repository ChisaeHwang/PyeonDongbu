import React from 'react';
import axios from 'axios';
import PostEditor from '../../components/PostEditor';
import { useNavigate } from 'react-router-dom';

const CreatePostPage = () => {
    const navigate = useNavigate();

    const handlePostSubmit = async (title: string, content: string, images: string[], tagNames: string[], payments: any[], recruitmentPostDetailsReq: any) => {
        try {
            const accessToken = sessionStorage.getItem('access-token');
            if (!accessToken) {
                throw new Error('액세스 토큰이 없습니다.');
            }

            const requestData = {
                title,
                content,
                images,
                tagNames,
                payments,
                recruitmentPostDetailsReq,
            };

            const response = await axios.post('http://localhost:8080/api/recruitment/posts', requestData, {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                    'Content-Type': 'application/json',
                },
                withCredentials: true
            });

            if (response.status === 200) {
                alert('게시글이 성공적으로 작성되었습니다.');
                navigate('/');
            }
        } catch (error) {
            console.error('게시글 작성 중 오류가 발생했습니다.', error);
            alert('게시글 작성 중 오류가 발생했습니다.');
        }
    };

    return (
        <div>
            <PostEditor onSubmit={handlePostSubmit} />
        </div>
    );
};

export default CreatePostPage;
