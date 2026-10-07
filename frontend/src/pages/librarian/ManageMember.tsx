import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { librarianService } from '../../services/librarianService';
import type { Member } from '../../types';
import './Librarian.css';

const ManageMember: React.FC = () => {
    const [members, setMembers] = useState<Member[]>([]);
    const [searchQuery, setSearchQuery] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const loadMembers = async () => {
            try {
                const data = await librarianService.getAllMembers();
                setMembers(data);
            } catch (error) {
                console.error("No subscribers found");
            }
        };
        loadMembers();
    }, []);

    const filteredMembers = members.filter(member => 
        member.id.toString().includes(searchQuery) || 
        member.fullName.toLowerCase().includes(searchQuery.toLowerCase())
    );

    const handleRowClick = (member: Member) => {
        navigate('/view-member', { state: { selectedMember: member } });
    };

    const handleScanCard = async () => {
        const barcode = window.prompt("Scan ReaderCard Barcode\nPlease enter the barcode to scan:");
        if (!barcode || !barcode.trim()) return;

        try {
            const member = await librarianService.getMemberByBarcode(barcode.trim());
            navigate('/view-member', { state: { selectedMember: member } });
        } catch (error) {
            alert("No member found with barcode.");
        }
    };

    return (
        <div className="librarian-container">
            <div className="search-header" style={{ justifyContent: 'center', backgroundColor: 'transparent', boxShadow: 'none' }}>
                <span className="label-text" style={{ fontSize: '20px' }}>Search members:</span>
                <input 
                    className="text-field" 
                    style={{ width: '400px' }}
                    placeholder="Enter member name or membership number" 
                    value={searchQuery}
                    onChange={e => setSearchQuery(e.target.value)}
                />
            </div>

            <table className="data-table">
                <thead>
                    <tr>
                        <th>Membership Number</th>
                        <th>Name</th>
                    </tr>
                </thead>
                <tbody>
                    {filteredMembers.map(member => (
                        <tr key={member.id} onClick={() => handleRowClick(member)}>
                            <td>{member.id}</td>
                            <td>{member.fullName}</td>
                        </tr>
                    ))}
                </tbody>
            </table>

            <div style={{ display: 'flex', justifyContent: 'center', marginTop: '20px' }}>
                <button className="menu-button" onClick={handleScanCard}>Scan Reader Card</button>
            </div>
        </div>
    );
};

export default ManageMember;