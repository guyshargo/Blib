import React, { useEffect, useState } from 'react';
import { memberService } from '../../services/memberService';
import type { Activity, Member } from '../../types';

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

    const inputClass = "rounded-[17px] text-sm px-[15px] py-2 border border-[#ccc] bg-[#f9f9f9] read-only:bg-[#eee] read-only:text-[#555] read-only:cursor-not-allowed";
    const btnClass = "bg-[#fec999] rounded-full text-black text-base font-semibold border-2 border-transparent px-5 py-2 cursor-pointer transition-all duration-200 hover:bg-[#fda95c]/65 hover:shadow-[inset_0_3px_8px_rgba(0,0,0,0.3)]";

    return (
        <div className="flex flex-col p-[30px] font-sans min-h-screen gap-[30px]">
            <div className="flex gap-10 items-center">
                <div className="flex items-center gap-2.5">
                    <span className="text-base font-bold text-[#333]">Membership Number:</span>
                    <input className={inputClass} readOnly value={member.id} />
                </div>
                <div className="flex items-center gap-2.5">
                    <span className="text-base font-bold text-[#333]">Member freeze status:</span>
                    <input className={inputClass} readOnly value={member.freezeStatus} />
                </div>
            </div>

            <div className="flex gap-10 items-center">
                <div className="flex items-center gap-2.5">
                    <span className="text-base font-bold text-[#333]">Phone Number:</span>
                    <input className={inputClass} readOnly value={member.phoneNum} />
                    <button className={btnClass} onClick={handleUpdatePhone}>Update Phone Number</button>
                </div>
                <div className="flex items-center gap-2.5">
                    <span className="text-base font-bold text-[#333]">Email Address:</span>
                    <input className={inputClass} readOnly value={member.email} />
                    <button className={btnClass} onClick={handleUpdateEmail}>Update Email</button>
                </div>
            </div>

            <div>
                <h3 className="text-base font-bold text-[#333] mb-2.5">Activities:</h3>
                <table className="w-full bg-[#fff8f2] rounded-[5px] border-collapse border-2 border-[#fec999] font-semibold">
                    <thead>
                        <tr>
                            <th className="bg-[#fec999] p-2.5 text-left border-r border-[#e5b488]">Date & Time</th>
                            <th className="bg-[#fec999] p-2.5 text-left border-r border-[#e5b488]">Activity Type</th>
                            <th className="bg-[#fec999] p-2.5 text-left border-r border-[#e5b488]">Description (Entity ID)</th>
                        </tr>
                    </thead>
                    <tbody>
                        {activities.length === 0 ? (
                            <tr><td colSpan={3} className="p-2.5 border-r border-[#f6e6d8] border-b border-[#f6e6d8]">No activities found yet.</td></tr>
                        ) : (
                            activities.map((act, idx) => (
                                <tr key={idx} className="odd:bg-[#fdf6f0] even:bg-[#ffffff] hover:bg-[#f4e2e1] hover:cursor-pointer">
                                    <td className="p-2.5 border-r border-[#f6e6d8] border-b border-[#f6e6d8]">{new Date(act.activityDateTime).toLocaleString()}</td>
                                    <td className="p-2.5 border-r border-[#f6e6d8] border-b border-[#f6e6d8]">{act.type}</td>
                                    <td className="p-2.5 border-r border-[#f6e6d8] border-b border-[#f6e6d8]">{act.entityId}</td>
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