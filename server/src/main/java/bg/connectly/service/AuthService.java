package bg.connectly.service;

import bg.connectly.dto.LoginRequestDto;
import bg.connectly.dto.RegisterRequestDto;
import jakarta.validation.Valid;

public interface AuthService {
    String getUsernameFromToken(String token);

    String authenticateUser(@Valid LoginRequestDto loginRequestDto);

    String createUser(@Valid RegisterRequestDto registerRequestDto);
}
