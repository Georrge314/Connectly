package bg.connectly.service;

import bg.connectly.dto.LoginRequest;
import bg.connectly.dto.RegisterRequest;
import jakarta.validation.Valid;

public interface AuthService {
    String getUsernameFromToken(String token);

    String authenticateUser(@Valid LoginRequest loginRequest);

    String createUser(@Valid RegisterRequest registerRequest);
}
