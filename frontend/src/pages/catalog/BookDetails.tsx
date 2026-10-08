import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { catalogService } from '../../services/catalogService';
import type { Book } from '../../types';

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
            // pulling the memberId
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
            <div className="flex flex-col items-center p-[30px] font-sans min-h-screen">
                <h2 className="text-2xl font-bold mb-4">No book selected.</h2>
                <button 
                    className="bg-[#fec999] rounded-full text-black text-lg font-semibold border-2 border-transparent px-[25px] py-2 transition-all duration-200 hover:bg-[#fda95c]/65 hover:text-[#333] hover:shadow-[inset_0_3px_8px_rgba(0,0,0,0.3)]" 
                    onClick={() => navigate(-1)}
                >
                    Return
                </button>
            </div>
        );
    }

    return (
        <div className="flex flex-col items-center p-[30px] font-sans min-h-screen">
            <div className="flex flex-col gap-5 w-[600px] bg-white/80 p-[30px] rounded-[10px]">
                <div className="flex items-center gap-5">
                    <span className="w-[180px] text-xl font-semibold">Book Name:</span>
                    <input className="flex-grow rounded-[30px] text-lg px-5 py-2.5 border border-[#ccc]" type="text" readOnly value={copyName} />
                </div>
                <div className="flex items-center gap-5">
                    <span className="w-[180px] text-xl font-semibold">Book Status:</span>
                    <input className="flex-grow rounded-[30px] text-lg px-5 py-2.5 border border-[#ccc]" type="text" readOnly value={status} />
                </div>
                <div className="flex items-center gap-5">
                    <span className="w-[180px] text-xl font-semibold">Shelf Location:</span>
                    <input className="flex-grow rounded-[30px] text-lg px-5 py-2.5 border border-[#ccc]" type="text" readOnly value={shelf} />
                </div>
                <div className="flex items-center gap-5">
                    <span className="w-[180px] text-xl font-semibold">Closest Return:</span>
                    <input className="flex-grow rounded-[30px] text-lg px-5 py-2.5 border border-[#ccc]" type="text" readOnly value={returnDate} />
                </div>
                <div className="flex items-center gap-5">
                    <span className="w-[180px] text-xl font-semibold">Book Summary:</span>
                    <textarea className="flex-grow text-lg px-5 py-2.5 border border-[#ccc] rounded-[15px] min-h-[120px] resize-none" readOnly value={selectedBook.summary || ''} />
                </div>
            </div>
        </div>
    );
};

export default BookDetails;