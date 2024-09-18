import React from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import CommunityPostEditor from '../../components/CommunityPostEditor';

const CreateCommunityPostPage: React.FC = () => {
    const navigate = useNavigate();

    const handlePostSubmit = async (title: string, content: string, tag: string) => {
        try {
            const accessToken = sessionStorage.getItem('access-token');
            if (!accessToken) {
                throw new Error('액세스 토큰이 없습니다.');
            }

            const requestData = {
                title,
                content,
                tag,
            };

            await axios.post('http://localhost:8080/api/community/posts', requestData, {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                },
                withCredentials: true,
            });

            navigate('/community'); // 커뮤니티 탭으로 이동
        } catch (error) {
            console.error('게시글 작성 중 오류가 발생했습니다.', error);
            
            if (axios.isAxiosError(error) && error.response?.status === 401) {
                alert('로그인 세션이 만료되었습니다. 다시 로그인해 주세요.');
                sessionStorage.removeItem('access-token'); // 만료된 토큰 제거
                navigate('/'); // 메인 페이지로 이동
            } else {
                alert('게시글 작성 중 오류가 발생했습니다.');
            }
        }
    };

    return (
        <div className="create-community-post-page">
            <CommunityPostEditor onSubmit={handlePostSubmit} />
        </div>
    );
};

export default CreateCommunityPostPage;