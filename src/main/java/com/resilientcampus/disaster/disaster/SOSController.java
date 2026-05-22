package com.resilientcampus.disaster.disaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SOSController {

    @Autowired
    private SOSRepository sosRepository;

    @PostMapping("/trigger-sos")
    public String triggerSOS(@ModelAttribute SOSRequest request) {
        // Set the timestamp
        request.setTime(new java.util.Date().toString());

        // Save everything (including the mediaUrl) to MongoDB
        sosRepository.save(request);

        System.out.println("🚨 CLOUD SOS RECEIVED from: " + request.getStudentId());
        return "SOS Signal Received and Saved to Cloud!";
    }

    @GetMapping("/all-alerts")
    public List<SOSRequest> getAllAlerts() {
        return sosRepository.findAll();
    }

    @DeleteMapping("/clear-all")
    public String clearAllAlerts() {
        sosRepository.deleteAll();
        return "All alerts cleared from database!";
    }
}