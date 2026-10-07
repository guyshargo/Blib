import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { librarianService } from '../../services/librarianService';
import { borrowService } from '../../services/borrowService';
import { memberService } from '../../services/memberService';
import type { Member, BorrowedBook, Activity } from '../../types';
import './Librarian.css';

const ViewMember: React.FC = () => {
    const location = useLocation();
    const passedMember = location.state?.selectedMember as Member;
    
    const [member, setMember] = useState<Member | null>(passedMember || null);
    const [activities, setActivities] = useState<Activity[]>([]);
    const [borrows, setBorrows] = useState<BorrowedBook[]>([]);
    const [filterQuery, setFilterQuery] = useState('');

    useEffect(() => {
        if (member) {
            loadData(member.id);
        }
    }, [member]);

    const loadData = async (memberId: number) => {
        const [acts, books] = await Promise.all([
            memberService.getActivities(memberId),
            borrowService.getMemberBorrows(memberId)
        ]);
        setActivities(acts);
        setBorrows(books);
    };

    const handleToggleFreeze = async (newStatus: string) => {
        if (!member) return;
        try {
            const today = new Date().toISOString().split('T')[0];
            await librarianService.updateFreezeStatus(member.id, newStatus, today);
            
            await borrowService.addActivity({
                membershipNumber: member.id,
                activityType: "freezeStatus",
                description: `freezeStatus,status changed from ${member.freezeStatus} to ${newStatus}`
            });
            
            setMember({ ...member, freezeStatus: newStatus as any });
            alert("Updating status successful");
        } catch (error) {
            alert("Error in updating Status");
        }
    };

    const handleChangeReturnDate = async (book: BorrowedBook) => {
        const newDate = window.prompt(`Enter the new return date for the book: ${book.title}\nFormat: YYYY-MM-DD`, book.returnDate);
        if (!newDate) return;

        try {
            const librarianUser = JSON.parse(localStorage.getItem('currentUser') || '{}');
            
            const payload = {
                memberId: member?.id,
                copyOfBookId: book.copyOfBookId,
                newReturnDate: newDate,
                librarianName: librarianUser.fullName || "Librarian",
                librarianId: librarianUser.id || 101,
                extensionDate: new Date().toISOString().split('T')[0]
            };

            await librarianService.changeReturnDate(payload);
            alert("Return date changed successfully.");
            if (member) loadData(member.id);
        } catch (error) {
            alert("Invalid Date or Failed to update return date on server.");
        }
    };

    const filteredBorrows = borrows.filter(b => b.title.toLowerCase().includes(filterQuery.toLowerCase()));

    if (!member) return <div>No member selected.</div>;

    return (
        <div className="librarian-container">
            <div className="search-header">
                <div className="input-group">
                    <span className="label-text">Full Name:</span>
                    <input className="text-field" readOnly value={member.fullName} />
                </div>
                <div className="input-group">
                    <span className="label-text">Email Address:</span>
                    <input className="text-field" readOnly value={member.email} />
                </div>
                <div className="input-group">
                    <span className="label-text">Membership Number:</span>
                    <input className="text-field" readOnly value={member.id} />
                </div>
                <div className="input-group">
                    <span className="label-text">Freeze Status:</span>
                    <select 
                        className="combo-box" 
                        value={member.freezeStatus} 
                        onChange={e => handleToggleFreeze(e.target.value)}
                    >
                        <option value="Frozen">Frozen</option>
                        <option value="Not Frozen">Not Frozen</option>
                    </select>
                </div>
            </div>

            <div style={{ display: 'flex', gap: '20px' }}>
                <div style={{ flex: 1 }}>
                    <h3 className="label-text">Activities</h3>
                    <ul style={{ background: 'white', padding: '20px', borderRadius: '5px', maxHeight: '200px', overflowY: 'auto' }}>
                        {activities.length === 0 ? <li>No activities found.</li> : activities.map((act, i) => (
                            <li key={i}>{i + 1}) {new Date(act.activityDateTime).toLocaleString()} - {act.type}</li>
                        ))}
                    </ul>
                </div>
            </div>

            <div className="input-group">
                <span className="label-text">Borrowed Books:</span>
                <input 
                    className="text-field" 
                    placeholder="Enter Book's Title to Filter" 
                    value={filterQuery} 
                    onChange={e => setFilterQuery(e.target.value)} 
                />
            </div>

            <table className="data-table">
                <thead>
                    <tr>
                        <th>Book Title</th>
                        <th>Borrow Date</th>
                        <th>Return Date (Click to Extend)</th>
                        <th>Librarian Name</th>
                    </tr>
                </thead>
                <tbody>
                    {filteredBorrows.map(book => (
                        <tr key={book.copyOfBookId}>
                            <td>{book.title}</td>
                            <td>{book.borrowDate}</td>
                            <td 
                                style={{ color: 'blue', textDecoration: 'underline' }} 
                                onClick={() => handleChangeReturnDate(book)}
                            >
                                {book.returnDate}
                            </td>
                            <td>{book.librarianName}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default ViewMember;