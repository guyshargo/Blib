package controller;

import logic.LibrarianLogic;
import logic.SubscriberLogic;
import model.Librarian;
import model.Subscriber;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LibrarianLogic librarianLogic = new LibrarianLogic();
    private final SubscriberLogic subLogic = new SubscriberLogic();

    @GetMapping("/login/librarian")
    public ResponseEntity<Librarian> loginLibrarian(@RequestParam String username) {
        Librarian lib = librarianLogic.fetchLibrarianByUsername(username);
        if (lib != null) {
            return ResponseEntity.ok(lib); // Returns OK + JSON data
        }
        return ResponseEntity.status(401).build(); // Returns Unauthorized
    }

    @GetMapping("/login/subscriber")
    public ResponseEntity<Subscriber> loginMember(@RequestParam String username) {
        Subscriber sub = subLogic.fetchMemberByUsername(username);
        if (sub != null) {
            return ResponseEntity.ok(sub);
        }
        return ResponseEntity.status(401).build();
    }
}