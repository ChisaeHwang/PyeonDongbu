import React from 'react';
import axios from 'axios';
import PostEditor from '../components/PostEditor';

const CreatePostPage = () => {
    const handlePostSubmit = async (title: string, content: string, images: string[], tagNames: string[], payments: any[], recruitmentPostDetailsReq: any) => {
        try {
            // 세션 스토리지에서 액세스 토큰 가져오기
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

            console.log('전송할 데이터:', requestData); // 전송할 데이터 로그 확인

            // API 요청 처리 (리프레시 토큰은 쿠키로 자동 전송됨)
            const response = await axios.post('http://localhost:8080/api/recruitment/posts', requestData, {
                headers: {
                    Authorization: `Bearer ${accessToken}`,  // 세션 스토리지에서 가져온 액세스 토큰
                },
                withCredentials: true, // 쿠키를 자동으로 포함하게 설정 (Cross-Origin 요청 시)
            });

            console.log('서버 응답:', response); // 서버 응답 확인
            alert('게시글이 성공적으로 작성되었습니다.');
        } catch (error) {
            console.error('게시글 작성 중 오류가 발생했습니다.', error);
            alert('게시글 작성 중 오류가 발생했습니다.');
        }
    };

    return (
        <div>
            <h1>새 게시글 작성</h1>
            <PostEditor onSubmit={handlePostSubmit} />
        </div>
    );
};

export default CreatePostPage;
