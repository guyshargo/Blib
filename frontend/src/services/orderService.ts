import api from './api';
import type { OrderedBook, Book, OrderRequest, ActivityRequest } from '../types';

export const orderService = {
    getMemberOrders: async (memberId: number): Promise<OrderedBook[]> => {
        const response = await api.get<OrderedBook[]>(`/orders/member/${memberId}`);
        return response.data;
    },

    searchToOrder: async (bookName: string, bookId: number): Promise<Book[]> => {
        const response = await api.get<Book[]>('/orders/search-to-order', {
            params: { bookName, bookId }
        });
        return response.data;
    },

    searchToCancel: async (bookId: number): Promise<Book[]> => {
        const response = await api.get<Book[]>('/orders/search-to-cancel', {
            params: { bookId }
        });
        return response.data;
    },

    orderBook: async (orderReq: OrderRequest) => {
        const response = await api.post('/orders/', orderReq);
        return response.data;
    },

    cancelOrder: async (orderId: number, bookName: string, arrivalStatus: string) => {
        const response = await api.delete(`/orders/${orderId}`, {
            params: { bookName, arrivalStatus }
        });
        return response.data;
    },

    updateOrderStatus: async (bookId: number, numberOfOrders: number, increase: boolean) => {
        await api.put('/orders/status', null, {
            params: { bookId, numberOfOrders, increase }
        });
    },

    addActivity: async (activityReq: ActivityRequest) => {
        await api.post('/activities/', activityReq);
    }
};