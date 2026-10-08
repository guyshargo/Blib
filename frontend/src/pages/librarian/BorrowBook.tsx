import React, { useState } from 'react';
import { librarianService } from '../../services/librarianService';
import { borrowService } from '../../services/borrowService';
import type { Member } from '../../types';

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
            const member = await librarianService.getMember(parseInt(memberIdInput));
            if (member.freezeStatus === 'FROZEN') {
                alert("Member Status Is Frozen.");
                setMember(null);
                return;
            }
            setMember(member);
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
            setMemberIdInput(member.memberId.toString());
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
            const copy = await borrowService.getAvailableCopyById(parseInt(bookIdInput), member.memberId);
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
            const copy = await borrowService.getAvailableCopyByBarcode(barcode.trim(), member.memberId);
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
            const borrows = await borrowService.getMemberBorrows(member.memberId);
            const lateBooks = borrows.filter(b => new Date(b.returnDate) < new Date());
            
            if (lateBooks.length > 0) {
                const lateTitles = lateBooks.map(b => `"${b.title}"`).join(',');
                alert(`The following borrowed books haven't been returned in time:\n${lateTitles}\nPlease contact the library.`);
                return;
            }

            const librarianUser = JSON.parse(localStorage.getItem('currentUser') || '{}');
            
            const payload = {
                memberId: member.memberId,
                bookCopyId: copyOfBook.copyId,
                librarianId: librarianUser.id || 101,
                librarianName: librarianUser.fullName || "Librarian"
            };

            await borrowService.commitBorrow(payload);
            
            await borrowService.addActivity({
                memberId: member.memberId,
                activityType: "borrow",
                entityId: copyOfBook.copyId
            });

            alert("Book Borrowed successfully.");
            
            // Reset state
            setMember(null); setMemberIdInput('');
            setCopyOfBook(null); setBookIdInput(''); setBookName(''); setAvailableCopies('');
        } catch (error: any) {
            alert(error.response?.data || "Borrow Unsuccessful");
        }
    };

    const baseBtn = "rounded-full text-base font-semibold border-2 border-transparent px-[25px] py-2.5 cursor-pointer transition-all duration-200 hover:shadow-[inset_0_3px_8px_rgba(0,0,0,0.3)] disabled:opacity-50 disabled:cursor-not-allowed";

    return (
        <div className="flex flex-col items-center p-[30px] font-sans min-h-screen gap-5">
            <h2 className="text-[23px] font-bold text-[#333]">Library Member Information</h2>
            
            <div className="flex flex-col w-[600px] gap-5 bg-white p-5 rounded-[10px] shadow-[0_2px_10px_rgba(0,0,0,0.05)]">
                <div className="flex gap-5 items-end w-full">
                    <div className="flex flex-col gap-1 flex-1">
                        <span className="text-base font-bold text-[#333]">Membership Number:</span>
                        <input className="rounded-[5px] text-sm px-[15px] py-2.5 border border-[#ccc] outline-none" value={memberIdInput} onChange={e => setMemberIdInput(e.target.value)} />
                    </div>
                    <button className={`${baseBtn} bg-[#edc915] text-black hover:bg-[#d4b412]`} onClick={handleScanMemberCard}>Scan Reader Card</button>
                </div>
                <div className="flex gap-5 items-end w-full">
                    <div className="flex flex-col gap-1 flex-1">
                        <span className="text-base font-bold text-[#333]">Full Name:</span>
                        <input className="rounded-[5px] text-sm px-[15px] py-2.5 border border-[#ccc] outline-none bg-[#f5f5f5] text-[#666] cursor-not-allowed" readOnly value={member?.fullName || ''} />
                    </div>
                    <button className={`${baseBtn} bg-[#fec999] text-black hover:bg-[#fda95c]/65`} onClick={handleFindMember}>Find Member</button>
                </div>
            </div>

            <hr className="w-full border-t border-[#ccc] my-5" />

            <h2 className="text-[23px] font-bold text-[#333]">Book Information</h2>

            <div className="flex flex-col w-[600px] gap-5 bg-white p-5 rounded-[10px] shadow-[0_2px_10px_rgba(0,0,0,0.05)]">
                <div className="flex gap-5 items-end w-full">
                    <div className="flex flex-col gap-1 flex-1">
                        <span className="text-base font-bold text-[#333]">Book ID:</span>
                        <input className="rounded-[5px] text-sm px-[15px] py-2.5 border border-[#ccc] outline-none disabled:bg-[#f5f5f5] disabled:cursor-not-allowed" value={bookIdInput} onChange={e => setBookIdInput(e.target.value)} disabled={!member} />
                    </div>
                    <button className={`${baseBtn} bg-[#edc915] text-black hover:bg-[#d4b412]`} onClick={handleScanBook} disabled={!member}>Scan Book</button>
                </div>
                <div className="flex gap-5 w-full">
                    <div className="flex flex-col gap-1 flex-[2]">
                        <span className="text-base font-bold text-[#333]">Book Name:</span>
                        <input className="rounded-[5px] text-sm px-[15px] py-2.5 border border-[#ccc] outline-none bg-[#f5f5f5] text-[#666] cursor-not-allowed" readOnly value={bookName} />
                    </div>
                    <div className="flex flex-col gap-1 flex-1">
                        <span className="text-base font-bold text-[#333]">Copies:</span>
                        <input className="rounded-[5px] text-sm px-[15px] py-2.5 border border-[#ccc] outline-none bg-[#f5f5f5] text-[#666] cursor-not-allowed" readOnly value={availableCopies} />
                    </div>
                </div>
            </div>

            <div className="flex gap-10 mt-[30px]">
                <button className={`${baseBtn} bg-[#fec999] text-black hover:bg-[#fda95c]/65`} onClick={handleFindBook} disabled={!member || !bookIdInput}>Find Book</button>
                <button className={`${baseBtn} bg-[#5caa58] text-white hover:bg-[#4d8f4a]`} onClick={handleCommitBorrow} disabled={!member || !copyOfBook}>
                    Commit Borrow
                </button>
            </div>
        </div>
    );
};

export default BorrowBook;