package controller;

import logic.BookControl.Book;
import logic.BookControl.BookLogic;
import logic.BorrowControl.BorrowLogic;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    
    private final BookLogic bookLogic = new BookLogic();
    private final BorrowLogic borrowLogic = new BorrowLogic();

    // find book by id
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable int id){
        Book book = borrowLogic.findBookById(id);

        if(book != null){
            return ResponseEntity.ok(book);
        }
        return ResponseEntity.notFound().build();
    }

    // search books
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(
        @RequestParam(defaultValue = "is empty") String name,
        @RequestParam(defaultValue = "is empty") String subject,
        @RequestParam(defaultValue = "is empty") String freeText){
            List<Book> bookList = bookLogic.searchBooks(name, subject, freeText);
            
            if(bookList != null){
                return ResponseEntity.ok(bookList);
            }
            return ResponseEntity.notFound().build();
        }

}
