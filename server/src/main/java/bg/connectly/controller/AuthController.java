package bg.connectly.controller;

import bg.connectly.dto.JwtResponse;
import bg.connectly.dto.LoginRequest;
import bg.connectly.dto.RegisterRequest;
import bg.connectly.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    public AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // Authenticate the user and generate a JWT token
        String jwtToken = authService.authenticateUser(loginRequest);

        if (jwtToken != null) {
            return ResponseEntity.ok(new JwtResponse(jwtToken)); // Return JWT token
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        authService.createUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }
}
