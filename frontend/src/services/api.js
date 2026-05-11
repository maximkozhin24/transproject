// src/services/api.js
import axios from 'axios';

// Используйте правильный URL для API
const API_BASE_URL = process.env.NODE_ENV === 'production' ? '/api' : 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Добавляем перехватчик для логирования ошибок
api.interceptors.response.use(
    response => response,
    error => {
        console.error('API Error:', error.response?.data || error.message);
        return Promise.reject(error);
    }
);

// API endpoints
export const cargoApi = {
    getAll: () => api.get('/cargo'),
    getById: (id) => api.get(`/cargo/${id}`),
    create: (data) => api.post('/cargo', data),
    update: (id, data) => api.put(`/cargo/${id}`, data),
    delete: (id) => api.delete(`/cargo/${id}`),
};

export const clientApi = {
    getAll: () => api.get('/clients'),
    getById: (id) => api.get(`/clients/${id}`),
    create: (data) => api.post('/clients', data),
    update: (id, data) => api.put(`/clients/${id}`, data),
    delete: (id) => api.delete(`/clients/${id}`),
};

export const orderApi = {
    getAll: (page = 0, size = 10) => api.get('/orders', { params: { page, size } }),
    getAllOptimized: () => api.get('/orders/optimized'),
    getById: (id) => api.get(`/orders/${id}`),
    create: (data) => api.post('/orders', data),
    update: (id, data) => api.put(`/orders/${id}`, data),
    delete: (id) => api.delete(`/orders/${id}`),
    getByCargo: (cargoName) => api.get('/orders/by-cargo', { params: { cargoName } }),
};

export const routeApi = {
    getAll: () => api.get('/routes'),
    getById: (id) => api.get(`/routes/${id}`),
    create: (data) => api.post('/routes', data),
    update: (id, data) => api.put(`/routes/${id}`, data),
    delete: (id) => api.delete(`/routes/${id}`),
};

export const vehicleApi = {
    getAll: () => api.get('/vehicles'),
    getById: (id) => api.get(`/vehicles/${id}`),
    create: (data) => api.post('/vehicles', data),
    update: (id, data) => api.put(`/vehicles/${id}`, data),
    delete: (id) => api.delete(`/vehicles/${id}`),
    assign: (data) => api.post('/vehicles/assign', data),
};