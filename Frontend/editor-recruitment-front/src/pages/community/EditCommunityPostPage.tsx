import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import CommunityPostEditor from '../../components/CommunityPostEditor';
import { useToast } from '../../hooks/useToast';

const EditCommunityPostPage: React.FC = () => {
    const { postId } = useParams<{ postId: string }>();
    const [initialData, setInitialData] = useState<{ title: string; content: string; tags: string[] } | null>(null);
    const navigate = useNavigate();
    const { showSuccessToast, showErrorToast } = useToast();

    useEffect(() => {
        const fetchPostData = async () => {
            try {
                const accessToken = sessionStorage.getItem('access-token');
                const response = await axios.get(`http://localhost:8080/api/community/posts/${postId}`, {
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                        'Content-Type': 'application/json',
                    },
                    withCredentials: true
                });
                setInitialData({
                    title: response.data.data.title,
                    content: response.data.data.content,
                    tags: response.data.data.tagNames
                });
            } catch (error) {
                console.error('게시글 데이터를 불러오는데 실패했습니다.', error);
                showErrorToast('게시글 데이터를 불러오는데 실패했습니다.');
            }
        };

        fetchPostData();
    }, [postId, showErrorToast]);

    const handleSubmit = async (title: string, content: string, tags: string[]) => {
        try {
            const accessToken = sessionStorage.getItem('access-token');
            await axios.put(`http://localhost:8080/api/community/posts/${postId}`, 
                { title, content, tagNames: tags },
                {
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                        'Content-Type': 'application/json',
                    },
                    withCredentials: true
                }
            );
            showSuccessToast('게시글이 성공적으로 수정되었습니다.');
            navigate(`/community/post/${postId}`);
        } catch (error) {
            console.error('게시글 수정 중 오류가 발생했습니다.', error);
            showErrorToast('게시글 수정 중 오류가 발생했습니다.');
        }
    };

    if (!initialData) {
        return <div>로딩 중...</div>;
    }

    return (
        <div className="edit-community-post-page">
            <h1>커뮤니티 게시글 수정</h1>
            <CommunityPostEditor 
                onSubmit={handleSubmit} 
                initialData={initialData}
            />
        </div>
    );
};

export default EditCommunityPostPage;