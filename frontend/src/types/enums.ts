export enum ActivityType {
    BORROW = 'Borrow',
    RETURN = 'Return',
    FREEZE_STATUS = 'Freeze Status',
    EXTEND = 'Borrow Extension',
    ORDER = 'Order',
    CANCEL_ORDER = 'CancelOrder',
    REGISTRATION = 'Registration',
    LATE_BOOK_RETURN = 'Late Return',
}

export enum ArrivalStatus {
    ARRIVED = 'Arrived',
    NOT_ARRIVED = 'Not Arrived',
}

export enum BookGenre {
    FICTION = 'Fiction',
    ROMANCE = 'Romance',
    FANTASY = 'Fantasy',
    SCI_FI = 'Sci-Fi',
    MYSTERY = 'Mystery',
    THRILLER = 'Thriller',
    HORROR = 'Horror',
    HISTORICAL_FICTION = 'Historical Fiction',
    LITERATURE = 'Literature',
    SCIENCE = 'Science',
    HISTORY = 'History',
    TECHNOLOGY = 'Technology',
    MATHEMATICS = 'Mathematics',
    ART = 'Art',
    PHILOSOPHY = 'Philosophy',
    HEALTH = 'Health',
    PSYCHOLOGY = 'Psychology',
    BUSINESS = 'Business',
    TRAVEL = 'Travel',
}

export enum BorrowStatus {
    BORROWED = 'Borrowed',
    NOT_BORROWED = 'NotBorrowed',
}

export enum FreezeStatus {
    FROZEN = 'Frozen',
    NOT_FROZEN = 'NotFrozen',
}

export enum IsOrdered {
    YES = 'Yes',
    NO = 'No',
}

export enum IsRead {
    READ = 'Read',
    NOT_READ = 'Not Read',
}

export enum ReportType {
    MEMBER_STATUS_REPORT = 'memberStatusReport',
    BORROW_REPORT = 'borrowReport',
    STATUS_TRACKING = 'statusTracking',
    BORROW_TRACKING = 'borrowTracking',
}

export enum Subject {
    GENERAL_MESSAGE = 'General',
    EXTENSION = 'Extension',
}