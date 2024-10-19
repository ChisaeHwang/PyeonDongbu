import { useState, useEffect } from 'react';
import axios from 'axios';

interface MemberDetailsRes {
  maxSubs: number;
  weeklyWorkload: number;
  videoTypes: string[];
  editedChannels: string[];
  currentChannels: string[];
  portfolio: string;
  skills: string[];
  remarks: string;
}

interface UserInfo {
  nickname: string;
  imageUrl: string;
  role: string;
  memberDetailsRes: MemberDetailsRes;
}

interface ApiResponse {
  code: string;
  message: string;
  data: UserInfo;
}

export const useAuth = () => {
  const [userInfo, setUserInfo] = useState<UserInfo | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const token = sessionStorage.getItem('access-token');
        if (!token) {
          setIsLoading(false);
          return;
        }
        const response = await axios.get<ApiResponse>('http://localhost:8080/api/member', {
          headers: {
            Authorization: `Bearer ${token}`
          },
          withCredentials: true
        });
        setUserInfo(response.data.data);
      } catch (error) {
        console.error('Failed to fetch user info:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchUserInfo();
  }, []);

  return { userInfo, isLoading };
};
