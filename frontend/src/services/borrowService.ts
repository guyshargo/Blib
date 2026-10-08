import api from './api';
import type { ActivityRequest, BorrowedBook, BorrowRequest } from '../types';

export const borrowService = {
    getMemberBorrows: async (memberId: number): Promise<BorrowedBook[]> => {
        const response = await api.get<BorrowedBook[]>(`/borrows/member/${memberId}`);
        return response.data;
    },

    extendBorrow: async (memberId: number, copyId: number) => {
        const response = await api.put(`/borrows/member/${memberId}/book/${copyId}/extend`);
        return response.data;
    },

    addActivity: async (activityReq: ActivityRequest) => {
        await api.post('/activities/', activityReq);
    },

    getAvailableCopyById: async (bookId: number, memberId: number) => {
        const response = await api.get(`/borrows/book/${bookId}/available-copy/member/${memberId}`);
        return response.data;
    },

    getAvailableCopyByBarcode: async (barcode: string, memberId: number) => {
        const response = await api.get(`/borrows/barcode/${barcode}/available-copy/member/${memberId}`);
        return response.data;
    },

    getBookDetails: async (bookId: number) => {
        const response = await api.get(`/books/${bookId}`);
        return response.data;
    },

    commitBorrow: async (borrowReq: BorrowRequest) => {
        const response = await api.post('/borrows/', borrowReq);
        return response.data;
    }
};