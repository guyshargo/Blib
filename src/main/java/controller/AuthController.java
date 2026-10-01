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
    public ResponseEntity<Librarian> loginLibrarian(@RequestParam String username, @RequestParam String password) {
        Librarian lib = librarianLogic.fetchLibrarianByUsername(username);
        if (lib != null && lib.getPassword().equals(password)) {
            return ResponseEntity.ok(lib);
        }
        return ResponseEntity.status(401).build();
    }

    @GetMapping("/login/subscriber")
    public ResponseEntity<Subscriber> loginMember(@RequestParam String username, @RequestParam String password) {
        Subscriber sub = subLogic.fetchMemberByUsername(username);
        if (sub != null && sub.getPassword().equals(password)) {
            return ResponseEntity.ok(sub);
        }
        return ResponseEntity.status(401).build();
    }
}