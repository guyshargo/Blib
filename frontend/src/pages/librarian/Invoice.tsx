import React, { useEffect, useState } from 'react';
import { invoiceService } from '../../services/invoiceService';
import type { InvoiceMessage } from '../../types';
import './Librarian.css';

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
        <div className="librarian-container">
            <h2 className="label-text" style={{ fontSize: '23px', alignSelf: 'center' }}>Invoice Messages</h2>
            
            <table className="data-table" style={{ marginTop: '0' }}>
                <thead>
                    <tr>
                        <th>Subject</th>
                        <th>Username</th>
                        <th>Name</th>
                        <th>Date</th>
                    </tr>
                </thead>
                <tbody>
                    {displayedInvoices.map(inv => (
                        <tr 
                            key={inv.msgId} 
                            onClick={() => handleInvoiceClick(inv)}
                            // Matches the -fx-background-color: #ADD8E6 for unread messages
                            style={{ backgroundColor: !inv.isRead ? '#ADD8E6' : undefined }}
                        >
                            <td>{inv.subject}</td>
                            <td>{inv.userName}</td>
                            <td>{inv.fullName || inv.userName}</td>
                            <td>{new Date(inv.date).toLocaleDateString()}</td>
                        </tr>
                    ))}
                </tbody>
            </table>

            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '20px', marginTop: '20px' }}>
                <span className="label-text">Show:</span>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <input 
                        type="checkbox" 
                        id="unreadFilter"
                        checked={showUnreadOnly} 
                        onChange={e => setShowUnreadOnly(e.target.checked)} 
                        style={{ width: '18px', height: '18px', cursor: 'pointer' }}
                    />
                    <label htmlFor="unreadFilter" className="label-text" style={{ cursor: 'pointer' }}>Unread Messages</label>
                </div>
            </div>

            <div className="search-header" style={{ gap: '15px', backgroundColor: 'transparent', boxShadow: 'none' }}>
                <div className="input-group">
                    <span className="label-text">From:</span>
                    <input className="text-field" readOnly value={selectedInvoice?.fullName || ''} />
                </div>
                <div className="input-group">
                    <span className="label-text">Subject:</span>
                    <input className="text-field" readOnly value={selectedInvoice?.subject || ''} />
                </div>
                <div className="input-group">
                    <span className="label-text">Date:</span>
                    <input className="text-field" readOnly value={selectedInvoice ? new Date(selectedInvoice.date).toLocaleDateString() : ''} />
                </div>
            </div>

            <div className="input-group" style={{ flex: 1 }}>
                <textarea 
                    readOnly 
                    className="text-field" 
                    style={{ minHeight: '150px', fontSize: '18px', resize: 'none' }} 
                    value={selectedInvoice?.content || ''}
                />
            </div>
        </div>
    );
};

export default Invoice;