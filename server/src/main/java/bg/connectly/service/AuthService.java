package bg.connectly.service;

import bg.connectly.configuration.JwtUtil;
import bg.connectly.dto.LoginRequest;
import bg.connectly.dto.RegisterRequest;
import bg.connectly.model.User;
import bg.connectly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String authenticateUser(LoginRequest loginRequest) {
        String generatedToken = jwtUtil.generateToken(loginRequest.getUsername());
        return generatedToken;
    }

    public User createUser(RegisterRequest registerRequest) {
        User user = new User();
        return null;
    }
}
