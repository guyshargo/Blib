import api from './api';
import type { Member, Librarian } from '../types';

export const authService = {
    loginMember: async (username: string, password: string): Promise<Member> => {
        // Fetch the user
        const response = await api.get<Member>('/auth/login/member', {
            params: { username, password }
        });
        
        const member = response.data;

        // Validate login status
        if (member.loginStatus) {
            throw new Error('The user is already logged into the system.');
        }

        // Update status in database to true
        await api.put(`/members/${member.memberId}/login-status`, null, {
            params: { status: true }
        });

        // Save session locally
        member.loginStatus = true;
        localStorage.setItem('currentUser', JSON.stringify(member));
        localStorage.setItem('userType', 'MEMBER');

        return member;
    },

    loginLibrarian: async (username: string, password: string): Promise<Librarian> => {
        // Fetch the librarian
        const response = await api.get<Librarian>('/auth/login/librarian', {
            params: { username, password }
        });
        
        const librarian = response.data;

        // Validate login status
        if (librarian.loginStatus) {
            throw new Error('The user is already logged into the system.');
        }

        // Update status in database to true
        await api.put(`/librarians/${librarian.id}/login-status`, null, {
            params: { status: true }
        });

        // Save session locally
        librarian.loginStatus = true;
        localStorage.setItem('currentUser', JSON.stringify(librarian));
        localStorage.setItem('userType', 'LIBRARIAN');

        return librarian;
    },

    logout: async () => {
        const userStr = localStorage.getItem('currentUser');
        const userType = localStorage.getItem('userType');

        if (userStr && userType) {
            const user = JSON.parse(userStr);
            
            try {
                if (userType === 'MEMBER') {
                    await api.put(`/members/${user.memberId}/login-status`, null, { params: { status: false } });
                } else if (userType === 'LIBRARIAN') {
                    await api.put(`/librarians/${user.id}/login-status`, null, { params: { status: false } });
                }
            } catch (error) {
                console.error("Failed to update logout status in database", error);
            }
        }

        // Clear the local session
        localStorage.removeItem('currentUser');
        localStorage.removeItem('userType');
    }
};