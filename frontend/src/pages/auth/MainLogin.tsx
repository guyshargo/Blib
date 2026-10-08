import React from 'react';
import { useNavigate } from 'react-router-dom';

const MainLogin: React.FC = () => {
    const navigate = useNavigate();

    return (
        <div className="min-h-screen flex items-center justify-center bg-black/30 bg-login bg-cover bg-center bg-blend-multiply font-sans">
            <div className="flex w-[800px] h-[600px] bg-[#e18b42]/80 shadow-[0_4px_20px_rgba(0,0,0,0.5)]">
                
                {/* Left Bar */}
                <div className="w-[305px] bg-white/60 flex flex-col items-center justify-center p-5 text-center">
                    <img src="/images/book_logo.png" alt="Book Logo" className="w-[227px] drop-shadow-md" />
                    <h1 className="text-[40px] font-bold text-[#15083b] my-4">Blib Library</h1>
                    <p className="text-[25px] font-semibold mt-auto mb-5">Not a member yet?</p>
                    
                    <button 
                        className="w-full max-w-[315px] bg-[#cdd8f1] hover:bg-[#88a6e8] text-black rounded-full text-xl font-semibold px-8 py-3 mb-4 transition-colors hover:shadow-inner" 
                        onClick={() => navigate('/home')}
                    >
                        Continue as a Guest
                    </button>
                </div>

                {/* Right Bar */}
                <div className="flex-1 bg-[#dcaf8c]/40 flex flex-col items-center justify-center p-10">
                    <img src="/images/laptop_main.png" alt="Laptop" className="w-[343px] mb-8" />
                    
                    <button 
                        className="w-full max-w-[315px] bg-[#fff2d8] hover:bg-[#f6ac9e] text-black rounded-full text-xl font-semibold px-8 py-3 mb-4 transition-colors hover:shadow-inner"
                        onClick={() => navigate('/login/member')}
                    >
                        Log in as a Member
                    </button>
                    
                    <button 
                        className="w-full max-w-[315px] bg-[#fff2d8] hover:bg-[#f6ac9e] text-black rounded-full text-xl font-semibold px-8 py-3 mb-4 transition-colors hover:shadow-inner"
                        onClick={() => navigate('/login/librarian')}
                    >
                        Log in as a Librarian
                    </button>
                </div>

            </div>
        </div>
    );
};

export default MainLogin;