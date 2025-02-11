package bg.connectly.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@RequiredArgsConstructor
public class RegisterRequestDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @NonNull
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @NonNull
    private String password;

    // Optional fields for initial setup, or add in update profile feature
    private String firstName;
    private String lastName;
    private String bio;
    private String profilePicture;
    private LocalDate dateOfBirth;
}
