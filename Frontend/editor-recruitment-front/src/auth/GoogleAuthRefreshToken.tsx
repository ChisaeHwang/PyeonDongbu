import React from "react";
import axios from "axios";

const GoogleAuthRefreshToken: React.FC = () => {
  const renewAccessToken = async () => {
    try {
      // axios 인스턴스 생성
      const axiosInstance = axios.create({
        baseURL: "http://localhost:8080/api",
        withCredentials: true,
      });

      // sessionStorage에서 액세스 토큰을 가져옴
      const token = sessionStorage.getItem("access-token");

      // axios 인스턴스를 사용하여 요청
      const response = await axiosInstance.post(
        "/token",
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      const accessToken = response.data.data.accessToken; // 갱신된 액세스 토큰
      console.log("New access token:", response.data.data.accessToken);

      // 갱신된 액세스 토큰을 sessionStorage에 저장
      sessionStorage.setItem("access-token", accessToken);
      alert("Access token renewed successfully!");
    } catch (error) {
      console.error("Error renewing access token:", error);
      alert("Failed to renew access token.");
    }
  };

  return (
    <div style={{ padding: "20px" }}>
      <h2>토큰 갱신 테스트</h2>
      <button onClick={renewAccessToken}>토큰 갱신</button>
    </div>
  );
};

export default GoogleAuthRefreshToken;
