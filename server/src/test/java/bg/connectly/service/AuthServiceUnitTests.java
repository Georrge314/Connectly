package bg.connectly.service;


import bg.connectly.configuration.JwtUtil;
import bg.connectly.dto.LoginRequestDto;
import bg.connectly.dto.RegisterRequestDto;
import bg.connectly.exception.AlreadyExistsException;
import bg.connectly.exception.AuthenticationException;
import bg.connectly.mapper.UserMapper;
import bg.connectly.model.User;
import bg.connectly.repository.UserRepository;
import bg.connectly.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceUnitTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private LoginRequestDto loginRequestDto;
    private RegisterRequestDto registerRequestDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");

        loginRequestDto = new LoginRequestDto("testuser", "password");
        registerRequestDto = new RegisterRequestDto(
                "newuser", "newuser@example.com", "password");

    }


    @Test
    void authenticateUserSuccess() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(anyString())).thenReturn("jwtToken");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        String token = authService.authenticateUser(loginRequestDto);

        assertEquals("jwtToken", token);
        verify(userRepository).save(user);
    }

    @Test
    void authenticateUserFailsWithIncorrectPassword() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> authService.authenticateUser(loginRequestDto));
    }

    @Test
    void authenticateUserFailsWithNotExistingUsername() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> authService.authenticateUser(loginRequestDto));
    }

    @Test
    void createUserSuccess() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userMapper.toUser(any(RegisterRequestDto.class))).thenReturn(user);
        when(jwtUtil.generateToken(anyString())).thenReturn("jwtToken");

        String token = authService.createUser(registerRequestDto);

        assertEquals("jwtToken", token);
        verify(userRepository).save(user);
    }

    @Test
    void createUserUsernameExists() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        assertThrows(AlreadyExistsException.class, () -> authService.createUser(registerRequestDto));
    }

    @Test
    void createUserEmailExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(AlreadyExistsException.class, () -> authService.createUser(registerRequestDto));
    }

    @Test
    void getUsernameFromToken() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("testuser");

        String username = authService.getUsernameFromToken("Bearer jwtToken");

        assertEquals("testuser", username);
    }
}
