import React, {useEffect} from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { authService } from '../services/authService';

const Layout: React.FC = () => {
    const navigate = useNavigate();
    const location = useLocation();

    const userStr = localStorage.getItem('currentUser');
    const userType = localStorage.getItem('userType');
    
    const user = userStr ? JSON.parse(userStr) : null;
    const userName = user?.fullName || 'Guest';
    const roleLabel = userType === 'LIBRARIAN' ? 'Librarian' : 'Member';

    useEffect(() => {
        const handleUnload = () => {
            const currentStr = localStorage.getItem('currentUser');
            const currentType = localStorage.getItem('userType');
            
            if (currentStr && currentType) {
                const currentUser = JSON.parse(currentStr);
                const endpoint = currentType === 'LIBRARIAN' 
                    ? `/librarians/${currentUser.id}/login-status` 
                    : `/members/${currentUser.id}/login-status`;
                
                fetch(`http://localhost:8080/api${endpoint}?status=false`, {
                    method: 'PUT',
                    keepalive: true
                });
            }
        };

        window.addEventListener('beforeunload', handleUnload);
        return () => window.removeEventListener('beforeunload', handleUnload);
    }, []);

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
        <div className="flex flex-col min-h-screen bg-[#F8F6F0] font-sans">
            
            {/* TOP HEADER */}
            <header className="flex items-center bg-[#dac4a5] px-8 py-4 shadow-md">
                <div className="flex gap-1 mr-8">
                    <button className="text-[#5A4A42] font-extrabold text-base px-2 py-1 rounded-full hover:bg-white/40" onClick={() => navigate(-1)}>◀</button>
                    <button className="text-[#5A4A42] font-extrabold text-base px-2 py-1 rounded-full hover:bg-white/40" onClick={() => navigate(1)}>▶</button>
                </div>

                <span className="font-extrabold text-[26px] text-[#3E3028] mr-auto">Blib Library</span>

                <div className="flex items-center bg-white rounded-full px-4 py-1 w-[350px] mr-8">
                    <input type="text" className="w-full bg-transparent text-sm text-[#3E3028] outline-none" placeholder="Search Title / Author..." />
                </div>

                <button className="flex items-center gap-3 px-4 py-1 rounded-full mr-4 hover:bg-white/40" onClick={() => navigate('/personal-info')}>
                    <div className="flex flex-col text-left">
                        <span className="font-extrabold text-sm text-[#3E3028]">{userName}</span>
                        <span className="text-[11px] text-[#6C5D53]">{roleLabel}</span>
                    </div>
                </button>
                
                <button className="border-[1.2px] border-[#B83A24] text-[#B83A24] rounded-full text-xs font-extrabold px-4 py-1 transition-colors hover:bg-[#B83A24] hover:text-white" onClick={handleLogout}>
                    Logout
                </button>
            </header>

            {/* HORIZONTAL NAV BAR */}
            <nav className="flex justify-center gap-8 bg-[#F0EAE1] px-5 border-y border-[#E1D7C6]">
                {navLinks.map(link => {
                    const isActive = location.pathname.startsWith(link.path);
                    return (
                        <button 
                            key={link.path}
                            className={`px-5 py-3 border-b-[3px] text-base cursor-pointer transition-colors ${
                                isActive 
                                ? 'text-black font-extrabold border-[#60bccd]' 
                                : 'text-[#5A4A42] font-semibold border-transparent hover:text-[#3E3028] hover:border-[#A0968F]'
                            }`}
                            onClick={() => navigate(link.path)}
                        >
                            {link.label}
                        </button>
                    );
                })}
            </nav>

            {/* DYNAMIC CENTER CONTENT */}
            <main className="flex-grow flex flex-col overflow-y-auto">
                <Outlet />
            </main>
            
        </div>
    );
};

export default Layout;