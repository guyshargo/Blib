package client;

import java.io.IOException;
import java.util.List;

import common.ChatIF;
import logic.ActivityControl.Activity;
import logic.BookControl.Book;
import logic.BookControl.CopyOfBook;
import logic.BookControl.OrderedBook;
import logic.InvoiceControl.InvoiceMessage;
import logic.Librarian;
import logic.Message;
import logic.ReportControl.Report;
import logic.Subscriber;
import ocsf.client.AbstractClient;
import logic.BookControl.BorrowedBook;

/**
 * Description:
 * Class for the Client in the system
 */
public class ChatClient extends AbstractClient {
    // Attributes
    ChatIF clientUI;
    public static String messageRegisterFailed = "";
    public static boolean awaitResponse = false;
    public static Subscriber sub = null;
    public static BorrowedBook borrowedBook = null;
    public static List<Subscriber> subList = null;
    public static Librarian librarianLogin = null;
    public static Subscriber subscriberLogin = null;
    public static List<BorrowedBook> borrowList = null;
    public static CopyOfBook availableCopy = null;
    public static Activity activity = null;
    public static List<Activity> ActivityList = null;
    public static List<Book> bookList = null;
    public static BorrowedBook closetReturnBook = null;
    public static boolean isExtend = false;
    public static List<OrderedBook> orderList = null;
    public static boolean isCanceled = false;
    public static String isOrdered = null;
    public static List<InvoiceMessage> invoiceList = null;
    public static List<Integer> monthsList = null;
    public static List<Integer> yearsList = null;
    public static Report report = null;
    public static Book foundBook;
    public static boolean isBorrowDeleted = false;

    /**
     * Description:
     * Method for constructing the given class
     *
     * @param host     String.class
     * @param port     int
     * @param clientUI ChatIF.class
     */
    public ChatClient(String host, int port, ChatIF clientUI) throws IOException {
        // Call the superclass constructor
        super(host, port);

        this.clientUI = clientUI;
        openConnection();
    }

    /**
     * Description:
     * Method for answering messages given by the Server, based on given message
     *
     * @param msg Object.class
     */
    public void handleMessageFromServer(Object msg) {
        awaitResponse = false;
        //if subscriber is not found
        if (msg == null) {
            sub = null;
            return;
        }
        Message messageFromServer = (Message) msg;
        switch (messageFromServer.GetCommand()) {
            case "Quit":
                quit();
                break;
            case "fetchAllActivitiesForASubscriber":
                ActivityList = (List<Activity>) messageFromServer.getData();
                break;
            case "FetchAllBorrowedBooks":
                borrowList = (List<BorrowedBook>) messageFromServer.getData();
                break;
            case "addActivity":
                break;
            case "viewReaderCard":
                sub = (Subscriber) messageFromServer.getData();
                break;
            case "UpdateReaderCardMember":
                sub = (Subscriber) messageFromServer.getData();
                break;
            case "memberNotFound":
                sub = null;
                break;
            case "FetchAllSubscribers":
                subList = (List<Subscriber>) messageFromServer.getData();
                break;
            case "RegistrationFailed":
                messageRegisterFailed = (String) messageFromServer.getData();
                sub = null;
                break;
            case "RegisterSucceeded":
                messageRegisterFailed = "";
                sub = (Subscriber) messageFromServer.getData();
                break;
            case "LogInMember":
                subscriberLogin = (Subscriber) messageFromServer.getData();
                break;
            case "LogInLibrarian":
                librarianLogin = (Librarian) messageFromServer.getData();
                break;
            case "BorrowBookSuccess":
                borrowedBook = (BorrowedBook) messageFromServer.getData();
                break;
            case "BorrowBookFailure":
                borrowedBook = null;
                break;
            case "ChangeReturnDate":
                break;
            case "findAvailableCopyOfBook":
                availableCopy = (CopyOfBook) messageFromServer.getData();
                break;
            case "setFreezeStatus":
                if (((String) messageFromServer.getData()).equals("False"))
                    sub = null;
                break;
            case "ClosestReturnDateBook":
                closetReturnBook = (BorrowedBook) messageFromServer.getData();
                break;
            case "ExtendBorrow":
                isExtend = Boolean.valueOf(messageFromServer.getData().toString());
                break;
            case "FetchAllOrderedBooks":
                orderList = (List<OrderedBook>) messageFromServer.getData();
                break;
            case "CancelOrderedBook":
                isCanceled = Boolean.valueOf(messageFromServer.getData().toString());
                break;
            case "SearchBooksResult":
                bookList = (List<Book>) messageFromServer.getData();
                break;
            case "OrderBook":
                isOrdered = messageFromServer.getData().toString();
                break;
            case "generateReport":
                if (messageFromServer.getData() == null)
                    report = null;
                else
                    report = (Report) messageFromServer.getData();
                break;
            case "fetchAvailableYearsAndMonthsSuccess":
                Object[] datesData = (Object[]) messageFromServer.getData();
                yearsList = (List<Integer>) datesData[0];
                monthsList = (List<Integer>) datesData[1];
                break;
            case "ImportInvoiceMessages":
                invoiceList = (List<InvoiceMessage>) messageFromServer.getData();
                break;
            case "FindBookById":
                foundBook = (Book) messageFromServer.getData(); // Assign the book from the server response
                break;
            case "ChangeLoginStatus":
                break;
            case "DeleteBorrowedBook":
                if (messageFromServer.getData().equals("Success"))
                    isBorrowDeleted = true;
                else
                    isBorrowDeleted = false;
                break;
            default:
                System.out.println("Message type not recognized");
                break;
        }
    }

    /**
     * Description:
     * Method for answering a message from the ClientUI, based on given message
     *
     * @param message Object.class
     */
    public void handleMessageFromClientUI(Object message) {
        try {
            awaitResponse = true;
            sendToServer(message);
            // wait for response
            while (awaitResponse) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            clientUI.display("Could not send message to server: Terminating client." + e);
            quit();
        }
    }

    /**
     * Description:
     * Method for shutting down the Client
     */
    public void quit() {
        try {
            //close the connection
            closeConnection();
        } catch (IOException e) {
            //return to enter ip for server page
        } finally {
            System.exit(0);
        }
    }
}