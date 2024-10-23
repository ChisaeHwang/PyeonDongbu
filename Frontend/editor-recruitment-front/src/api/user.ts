import api from './axios';

export const getUserInfo = () => {
    return api.get('/api/member');
};