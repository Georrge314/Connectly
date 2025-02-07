package bg.connectly.Controller;

import bg.connectly.configuration.JwtUtil;
import bg.connectly.controller.AuthController;
import bg.connectly.dto.JwtResponse;
import bg.connectly.dto.LoginRequest;
import bg.connectly.dto.RegisterRequest;
import bg.connectly.exception.AlreadyExistsException;
import bg.connectly.exception.AuthenticationException;
import bg.connectly.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerUnitTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;


    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
                    .authorizeHttpRequests(auth -> auth
                            .anyRequest().permitAll()// unrestricted access to all endpoints
                    )
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless session for JWT
                    );

            return http.build();
        }
    }

    @Test
    @Order(1)
    void loginWithValidCredentialsReturnsJwtToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("test-user", "test-password");
        JwtResponse jwtResponse = new JwtResponse("valid-token");

        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(jwtResponse.getToken());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))) //JSON request
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("valid-token"));
    }

    @Test
    @Order(2)
    void loginWithInvalidCredentialsReturnsUnauthorized() throws Exception {
        LoginRequest loginRequest = new LoginRequest("invalid-username", "invalid-password");

        when(authService.authenticateUser(any(LoginRequest.class)))
                .thenThrow(new AuthenticationException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))) //JSON request
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(3)
    void registerWithValidDetailsReturnsJwtToken() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "new-user",
                "email@example.com",
                "new-password",
                "new-password");
        JwtResponse jwtResponse = new JwtResponse("valid-jwt-token");

        when(authService.createUser(any(RegisterRequest.class))).thenReturn(jwtResponse.getToken());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("valid-jwt-token"));
    }

    @Test
    @Order(4)
    void registerWithExistingUsernameOrEmailReturnsConflict() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "new-user",
                "email@example.com",
                "new-password",
                "new-password");

        when(authService.createUser(any(RegisterRequest.class)))
                .thenThrow(new AlreadyExistsException("Username or email already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(5)
    void registerWithInvalidUsernameReturnsBadRequest() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "JK", // invalid username (size must be at least 3 symbols)
                "email@example.com",
                "new-password",
                "new-password");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    void registerWithInvalidJsonReturnsBadRequest() throws Exception {
        String invalidJson = "{\"username\":\"user\", \"email\":\"email@example.com\", \"password\":\"password\"}"; // Missing confirmPassword

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

}
