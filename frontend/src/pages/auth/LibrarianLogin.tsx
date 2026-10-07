import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../../services/authService';

const LibrarianLogin: React.FC = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    const navigate = useNavigate();

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setErrorMessage('');

        try {
            const librarian = await authService.loginLibrarian(username, password);
            console.log('Librarian Login successful!', librarian);
            alert(`Welcome back, ${librarian.fullName}!`);
            navigate('/librarian-dashboard');

        } catch (error: any) {
            if (error.response?.status === 401 || error.response?.status === 404) {
                setErrorMessage('Username or Password are incorrect. Please try again.');
            } else {
                setErrorMessage('An unexpected error occurred. Please try again.');
            }
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-black/30 bg-login bg-cover bg-center bg-blend-multiply font-sans">
            <div className="flex w-[800px] h-[600px] bg-[#e18b42]/80 shadow-[0_4px_20px_rgba(0,0,0,0.5)]">
                
                {/* Left Bar */}
                <div className="w-[305px] bg-white/60 flex flex-col items-center justify-center p-5 text-center">
                    <img src="/images/rb_82482.png" alt="Library Logo" className="w-[227px] mt-[70px] drop-shadow-[2px_4px_6px_rgba(0,0,0,0.3)]" />
                    <h1 className="text-[44px] font-bold text-[#15083b] my-[15px]">Blib Library</h1>
                    <button className="w-[140px] mt-auto bg-[#95918b] text-white rounded-full text-[19px] font-semibold px-[30px] py-3 transition-colors hover:bg-[#808080] active:bg-[#696969]" onClick={() => navigate('/login')}>
                        Return
                    </button>
                </div>

                {/* Right Bar */}
                <div className="flex-1 bg-[#dcaf8c]/40 flex flex-col items-center justify-center p-10">
                    <h1 className="text-[42px] font-bold text-[#15083b] mb-10">Welcome back, Librarian!</h1>
                    
                    {errorMessage && <div className="text-[#d32f2f] font-bold mb-[15px]">{errorMessage}</div>}

                    <form onSubmit={handleLogin} className="w-full flex flex-col items-center">
                        
                        <div className="flex items-center gap-[15px] mb-5 w-full justify-center">
                            <img src="/images/user.png" alt="User" className="w-[45px] h-[45px] object-contain" />
                            <input 
                                type="text" 
                                placeholder="UserName"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                required
                                className="rounded-[40px] text-[20px] font-semibold px-5 py-2.5 border-none w-[290px] outline-none"
                            />
                        </div>
                        
                        <div className="flex items-center gap-[15px] mb-5 w-full justify-center">
                            <img src="/images/padlock.png" alt="Padlock" className="w-[45px] h-[45px] object-contain" />
                            <input 
                                type="password" 
                                placeholder="Password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                                className="rounded-[40px] text-[20px] font-semibold px-5 py-2.5 border-none w-[290px] outline-none"
                            />
                        </div>

                        <button type="submit" className="w-[105px] mt-5 bg-[#fff2d8] text-black rounded-full text-[20px] font-semibold border-2 border-transparent px-[30px] py-3 cursor-pointer transition-all duration-200 hover:bg-[#f6ac9e] hover:text-[#333] hover:shadow-[inset_0_3px_5px_rgba(0,0,0,0.3)]">
                            Login
                        </button>
                    </form>
                </div>

            </div>
        </div>
    );
};

export default LibrarianLogin;