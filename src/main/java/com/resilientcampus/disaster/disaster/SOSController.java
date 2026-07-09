package com.resilientcampus.disaster.disaster;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SOSController {

    @Autowired
    private SOSRepository sosRepository;

    @Autowired
    private Cloudinary cloudinary;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", "sos-disaster-alerts", "resource_type", "auto")
            );
            String url = (String) uploadResult.get("secure_url");
            return ResponseEntity.ok(Map.of("success", true, "url", url));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "error", "Upload failed: " + e.getMessage()));
        }
    }

    @PostMapping("/trigger-sos")
    public ResponseEntity<?> triggerSOS(@RequestBody SOSRequest request) {
        try {
            request.setTime(new java.util.Date().toString());
            if (request.getStatus() == null || request.getStatus().isEmpty()) {
                request.setStatus("active");
            }
            sosRepository.save(request);
            return ResponseEntity.ok(Map.of("success", true, "message", "SOS Saved!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "error", "Save failed: " + e.getMessage()));
        }
    }

    @GetMapping("/all-alerts")
    public ResponseEntity<?> getAllAlerts() {
        try {
            List<SOSRequest> alerts = sosRepository.findAll();
            return ResponseEntity.ok(Map.of("success", true, "alerts", alerts, "count", alerts.size()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "error", "Fetch failed: " + e.getMessage()));
        }
    }

    @DeleteMapping("/alert/{id}")
    public ResponseEntity<?> deleteAlert(@PathVariable String id) {
        try {
            if (sosRepository.existsById(id)) {
                sosRepository.deleteById(id);
                return ResponseEntity.ok(Map.of("success", true, "message", "Deleted"));
            }
            return ResponseEntity.status(404).body(Map.of("success", false, "error", "Not found"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "error", "Delete failed: " + e.getMessage()));
        }
    }

    @PutMapping("/alert/{id}/resolve")
    public ResponseEntity<?> resolveAlert(@PathVariable String id) {
        try {
            Optional<SOSRequest> alert = sosRepository.findById(id);
            if (alert.isEmpty()) return ResponseEntity.status(404).body(Map.of("success", false, "error", "Not found"));
            SOSRequest a = alert.get();
            a.setStatus("resolved");
            sosRepository.save(a);
            return ResponseEntity.ok(Map.of("success", true, "message", "Resolved"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "error", "Resolve failed: " + e.getMessage()));
        }
    }

    @DeleteMapping("/clear-all")
    public ResponseEntity<?> clearAllAlerts() {
        try {
            sosRepository.deleteAll();
            return ResponseEntity.ok(Map.of("success", true, "message", "All cleared!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "error", "Clear failed: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "alive", "service", "disaster-api"));
    }
}
