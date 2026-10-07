import React, { useEffect, useState } from 'react';
import { borrowService } from '../../services/borrowService';
import type { BorrowedBook, Member } from '../../types';
import './Member.css';

const ExtendBorrow: React.FC = () => {
    const [member, setMember] = useState<Member | null>(null);
    const [borrowedBooks, setBorrowedBooks] = useState<BorrowedBook[]>([]);
    
    const [searchName, setSearchName] = useState('');
    const [isOverdueOnly, setIsOverdueOnly] = useState(false);
    const [selectedCopyId, setSelectedCopyId] = useState<string>('');

    useEffect(() => {
        const userStr = localStorage.getItem('currentUser');
        if (userStr) {
            const member = JSON.parse(userStr);
            setMember(member);
            loadBorrows(member.id);
        }
    }, []);

    const loadBorrows = async (memberId: number) => {
        try {
            const data = await borrowService.getMemberBorrows(memberId);
            setBorrowedBooks(data);
        } catch (error) {
            console.error("Failed to load borrowed books", error);
        }
    };

    const filteredBooks = borrowedBooks.filter(book => {
        const matchesName = book.title.toLowerCase().includes(searchName.toLowerCase());
        
        let matchesOverdue = true;
        if (isOverdueOnly) {
            const returnDate = new Date(book.returnDate);
            returnDate.setHours(0, 0, 0, 0);
            const now = new Date();
            now.setHours(0, 0, 0, 0);
            matchesOverdue = now > returnDate;
        }

        return matchesName && matchesOverdue;
    });

    const handleExtend = async () => {
        if (!selectedCopyId) {
            alert("Extension Request: Must insert the ID of the book you want to extend the return date");
            return;
        }

        const selectedBook = borrowedBooks.find(b => b.copyOfBookId.toString() === selectedCopyId);
        
        if (!selectedBook || !member) {
            alert("Extension Request: There is no book with the inserted ID");
            return;
        }

        // Exact local validations from ExtendBorrowController.java[cite: 49]
        const returnDate = new Date(selectedBook.returnDate);
        returnDate.setHours(0, 0, 0, 0);
        
        const now = new Date();
        now.setHours(0, 0, 0, 0);

        const oneWeekBefore = new Date(returnDate);
        oneWeekBefore.setDate(oneWeekBefore.getDate() - 7);

        if (now > returnDate) {
            alert("Extension Request: Must return book, please contact a librarian from the library");
            return;
        }

        if (now < oneWeekBefore) {
            alert("Extension Request: Borrowed book must be at least one week till return date");
            return;
        }

        if (member.freezeStatus === 'Frozen') {
            alert("Extension Request: Member must not be in frozen status. Please contact the library");
            return;
        }

        try {
            await borrowService.extendBorrow(member.id, selectedBook.copyOfBookId);
            
            await borrowService.addActivity({
                memberId: member.id,
                activityType: "extendBorrow",
                description: `Extending borrowed book ${selectedBook.title}`
            });

            alert("Extension Request: Approved");
            loadBorrows(member.id);
        } catch (error: any) {
            alert("Extension Request: Not Approved there are orders for the book");
        }
    };

    return (
        <div className="member-container">
            <h2 className="label-text" style={{ fontSize: '20px', alignSelf: 'center' }}>Search Borrowed Books</h2>
            
            <div className="info-row" style={{ justifyContent: 'center' }}>
                <div className="info-group">
                    <span className="label-text" style={{ fontSize: '20px' }}>Copy of book ID:</span>
                    <input 
                        className="text-field" 
                        value={selectedCopyId}
                        onChange={e => setSelectedCopyId(e.target.value)}
                    />
                </div>
            </div>

            <table className="data-table" style={{ marginTop: '20px' }}>
                <thead>
                    <tr>
                        <th>Borrowed book ID</th>
                        <th>Name</th>
                        <th>Return Date</th>
                    </tr>
                </thead>
                <tbody>
                    {filteredBooks.map(book => {
                        const isOverdue = new Date(book.returnDate).setHours(0,0,0,0) < new Date().setHours(0,0,0,0);
                        const isSelected = selectedCopyId === book.copyOfBookId.toString();
                        
                        return (
                            <tr 
                                key={book.copyOfBookId} 
                                onClick={() => setSelectedCopyId(book.copyOfBookId.toString())}
                                className={isSelected ? 'selected' : ''}
                                style={{ backgroundColor: isOverdue && !isSelected ? '#ffcccc' : undefined }}
                            >
                                <td>{book.copyOfBookId}</td>
                                <td>{book.title}</td>
                                <td>{book.returnDate}</td>
                            </tr>
                        );
                    })}
                </tbody>
            </table>

            <div style={{ display: 'flex', justifyContent: 'center', margin: '20px 0' }}>
                <button className="menu-button" onClick={handleExtend}>Extend book</button>
            </div>

            <div className="info-row" style={{ justifyContent: 'center' }}>
                <div className="info-group">
                    <span className="label-text" style={{ fontSize: '19px' }}>Book Name:</span>
                    <input 
                        className="text-field" 
                        value={searchName}
                        onChange={e => setSearchName(e.target.value)}
                    />
                </div>
                
                <div className="info-group" style={{ marginLeft: '20px' }}>
                    <input 
                        type="checkbox" 
                        id="overdue"
                        checked={isOverdueOnly}
                        onChange={e => setIsOverdueOnly(e.target.checked)}
                        style={{ width: '18px', height: '18px', cursor: 'pointer' }}
                    />
                    <label htmlFor="overdue" className="label-text" style={{ cursor: 'pointer' }}>Overdue</label>
                </div>
            </div>
        </div>
    );
};

export default ExtendBorrow;