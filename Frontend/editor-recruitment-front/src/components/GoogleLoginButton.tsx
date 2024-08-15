import React from 'react';
import { GoogleOAuthProvider, GoogleLogin } from '@react-oauth/google';
import axios from 'axios';
import '../styles/GoogleLoginButton.css'; // 스타일 임포트

const GoogleLoginButton: React.FC = () => {

    const handleLoginSuccess = async (response: any) => {
        console.log('로그인 성공!', response);

        // response의 credential을 서버로 보내서 액세스 토큰 요청
        try {
            const serverResponse = await axios.post(
                'http://localhost:8080/api/login', // 서버의 구글 로그인 처리 엔드포인트
                { credential: response.credential },
                {
                    headers: { 'Content-Type': 'application/json' },
                    withCredentials: true, // 서버에서 리프레시 토큰 쿠키로 받을 수 있도록 설정
                }
            );

            const accessToken = serverResponse.data.data.accessToken;
            if (accessToken) {
                sessionStorage.setItem('access-token', accessToken); // 액세스 토큰을 세션 스토리지에 저장
            } else {
                console.error('Access token not found in server response');
            }
        } catch (error) {
            console.error('로그인 처리 중 오류 발생:', error);
        }
    };

    const handleLoginFailure = () => {
        console.log('로그인 실패!');
    };

    return (
        <GoogleOAuthProvider clientId={process.env.REACT_APP_GOOGLE_CLIENT_ID || ''}>
            <GoogleLogin
                onSuccess={handleLoginSuccess} // 구글 로그인 성공 시 처리
                onError={handleLoginFailure} // 구글 로그인 실패 시 처리
                useOneTap={false} 
                ux_mode="redirect"
            />
        </GoogleOAuthProvider>
    );
};

export default GoogleLoginButton;
