import api from './api';
import type { InvoiceMessage } from '../types';

export const invoiceService = {
    getInvoices: async (): Promise<InvoiceMessage[]> => {
        const response = await api.get<InvoiceMessage[]>('/invoices/');
        return response.data;
    },
    markAsRead: async (messageId: number) => {
        await api.put(`/invoices/${messageId}/read-status`);
    }
};