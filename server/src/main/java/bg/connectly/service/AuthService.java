package bg.connectly.service;

import bg.connectly.configuration.JwtUtil;
import bg.connectly.dto.LoginRequest;
import bg.connectly.dto.RegisterRequest;
import bg.connectly.exception.AuthenticationException;
import bg.connectly.exception.EmailAlreadyExistsException;
import bg.connectly.exception.UsernameAlreadyExistsException;
import bg.connectly.mapper.UserMapper;
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

    @Autowired
    private UserMapper userMapper;

    public String authenticateUser(LoginRequest loginRequest) {
        User user = userRepository
                .findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new AuthenticationException("Username not found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Incorrect password");
        }

        return jwtUtil.generateToken(user.getUsername());
    }

    public String createUser(RegisterRequest registerRequest) {
        validateUsernameAvailability(registerRequest.getUsername());
        validateEmailAvailability(registerRequest.getEmail());

        User user = userMapper.toUser(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // Ensure password is encoded
        userRepository.save(user);

        return jwtUtil.generateToken(user.getUsername());
    }

    private void validateUsernameAvailability(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExistsException("Username is already taken");
        }
    }

    private void validateEmailAvailability(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }
    }

}
