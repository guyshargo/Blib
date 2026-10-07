import { BookGenre,
    FreezeStatus,
    ActivityType,
    IsOrdered,
    BorrowStatus,
    Subject,
    IsRead,
    ArrivalStatus,
    ReportType
 } from './enums';

export interface Activity {
    membershipNumber: number;
    type: ActivityType;
    entityId: number;
    activityDateTime: string;
}

export interface Book {
    title: string;
    genre: BookGenre;
    copiesNum: number;
    borrowedNum: number;
    keywords: string;
    isOrdered: IsOrdered;
    bookId: number;
    ordersNum: number;
    summary?: string;
}

export interface BorrowedBook {
    borrowDate: string;
    returnDate: string;
    memberId: number;
    title: string;
    librarianName: string;
    copyOfBookId: number;
    extensionDate?: string;
    librarianId: number;
    bookId: number;
}

export interface BorrowHistory {
    copyOfBookId: number;
    memberId: number;
    memberName: string;
    bookName: string;
    borrowDate: string;
    originalReturnDate: string;
    actualReturnDate?: string;
    lateDays: number;
}

export interface BorrowTracking {
    date: string;
    borrowCount: number;
    lateCount: number;
}

export interface CopyOfBook {
    copyId: number;
    CopyOfBookName: string;
    borrowStatus: BorrowStatus;
    shelfLocation: string;
    barcode: string;
    bookId: number;
}

export interface InvoiceMessage {
    msgId: number;
    memberId: number;
    userName: string;
    fullName: string;
    subject: Subject;
    content: string;
    date: string;
    isRead: IsRead;
}

export interface Librarian {
    fullName: string;
    phoneNum: string;
    id: number;
    userName: string;
    password?: string;
    email: string;
    loginStatus: boolean;
}

export interface MemberStatusChange {
    id: number;
    fullName: string;
    freezeStatus: FreezeStatus;
    changeStatusDate: string;
}

export interface OrderedBook {
    orderId: number;
    memberName: string;
    memberPhone: string;
    memberEmail: string;
    memberId: number;
    orderDate: string;
    arrivalStatus: ArrivalStatus;
    bookId: number;
    title: string;
    arrivalDate?: string;
}

export interface Report {
    date: string;
    type: ReportType;
    data: string;
}

export interface StatusTracking {
    date: string;
    frozenMembers: number;
    notFrozenMembers: number;
}

export interface Member {
    id: number;
    fullName: string;
    userName: string;
    password?: string;
    freezeStatus: FreezeStatus;
    email: string;
    phoneNum: string;
    freezeStatusDate?: string;
    readerCardBarcode: string;
    loginStatus: boolean;
}

export interface BorrowRequest {
    memberId: number;
    copyOfBookId: number;
    librarianId: number;
    librarianName: string;
}

export interface ChangeReturnDateRequest {
    memberId: number;
    copyOfBookId: number;
    newReturnDate: string;
    librarianName: string;
    librarianId: number;
    extensionDate: string;
}

export interface OrderRequest {
    bookId: number;
    memberId: number;
    memberName: string;
    memberPhone: string;
    memberEmail: string;
}

export interface RegisterRequest {
    membershipNumber: number;
    fullName: string;
    userName: string; 
    password: string;
    phone: string;
    email: string;
}

export interface ActivityRequest {
    membershipNumber: number;
    activityType: string;
    entityId: number;
}
