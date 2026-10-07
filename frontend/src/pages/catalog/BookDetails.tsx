import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { catalogService } from '../../services/catalogService';
import type { Book } from '../../types';
import './Catalog.css';

const BookDetails: React.FC = () => {
    const location = useLocation();
    const navigate = useNavigate();
    
    // Retrieves the book passed from the SearchCatalog table click
    const selectedBook = location.state?.selectedBook as Book;

    const [status, setStatus] = useState('Checking...');
    const [shelf, setShelf] = useState('-');
    const [returnDate, setReturnDate] = useState('-');
    const [copyName, setCopyName] = useState(selectedBook?.title || '');

    useEffect(() => {
        if (!selectedBook) return;

        const fetchAvailability = async () => {
            // Replicates pulling the memberId from SessionManager
            const userStr = localStorage.getItem('currentUser');
            const userType = localStorage.getItem('userType');
            let memberId = 0;

            if (userStr && userType === 'MEMBER') {
                memberId = JSON.parse(userStr).membershipNumber;
            }

            try {
                // Try to fetch an available copy
                const copy = await catalogService.getAvailableCopy(selectedBook.bookId, memberId);
                setCopyName(copy.CopyOfBookName);
                setStatus('Available');
                setShelf(copy.shelfLocation);
                setReturnDate('-');
            } catch (error: any) {
                // If 404, fallback to closest return date
                try {
                    const closest = await catalogService.getClosestReturnDate(selectedBook.bookId);
                    setCopyName(closest.title); // Using title from BorrowedBook
                    setStatus('Borrowed');
                    setShelf('-');
                    setReturnDate(closest.returnDate);
                } catch (fallbackError) {
                    setStatus('No copies found');
                    setShelf('-');
                    setReturnDate('-');
                }
            }
        };

        fetchAvailability();
    }, [selectedBook]);

    if (!selectedBook) {
        return (
            <div className="catalog-container">
                <h2>No book selected.</h2>
                <button className="menu-button" onClick={() => navigate(-1)}>Return</button>
            </div>
        );
    }

    return (
        <div className="catalog-container">
            <div className="details-box">
                <div className="detail-row">
                    <span>Book Name:</span>
                    <input type="text" readOnly value={copyName} />
                </div>
                <div className="detail-row">
                    <span>Book Status:</span>
                    <input type="text" readOnly value={status} />
                </div>
                <div className="detail-row">
                    <span>Shelf Location:</span>
                    <input type="text" readOnly value={shelf} />
                </div>
                <div className="detail-row">
                    <span>Closest Return:</span>
                    <input type="text" readOnly value={returnDate} />
                </div>
                <div className="detail-row">
                    <span>Book Summary:</span>
                    <textarea readOnly value={selectedBook.summary || ''} />
                </div>
                
                <button 
                    className="menu-button" 
                    style={{ width: '150px', alignSelf: 'center', marginTop: '10px' }} 
                    onClick={() => navigate(-1)} // Navigates back to the previous page
                >
                    Return
                </button>
            </div>
        </div>
    );
};

export default BookDetails;