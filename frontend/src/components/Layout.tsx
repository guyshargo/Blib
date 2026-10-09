import React, {useEffect, useRef, useState} from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { authService } from '../services/authService';

const Layout: React.FC = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const [browseOpen, setBrowseOpen] = useState(false);
    const browseMenuRef = useRef<HTMLDivElement>(null);

    const userStr = localStorage.getItem('currentUser');
    const userType = localStorage.getItem('userType');
    
    const user = userStr ? JSON.parse(userStr) : null;
    const userName = user?.fullName || 'Guest';
    const roleLabel = userType === 'LIBRARIAN' ? 'Librarian' : (userType === 'MEMBER' ? 'Member' : '');

    useEffect(() => {
        const handleUnload = () => {
            const currentStr = localStorage.getItem('currentUser');
            const currentType = localStorage.getItem('userType');
            
            if (currentStr && currentType) {
                const currentUser = JSON.parse(currentStr);

                const endpoint = currentType === 'LIBRARIAN' 
                    ? `/librarians/${currentUser.librarianId}/login-status`
                    : `/members/${currentUser.memberId}/login-status`;
                
                fetch(`http://localhost:8080/api${endpoint}?status=false`, {
                    method: 'PUT',
                    keepalive: true
                });
            }
        };

        window.addEventListener('beforeunload', handleUnload);
        return () => window.removeEventListener('beforeunload', handleUnload);
    }, []);

    useEffect(() => {
        if (!browseOpen) return;

        const closeOnOutsideClick = (event: MouseEvent) => {
            if (!browseMenuRef.current?.contains(event.target as Node)) {
                setBrowseOpen(false);
            }
        };
        const closeOnEscape = (event: KeyboardEvent) => {
            if (event.key === 'Escape') setBrowseOpen(false);
        };

        document.addEventListener('mousedown', closeOnOutsideClick);
        document.addEventListener('keydown', closeOnEscape);
        return () => {
            document.removeEventListener('mousedown', closeOnOutsideClick);
            document.removeEventListener('keydown', closeOnEscape);
        };
    }, [browseOpen]);

    const handleLogout = async () => {
        await authService.logout(); // Triggers the DB update and clears session
        navigate('/login');
    };

    const isLibrarian = userType === 'LIBRARIAN';
    const browseLinks = isLibrarian
        ? [
            { path: '/librarian-dashboard', label: 'Dashboard' },
            { path: '/manage-members', label: 'Manage Members' },
            { path: '/register-member', label: 'Register Member' },
            { path: '/borrow-book', label: 'Borrow Book' },
            { path: '/return-book', label: 'Return Book' },
            { path: '/view-member', label: 'View Members' },
            { path: '/reports', label: 'Reports' },
            { path: '/invoices', label: 'Invoices' }
          ]
        : [
            { path: '/home', label: 'Home' },
            { path: '/catalog', label: 'Browse Catalog' },
            { path: '/orders', label: 'Order Requests' }
          ];
    const browseIsActive = browseLinks.some(link => location.pathname.startsWith(link.path));

    return (
        <div className="flex min-h-screen flex-col bg-[#E9DFC5] font-sans">
            {/* TOP HEADER */}
            <header className="flex items-center bg-[#8B4A32] px-8 py-4"></header>
            {/* bg-[#CBA471] */}

            {/* HORIZONTAL NAV BAR */}
            <nav className="bg-transparent px-4">
                <div className="mx-auto flex max-w-6xl items-center justify-center gap-8">

                <button className="font-extrabold text-[26px] text-[#3E3028] mr-auto" onClick={() => navigate('/home')}>
                    Blib Library
                </button>
                    {!isLibrarian && (
                        <button
                            className={`flex items-center gap-2 rounded-md px-5 py-3 text-base font-semibold transition-colors hover:bg-[#8B4A32]/20 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#8B4A32] ${
                                location.pathname.startsWith('/my-borrows')
                                    ? 'text-[#3E3028]'
                                    : 'text-[#5A4A42] hover:text-[#3E3028]'
                            }`}
                            onClick={() => navigate('/my-borrows')}
                        >
                            <svg aria-hidden="true" viewBox="0 0 24 24" className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinejoin="round">
                                <path d="M6.5 4.75h11v15l-5.5-3.5-5.5 3.5z" />
                            </svg>
                            My Books
                        </button>
                    )}

                    <div className="relative" ref={browseMenuRef}>
                        <button
                            type="button"
                            aria-haspopup="menu"
                            aria-expanded={browseOpen}
                            className={`flex items-center gap-2 rounded-md px-5 py-3 text-base font-semibold transition-colors hover:bg-[#8B4A32]/20 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#8B4A32] ${
                                browseIsActive || browseOpen
                                    ? `text-[#3E3028] ${browseOpen ? 'bg-[#D8D0BA]' : ''}`
                                    : 'text-[#5A4A42] hover:text-[#3E3028]'
                            }`}
                            onClick={() => setBrowseOpen(open => !open)}
                        >
                            <svg aria-hidden="true" viewBox="0 0 24 24" className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinejoin="round">
                                <path d="M6 4.75h4.5A3.5 3.5 0 0 1 14 8.25v11a3.5 3.5 0 0 0-3.5-3.5H6zM18 4.75h-4A3.5 3.5 0 0 0 10.5 8.25v11a3.5 3.5 0 0 1 3.5-3.5H18z" />
                            </svg>
                            Browse
                            <svg aria-hidden="true" viewBox="0 0 20 20" className={`h-3.5 w-3.5 transition-transform ${browseOpen ? 'rotate-180' : ''}`} fill="none" stroke="currentColor" strokeWidth="1.8">
                                <path d="m5 7.5 5 5 5-5" />
                            </svg>
                        </button>

                        {browseOpen && (
                            <div className="absolute left-0 top-full z-20 mt-1 w-64 border border-[#E1D7C6] bg-[#FFFDF9] py-2 shadow-[0_8px_24px_rgba(62,48,40,0.16)]" role="menu" aria-label="Browse pages">
                                {browseLinks.map(link => {
                                    const isActive = location.pathname.startsWith(link.path);
                                    return (
                                        <button
                                            key={link.path}
                                            role="menuitem"
                                            className={`block w-full px-5 py-2.5 text-left text-sm transition-colors ${
                                                isActive
                                                    ? 'bg-[#F0EAE1] font-semibold text-[#8B4A32]'
                                                    : 'text-[#5A4A42] hover:bg-[#F8F6F0] hover:text-[#3E3028]'
                                            }`}
                                            onClick={() => {
                                                setBrowseOpen(false);
                                                navigate(link.path);
                                            }}
                                        >
                                            {link.label}
                                        </button>
                                    );
                                })}
                            </div>
                        )}
                    </div>

                    <div className="flex items-center bg-white rounded-full px-4 py-1 w-[350px] mr-8">
                    <input type="text" className="w-full bg-transparent text-sm text-[#3E3028] outline-none" placeholder="Search Title / Author..." />
                </div>

                <button className="flex items-center gap-3 px-4 py-1 rounded-full mr-4 hover:hover:bg-[#8B4A32]/20" onClick={() => navigate('/personal-info')}>
                    <div className="flex flex-col text-left">
                        <span className="font-extrabold text-sm text-[#3E3028]">{userName}</span>
                        <span className="text-[11px] text-[#6C5D53]">{roleLabel}</span>
                    </div>
                </button>
                
                <button className="border-[1.2px] border-[#B83A24] text-[#B83A24] rounded-full text-xs font-extrabold px-4 py-1 transition-colors hover:bg-[#B83A24] hover:text-white" 
                    onClick={user ? handleLogout : () => navigate('/login')}
                >
                    {user ? 'Logout' : 'Login'}
                </button>
                </div>
            </nav>

            {/* DYNAMIC CENTER CONTENT */}
            <main className="mx-auto my-4 w-[calc(100%-2rem)] max-w-6xl flex-grow rounded-xl bg-[#FFFDF9] shadow-[0_8px_28px_rgba(62,48,40,0.08)]">
                <Outlet />
            </main>
            
        </div>
    );
};

export default Layout;