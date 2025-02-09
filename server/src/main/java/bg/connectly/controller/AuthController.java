package bg.connectly.controller;

import bg.connectly.dto.JwtResponse;
import bg.connectly.dto.LoginRequestDto;
import bg.connectly.dto.RegisterRequestDto;
import bg.connectly.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for managing authentication-related operations.
 * This class provides endpoints for user login and registration.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {
    public final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint for authenticating a user and generating a JWT token.
     *
     * @param loginRequestDto the login request containing username and password
     * @return a ResponseEntity containing the JWT token
     */
    @Operation(summary = "Authenticate user and generate JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        String jwtToken = authService.authenticateUser(loginRequestDto);
        return ResponseEntity.ok(new JwtResponse(jwtToken));
    }


    /**
     * Endpoint for registering a new user and generating a JWT token.
     *
     * @param registerRequestDto the registration request containing user details
     * @return a ResponseEntity containing the JWT token
     */
    @Operation(summary = "Register a new user and generate JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid registration details"),
            @ApiResponse(responseCode = "409", description = "Username or email already exists"),
    })
    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        String jwtToken = authService.createUser(registerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new JwtResponse(jwtToken));
    }
}
