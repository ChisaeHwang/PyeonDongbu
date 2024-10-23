import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import PostEditor from '../../components/PostEditor';
import { useToast } from '../../hooks/useToast';
import api from '../../api/axios';
import { AxiosError } from 'axios';

enum PaymentType {
    PER_HOUR = 'PER_HOUR',
    PER_PROJECT = 'PER_PROJECT',
    MONTHLY_SALARY = 'MONTHLY_SALARY',
    NEGOTIABLE = 'NEGOTIABLE'
}

interface Payment {
    type: PaymentType;
    amount: number;
}

const EditPostPage: React.FC = () => {
    const { postId } = useParams<{ postId: string }>();
    const [initialData, setInitialData] = useState<any>(null);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();
    const { showSuccessToast, showErrorToast } = useToast();

    useEffect(() => {
        const fetchPostData = async () => {
            try {
                const response = await api.get(`/api/recruitment/posts/${postId}/edit`);
                setInitialData(response.data.data);
                setLoading(false);
            } catch (error) {
                console.error('게시글 데이터를 불러오는데 실패했습니다.', error);
                showErrorToast('게시글 데이터를 불러오는데 실패했습니다.');
                navigate('/');
            }
        };

        fetchPostData();
    }, [postId, navigate, showErrorToast]);

    const handlePostSubmit = async (
        title: string,
        content: string,
        imageUrl: string,
        tagNames: string[],
        payment: Payment,
        recruitmentPostDetailsReq: any
    ) => {
        try {
            const requestData = {
                title,
                content,
                imageUrl,
                tagNames,
                payment,
                recruitmentPostDetailsReq,
            };

            await api.put(`/api/recruitment/posts/${postId}`, requestData);

            showSuccessToast('게시글이 성공적으로 수정되었습니다.');
            navigate(`/post/${postId}`);
        } catch (error) {
            console.error('게시글 수정 중 오류가 발생했습니다.', error);
            if (error instanceof AxiosError) {
                if (error.response?.status === 401) {
                    showErrorToast('로그인 세션이 만료되었습니다. 다시 로그인해 주세요.');
                    navigate('/login');
                } else {
                    showErrorToast('게시글 수정 중 오류가 발생했습니다.');
                }
            } else {
                showErrorToast('알 수 없는 오류가 발생했습니다.');
            }
        }
    };

    if (loading) {
        return <div>로딩 중...</div>;
    }

    return (
        <div>
            <h1>게시글 수정</h1>
            <PostEditor onSubmit={handlePostSubmit} initialData={initialData} />
        </div>
    );
};

export default EditPostPage;
