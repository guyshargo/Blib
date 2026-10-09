import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../../services/authService';
import type { LoginRole } from '../../types/enums';


const Login: React.FC = () => {
    const [role, setRole] = useState<LoginRole>('MEMBER');
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    const navigate = useNavigate();

    const handleRoleChange = (selectedRole: LoginRole) => {
        setRole(selectedRole);
        setErrorMessage('');
    };

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setErrorMessage('');

        try {
            if (role === 'MEMBER') {
                const member = await authService.loginMember(username, password);
                alert(`Welcome back, ${member.fullName}!`);
                navigate('/home');
            } else {
                const librarian = await authService.loginLibrarian(username, password);
                alert(`Welcome back, ${librarian.fullName}!`);
                navigate('/librarian-dashboard');
            }
        } catch (error: any) {
            if (error.response?.status === 401 || error.response?.status === 404) {
                setErrorMessage('Username or Password are incorrect. Please try again.');
            } else if (error.message === 'The user is already logged into the system.') {
                setErrorMessage(error.message);
            } else if (error.code === 'ERR_NETWORK') {
                setErrorMessage('Network Error: Cannot connect to the server.');
            } else {
                setErrorMessage('An unexpected error occurred. Please try again.');
            }
        }
    };

    return (
        <main className="min-h-screen bg-[#dac4a5] px-4 py-6 font-sans text-[#3E3028] sm:px-8 sm:py-10">
            <div className="mx-auto flex min-h-[min(760px,calc(100vh-3rem))] w-full max-w-6xl flex-col overflow-hidden bg-[#FFFDF9] shadow-[0_12px_40px_rgba(62,48,40,0.12)] md:min-h-[680px] md:flex-row">
                <section className="flex w-full flex-col justify-center bg-[#F0EAE1] px-8 py-12 sm:px-12 md:w-[46%] md:px-16 lg:px-20">
                    <div className="max-w-md">
                        <p className="mb-5 text-sm font-semibold uppercase tracking-[0.2em] text-[#B83A24]">
                            Your library, wherever you are
                        </p>
                        <h1 className="text-4xl font-semibold tracking-tight text-[#3E3028] sm:text-5xl">
                            Blib Library
                        </h1>
                        <p className="mt-6 max-w-sm text-lg leading-8 text-[#6C5D53]">
                            Sign in to borrow books, manage your reading, and stay connected with your library.
                        </p>
                    </div>

                    <div className="mt-12 border-t border-[#D8C9B5] pt-6">
                        <p className="text-sm text-[#6C5D53]">Just browsing?</p>
                        <button
                            className="mt-2 text-sm font-semibold text-[#8B4A32] underline decoration-[#CBA471] underline-offset-4 transition-colors hover:text-[#B83A24]"
                            onClick={() => navigate('/home')}
                        >
                            Continue as a guest
                        </button>
                    </div>
                </section>

                <section className="flex flex-1 items-center justify-center px-7 py-12 sm:px-12 md:px-14 lg:px-20">
                    <div className="w-full max-w-md">
                        <h2 className="text-3xl font-semibold tracking-tight text-[#3E3028]">Log in</h2>
                        <p className="mt-2 text-sm leading-6 text-[#6C5D53]">
                            Enter your account details to access Blib Library.
                        </p>

                        <div className="mt-8 flex border-b border-[#D8C9B5]" role="tablist" aria-label="Login type">
                            <button
                                type="button"
                                role="tab"
                                aria-selected={role === 'MEMBER'}
                                className={`-mb-px border-b-2 px-5 py-3 text-sm font-semibold transition-colors ${
                                    role === 'MEMBER'
                                        ? 'border-[#B83A24] text-[#3E3028]'
                                        : 'border-transparent text-[#85786D] hover:text-[#3E3028]'
                                }`}
                                onClick={() => handleRoleChange('MEMBER')}
                            >
                                Member
                            </button>
                            <button
                                type="button"
                                role="tab"
                                aria-selected={role === 'LIBRARIAN'}
                                className={`-mb-px border-b-2 px-5 py-3 text-sm font-semibold transition-colors ${
                                    role === 'LIBRARIAN'
                                        ? 'border-[#B83A24] text-[#3E3028]'
                                        : 'border-transparent text-[#85786D] hover:text-[#3E3028]'
                                }`}
                                onClick={() => handleRoleChange('LIBRARIAN')}
                            >
                                Librarian
                            </button>
                        </div>

                        <form onSubmit={handleLogin} className="mt-7 space-y-5">
                            <div>
                                <label htmlFor="login-username" className="mb-2 block text-sm font-semibold text-[#5A4A42]">
                                    Username
                                </label>
                                <input
                                    id="login-username"
                                    type="text"
                                    autoComplete="username"
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    required
                                    className="w-full border border-[#D8C9B5] bg-[#FFFDF9] px-3.5 py-3 text-base text-[#3E3028] outline-none transition focus:border-[#8B4A32] focus:ring-2 focus:ring-[#8B4A32]/15"
                                />
                            </div>

                            <div>
                                <label htmlFor="login-password" className="mb-2 block text-sm font-semibold text-[#5A4A42]">
                                    Password
                                </label>
                                <input
                                    id="login-password"
                                    type="password"
                                    autoComplete="current-password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                    className="w-full border border-[#D8C9B5] bg-[#FFFDF9] px-3.5 py-3 text-base text-[#3E3028] outline-none transition focus:border-[#8B4A32] focus:ring-2 focus:ring-[#8B4A32]/15"
                                />
                            </div>

                            {errorMessage && (
                                <div className="border-l-4 border-[#b33a32] bg-[#fbf1ef] px-3 py-2.5 text-sm text-[#8f2923]" role="alert">
                                    {errorMessage}
                                </div>
                            )}

                            <button
                                type="submit"
                                className="w-full bg-[#8B4A32] px-4 py-3 text-base font-semibold text-white transition-colors hover:bg-[#703A29] focus:outline-none focus:ring-2 focus:ring-[#8B4A32] focus:ring-offset-2"
                            >
                                Log in as {role === 'MEMBER' ? 'Member' : 'Librarian'}
                            </button>
                        </form>
                    </div>
                </section>
            </div>
        </main>
    );
};

export default Login;
