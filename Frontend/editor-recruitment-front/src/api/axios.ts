import axios from 'axios';
import { getApiBaseUrl } from '../utils/env';
import { getAccessToken } from '../utils/auth';

const api = axios.create({
    baseURL: getApiBaseUrl(),
    withCredentials: true,
});

api.interceptors.request.use((config) => {
    const token = getAccessToken();
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export default api;