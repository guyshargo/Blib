export type ActivityType = 
    | 'DEFAULT' 
    | 'BORROW' 
    | 'RETURN' 
    | 'FREEZE_STATUS' 
    | 'EXTEND' 
    | 'ORDER' 
    | 'CANCEL_ORDER' 
    | 'REGISTRATION' 
    | 'LATE_BOOK_RETURN';

export type ArrivalStatus = 
    | 'ARRIVED' 
    | 'NOT_ARRIVED';

export type BookGenre = 
    | 'FICTION' 
    | 'ROMANCE' 
    | 'FANTASY' 
    | 'SCI_FI' 
    | 'MYSTERY' 
    | 'THRILLER' 
    | 'HORROR' 
    | 'HISTORICAL_FICTION' 
    | 'LITERATURE' 
    | 'SCIENCE' 
    | 'HISTORY' 
    | 'TECHNOLOGY' 
    | 'MATHEMATICS' 
    | 'ART' 
    | 'PHILOSOPHY' 
    | 'HEALTH' 
    | 'PSYCHOLOGY' 
    | 'BUSINESS' 
    | 'TRAVEL';

export type BorrowStatus = 
    | 'BORROWED' 
    | 'NOT_BORROWED';

export type FreezeStatus = 
    | 'FROZEN' 
    | 'NOT_FROZEN';

export type IsOrdered = 
    | 'YES' 
    | 'NO';

export type IsRead = 
    | 'READ' 
    | 'NOT_READ';

export type ReportType = 
    | 'MEMBER_STATUS_REPORT' 
    | 'BORROW_REPORT' 
    | 'STATUS_TRACKING' 
    | 'BORROW_TRACKING';

export type InvoiceSubject = 
    | 'GENERAL_MESSAGE' 
    | 'EXTENSION';

export type LoginRole = 
    | 'MEMBER' 
    | 'LIBRARIAN';
