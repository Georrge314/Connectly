package bg.connectly.exception;

/**
 * Custom exception for authentication failures in the application.
 */
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}