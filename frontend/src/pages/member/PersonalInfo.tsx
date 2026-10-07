import React, { useEffect, useState } from 'react';
import { memberService } from '../../services/memberService';
import type { Activity, Member } from '../../types';
import './Member.css';

const PersonalInfo: React.FC = () => {
    const [member, setMember] = useState<Member | null>(null);
    const [activities, setActivities] = useState<Activity[]>([]);

    useEffect(() => {
        const userStr = localStorage.getItem('currentUser');
        if (userStr) {
            const sub: Member = JSON.parse(userStr);
            setMember(sub);
            loadActivities(sub.id);
        }
    }, []);

    const loadActivities = async (memberId: number) => {
        try {
            const data = await memberService.getActivities(memberId);
            setActivities(data);
        } catch (error) {
            console.error('Data Error: Could not load activities', error);
        }
    };

    const handleUpdatePhone = async () => {
        if (!member) return;
        const newPhone = window.prompt("Update Phone Number\nPlease enter new Phone Number here:", member.phoneNum);
        
        if (newPhone === null) return;
        if (newPhone.length !== 10 || !/^\d+$/.test(newPhone)) {
            alert("Phone number must be 10 digits and contain only numbers.");
            return;
        }
        await updateContactData(newPhone, member.email);
    };

    const handleUpdateEmail = async () => {
        if (!member) return;
        const newEmail = window.prompt("Update Email Address\nPlease enter new Email Address here:", member.email);
        
        if (newEmail === null) return;
        if (!newEmail.includes("@") || !newEmail.includes(".")) {
            alert("Please enter a valid email address.");
            return;
        }
        await updateContactData(member.phoneNum, newEmail);
    };

    const updateContactData = async (phone: string, email: string) => {
        if (!member) return;
        try {
            const updatedSub = await memberService.updateContact(member.id, phone, email);
            setMember(updatedSub);
            localStorage.setItem('currentUser', JSON.stringify(updatedSub));
            alert("Personal information updated successfully.");
        } catch (error) {
            alert("Network connection failed or failed to update on server.");
        }
    };

    if (!member) return <div>Loading...</div>;

    return (
        <div className="member-container">
            <div className="info-row">
                <div className="info-group">
                    <span className="label-text">Membership Number:</span>
                    <input className="text-field" readOnly value={member.id} />
                </div>
                <div className="info-group">
                    <span className="label-text">Member freeze status:</span>
                    <input className="text-field" readOnly value={member.freezeStatus} />
                </div>
            </div>

            <div className="info-row">
                <div className="info-group">
                    <span className="label-text">Phone Number:</span>
                    <input className="text-field" readOnly value={member.phoneNum} />
                    <button className="menu-button" onClick={handleUpdatePhone}>Update Phone Number</button>
                </div>
                <div className="info-group">
                    <span className="label-text">Email Address:</span>
                    <input className="text-field" readOnly value={member.email} />
                    <button className="menu-button" onClick={handleUpdateEmail}>Update Email</button>
                </div>
            </div>

            <div>
                <h3 className="label-text" style={{ marginBottom: '10px' }}>Activities:</h3>
                <table className="data-table">
                    <thead>
                        <tr>
                            <th>Date & Time</th>
                            <th>Activity Type</th>
                            <th>Description (Entity ID)</th>
                        </tr>
                    </thead>
                    <tbody>
                        {activities.length === 0 ? (
                            <tr><td colSpan={3}>No activities found yet.</td></tr>
                        ) : (
                            activities.map((act, idx) => (
                                <tr key={idx}>
                                    <td>{new Date(act.activityDateTime).toLocaleString()}</td>
                                    <td>{act.type}</td>
                                    <td>{act.entityId}</td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default PersonalInfo;