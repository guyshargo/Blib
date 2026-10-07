import React from 'react';
import { useNavigate } from 'react-router-dom';
import './Librarian.css';

const LibrarianDashboard: React.FC = () => {
    const navigate = useNavigate();

    return (
        <div className="librarian-container" style={{ alignItems: 'center', justifyContent: 'center' }}>
            <div style={{ position: 'relative', zIndex: 1, display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '30px', marginBottom: '40px' }}>
                <button className="menu-button" style={{ height: '75px', fontSize: '23px' }} onClick={() => navigate('/catalog')}>Search Book</button>
                <button className="menu-button" style={{ height: '75px', fontSize: '23px' }} onClick={() => navigate('/manage-members')}>Manage Member</button>
                <button className="menu-button" style={{ height: '75px', fontSize: '23px' }} onClick={() => navigate('/register-member')}>Register Member</button>
                
                <button className="menu-button" style={{ height: '75px', fontSize: '23px' }} onClick={() => navigate('/borrow-book')}>Borrow Page</button>
                <button className="menu-button" style={{ height: '75px', fontSize: '23px' }} onClick={() => navigate('/return-book')}>Return Book Page</button>
                <button className="menu-button" style={{ height: '75px', fontSize: '23px' }} onClick={() => navigate('/reports')}>Report Page</button>
            </div>
            
            <div style={{ opacity: 0.5 }}>
                <img src="/images/computerbook.png" alt="Library Graphic" style={{ maxWidth: '450px' }} />
            </div>
        </div>
    );
};

export default LibrarianDashboard;