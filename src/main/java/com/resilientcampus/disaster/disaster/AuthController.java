package com.resilientcampus.disaster.disaster;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${super.master.email}")
    private String superMasterEmail;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        String name = body.get("name");

        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Email and password are required"));
        }

        if (password.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Password must be at least 6 characters"));
        }

        if (adminUserRepository.findByEmail(email) != null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Email already registered"));
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        AdminUser user = new AdminUser();
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setName(name != null ? name : "");
        user.setCreatedAt(System.currentTimeMillis());

        // Check if this is the super master
        if (superMasterEmail != null && !superMasterEmail.isBlank() && superMasterEmail.equalsIgnoreCase(email)) {
            user.setRole("super_master");
            user.setStatus("approved");
            adminUserRepository.save(user);

            String token = jwtUtil.generateToken(email);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("token", token);
            result.put("email", email);
            result.put("name", name);
            result.put("role", "super_master");
            return ResponseEntity.ok(result);
        }

        // Regular user — needs approval
        user.setRole("admin");
        user.setStatus("pending");
        adminUserRepository.save(user);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("status", "pending");
        result.put("message", "Account created. Waiting for super master approval.");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Email and password are required"));
        }

        AdminUser user = adminUserRepository.findByEmail(email);
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "Invalid email or password"));
        }

        if ("pending".equals(user.getStatus())) {
            return ResponseEntity.status(403)
                    .body(Map.of("success", false, "status", "pending", "message", "Your account is pending approval by the super master."));
        }

        if ("rejected".equals(user.getStatus())) {
            return ResponseEntity.status(403)
                    .body(Map.of("success", false, "status", "rejected", "message", "Your account has been rejected. Contact the super master."));
        }

        String token = jwtUtil.generateToken(email);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("token", token);
        result.put("email", email);
        result.put("name", user.getName());
        result.put("role", user.getRole());
        return ResponseEntity.ok(result);
    }
}
