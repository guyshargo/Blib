import React from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { authService } from '../services/authService';
import './Layout.css';

const Layout: React.FC = () => {
    const navigate = useNavigate();
    const location = useLocation();

    // Replaces SessionManager checking logic
    const userStr = localStorage.getItem('currentUser');
    const userType = localStorage.getItem('userType');
    
    const user = userStr ? JSON.parse(userStr) : null;
    const userName = user?.fullName || 'Guest';
    const roleLabel = userType === 'LIBRARIAN' ? 'Librarian' : 'Member';

    const handleLogout = async () => {
        await authService.logout(); // Triggers the DB update and clears session
        navigate('/login');
    };

    // Dynamically generates the nav bar based on user role
    const navLinks = userType === 'LIBRARIAN' 
        ? [
            { path: '/librarian-dashboard', label: 'Dashboard' },
            { path: '/manage-catalog', label: 'Catalog Management' }
          ]
        : [
            { path: '/home', label: 'Home' },
            { path: '/catalog', label: 'Browse Catalog' },
            { path: '/my-borrows', label: 'My Borrows' },
            { path: '/orders', label: 'Order Requests' }
          ];

    return (
        <div className="layout-root">
            
            {/* TOP HEADER */}
            <header className="header-bar">
                <div className="history-buttons">
                    <button className="history-button" onClick={() => navigate(-1)}>◀</button>
                    <button className="history-button" onClick={() => navigate(1)}>▶</button>
                </div>

                <span className="brand-title">Blib Library</span>

                <div className="search-bar-container">
                    <input type="text" className="search-bar" placeholder="Search Title / Author..." />
                </div>

                <button className="profile-button" onClick={() => navigate('/personal-info')}>
                    <div className="user-info">
                        <span className="user-profile-label">{userName}</span>
                        <span className="user-role-label">{roleLabel}</span>
                    </div>
                </button>
                
                <button className="logout-button" onClick={handleLogout}>Logout</button>
            </header>

            {/* HORIZONTAL NAV BAR */}
            <nav className="nav-bar">
                {navLinks.map(link => (
                    <button 
                        key={link.path}
                        className={`nav-button ${location.pathname.startsWith(link.path) ? 'nav-button-active' : ''}`}
                        onClick={() => navigate(link.path)}
                    >
                        {link.label}
                    </button>
                ))}
            </nav>

            {/* DYNAMIC CENTER CONTENT */}
            <main className="main-content">
                <Outlet />
            </main>
            
        </div>
    );
};

export default Layout;