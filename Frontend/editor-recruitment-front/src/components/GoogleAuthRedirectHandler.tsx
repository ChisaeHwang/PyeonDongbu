import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import { getAccessToken, setAccessToken } from "../utils/auth";
import { getApiBaseUrl } from "../utils/env";

const GoogleAuthRedirectHandler: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    console.log("API Base URL:", getApiBaseUrl());
    const searchParams = new URLSearchParams(location.search);
    const code = searchParams.get("code");

    const sendAuthCodeToServer = async (code: string) => {
      if (isSubmitting) return;
      setIsSubmitting(true);

      try {
        const response = await axios.post(
          `${getApiBaseUrl()}/api/login`,
          { code },
          {
            headers: { "Content-Type": "application/json" },
            withCredentials: true,
          }
        );
        const accessToken = response.data.data.accessToken;
        if (accessToken) {
          setAccessToken(accessToken); // 비공개 변수에 토큰 저장
          console.log("Access Token:", accessToken);
          console.log(getAccessToken());
          
          navigate("/"); 
          window.location.reload();
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
        setIsSubmitting(false);
      }
    };

    if (code && !isSubmitting) {
      sendAuthCodeToServer(code);
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [location, navigate]);

  return (
    <div>
      {error ? (
        <p>Error: {error}</p>
      ) : (
        <p></p>
      )}
    </div>
  );
};

export default GoogleAuthRedirectHandler;
