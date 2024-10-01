import React from 'react';
import axios, { AxiosError } from 'axios';
import PostEditor from '../../components/PostEditor';
import { useNavigate } from 'react-router-dom';
import { useToast } from '../../hooks/useToast';

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

interface ExceptionResponse {
    status: number;
    message: string;
}

const CreatePostPage = () => {
    const navigate = useNavigate();
    const { showSuccessToast, showErrorToast, showWarningToast } = useToast();

    const handlePostSubmit = async (
        title: string,
        content: string,
        imageUrl: string,
        tagNames: string[],
        payment: Payment,
        recruitmentPostDetailsReq: any
    ) => {
        try {
            const accessToken = sessionStorage.getItem('access-token');
            if (!accessToken) {
                throw new Error('액세스 토큰이 없습니다.');
            }

            const requestData = {
                title,
                content,
                imageUrl,
                tagNames,
                payment,
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
                showSuccessToast('게시글이 성공적으로 작성되었습니다.');
                navigate('/');
            }
        } catch (error) {
            if (axios.isAxiosError(error)) {
                const axiosError = error as AxiosError<ExceptionResponse>;
                if (axiosError.response) {
                    if (axiosError.response.status === 400) {
                        const errorMessage = axiosError.response.data?.message || '모든 내용을 채워주십시오.';
                        showWarningToast(errorMessage);
                    } else if (axiosError.response.status === 401) {
                        showErrorToast('로그인 세션이 만료되었습니다. 다시 로그인해 주세요.');
                        sessionStorage.removeItem('access-token');
                        navigate('/login');
                    } else {
                        showErrorToast('게시글 작성 중 오류가 발생했습니다.');
                    }
                    console.error('에러 상세 정보:', axiosError.response.data);
                } else if (axiosError.request) {
                    showErrorToast('서버로부터 응답을 받지 못했습니다. 네트워크 연결을 확인해주세요.');
                } else {
                    showErrorToast('요청 설정 중 오류가 발생했습니다.');
                }
            } else {
                showErrorToast('알 수 없는 오류가 발생했습니다.');
            }
            console.error('게시글 작성 중 오류가 발생했습니다.', error);
        }
    };

    return (
        <div>
            <PostEditor onSubmit={handlePostSubmit} />
        </div>
    );
};

export default CreatePostPage;
