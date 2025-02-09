package bg.connectly.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserDto {
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    @Email(message = "Email should be valid")
    private String email;
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
    @Size(max = 50)
    private String firstName;
    @Size(max = 50)
    private String lastName;
    private String profilePicture;
    @Size(max = 500)
    private String bio;
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;;
}
