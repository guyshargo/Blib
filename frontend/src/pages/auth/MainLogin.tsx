import React from 'react';
import './SharedLogin.css';

const MainLogin: React.FC = () => {
    return (
        <div className="login-root">
            <div className="login-overlay">
                
                {/* Left Bar */}
                <div className="left-bar">
                    <img src="/images/book_logo.png" alt="Book Logo" style={{ width: '227px', filter: 'drop-shadow(2px 4px 6px rgba(0,0,0,0.3))' }} />
                    <h1 className="label-blib">Blib Library</h1>
                    <p className="left-bar-text">Not a member yet?</p>
                    <button id="guestBtn" className="all-button" onClick={() => console.log('Route to Guest')}>
                        Continue as a Guest
                    </button>
                </div>

                {/* Right Bar */}
                <div className="right-bar">
                    <h1 className="label-blib" style={{ fontSize: '51px', marginBottom: '20px' }}>Welcome back!</h1>
                    <img src="/images/laptop_main.png" alt="Laptop" style={{ width: '343px', marginBottom: '30px' }} />
                    
                    <button className="all-button" onClick={() => console.log('Route to Member Login')}>
                        Log in as a Member
                    </button>
                    <button className="all-button" onClick={() => console.log('Route to Librarian Login')}>
                        Log in as a Librarian
                    </button>
                </div>

            </div>
        </div>
    );
};

export default MainLogin;