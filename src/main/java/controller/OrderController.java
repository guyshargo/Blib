package controller;

import logic.BookControl.Book;
import logic.BookControl.BookLogic;
import logic.BookControl.OrderedBook;
import logic.BorrowControl.BorrowLogic;
import logic.OrderControl.OrderLogic;
import logic.OrderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final BookLogic bookLogic = new BookLogic();
    private final BorrowLogic borrowLogic = new BorrowLogic();
    private final OrderLogic orderLogic = new OrderLogic();

    // view all book orders of a specific subscriber
    @GetMapping("/subscriber/{subId}")
    public ResponseEntity<List<OrderedBook>> getAllOrderedBooks(@PathVariable int subId){
        List<OrderedBook> subOrderedBooks = bookLogic.importOrderedBooks(subId);
        return ResponseEntity.ok(subOrderedBooks);
    }

    // ordering a book
    @PostMapping("/")
    public ResponseEntity<String> orderBook(@RequestBody OrderRequest request){
        try{
            String orderMessage = orderLogic.orderBook(
                request.getBookName(),
                request.getBookId(),
                request.getMemberId(),
                request.getMemberName(),
                request.getMemberPhone(),
                request.getMemberEmail()
            );
            return ResponseEntity.ok(orderMessage);            
        } catch(Exception e){
            return ResponseEntity.status(500).body(e.toString());
        }
    }

    // cancel a book order
    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable int orderId,@RequestParam String bookName , @RequestParam String arrivalStatus){
        boolean isCanceled = orderLogic.cancelOrder(orderId);

        if(isCanceled && "Arrived".equals(arrivalStatus)){
            orderLogic.changeArrivalStatus(bookName);
        }

        if(isCanceled){
            return ResponseEntity.ok("Order successfully canceled.");
        }
        return ResponseEntity.badRequest().body("Failed to cancel order.");
    }

    // search book to order
    @GetMapping("/search-to-order")
    public ResponseEntity<List<Book>> searchBookToOrder(
        @RequestParam String bookName,
        @RequestParam(required = false) Integer bookId){

            int id = (bookId != null) ? bookId : -1;
            List<Book> foundBooks = orderLogic.searchToOrderBook(bookName, id);
            return ResponseEntity.ok(foundBooks);
    }

    // search a specific book to cancel its order
    @GetMapping("/search-to-cancel")
    public ResponseEntity<List<Book>> searchBookToCancel(@RequestParam int bookId) {
        // EchoServer hardcodes the string "null" for the book name in this case
        List<Book> cancelBook = orderLogic.searchToOrderBook("null", bookId);
        return ResponseEntity.ok(cancelBook);
    }

    // change if a book is ordered or not
    @PutMapping("/status")
    public ResponseEntity<String> changeOrderStatus(
            @RequestParam int bookId, 
            @RequestParam int numberOfOrders, 
            @RequestParam boolean increase) {
        
        boolean statusChanged = orderLogic.changeBookOrderStatus(bookId, numberOfOrders, increase);
        if (statusChanged) {
            return ResponseEntity.ok("Order status updated.");
        }
        return ResponseEntity.badRequest().body("Failed to update order status.");
    }

    //
    @PutMapping("/book/{bookName}/arrival-status")
    public ResponseEntity<String> changeArrivalStatus(@PathVariable String bookName) {
        try {
            // EchoServer strictly routed this specific case through borrowLogic
            boolean statusChanged = borrowLogic.changeArrivalStatus(bookName);
            if (statusChanged) {
                return ResponseEntity.ok("Success");
            }
            return ResponseEntity.badRequest().body("Failure");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.toString());
        }
    }
}
