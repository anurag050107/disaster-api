package com.resilientcampus.disaster.disaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SOSController {

    @Autowired
    private SOSRepository sosRepository;

    @PostMapping("/trigger-sos")
    public String triggerSOS(@RequestBody SOSRequest request) {
        sosRepository.save(request);

        System.out.println("🚨 CLOUD SOS RECEIVED from: " + request.getStudentId());

        return "SOS Signal Received and Saved to Cloud!";
    }
}