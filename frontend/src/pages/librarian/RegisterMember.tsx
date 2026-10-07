import React, { useState } from 'react';
import { librarianService } from '../../services/librarianService';
import './Librarian.css';

const RegisterMember: React.FC = () => {
    const [id, setId] = useState('');
    const [fullName, setFullName] = useState('');
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [phone, setPhone] = useState('');
    const [email, setEmail] = useState('');

    const handleClear = () => {
        setId(''); setFullName(''); setUsername('');
        setPassword(''); setPhone(''); setEmail('');
    };

    const handleSubmit = async () => {
        if (!id.trim() || !fullName.trim() || !username.trim() || !password.trim() || !phone.trim() || !email.trim()) {
            alert("Please fill all fields to complete registration.");
            return;
        }

        if (!/^\d+$/.test(phone) || phone.length !== 10) {
            alert("Please enter: a valid phone number with only digits and with only 10 digits.");
            return;
        }

        if (!/^\d+$/.test(id) || id.length !== 9) {
            alert("Please enter a valid ID with only digits and with only 9 digits.");
            return;
        }

        if (!email.includes("@") || !email.includes(".")) {
            alert("Please enter a valid email address.");
            return;
        }

        try {
            await librarianService.registerMember({
                memberId: parseInt(id),
                fullName,
                username,
                password,
                phone,
                email
            });
            alert("You have been successfully registered.");
            handleClear();
        } catch (error: any) {
            alert(error.response?.data || "Registration Failed.");
        }
    };

    return (
        <div className="librarian-container" style={{ alignItems: 'center' }}>
            <div style={{ backgroundColor: '#f8eaea', borderRadius: '25px', padding: '30px', width: '500px', display: 'flex', flexDirection: 'column', gap: '15px' }}>
                <div className="input-group" style={{ flexDirection: 'row', alignItems: 'center' }}>
                    <span className="label-text" style={{ width: '160px' }}>ID:</span>
                    <input className="text-field" style={{ flex: 1 }} value={id} onChange={e => setId(e.target.value)} />
                </div>
                <div className="input-group" style={{ flexDirection: 'row', alignItems: 'center' }}>
                    <span className="label-text" style={{ width: '160px' }}>Full Name:</span>
                    <input className="text-field" style={{ flex: 1 }} value={fullName} onChange={e => setFullName(e.target.value)} />
                </div>
                <div className="input-group" style={{ flexDirection: 'row', alignItems: 'center' }}>
                    <span className="label-text" style={{ width: '160px' }}>Username:</span>
                    <input className="text-field" style={{ flex: 1 }} value={username} onChange={e => setUsername(e.target.value)} />
                </div>
                <div className="input-group" style={{ flexDirection: 'row', alignItems: 'center' }}>
                    <span className="label-text" style={{ width: '160px' }}>Password:</span>
                    <input className="text-field" type="password" style={{ flex: 1 }} value={password} onChange={e => setPassword(e.target.value)} />
                </div>
                <div className="input-group" style={{ flexDirection: 'row', alignItems: 'center' }}>
                    <span className="label-text" style={{ width: '160px' }}>Phone Number:</span>
                    <input className="text-field" style={{ flex: 1 }} value={phone} onChange={e => setPhone(e.target.value)} />
                </div>
                <div className="input-group" style={{ flexDirection: 'row', alignItems: 'center' }}>
                    <span className="label-text" style={{ width: '160px' }}>Email Address:</span>
                    <input className="text-field" style={{ flex: 1 }} value={email} onChange={e => setEmail(e.target.value)} />
                </div>
            </div>

            <div style={{ display: 'flex', gap: '20px', marginTop: '30px' }}>
                <button className="menu-button" style={{ backgroundColor: '#6ab7f6', color: 'white' }} onClick={handleSubmit}>Submit</button>
                <button className="menu-button" style={{ backgroundColor: '#ffba52', color: 'white' }} onClick={handleClear}>Clear</button>
            </div>
        </div>
    );
};

export default RegisterMember;