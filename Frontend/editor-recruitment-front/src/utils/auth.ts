export const setAccessToken = (token: string) => {
  sessionStorage.setItem('access-token', token);
};

export const getAccessToken = () => {
  return sessionStorage.getItem('access-token');
};

export const clearAccessToken = () => {
  sessionStorage.removeItem('access-token');
};
