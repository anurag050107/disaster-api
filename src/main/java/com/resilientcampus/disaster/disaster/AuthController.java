package com.resilientcampus.disaster.disaster;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
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
        adminUserRepository.save(user);

        String token = jwtUtil.generateToken(email);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("token", token);
        result.put("email", email);
        result.put("name", name);
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

        String token = jwtUtil.generateToken(email);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("token", token);
        result.put("email", email);
        result.put("name", user.getName());
        return ResponseEntity.ok(result);
    }
}
