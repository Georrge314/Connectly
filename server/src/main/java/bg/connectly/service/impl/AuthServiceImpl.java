package bg.connectly.service.impl;

import bg.connectly.configuration.JwtUtil;
import bg.connectly.dto.LoginRequestDto;
import bg.connectly.dto.RegisterRequestDto;
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

/**
 * Implementation of the AuthService interface.
 * Provides authentication and user management services.
 */
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

    /**
     * Authenticates a user based on the provided login request.
     *
     * @param loginRequestDto the login request containing email and password
     * @return a JWT token if authentication is successful
     * @throws AuthenticationException if authentication fails
     */
    @Override
    public String authenticateUser(LoginRequestDto loginRequestDto) {
        logger.info("Authenticating user: {}", loginRequestDto.getEmail());
        User user = userRepository
                .findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new AuthenticationException("Email " + loginRequestDto.getEmail() + " not found"));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            logger.warn("Authentication failed for user: {}", loginRequestDto.getEmail());
            throw new AuthenticationException("Incorrect password");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        logger.info("User authenticated successfully: {}", loginRequestDto.getEmail());

        return jwtUtil.generateToken(user.getEmail());
    }

    /**
     * Creates a new user based on the provided register request.
     *
     * @param registerRequestDto the register request containing user details
     * @return a JWT token for the newly created user
     * @throws AlreadyExistsException if the email or email is already taken
     */
    @Override
    public String createUser(RegisterRequestDto registerRequestDto) {
        logger.info("Creating user: {}", registerRequestDto.getEmail());
        validateemailAvailability(registerRequestDto.getEmail());
        validateEmailAvailability(registerRequestDto.getEmail());

        User user = userMapper.toUser(registerRequestDto);
        userRepository.save(user);

        logger.info("User created successfully: {}", registerRequestDto.getEmail());
        return jwtUtil.generateToken(user.getEmail());
    }

    /**
     * Extracts the email from the provided JWT token.
     *
     * @param token the JWT token
     * @return the email extracted from the token
     */
    @Override
    public String getEmailFromToken(String token) {
        return jwtUtil.extractEmail(token.substring(7));
    }

    /**
     * Validates the availability of the provided email.
     *
     * @param email the email to validate
     * @throws AlreadyExistsException if the email is already taken
     */
    private void validateemailAvailability(String email) {
        logger.info("Validating email availability: {}", email);
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new AlreadyExistsException("email " + email + " is already taken");
        });
    }

    /**
     * Validates the availability of the provided email.
     *
     * @param email the email to validate
     * @throws AlreadyExistsException if the email is already registered
     */
    private void validateEmailAvailability(String email) {
        logger.info("Validating email availability: {}", email);
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new AlreadyExistsException("Email " + email + " is already registered");
        });
    }

}
