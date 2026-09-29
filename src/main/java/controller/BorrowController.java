package controller;

import logic.BookControl.BookLogic;
import logic.BookControl.BorrowedBook;
import logic.BookControl.CopyOfBook;
import logic.BookControl.BorrowStatus;
import logic.BorrowControl.BorrowLogic;
import logic.ExtensionControl.ExtensionLogic;
import logic.InvoiceControl.InvoiceLogic;
import logic.InvoiceControl.Subject;
import logic.ReportControl.BorrowHistory;
import logic.ReportControl.BorrowTracking;
import logic.ReportControl.ReportLogic;
import logic.Subscriber;
import logic.subscriberLogic;
import logic.BorrowRequest;
import logic.ChangeReturnDateRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/borrows")
public class BorrowController {
    
    private final BorrowLogic borrowLogic = new BorrowLogic();
    private final BookLogic bookLogic = new BookLogic();
    private final ReportLogic reportLogic = new ReportLogic();
    private final subscriberLogic subLogic = new subscriberLogic();
    private final ExtensionLogic extensionLogic = new ExtensionLogic();
    private final InvoiceLogic invoiceLogic = new InvoiceLogic();
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // borrow a book
    @PostMapping("/")
    public ResponseEntity<?> borrowBook(@RequestBody BorrowRequest request) {
        BorrowedBook book = borrowLogic.borrowBook(
            request.getMemberId(), 
            request.getCopyOfBookId(), 
            request.getLibrarianId(), 
            request.getLibrarianName()
        );

        if(book != null){
            Subscriber sub = subLogic.fetchSubscriberById(request.getMemberId());

            reportLogic.saveBorrowHistory(new BorrowHistory(
                sub.getMembershipNumber(), 
                sub.getMemberFullName(), 
                book.getNameOfBook(),
                book.getBorrowDate(), 
                book.getReturnDate(),
                request.getCopyOfBookId()
            ));
            reportLogic.updateBorrowTracking(new BorrowTracking(LocalDate.now(), 1, 0));
            
            return ResponseEntity.ok(book);
        }
        return ResponseEntity.badRequest().body("Borrow process failed.");
    }
}
