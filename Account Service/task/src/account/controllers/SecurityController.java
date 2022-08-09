package account.controllers;

import account.models.SecurityEvent;
import account.repositories.SecurityEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/security")
public class SecurityController {

    @Autowired
    SecurityEventRepository securityEventRepository;

    @GetMapping("/events")
    public ResponseEntity<List<SecurityEvent>> getEvents() {
        List<SecurityEvent> securityEvents = new ArrayList<>();
        for(SecurityEvent event: securityEventRepository.findAll()) {
            securityEvents.add(event);
        }

        return new ResponseEntity<>(securityEvents, HttpStatus.OK);
    }
}
