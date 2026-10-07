import React, { useState } from 'react';
import { librarianService } from '../../services/librarianService';
import { borrowService } from '../../services/borrowService';
import type { BorrowedBook, Member } from '../../types';
import './Librarian.css';

const ReturnBook: React.FC = () => {
    const [memberIdInput, setMemberIdInput] = useState('');
    const [member, setMember] = useState<Member | null>(null);
    const [borrows, setBorrows] = useState<BorrowedBook[]>([]);

    const handleSearch = async () => {
        if (!memberIdInput.trim()) return;
        try {
            const memberData = await librarianService.getMember(parseInt(memberIdInput));
            setMember(memberData);
            
            const borrowsData = await borrowService.getMemberBorrows(memberData.id);
            setBorrows(borrowsData);
            
            if (borrowsData.length === 0) {
                alert("This member has no borrowed books.");
            }
        } catch (error) {
            setMember(null);
            setBorrows([]);
            alert("The member does not exist or has no books.");
        }
    };

    const handleReturn = async (book: BorrowedBook) => {
        if (!window.confirm("Are you sure you want to return this book?")) return;

        try {
            const returnDate = new Date();
            const dueDate = new Date(book.returnDate);
            
            const oneWeekLate = new Date(dueDate);
            oneWeekLate.setDate(oneWeekLate.getDate() + 7);
            
            if (returnDate > oneWeekLate) {
                await librarianService.updateFreezeStatus(book.memberId, "Frozen", returnDate.toISOString().split('T')[0]);
            }

            await librarianService.returnBook(book.copyOfBookId, book.memberId);
            
            const isLate = returnDate > dueDate;
            const diffDays = Math.ceil((returnDate.getTime() - dueDate.getTime()) / (1000 * 3600 * 24));
            const activityDesc = isLate 
                ? `LATE_BOOK_RETURN,returned ${book.title} late by ${diffDays} days`
                : `returning,return ${book.title}`;

            await borrowService.addActivity({
                membershipNumber: book.memberId,
                activityType: activityDesc.split(',')[0],
                description: activityDesc
            });

            alert("The book has been successfully returned.");
            setBorrows(prev => prev.filter(b => b.copyOfBookId !== book.copyOfBookId));
        } catch (error) {
            alert("The book could not be returned.");
        }
    };

    return (
        <div className="librarian-container">
            <h2>Return Book</h2>
            <div className="search-header">
                <div className="input-group">
                    <span className="label-text">Membership Number:</span>
                    <input 
                        className="text-field" 
                        value={memberIdInput} 
                        onChange={e => setMemberIdInput(e.target.value)} 
                    />
                </div>
                <button className="menu-button" onClick={handleSearch}>Find</button>
                <div className="input-group" style={{ marginLeft: '20px' }}>
                    <span className="label-text">Member name:</span>
                    <input className="text-field" readOnly value={member?.fullName || ''} />
                </div>
            </div>

            <table className="data-table">
                <thead>
                    <tr>
                        <th>Copy of book ID</th>
                        <th>Book Name</th>
                    </tr>
                </thead>
                <tbody>
                    {borrows.map(book => (
                        <tr key={book.copyOfBookId} onClick={() => handleReturn(book)}>
                            <td>{book.copyOfBookId}</td>
                            <td>{book.title}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default ReturnBook;