import React, { useState } from 'react';
import { librarianService } from '../../services/librarianService';
import { borrowService } from '../../services/borrowService';
import type { Member } from '../../types';
import './Librarian.css';

const BorrowBook: React.FC = () => {
    // Member State
    const [memberIdInput, setMemberIdInput] = useState('');
    const [member, setMember] = useState<Member | null>(null);

    // Book State
    const [bookIdInput, setBookIdInput] = useState('');
    const [bookName, setBookName] = useState('');
    const [availableCopies, setAvailableCopies] = useState('');
    const [copyOfBook, setCopyOfBook] = useState<any>(null);

    const handleFindMember = async () => {
        if (!memberIdInput.trim() || !/^\d+$/.test(memberIdInput)) {
            alert("The ID must contain only digits and cannot be empty.");
            return;
        }
        try {
            const sub = await librarianService.getMember(parseInt(memberIdInput));
            if (sub.freezeStatus === 'Frozen') {
                alert("Member Status Is Frozen.");
                setMember(null);
                return;
            }
            setMember(sub);
        } catch (error) {
            setMember(null);
            alert("No member found with the given ID.");
        }
    };

    const handleScanMemberCard = async () => {
        const barcode = window.prompt("Scan ReaderCard Barcode\nPlease enter the barcode to scan:");
        if (!barcode || !barcode.trim()) return;
        
        try {
            const member = await librarianService.getMemberByBarcode(barcode.trim());
            setMemberIdInput(member.id.toString());
            setMember(member);
        } catch (error) {
            alert("No member found with barcode.");
        }
    };

    const handleFindBook = async () => {
        if (!bookIdInput.trim() || !member) {
            alert("Book ID cannot be empty and a member must be selected.");
            return;
        }
        try {
            const copy = await borrowService.getAvailableCopyById(parseInt(bookIdInput), member.id);
            setCopyOfBook(copy);
            setBookName(copy.CopyOfBookName);
            
            const bookDetails = await borrowService.getBookDetails(copy.bookId);
            setAvailableCopies((bookDetails.copiesNum - bookDetails.borrowedNum).toString());
        } catch (error) {
            alert("No available copy of the book found.");
            setCopyOfBook(null);
            setBookName('');
            setAvailableCopies('');
        }
    };

    const handleScanBook = async () => {
        if (!member) return;
        const barcode = window.prompt("Scan Barcode\nPlease enter the barcode to scan:");
        if (!barcode || !barcode.trim()) return;

        try {
            const copy = await borrowService.getAvailableCopyByBarcode(barcode.trim(), member.id);
            setCopyOfBook(copy);
            setBookIdInput(copy.bookId.toString());
            setBookName(copy.CopyOfBookName);
        } catch (error) {
            alert("The copy is not available.");
            setCopyOfBook(null);
            setBookName('');
        }
    };

    const handleCommitBorrow = async () => {
        if (!member || !copyOfBook) return;

        try {
            // Check for late books before borrowing
            const borrows = await borrowService.getMemberBorrows(member.id);
            const lateBooks = borrows.filter(b => new Date(b.returnDate) < new Date());
            
            if (lateBooks.length > 0) {
                const lateTitles = lateBooks.map(b => `"${b.title}"`).join(',');
                alert(`The following borrowed books haven't been returned in time:\n${lateTitles}\nPlease contact the library.`);
                return;
            }

            const librarianUser = JSON.parse(localStorage.getItem('currentUser') || '{}');
            
            const payload = {
                member_id: member.id,
                copy_id: copyOfBook.copyId,
                librarian_id: librarianUser.id || 101,
                librarian_name: librarianUser.fullName || "Librarian"
            };

            const borrowedBook = await borrowService.commitBorrow(payload);
            
            await borrowService.addActivity({
                membershipNumber: member.id,
                activityType: "borrow",
                description: `borrowed ${borrowedBook.title}`
            });

            alert("Book Borrowed successfully.");
            
            // Reset state
            setMember(null); setMemberIdInput('');
            setCopyOfBook(null); setBookIdInput(''); setBookName(''); setAvailableCopies('');
        } catch (error: any) {
            alert(error.response?.data || "Borrow Unsuccessful");
        }
    };

    return (
        <div className="librarian-container" style={{ alignItems: 'center' }}>
            <h2 className="label-text" style={{ fontSize: '23px' }}>Library Member Information</h2>
            
            <div className="search-header" style={{ flexDirection: 'column', width: '600px', gap: '20px' }}>
                <div style={{ display: 'flex', gap: '20px', alignItems: 'flex-end', width: '100%' }}>
                    <div className="input-group" style={{ flex: 1 }}>
                        <span className="label-text">Membership Number:</span>
                        <input className="text-field" value={memberIdInput} onChange={e => setMemberIdInput(e.target.value)} />
                    </div>
                    <button className="menu-button" style={{ backgroundColor: '#edc915' }} onClick={handleScanMemberCard}>Scan Reader Card</button>
                </div>
                <div style={{ display: 'flex', gap: '20px', alignItems: 'flex-end', width: '100%' }}>
                    <div className="input-group" style={{ flex: 1 }}>
                        <span className="label-text">Full Name:</span>
                        <input className="text-field" readOnly value={member?.fullName || ''} />
                    </div>
                    <button className="menu-button" onClick={handleFindMember}>Find Member</button>
                </div>
            </div>

            <hr style={{ width: '100%', borderTop: '1px solid #ccc', margin: '20px 0' }} />

            <h2 className="label-text" style={{ fontSize: '23px' }}>Book Information</h2>

            <div className="search-header" style={{ flexDirection: 'column', width: '600px', gap: '20px' }}>
                <div style={{ display: 'flex', gap: '20px', alignItems: 'flex-end', width: '100%' }}>
                    <div className="input-group" style={{ flex: 1 }}>
                        <span className="label-text">Book ID:</span>
                        <input className="text-field" value={bookIdInput} onChange={e => setBookIdInput(e.target.value)} disabled={!member} />
                    </div>
                    <button className="menu-button" style={{ backgroundColor: '#edc915' }} onClick={handleScanBook} disabled={!member}>Scan Book</button>
                </div>
                <div style={{ display: 'flex', gap: '20px', width: '100%' }}>
                    <div className="input-group" style={{ flex: 2 }}>
                        <span className="label-text">Book Name:</span>
                        <input className="text-field" readOnly value={bookName} />
                    </div>
                    <div className="input-group" style={{ flex: 1 }}>
                        <span className="label-text">Copies:</span>
                        <input className="text-field" readOnly value={availableCopies} />
                    </div>
                </div>
            </div>

            <div style={{ display: 'flex', gap: '40px', marginTop: '30px' }}>
                <button className="menu-button" onClick={handleFindBook} disabled={!member || !bookIdInput}>Find Book</button>
                <button className="menu-button" style={{ backgroundColor: '#5caa58', color: 'white' }} onClick={handleCommitBorrow} disabled={!member || !copyOfBook}>
                    Commit Borrow
                </button>
            </div>
        </div>
    );
};

export default BorrowBook;