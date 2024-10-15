import { useEffect, useCallback } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import { useToast } from './useToast';

const useAxiosInterceptor = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { showErrorToast } = useToast();

  const handleLogout = useCallback(() => {
    sessionStorage.removeItem('access-token');
    showErrorToast('세션이 만료되었습니다. 다시 로그인해주세요.');
    if (location.pathname !== '/') {
      navigate('/', { replace: true });
    } else {
      window.location.reload();
    }
  }, [navigate, showErrorToast, location]);

  useEffect(() => {
    const interceptor = axios.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response && (error.response.status === 401 || (error.response.status === 400 && error.response.data.code === 9104))) {
          handleLogout();
        }
        return Promise.reject(error);
      }
    );

    return () => {
      axios.interceptors.response.eject(interceptor);
    };
  }, [handleLogout]);
};

export default useAxiosInterceptor;
