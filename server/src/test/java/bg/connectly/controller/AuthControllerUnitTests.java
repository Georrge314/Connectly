package bg.connectly.controller;

import bg.connectly.configuration.JwtUtil;
import bg.connectly.dto.JwtResponse;
import bg.connectly.dto.LoginRequestDto;
import bg.connectly.dto.RegisterRequestDto;
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
        LoginRequestDto loginRequestDto = new LoginRequestDto("test-user@abv.bg", "test-password");
        JwtResponse jwtResponse = new JwtResponse("valid-token");

        when(authService.authenticateUser(any(LoginRequestDto.class))).thenReturn(jwtResponse.getToken());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto))) //JSON request
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("valid-token"));
    }

    @Test
    @Order(2)
    void loginWithInvalidCredentialsReturnsUnauthorized() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto("invalid-email", "invalid-password");

        when(authService.authenticateUser(any(LoginRequestDto.class)))
                .thenThrow(new AuthenticationException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto))) //JSON request
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(3)
    void registerWithValidDetailsReturnsJwtToken() throws Exception {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                "email@example.com",
                "new-password");
        JwtResponse jwtResponse = new JwtResponse("valid-jwt-token");

        when(authService.createUser(any(RegisterRequestDto.class))).thenReturn(jwtResponse.getToken());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("valid-jwt-token"));
    }

    @Test
    @Order(4)
    void registerWithExistingEmailOrEmailReturnsConflict() throws Exception {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                "email@example.com",
                "new-password");

        when(authService.createUser(any(RegisterRequestDto.class)))
                .thenThrow(new AlreadyExistsException("Email already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequestDto)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(5)
    void registerWithInvalidEmailReturnsBadRequest() throws Exception {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                "emailexample.com", // invalid email (missing '@')
                "new-password");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    void registerWithInvalidJsonReturnsBadRequest() throws Exception {
        String invalidJson = "{\"email\":\"email@example.com\"}"; // Missing password

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

}
