import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";

const GoogleAuthRedirectHandler: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false); // 중복 요청 방지를 위한 상태

  useEffect(() => {
    // 코드를 얻기 위한 로직은 여전히 한 번만 실행됩니다.
    const searchParams = new URLSearchParams(location.search);
    const code = searchParams.get("code");

    const sendAuthCodeToServer = async (code: string) => {
      if (isSubmitting) return; // 중복 요청 방지
      setIsSubmitting(true); // 요청 시작

      try {
        const response = await axios.post(
          "http://localhost:8080/api/login",
          { code },
          {
            headers: { "Content-Type": "application/json" },
            withCredentials: true,
          }
        );
        const accessToken = response.data.data.accessToken;
        if (accessToken) {
          sessionStorage.setItem("access-token", accessToken);
          navigate("/"); // 로그인 성공 시 홈으로 이동
        } else {
          setError("Access token not found in response");
        }
      } catch (error) {
        if (axios.isAxiosError(error) && error.response) {
          setError(error.response.data.error || error.message);
        } else {
          setError("An unknown error occurred");
        }
      } finally {
        setIsSubmitting(false); // 요청 완료
      }
    };

    if (code && !isSubmitting) {
      sendAuthCodeToServer(code);
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [location, navigate]); // isSubmitting을 의존성 배열에서 제거

  return (
    <div>
      {error ? (
        <p></p>
      ) : (
        <p></p>
      )}
    </div>
  );
};

export default GoogleAuthRedirectHandler;