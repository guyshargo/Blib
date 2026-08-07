package Server;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import client.ChatClient;
import gui.ServerGUI.ServerConnectionTable;
import logic.ActivityControl.ActivityType;
import logic.ReportControl.*;
import logic.InvoiceControl.InvoiceLogic;
import logic.InvoiceControl.InvoiceMessage;
import logic.InvoiceControl.Subject;
import logic.OrderControl.OrderLogic;
import logic.ExtensionControl.ExtensionLogic;

import logic.*;
import logic.ActivityControl.Activity;
import logic.ActivityControl.activityLogic;
import logic.BookControl.*;
import logic.BorrowControl.BorrowLogic;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

/**
 * Description:
 * Class for the Server of the system
 */
public class EchoServer extends AbstractServer {

    /**
     * Description:
     * Method for constructing the given class
     *
     * @param port int
     */
    public EchoServer(int port) {
        super(port);
        LibrarianLogic librarianLogic = new LibrarianLogic();
        subscriberLogic subscriberLogic = new subscriberLogic();
        librarianLogic.logOutStatusToAllLibrarian();
        subscriberLogic.logOutStatusToAllSubscribers();
    }

    /**
     * Description:
     * Method for processing the message given by the Client
     *
     * @param msg    Object.class
     * @param client ConnectionToClient.class
     */
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        // Get object Message
        Message messageC = (Message) msg;
        String[] data = (String[]) messageC.getData();

        // Variables to use
        Subscriber sub = null;
        Librarian lib;
        Message msgReturn;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // System logics to use
        OrderLogic orderLogic = new OrderLogic();
        InvoiceLogic invoiceLogic = new InvoiceLogic();
        ReportLogic reportLogic = new ReportLogic();
        BookLogic bookLogic = new BookLogic();
        subscriberLogic subLogic = new subscriberLogic();
        activityLogic actLogic = new activityLogic();
        BorrowLogic borrowLogic = new BorrowLogic();
        LibrarianLogic liblogic = new LibrarianLogic();

        // Switch loop using the provided command in msg
        switch (messageC.GetCommand()) {

            // Case when librarian wants to view a member's reader card
            case "ViewReaderCard":
                sub = subLogic.fetchSubscriberById(Integer.valueOf(data[0]));
                if (sub == null) {
                    msgReturn = new Message("memberNotFound", null);
                } else {
                    msgReturn = new Message("viewReaderCard", sub);
                }
                try {
                    // Send the subscriber details to the client
                    client.sendToClient(msgReturn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // Case when librarian wants to update a member's reader card
            case "UpdateReaderCardMember":
                int id = Integer.valueOf(data[0]);
                String phone = data[1];
                String email = data[2];
                boolean isUpdated = subLogic.updateSubscriberContact(id, phone, email);
                try {
                    if (isUpdated) {
                        msgReturn = new Message("viewReaderCard", sub);
                        client.sendToClient(msgReturn);
                    } else {
                        client.sendToClient(null);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // Case to retrieve all subscribers from the member_db
            case "FetchAllSubscribers":
                List<Subscriber> subscriberList = subLogic.fetchAllSubscribers();
                msgReturn = new Message("FetchAllSubscribers", subscriberList);
                try {
                    client.sendToClient(msgReturn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // Case when librarian wants to register a new member
            case "RegisterMember":
                int registerId = Integer.valueOf(data[0]);
                String fullName = data[1];
                String username = data[2];
                String password = data[3];
                String memberPhone = data[4];
                String memberEmail = data[5];

                // Check for the same given user details in the member_db
                msgReturn = subLogic.checkDuplicates(registerId, username, memberEmail);
                if (!msgReturn.GetCommand().equals("RegistrationFailed")) {
                    Subscriber registerNewMember = subLogic.registerNewMember(
                            registerId, fullName, username, password, FreezeStatus.NOT_FROZEN, memberPhone, memberEmail
                    );
                    reportLogic.saveMemberStatusChange(new MemberStatusChange(registerId, fullName, FreezeStatus.NOT_FROZEN, LocalDate.now()));
                    reportLogic.updateStatusTracking(new StatusTracking(LocalDate.now(), 0, 1));
                    msgReturn = new Message("RegisterSucceeded", registerNewMember);
                }
                try {
                    client.sendToClient(msgReturn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // Case to retrieve all borrowed books from the borrowedBooks_db
            case "FetchAllBorrowedBooks":
                List<BorrowedBook> subBorrowedBooks = bookLogic.importBorrowedBooks(Integer.valueOf(data[0]));
                msgReturn = new Message("FetchAllBorrowedBooks", subBorrowedBooks);
                try {
                    client.sendToClient(msgReturn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // Case to retrieve all activities of a given subscriber from the activity_db
            case "fetchAllActivitiesForASubscriber":
                List<Activity> activityList = actLogic.fetchAllActivitiesForASubscriber(Integer.valueOf(data[0]));
                msgReturn = new Message("fetchAllActivitiesForASubscriber", activityList);
                try {
                    client.sendToClient(msgReturn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // Case when a librarian changes the return date of a borrowed book of a subscriber
            case "ChangeReturnDate":
                try {
                    // Parsing the string as LocalDate
                    LocalDate newReturnDate = LocalDate.parse(data[2], formatter);
                    LocalDate extensionDate = LocalDate.parse(data[5], formatter);
                    // Updating the date and marking the librarian id who did the extension
                    boolean updatedReturnDate = borrowLogic.setReturnDate(Integer.valueOf(data[0]), Integer.valueOf(data[1]), newReturnDate);
                    boolean updateLibrarian = borrowLogic.setLibrarianForReturnDate(Integer.valueOf(data[0]), Integer.valueOf(data[1]), data[3], Integer.valueOf(data[4]), extensionDate);
                    BorrowedBook borrowedBook = bookLogic.fetchBorrowedBook(Integer.valueOf(data[0]), Integer.valueOf(data[1]));
                    if (updatedReturnDate && updateLibrarian) {
                        reportLogic.updateOriginalReturnDate(borrowedBook.getMembershipNumber(), borrowedBook.getCopyOfBookId(), borrowedBook.getBorrowDate(), borrowedBook.getReturnDate());
                        msgReturn = new Message("ChangeReturnDate", "True");
                    } else {
                        msgReturn = new Message("ChangeReturnDate", "False");
                    }
                    client.sendToClient(msgReturn);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            // Case when a librarian wants to change a subscriber's freeze status
            case "SetSubscriberStatus":
                try {
                    LocalDate freezeDate = LocalDate.parse(data[2], formatter);
                    boolean setStatus = subLogic.setFreezeStatus(Integer.valueOf(data[0]), data[1], freezeDate);
                    Subscriber subscriber = subLogic.fetchSubscriberById(Integer.valueOf(data[0]));
                    if (setStatus) {
                        reportLogic.saveMemberStatusChange(new MemberStatusChange(subscriber.getMembershipNumber(), subscriber.getMemberFullName(), FreezeStatus.fromDbValue(data[1]), freezeDate));
                        if (subscriber.getMemberFreezeStatus() == FreezeStatus.NOT_FROZEN) {
                            reportLogic.updateStatusTracking(new StatusTracking(LocalDate.now(), -1, 1));
                        } else {
                            reportLogic.updateStatusTracking(new StatusTracking(LocalDate.now(), 1, -1));
                        }
                        msgReturn = new Message("setFreezeStatus", "True");
                    } else
                        msgReturn = new Message("setFreezeStatus", "False");
                    client.sendToClient(msgReturn);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            // Case when a librarian wants to scan the subscribers reader card
            case "ScanReaderCard":
                sub = subLogic.fetchSubscriberScanBarcode((data[0]));
                if (sub == null) {
                    msgReturn = new Message("memberNotFound", null);
                } else {
                    msgReturn = new Message("viewReaderCard", sub);
                }
                try {
                    client.sendToClient(msgReturn);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            // Case when a librarian logs in the system
            case "LogInLibrarian":
                lib = liblogic.fetchLibrarianByUsername(data[0]);
                msgReturn = new Message("LogInLibrarian", lib);
                try {
                    // Send the librarian details to the client
                    client.sendToClient(msgReturn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // Case when a member logs in the system
            case "LogInMember":
                sub = subLogic.fetchMemberByUsername(data[0]);
                msgReturn = new Message("LogInMember", sub);
                try {
                    client.sendToClient(msgReturn); // Send the member details to the client
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // Case when a member borrows a book from the library
            case "BorrowBook":
                // Extract the data from the message
                int memberId = Integer.valueOf(data[0]);
                int copyOfBookID = Integer.valueOf(data[1]);
                int librarianID = Integer.valueOf(data[2]);
                Subscriber subBorrow = subLogic.fetchSubscriberById(memberId);
                String librarianName = data[3];
                BorrowedBook borrowBook = borrowLogic.borrowBook(memberId, copyOfBookID, librarianID, librarianName);
                if (borrowBook != null) {
                    reportLogic.saveBorrowHistory(new BorrowHistory(memberId, subBorrow.getMemberFullName(),
                            borrowBook.getNameOfBook(), borrowBook.getBorrowDate(), borrowBook.getReturnDate(),
                            copyOfBookID));
                    reportLogic.updateBorrowTracking(new BorrowTracking(LocalDate.now(), 1, 0));
                    // Respond to the client that the borrow process was successful
                    msgReturn = new Message("BorrowBookSuccess", borrowBook);
                } else {
                    // Respond to the client that the borrow process has failed
                    msgReturn = new Message("BorrowBookFailure", null);
                }
                try {
                    client.sendToClient(msgReturn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // Case when the system wants to add an activity into the activity_db
            case "addActivity":
                try {
                    // Attributes given by the message
                    int act_membershipNumber = Integer.valueOf(data[0]);
                    ActivityType activityType = actLogic.generateActivityType(data[1]);
                    String act_description = data[2];
                    // Making a new activity
                    Activity activity = new Activity(
                            act_membershipNumber,
                            activityType,
                            act_description,
                            LocalDateTime.now());
                    // Adding the new activity to the database
                    actLogic.addActivity(activity);
                    msgReturn = new Message("addActivity", null);
                    client.sendToClient(msgReturn);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "findAvailableCopyByBarcode":
                // Fetch available copy of the book for borrowing
                CopyOfBook availableBookCopyBarcode = borrowLogic.getAvailableCopyOfBookByBarcode(data[0]);
                if (availableBookCopyBarcode != null) {
                    Book findBook = bookLogic.fetchBook(availableBookCopyBarcode.getBookId());
                    boolean checkIfAbookIsntReserved = borrowLogic.checkIfAMemberCanBorrowTheBook(Integer.valueOf(data[1]), availableBookCopyBarcode, findBook);
                    if (checkIfAbookIsntReserved)
                        msgReturn = new Message("findAvailableCopyOfBook", availableBookCopyBarcode);
                    else {
                        msgReturn = new Message("findAvailableCopyOfBook", null);
                    }
                } else {
                    msgReturn = new Message("findAvailableCopyOfBook", null);
                }
                try {
                    client.sendToClient(msgReturn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case "findAvailableCopyOfBook":
                // Fetch available copy of the book for borrowing
                CopyOfBook availableBookCopy = borrowLogic.getAvailableCopyOfBook(Integer.valueOf(data[0]));
                if (availableBookCopy != null) {
                    Book findBook = bookLogic.fetchBook(availableBookCopy.getBookId());
                    boolean checkIfAbookIsntReserved = borrowLogic.checkIfAMemberCanBorrowTheBook(Integer.valueOf(data[1]), availableBookCopy, findBook);
                    if (checkIfAbookIsntReserved)
                        msgReturn = new Message("findAvailableCopyOfBook", availableBookCopy);
                    else {
                        msgReturn = new Message("findAvailableCopyOfBook", null);
                    }
                } else {
                    msgReturn = new Message("findAvailableCopyOfBook", null);
                }
                try {
                    client.sendToClient(msgReturn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "ClosestReturnDateBook":
                msgReturn = new Message("ClosestReturnDateBook", borrowLogic.getCloserReturnDateBook(Integer.valueOf(data[0])));
                try {
                    client.sendToClient(msgReturn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "SearchBooks":
                String bookName = data[0];
                String bookSubject = data[1];
                String freeText = data[2];
                List<Book> foundBooks = bookLogic.searchBooks(bookName, bookSubject, freeText);
                msgReturn = new Message("SearchBooksResult", foundBooks);
                try {
                    client.sendToClient(msgReturn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // case when subscriber wants to fetch all his ordered books
            case "FetchAllOrderedBooks":
                List<OrderedBook> subOrderedBooks = bookLogic.importOrderedBooks(Integer.valueOf(data[0]));
                messageC = new Message("FetchAllOrderedBooks", subOrderedBooks);

                try {
                    client.sendToClient(messageC);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // case when subscriber wants to extend his borrowed book date of return
            case "ExtendBorrow":
                ExtensionLogic extensionLogic = new ExtensionLogic();
                BookLogic book_Logic = new BookLogic();
                // Attributes of borrowed book
                int membershipNumber = Integer.valueOf(data[0]);
                int copyOfBookId = Integer.valueOf(data[1]);
                // Fetch the borrowed book of the subscriber
                BorrowedBook borrowedBook = book_Logic.fetchBorrowedBook(membershipNumber, copyOfBookId);
                // Extend the books return date, return boolean for confirmation
                boolean isExtend = extensionLogic.borrowExtensionRequest(borrowedBook);
                if (isExtend) {
                    BorrowedBook updatedborrowedBook = book_Logic.fetchBorrowedBook(membershipNumber, copyOfBookId);
                    reportLogic.updateOriginalReturnDate(updatedborrowedBook.getMembershipNumber(), updatedborrowedBook.getCopyOfBookId(), updatedborrowedBook.getBorrowDate(), updatedborrowedBook.getReturnDate());
                }
                // Retrieve subscriber that made the extenstion
                Subscriber subscriber = subLogic.fetchSubscriberById(membershipNumber);
                // Content of extension message
                String content = "Subscriber " + subscriber.getMemberFullName() + " has extended the book \"" +
                        borrowedBook.getNameOfBook() + "\". Be aware of changes made in his Activity list.";
                // Sending an invoice message to the librarian (inserting to invoice_db)
                invoiceLogic.sendMessage(subscriber.getMembershipNumber(), subscriber.getUserName(),
                        subscriber.getMemberFullName(), Subject.EXTENSION, content);

                // Send to Client
                messageC = new Message("ExtendBorrow", "" + isExtend);
                try {
                    client.sendToClient(messageC);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // case subscriber wants to cancel an ordered book
            case "CancelOrderedBook":
                int orderID = Integer.valueOf((data[0]));
                // Cancel ordered book, return if succeeded
                boolean cancelMessage = orderLogic.cancelOrder(orderID);

                // If successfully canceled book change the arrival status for the next ordered book
                if (cancelMessage && data[2].equals("Arrived"))
                    orderLogic.changeArrivalStatus(data[1]);

                messageC = new Message("CancelOrderedBook", "" + true);
                try {
                    client.sendToClient(messageC);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // case subscriber wants to search specific book to order
            case "SearchBookToOrder":
                String orderbookName = data[0];
                int book_ID = -1;
                if (!data[1].equals("null")) {
                    try {
                        Integer.parseInt(data[1]);
                        book_ID = Integer.valueOf(data[1]);
                    } catch (NumberFormatException e) {
                        book_ID = -1;
                    }
                }
                List<Book> foundBook = orderLogic.searchToOrderBook(orderbookName, book_ID);
                messageC = new Message("SearchBooksResult", foundBook);
                try {
                    client.sendToClient(messageC);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // case subscriber wants to search specific book to cancel
            case "SearchBookToCancel":
                int bookID;
                try {
                    Integer.parseInt(data[0]);
                    bookID = Integer.valueOf(data[0]);
                } catch (NumberFormatException e) {
                    bookID = -1;
                }
                List<Book> cancelBook = orderLogic.searchToOrderBook("null", bookID);
                messageC = new Message("SearchBooksResult", cancelBook);
                try {
                    client.sendToClient(messageC);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // case when ordering or canceling a book, change book's order status
            case "ChangeOrderStatus":
                orderLogic.changeBookOrderStatus(Integer.valueOf(data[0]), Integer.valueOf(data[1]),
                        Boolean.valueOf(data[2]));
                messageC = new Message("", "");
                try {
                    client.sendToClient(messageC);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // case subscriber wants to order a searched book
            case "OrderBook":
                String orderMessage;
                try {
                    orderMessage = orderLogic.orderBook(data[0], Integer.valueOf(data[1]), Integer.valueOf(data[2]), data[3],
                            data[4], data[5]);
                    messageC = new Message("OrderBook", orderMessage);
                    try {
                        client.sendToClient(messageC);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            // case import invoice messages
            case "ImportInvoiceMessages":
                List<InvoiceMessage> invoiceList;
                // Import all messages to librarian
                invoiceList = invoiceLogic.importMessages();
                // Send to client
                messageC = new Message("ImportInvoiceMessages", invoiceList);
                try {
                    client.sendToClient(messageC);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // case where changing reader's status
            case "ChangeReadStatus":
                invoiceLogic.readMessage(Integer.valueOf(data[0]));
                messageC = new Message("", "");
                try {
                    client.sendToClient(messageC);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // case where librarian wants to generate a report
            case "generateReport":
                int year = Integer.parseInt(data[2]); // Parse year
                int monthNumber = Integer.parseInt(data[1]); // Parse month number
                // Get the last day of the month
                LocalDate lastDayOfMonth = LocalDate.of(year, monthNumber, 1).withDayOfMonth(
                        LocalDate.of(year, monthNumber, 1).lengthOfMonth());
                Report report = reportLogic.fetchReportByDate(data[0], lastDayOfMonth);
                if (report != null) {
                    msgReturn = new Message("generateReport", report);
                } else {
                    msgReturn = new Message("generateReport", null);
                }
                try {
                    client.sendToClient(msgReturn);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            // case were the system wants to retrieve available years and moths for generating a report
            case "fetchAvailableYearsAndMonths":
                try {
                    // Initialize ReportLogic to fetch available years and months
                    // Fetch available years and months from the database
                    List<Integer> years = reportLogic.getAvailableReportYears();
                    List<Integer> months = reportLogic.getAvailableReportMonths();
                    // Send the years and months to the client
                    Object[] datesData = new Object[]{years, months};
                    msgReturn = new Message("fetchAvailableYearsAndMonthsSuccess", datesData);
                    client.sendToClient(msgReturn);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            // case when deleting a borrowed book
            case "DeleteBorrowedBook":
                try {
                    boolean deleted = false;
                    BorrowedBook updatedborrowedBook = bookLogic.fetchBorrowedBook(Integer.valueOf(data[1]), Integer.valueOf(data[0]));
                    if (updatedborrowedBook != null)
                        deleted = borrowLogic.deleteBorrowedBook(Integer.valueOf(data[0])); // data[0] contains the book ID
                    if (deleted) {
                        // Update the borrow status of the selected book
                        CopyOfBook availableCopy = borrowLogic.findCopyOfBook(Integer.valueOf(data[0]));
                        availableCopy.setBorrowStatus(BorrowStatus.NOT_BORROWED);
                        bookLogic.changeBookCopyBorrowStatus(availableCopy);
                        reportLogic.updateBorrowTracking(new BorrowTracking(LocalDate.now(), -1, 0));
                        reportLogic.updateActualReturnDate(updatedborrowedBook.getMembershipNumber(), updatedborrowedBook.getCopyOfBookId(), updatedborrowedBook.getBorrowDate(), LocalDate.now());
                    }
                    msgReturn = new Message("DeleteBorrowedBook", deleted ? "Success" : "Failure");
                    client.sendToClient(msgReturn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // case when decreasing number of borrowed copies of a book
            case "DecreaseBorrowedCopies":
                try {
                    boolean decreased = borrowLogic.decreaseBorrowedCopies(Integer.valueOf(data[0])); // data[0] contains the book ID
                    msgReturn = new Message("DecreaseBorrowedCopies", decreased ? "Success" : "Failure");
                    client.sendToClient(msgReturn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // case when system needs to fetch a book by given book id
            case "FindBookById":
                try {
                    int bookId = Integer.parseInt(data[0]); // data[0] contains the book ID
                    Book book = borrowLogic.findBookById(bookId);
                    if (book != null) {
                        msgReturn = new Message("FindBookById", book);
                    } else {
                        msgReturn = new Message("FindBookById", "NotFound");
                    }
                    client.sendToClient(msgReturn);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            // case changing the arrival status of an ordered book
            case "ChangeArrivalStatus":
                try {
                    boolean statusChanged = borrowLogic.changeArrivalStatus(data[0]); // data[0] contains the book name
                    msgReturn = new Message("ChangeArrivalStatus", statusChanged ? "Success" : "Failure");
                    client.sendToClient(msgReturn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // case when subscriber logins into the system
            case "SubscriberSetLoginStatus":
                try {
                    boolean statusChanged = subLogic.ChangeLogInStatus(Integer.valueOf(data[0]), Boolean.valueOf(data[1]));
                    msgReturn = new Message("ChangeLoginStatus", statusChanged ? "Success" : "Failure");
                    client.sendToClient(msgReturn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // case when librarian logins into the system
            case "LibrarianSetLoginStatus":
                try {
                    boolean statusChanged = liblogic.ChangeLogInStatus(Integer.valueOf(data[0]), Boolean.valueOf(data[1]));
                    msgReturn = new Message("ChangeLoginStatus", statusChanged ? "Success" : "Failure");
                    client.sendToClient(msgReturn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // case when the system needs to shut down
            case "Quit":
                try {
                    // Close the client connection
                    clientDisconnected(client);
                    client.sendToClient(new Message("Quit", null));

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            // case none of the above cases
            default:
                System.out.println("Unknown command received: " + messageC.GetCommand());
        }
    }

    /**
     * Description:
     * Method for connecting the Client to the Server
     *
     * @param client ConnectionToClient.class
     */
    @Override
    public void clientConnected(ConnectionToClient client) {
        // Get the client's IP address and hostname and change connection status
        ClientInfo clientInfo = new ClientInfo(client.getInetAddress().getHostAddress(), client.getInetAddress().getHostName(), "Connected");
        try {
            ServerConnectionTable.addClientToTable(clientInfo, client);
        } catch (Exception e) {
            System.out.println("Couldnt recieve client information" + e.getMessage());
        }
    }

    /**
     * Description:
     * Method for disconnecting the Client to the Server
     *
     * @param client ConnectionToClient.class
     */
    @Override
    public void clientDisconnected(ConnectionToClient client) {
        try {
            ClientInfo clientInfo = new ClientInfo(client.getInetAddress().getHostAddress(), "Disconnected");
            ServerConnectionTable.removeClientFromTable(clientInfo, client);
        } catch (Exception e) {
            System.out.println("Couldnt disconnect and recive client information" + e.getMessage());
        }
    }

    /**
     * Description:
     * Method for running the Server
     */
    protected void serverStarted() {
        System.out.println("Server listening for connections on port " + getPort());
    }

    /**
     * Description:
     * Method for stopping the Server
     */
    protected void serverStopped() {
        System.out.println("Server has stopped listening for connections.");
    }
}