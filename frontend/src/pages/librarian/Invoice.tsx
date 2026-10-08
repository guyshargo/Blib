import React, { useEffect, useState } from 'react';
import { invoiceService } from '../../services/invoiceService';
import type { InvoiceMessage } from '../../types';

const Invoice: React.FC = () => {
    const [invoices, setInvoices] = useState<InvoiceMessage[]>([]);
    const [selectedInvoice, setSelectedInvoice] = useState<InvoiceMessage | null>(null);
    const [showUnreadOnly, setShowUnreadOnly] = useState(false);

    useEffect(() => {
        loadInvoices();
    }, []);

    const loadInvoices = async () => {
        try {
            const data = await invoiceService.getInvoices();
            setInvoices(data);
        } catch (error) {
            console.error("Failed to load invoices", error);
        }
    };

    const handleInvoiceClick = async (invoice: InvoiceMessage) => {
        setSelectedInvoice(invoice);
        
        if (!invoice.isRead) {
            try {
                await invoiceService.markAsRead(invoice.msgId);
                await loadInvoices(); // Refresh the list to remove the unread highlight
            } catch (error) {
                console.error("Failed to mark as read", error);
            }
        }
    };

    const displayedInvoices = showUnreadOnly 
        ? invoices.filter(inv => !inv.isRead) 
        : invoices;

    return (
        <div className="flex flex-col p-[30px] font-sans min-h-screen gap-5">
            <h2 className="text-[23px] font-bold text-[#333] self-center">Invoice Messages</h2>
            
            <table className="w-full bg-[#fff8f2] rounded-[5px] border-collapse font-semibold">
                <thead>
                    <tr>
                        <th className="bg-[#fec999] p-3 text-left">Subject</th>
                        <th className="bg-[#fec999] p-3 text-left">Username</th>
                        <th className="bg-[#fec999] p-3 text-left">Name</th>
                        <th className="bg-[#fec999] p-3 text-left">Date</th>
                    </tr>
                </thead>
                <tbody>
                    {displayedInvoices.map(inv => (
                        <tr 
                            key={inv.msgId} 
                            onClick={() => handleInvoiceClick(inv)}
                            className={!inv.isRead ? 'bg-[#ADD8E6] hover:bg-[#9bc2ce] cursor-pointer' : 'odd:bg-[#fdf6f0] even:bg-[#fee2cd] hover:bg-[#f4e2e1] cursor-pointer'}
                        >
                            <td className="p-3 border-b border-[#f6e6d8]">{inv.subject}</td>
                            <td className="p-3 border-b border-[#f6e6d8]">{inv.userName}</td>
                            <td className="p-3 border-b border-[#f6e6d8]">{inv.fullName || inv.userName}</td>
                            <td className="p-3 border-b border-[#f6e6d8]">{new Date(inv.date).toLocaleDateString()}</td>
                        </tr>
                    ))}
                </tbody>
            </table>

            <div className="flex items-center justify-center gap-5 mt-5">
                <span className="text-base font-bold text-[#333]">Show:</span>
                <div className="flex items-center gap-2">
                    <input 
                        type="checkbox" 
                        id="unreadFilter"
                        checked={showUnreadOnly} 
                        onChange={e => setShowUnreadOnly(e.target.checked)} 
                        className="w-[18px] h-[18px] cursor-pointer"
                    />
                    <label htmlFor="unreadFilter" className="text-base font-bold text-[#333] cursor-pointer">Unread Messages</label>
                </div>
            </div>

            <div className="flex items-end gap-[15px] bg-transparent">
                <div className="flex flex-col gap-1">
                    <span className="text-base font-bold text-[#333]">From:</span>
                    <input className="rounded-[5px] text-sm px-[15px] py-2.5 border border-[#ccc] outline-none min-w-[200px] bg-[#f5f5f5] text-[#666] cursor-not-allowed" readOnly value={selectedInvoice?.fullName || ''} />
                </div>
                <div className="flex flex-col gap-1">
                    <span className="text-base font-bold text-[#333]">Subject:</span>
                    <input className="rounded-[5px] text-sm px-[15px] py-2.5 border border-[#ccc] outline-none min-w-[200px] bg-[#f5f5f5] text-[#666] cursor-not-allowed" readOnly value={selectedInvoice?.subject || ''} />
                </div>
                <div className="flex flex-col gap-1">
                    <span className="text-base font-bold text-[#333]">Date:</span>
                    <input className="rounded-[5px] text-sm px-[15px] py-2.5 border border-[#ccc] outline-none min-w-[200px] bg-[#f5f5f5] text-[#666] cursor-not-allowed" readOnly value={selectedInvoice ? new Date(selectedInvoice.date).toLocaleDateString() : ''} />
                </div>
            </div>

            <div className="flex flex-col gap-1 flex-1">
                <textarea 
                    readOnly 
                    className="rounded-[5px] px-[15px] py-2.5 border border-[#ccc] outline-none bg-[#f5f5f5] text-[#666] cursor-not-allowed min-h-[150px] text-[18px] resize-none" 
                    value={selectedInvoice?.content || ''}
                />
            </div>
        </div>
    );
};

export default Invoice;