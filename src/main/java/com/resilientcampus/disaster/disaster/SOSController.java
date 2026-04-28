package com.resilientcampus.disaster.disaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SOSController {

    @Autowired
    private CSVStorageService csvStorageService;
    @PostMapping("/trigger-sos")
    public String handleSOS(@RequestBody SOSRequest request) {
        System.out.println("🚨 SOS RECEIVED!");
        System.out.println("ID: " + request.getStudentId());
        System.out.println("Location: " + request.getLatitude() + "," + request.getLongitude());
        csvStorageService.saveOne(request);
        return "SOS Sent to HQ";
    }
}