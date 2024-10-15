import { toast } from 'react-toastify';

export const handleLoginExpiration = () => {
  localStorage.removeItem('access-token');
  
  toast.error('로그인이 만료되었습니다. 다시 로그인해 주세요.', {
    position: "top-center",
    autoClose: 5000,
    hideProgressBar: false,
    closeOnClick: true,
    pauseOnHover: true,
    draggable: true,
  });

  window.location.href = '/';
};