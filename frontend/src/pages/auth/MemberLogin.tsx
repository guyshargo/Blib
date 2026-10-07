import React, { useState } from 'react';
import { authService } from '../../services/authService';
import './SharedLogin.css';

const MemberLogin: React.FC = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setErrorMessage('');

        try {
            const subscriber = await authService.loginMember(username, password);
            console.log('Login successful!', subscriber);
            alert(`Welcome back, ${subscriber.fullName}!`);
            // route to catalog here
        } catch (error: any) {
            if (error.response?.status === 401 || error.response?.status === 404) {
                setErrorMessage('Username or Password are incorrect. Please try again.');
            } else {
                setErrorMessage('An unexpected error occurred. Please try again.');
            }
        }
    };

    return (
        <div className="login-root">
            <div className="login-overlay">
                
                {/* Left Bar */}
                <div className="left-bar">
                    <img src="/images/rb_82482.png" alt="Library Logo" style={{ width: '227px', marginTop: '70px', filter: 'drop-shadow(2px 4px 6px rgba(0,0,0,0.3))' }} />
                    <h1 className="label-blib" style={{ fontSize: '44px' }}>Blib Library</h1>
                    <button id="btnReturn" className="button" onClick={() => console.log('Route to Main Login')}>
                        Return
                    </button>
                </div>

                {/* Right Bar */}
                <div className="right-bar">
                    <h1 className="label-blib" style={{ fontSize: '50px', marginBottom: '40px' }}>Hello Member!</h1>
                    
                    {errorMessage && <div className="error-message">{errorMessage}</div>}

                    <form onSubmit={handleLogin} style={{ width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                        
                        <div className="input-row">
                            <img src="/images/user.png" alt="User" className="input-icon" />
                            <input 
                                type="text" 
                                placeholder="UserName"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                required
                                className="text-field"
                            />
                        </div>
                        
                        <div className="input-row">
                            <img src="/images/padlock.png" alt="Padlock" className="input-icon" />
                            <input 
                                type="password" 
                                placeholder="Password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                                className="text-field"
                            />
                        </div>

                        <button type="submit" className="button" style={{ width: '105px', marginTop: '20px' }}>
                            Login
                        </button>
                    </form>
                </div>

            </div>
        </div>
    );
};

export default MemberLogin;