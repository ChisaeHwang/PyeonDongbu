import React from 'react';
import { GoogleOAuthProvider, GoogleLogin } from '@react-oauth/google';
import '../styles/GoogleLoginButton.css'; // 스타일 임포트

const GoogleLoginButton = () => {
    const handleLoginSuccess = (response: any) => {
        console.log('로그인 성공!', response);
    };

    const handleLoginFailure = () => {
        console.log('로그인 실패!');
    };

    return (
        <GoogleOAuthProvider clientId="YOUR_GOOGLE_CLIENT_ID">
            <GoogleLogin
                onSuccess={handleLoginSuccess}
                onError={handleLoginFailure}
                containerProps={{
                    className: 'google-login-button',
                }}
            />
        </GoogleOAuthProvider>
    );
};

export default GoogleLoginButton;
