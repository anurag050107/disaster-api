package com.resilientcampus.disaster.disaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/users")
@CrossOrigin(origins = "*")
public class SuperMasterController {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String getEmailFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.extractEmail(token);
        }
        return null;
    }

    private boolean isSuperMaster(String email) {
        if (email == null) return false;
        AdminUser user = adminUserRepository.findByEmail(email);
        return user != null && "super_master".equals(user.getRole());
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
        String email = getEmailFromRequest(request);
        if (!isSuperMaster(email)) {
            return ResponseEntity.status(403)
                    .body(Map.of("success", false, "message", "Super master access only"));
        }

        List<AdminUser> users = adminUserRepository.findAll();
        // Remove password from response
        List<Map<String, Object>> safeUsers = users.stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", u.getId());
            m.put("email", u.getEmail());
            m.put("name", u.getName());
            m.put("role", u.getRole());
            m.put("status", u.getStatus());
            m.put("createdAt", u.getCreatedAt());
            return m;
        }).toList();

        return ResponseEntity.ok(Map.of("success", true, "users", safeUsers, "count", safeUsers.size()));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveUser(@PathVariable String id, HttpServletRequest request) {
        String email = getEmailFromRequest(request);
        if (!isSuperMaster(email)) {
            return ResponseEntity.status(403)
                    .body(Map.of("success", false, "message", "Super master access only"));
        }

        AdminUser user = adminUserRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setStatus("approved");
        adminUserRepository.save(user);
        return ResponseEntity.ok(Map.of("success", true, "message", user.getEmail() + " has been approved"));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectUser(@PathVariable String id, HttpServletRequest request) {
        String email = getEmailFromRequest(request);
        if (!isSuperMaster(email)) {
            return ResponseEntity.status(403)
                    .body(Map.of("success", false, "message", "Super master access only"));
        }

        AdminUser user = adminUserRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setStatus("rejected");
        adminUserRepository.save(user);
        return ResponseEntity.ok(Map.of("success", true, "message", user.getEmail() + " has been rejected"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id, HttpServletRequest request) {
        String email = getEmailFromRequest(request);
        if (!isSuperMaster(email)) {
            return ResponseEntity.status(403)
                    .body(Map.of("success", false, "message", "Super master access only"));
        }

        AdminUser user = adminUserRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Prevent deleting yourself
        if (user.getEmail().equals(email)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Cannot delete your own account"));
        }

        adminUserRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("success", true, "message", user.getEmail() + " has been deleted"));
    }
}
