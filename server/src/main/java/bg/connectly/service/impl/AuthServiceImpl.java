package bg.connectly.service.impl;

import bg.connectly.configuration.JwtUtil;
import bg.connectly.dto.LoginRequest;
import bg.connectly.dto.RegisterRequest;
import bg.connectly.exception.AuthenticationException;
import bg.connectly.exception.AlreadyExistsException;
import bg.connectly.mapper.UserMapper;
import bg.connectly.model.User;
import bg.connectly.repository.UserRepository;
import bg.connectly.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    @Override
    public String authenticateUser(LoginRequest loginRequest) {
        logger.info("Authenticating user: {}", loginRequest.getUsername());
        User user = userRepository
                .findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new AuthenticationException("Username " + loginRequest.getUsername() + " not found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            logger.warn("Authentication failed for user: {}", loginRequest.getUsername());
            throw new AuthenticationException("Incorrect password");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        logger.info("User authenticated successfully: {}", loginRequest.getUsername());

        return jwtUtil.generateToken(user.getUsername());
    }

    @Override
    public String createUser(RegisterRequest registerRequest) {
        logger.info("Creating user: {}", registerRequest.getUsername());
        validateUsernameAvailability(registerRequest.getUsername());
        validateEmailAvailability(registerRequest.getEmail());

        User user = userMapper.toUser(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userRepository.save(user);

        logger.info("User created successfully: {}", registerRequest.getUsername());
        return jwtUtil.generateToken(user.getUsername());
    }

    @Override
    public String getUsernameFromToken(String token) {
        return jwtUtil.extractUsername(token.substring(7));
    }

    private void validateUsernameAvailability(String username) {
        logger.info("Validating username availability: {}", username);
        userRepository.findByUsername(username).ifPresent(user -> {
            throw new AlreadyExistsException("Username " + username + " is already taken");
        });
    }

    private void validateEmailAvailability(String email) {
        logger.info("Validating email availability: {}", email);
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new AlreadyExistsException("Email " + email + " is already registered");
        });
    }

}
