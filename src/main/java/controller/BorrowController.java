package controller;

import logic.BookLogic;
import logic.BorrowLogic;
import logic.ExtensionLogic;
import logic.InvoiceLogic;
import logic.ReportLogic;
import model.Book;
import model.BorrowHistory;
import model.BorrowTracking;
import model.BorrowedBook;
import model.CopyOfBook;
import model.Subscriber;
import logic.SubscriberLogic;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dto.BorrowRequest;
import dto.ChangeReturnDateRequest;
import enums.BorrowStatus;
import enums.Subject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/borrows")
public class BorrowController {
    
    private final BorrowLogic borrowLogic = new BorrowLogic();
    private final BookLogic bookLogic = new BookLogic();
    private final ReportLogic reportLogic = new ReportLogic();
    private final SubscriberLogic subLogic = new SubscriberLogic();
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

    // view all borrowed books of a specific subscriber
    @GetMapping("/subscriber/{subId}")
    public ResponseEntity<List<BorrowedBook>> getBorrowedBooks(@PathVariable int subId){
        List<BorrowedBook> suBorrowedBooks = borrowLogic.importBorrowedBooks(subId);
        return ResponseEntity.ok(suBorrowedBooks);
    }

    // extending due date
    @PutMapping("/due-date-extension")
    public ResponseEntity<String> dueDateExtenstion(@RequestBody ChangeReturnDateRequest request){
        try {
            LocalDate extendedDueDate = LocalDate.parse(request.getNewReturnDate(), formatter);
            LocalDate extensionApprovalDate = LocalDate.parse(request.getExtensionDate(), formatter);

            boolean updatedReturnDate = borrowLogic.setReturnDate(
                request.getMemberId(),
                request.getCopyOfBookId(),
                extendedDueDate
            );

            boolean opLibrarian = borrowLogic.setLibrarianForReturnDate(
                request.getMemberId(),
                request.getCopyOfBookId(),
                request.getLibrarianName(),
                request.getLibrarianId(),
                extensionApprovalDate
            );

            if(updatedReturnDate && opLibrarian){
                BorrowedBook book = borrowLogic.fetchBorrowedBook(
                    request.getMemberId(),
                    request.getCopyOfBookId()
                );

                reportLogic.updateOriginalReturnDate(
                    request.getMemberId(),
                    request.getCopyOfBookId(),
                    book.getBorrowDate(),
                    extendedDueDate
                );
                return ResponseEntity.ok("Due date successfully changed.");
            }
            return ResponseEntity.badRequest().body("Failed to change due date.");
        } catch(Exception e){
            return ResponseEntity.status(500).body(e.toString());
        }
    }

    // due date extension request
    @PutMapping("/subscriber/{subId}/book/{copyOfBookId}/extend")
    public ResponseEntity<String> extendBorrow(@PathVariable int subId, @PathVariable int copyOfBookId){
        BorrowedBook book = borrowLogic.fetchBorrowedBook(subId, copyOfBookId);
        
        if (book == null) {
            return ResponseEntity.notFound().build();
        }

        boolean isExtend = extensionLogic.borrowExtensionRequest(book);
        if(isExtend){
            BorrowedBook exBook = borrowLogic.fetchBorrowedBook(subId, copyOfBookId);
            reportLogic.updateOriginalReturnDate(subId, copyOfBookId, exBook.getBorrowDate(), exBook.getReturnDate());
        
            Subscriber sub = subLogic.fetchSubscriberById(subId);
            String notification = "Subscriber " + sub.getMemberFullName() + "has extended the due date of the book \"" +
                exBook.getNameOfBook() + "\". Please notice the change in his Activity List.";
            
            invoiceLogic.sendMessage(subId, sub.getUserName(), sub.getMemberFullName(), Subject.EXTENSION, notification);

            return ResponseEntity.ok("Borrow successfully extended.");
        }
        return ResponseEntity.badRequest().body("Extension failed or rejected.");
    }

    // a borrowed book was returned - delete it from "borrowed"
    @DeleteMapping("/copy/{copyOfBookId}/subscriber/{subId}")
    public ResponseEntity<String> deleteBorrowedBook(@PathVariable int copyOfBookId, @PathVariable int subId){
        BorrowedBook book = borrowLogic.fetchBorrowedBook(subId, copyOfBookId);

        if(book != null){
            boolean delBook = borrowLogic.deleteBorrowedBook(copyOfBookId); 
            if(delBook){
                CopyOfBook delBookCopyId = bookLogic.findCopyOfBook(copyOfBookId);
                delBookCopyId.setBorrowStatus(BorrowStatus.NOT_BORROWED);
                bookLogic.changeBookCopyBorrowStatus(delBookCopyId);

                reportLogic.updateBorrowTracking(new BorrowTracking(LocalDate.now(), -1, 0));
                reportLogic.updateActualReturnDate(subId, copyOfBookId, book.getBorrowDate(), LocalDate.now());

                return ResponseEntity.ok("Borrowed book deleted and stock updated.");
            }
        }
        return ResponseEntity.badRequest().body("Failed to delete borrowed book.");
    }

    // find a book copy which can be borrowed
    @GetMapping("/book/{bookId}/available-copy/subscriber/{subId}")
    public ResponseEntity<CopyOfBook> findAvailableCopy(@PathVariable int bookId, @PathVariable int subId){
        CopyOfBook availableBookCopy = bookLogic.getAvailableCopyOfBook(bookId);

        if(availableBookCopy != null){
            Book book = bookLogic.fetchBook(bookId);
            boolean isNotReserved = borrowLogic.checkIfAMemberCanBorrowTheBook(subId, availableBookCopy, book);
        
            if(isNotReserved){
                return ResponseEntity.ok(availableBookCopy);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // view closest return date of a book from all its copies
    @GetMapping("/book/{bookId}/closest-return-date")
    public ResponseEntity<BorrowedBook> getClosestReturnDate(@PathVariable int bookId){
        BorrowedBook closestBook = borrowLogic.getCloserReturnDateBook(bookId);
        if(closestBook != null){
            return ResponseEntity.ok(closestBook);
        }
        return ResponseEntity.notFound().build();
    }

    // reduce number of borrowed copies
    @PutMapping("/book/{bookId}/decrease-copies")
    public ResponseEntity<String> decreaseBorrowedCopies(@PathVariable int bookId){
        try{
            boolean decrease = bookLogic.decreaseBorrowedCopies(bookId);

            if(decrease){
                return ResponseEntity.ok("Decreased copies amount by 1.");
            }
            return ResponseEntity.badRequest().body("Decrease failed.");
        } catch(Exception e){
            return ResponseEntity.status(500).body(e.toString());
        }
    }
}
