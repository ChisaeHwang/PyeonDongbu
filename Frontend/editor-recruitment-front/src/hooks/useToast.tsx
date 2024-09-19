import { useCallback } from 'react';
import { toast, ToastOptions } from 'react-toastify';

const defaultOptions: ToastOptions = {
  position: "top-right",
  autoClose: 3000,
  hideProgressBar: false,
  closeOnClick: true,
  pauseOnHover: true,
  draggable: true,
};

export const useToast = () => {
  const showSuccessToast = useCallback((message: string, options?: ToastOptions) => {
    toast.success(message, { ...defaultOptions, ...options });
  }, []);

  const showErrorToast = useCallback((message: string, options?: ToastOptions) => {
    toast.error(message, { ...defaultOptions, ...options });
  }, []);

  const showInfoToast = useCallback((message: string, options?: ToastOptions) => {
    toast.info(message, { ...defaultOptions, ...options });
  }, []);

  const showWarningToast = useCallback((message: string, options?: ToastOptions) => {
    toast.warning(message, { ...defaultOptions, ...options });
  }, []);

  return {
    showSuccessToast,
    showErrorToast,
    showInfoToast,
    showWarningToast,
  };
};