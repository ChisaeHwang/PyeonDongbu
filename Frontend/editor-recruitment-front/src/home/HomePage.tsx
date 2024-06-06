// HomePage.tsx
import React from "react";

const HomePage: React.FC = () => {
  // 구글 OAuth2 로그인 페이지로 리다이렉트하는 함수
  const handleLogin = () => {
    window.location.href = `http://localhost:8080/auth/google`;
  };

  return (
    <div>
      <h1>Welcome to the Home Page</h1>
      <button onClick={handleLogin}>Login with Google</button>
    </div>
  );
};

export default HomePage;
