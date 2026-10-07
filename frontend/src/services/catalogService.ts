import api from './api';
import type { Book, CopyOfBook, BorrowedBook } from '../types';

export const catalogService = {
    searchBooks: async (name: string, genre: string, freeText: string): Promise<Book[]> => {
        const response = await api.get<Book[]>('/books/search', {
            params: {
                name: name || 'is empty',
                genre: genre || 'is empty',
                freeText: freeText || 'is empty'
            }
        });
        return response.data;
    },

    getAvailableCopy: async (bookId: number, memberId: number): Promise<CopyOfBook> => {
        const response = await api.get<CopyOfBook>(`/borrows/book/${bookId}/available-copy/subscriber/${memberId}`);
        return response.data;
    },

    getClosestReturnDate: async (bookId: number): Promise<BorrowedBook> => {
        const response = await api.get<BorrowedBook>(`/borrows/book/${bookId}/closest-return-date`);
        return response.data;
    }
};