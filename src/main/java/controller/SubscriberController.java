package controller;

import logic.RegisterRequest;
import logic.FreezeStatus;
import logic.Subscriber;
import logic.subscriberLogic;
import logic.ReportControl.MemberStatusChange;
import logic.ReportControl.ReportLogic;
import logic.ReportControl.StatusTracking;

import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
    
@RestController
@RequestMapping("/api/subscribers")
public class SubscriberController {

    private final subscriberLogic subLogic = new subscriberLogic();
    private final ReportLogic reportLogic = new ReportLogic();

    // view subscriber data
    @GetMapping("/{id}")
    public ResponseEntity<Subscriber> getSubscriber(@PathVariable int id){
        Subscriber sub = subLogic.fetchSubscriberById(id);
        if(sub != null){
            return ResponseEntity.ok(sub);
        }
        return ResponseEntity.notFound().build();
    }

    // update existing subscriber
    @PutMapping("/{id}/edit-contact")
    public ResponseEntity<String> editContact(@PathVariable int id, @RequestParam String phone, @RequestParam String email){
        boolean updated = subLogic.updateSubscriberContact(id, phone, email);

        if(updated){
            return ResponseEntity.ok("Contact information updated successfully");
        }
        return ResponseEntity.badRequest().body("Failed to update contact information");
    }

    // view all registered subscribers
    @GetMapping("/all")
    public ResponseEntity<List<Subscriber>> getAllSubscribers(){
        List<Subscriber> subList = subLogic.fetchAllSubscribers();
        return ResponseEntity.ok(subList);
    }

    // register a new subsriber
    @PostMapping("/register")
    public ResponseEntity<?> registerContact(@RequestBody RegisterRequest request){
        String dupErrors = subLogic.checkDuplicates(
            request.getMembershipNumber(),
            request.getUserName(),
            request.getEmail()
        );

        // one of the inputs already exists in the db
        if(dupErrors != null){
            return ResponseEntity.badRequest().body(dupErrors);
        }

        Subscriber newSubscriber = subLogic.registerNewMember(
            request.getMembershipNumber(),
            request.getFullName(),
            request.getUserName(),
            request.getPassword(),
            FreezeStatus.NOT_FROZEN,
            request.getPhone(),
            request.getEmail()
        );

        if(newSubscriber != null){
            reportLogic.saveMemberStatusChange(new MemberStatusChange(
                request.getMembershipNumber(), request.getFullName(), FreezeStatus.NOT_FROZEN, LocalDate.now()
            ));
            reportLogic.updateStatusTracking(new StatusTracking(LocalDate.now(), 0, 1));
            return ResponseEntity.ok(newSubscriber);
        }

        return ResponseEntity.status(500).body("Registration failed.");
    }

    // freezing/unfreezing a subscriber
    @PutMapping("/{id}/freeze-status")
    public ResponseEntity<String> setSubscriberStatus(@PathVariable int id, @RequestParam String status, @RequestParam String date){
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate freezDate = LocalDate.parse(date, formatter);
            
            boolean setStatus = subLogic.setFreezeStatus(id, status, freezDate);
            if(setStatus){
                Subscriber sub = subLogic.fetchSubscriberById(id);
                reportLogic.saveMemberStatusChange(new MemberStatusChange(
                    sub.getMembershipNumber(), sub.getMemberFullName(), FreezeStatus.fromDbValue(status), freezDate)
                );

                if(sub.getMemberFreezeStatus() == FreezeStatus.NOT_FROZEN){
                    reportLogic.updateStatusTracking(new StatusTracking(LocalDate.now(), -1, 1));
                } else{
                    reportLogic.updateStatusTracking(new StatusTracking(LocalDate.now(), 1, -1));
                }

                return ResponseEntity.ok("Status updated successfully");
            }
            return ResponseEntity.badRequest().body("Failed to update status");
        
        } catch (Exception error) {
            return ResponseEntity.status(500).body(error.toString());
        }
    }

    // change login status when user logins\logouts
    @PutMapping("/{id}/login-status")
    public ResponseEntity<String> updateLoginStatus(@PathVariable int id, @RequestParam boolean status){
        boolean updated = subLogic.ChangeLogInStatus(id, status);
        if(updated){
            return ResponseEntity.ok("Login status updated successfully");
        }
        return ResponseEntity.badRequest().body("Failed to update login status");
    }
}
