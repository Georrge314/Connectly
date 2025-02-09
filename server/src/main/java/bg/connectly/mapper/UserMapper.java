package bg.connectly.mapper;


import bg.connectly.dto.RegisterRequestDto;
import bg.connectly.dto.UserDto;
import bg.connectly.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper class for converting between UserDto and User entities.
 */
@Component
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Converts a RegisterRequestDto to a User entity.
     *
     * @param registerRequestDto the data transfer object containing registration details
     * @return the created User entity
     */
    public User toUser(RegisterRequestDto registerRequestDto) {
        //required fields
        User user = new User();
        user.setUsername(registerRequestDto.getUsername());
        user.setEmail(registerRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));

        //optional fields
        user.setFirstName(registerRequestDto.getFirstName());
        user.setLastName(registerRequestDto.getLastName());
        user.setBio(registerRequestDto.getBio());
        user.setProfilePicture(registerRequestDto.getProfilePicture());
        user.setDateOfBirth(registerRequestDto.getDateOfBirth());

        //default values
        user.setLastLogin(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    /**
     * Updates an existing User entity from a UserDto.
     *
     * @param userDto the data transfer object containing updated user details
     * @param user the existing User entity to be updated
     * @return true if the user was updated, false otherwise
     */
    public User updateUserFromDto(UserDto userDto, User user) {
        boolean isUpdated = false;

        //Update the existing user fields if they are different from the user dto

        if (userDto.getUsername() != null && !userDto.getUsername().equals(user.getUsername())) {
            user.setUsername(userDto.getUsername());
            isUpdated = true;
        }
        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            user.setEmail(userDto.getEmail());
            isUpdated = true;
        }
        if (userDto.getPassword() != null && !userDto.getPassword().equals(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            isUpdated = true;
        }
        if (userDto.getFirstName() != null && !userDto.getFirstName().equals(user.getFirstName())) {
            user.setFirstName(userDto.getFirstName());
            isUpdated = true;
        }
        if (userDto.getLastName() != null && !userDto.getLastName().equals(user.getLastName())) {
            user.setLastName(userDto.getLastName());
            isUpdated = true;
        }
        if (userDto.getBio() != null && !userDto.getBio().equals(user.getBio())) {
            user.setBio(userDto.getBio());
            isUpdated = true;
        }
        if (userDto.getProfilePicture() != null && !userDto.getProfilePicture().equals(user.getProfilePicture())) {
            user.setProfilePicture(userDto.getProfilePicture());
            isUpdated = true;
        }
        if (userDto.getDateOfBirth() != null && !userDto.getDateOfBirth().equals(user.getDateOfBirth())) {
            user.setDateOfBirth(userDto.getDateOfBirth());
            isUpdated = true;
        }

        if (isUpdated) {
            user.setUpdatedAt(LocalDateTime.now());
        }

        return user;
    }
}
