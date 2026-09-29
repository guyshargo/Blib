package controller;

import logic.LibrarianLogic;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/librarians")
public class LibrarianController {
    private final LibrarianLogic librarianLogic = new LibrarianLogic();

    // change login status when user logins\logouts
    @PutMapping("/{id}/login-status")
    public ResponseEntity<String> updateLoginStatus(@PathVariable int id, @RequestParam boolean status){
        boolean updated = librarianLogic.ChangeLogInStatus(id, status);
        if(updated){
            return ResponseEntity.ok("Login status updated successfully");
        }
        return ResponseEntity.badRequest().body("Failed to update login status");
    }
}
