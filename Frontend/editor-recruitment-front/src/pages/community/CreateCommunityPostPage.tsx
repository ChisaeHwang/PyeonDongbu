import React from 'react';
import { useNavigate } from 'react-router-dom';
import CommunityPostEditor from '../../components/CommunityPostEditor';
import api from '../../api/axios';
import { AxiosError } from 'axios';
import { useToast } from '../../hooks/useToast';
import { clearAccessToken } from '../../utils/auth';

const CreateCommunityPostPage: React.FC = () => {
    const navigate = useNavigate();
    const { showSuccessToast, showErrorToast } = useToast();

    const handlePostSubmit = async (title: string, content: string, tags: string[]) => {
        try {
            const requestData = {
                title,
                content,
                tagNames: tags, 
            };

            await api.post('/api/community/posts', requestData);

            showSuccessToast('게시글이 성공적으로 작성되었습니다.');
            navigate('/community');
        } catch (error) {
            console.error('게시글 작성 중 오류가 발생했습니다.', error);
            
            if (error instanceof AxiosError && error.response?.status === 401) {
                showErrorToast('로그인 세션이 만료되었습니다. 다시 로그인해 주세요.');
                clearAccessToken();
                navigate('/');
            } else {
                showErrorToast('게시글 작성 중 오류가 발생했습니다.');
            }
        }
    };

    return (
        <div className="create-community-post-page">
            <h1>커뮤니티 게시글 작성</h1>
            <CommunityPostEditor onSubmit={handlePostSubmit} />
        </div>
    );
};

export default CreateCommunityPostPage;
