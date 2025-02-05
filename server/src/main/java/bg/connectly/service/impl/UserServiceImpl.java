package bg.connectly.service.impl;

import bg.connectly.dto.EditUserDto;
import bg.connectly.exception.AlreadyExistsException;
import bg.connectly.exception.NotFoundException;
import bg.connectly.model.User;
import bg.connectly.repository.UserRepository;
import bg.connectly.service.UserService;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service class for managing user-related operations.
 * This class provides methods for searching, updating, and validating users.
 * It interacts with the UserRepository to perform database operations.
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Searches for users based on the provided search text.
     * The search is performed on username, email, first name, and last name fields.
     *
     * @param searchText the text to search for
     * @param pageable   the pagination information
     * @return a page of users matching the search criteria
     */
    @Override
    public Page<User> searchUsers(String searchText, Pageable pageable) {
        logger.info("Searching users with text: {}", searchText);
        // Perform a custom query using JPA Criteria API
        return userRepository.findAll((root, query, criteriaBuilder) -> {
            String likeSearch = "%" + searchText.toLowerCase() + "%";

            // Create predicates for each field to search
            Predicate usernamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), likeSearch);
            Predicate emailPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likeSearch);
            Predicate firstNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), likeSearch);
            Predicate lastNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), likeSearch);

            // Combine all the predicates using a logical OR
            return criteriaBuilder.or(usernamePredicate, emailPredicate, firstNamePredicate, lastNamePredicate);
        }, pageable);
    }

    /**
     * Updates the user information based on the provided EditUserDto.
     * Validates the availability of the new username and email if they are changed.
     *
     * @param userDto  the user data transfer object containing updated information
     * @param username the username of the user to update
     * @return the updated user
     */
    @Override
    public User updateUser(EditUserDto userDto, String username) {
        logger.info("Updating user: {}", username);
        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Username " + username + " not found"));

        updateExistingUserInfo(existingUser, userDto);
        existingUser.setUpdatedAt(LocalDateTime.now());

        logger.info("User updated successfully: {}", username);
        return userRepository.save(existingUser);
    }

    /**
     * Updates the existing user information with the data from the EditUserDto.
     * Validates the availability of the new username and email if they are changed.
     *
     * @param existingUser the existing user to update
     * @param userDto      the user data transfer object containing updated information
     */
    private void updateExistingUserInfo(User existingUser, EditUserDto userDto) {
        if (userDto.getUsername() != null && !userDto.getUsername().equals(existingUser.getUsername())) {
            validateUsernameAvailability(userDto.getUsername());
            existingUser.setUsername(userDto.getUsername());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().equals(existingUser.getEmail())) {
            validateEmailAvailability(userDto.getEmail());
            existingUser.setEmail(userDto.getEmail());
        }

        if (userDto.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        if (userDto.getFirstName() != null) {
            existingUser.setFirstName(userDto.getFirstName());
        }

        if (userDto.getLastName() != null) {
            existingUser.setLastName(userDto.getLastName());
        }

        if (userDto.getBio() != null) {
            existingUser.setBio(userDto.getBio());
        }

        if (userDto.getProfilePicture() != null) {
            existingUser.setProfilePicture(userDto.getProfilePicture());
        }

        if (userDto.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(userDto.getDateOfBirth());
        }
    }

    /**
     * Validates the availability of the provided username.
     * Logs the validation process and checks if the username is already taken.
     * If the username is already taken, throws an AlreadyExistsException.
     *
     * @param newUsername the username to validate
     */
    private void validateUsernameAvailability(String newUsername) {
        logger.info("Validating username availability: {}", newUsername);
        userRepository.findByUsername(newUsername).ifPresent(user -> {
            throw new AlreadyExistsException("Username " + newUsername + " is already taken");
        });
    }

    /**
     * Validates the availability of the provided email address.
     * Logs the validation process and checks if the email is already registered.
     * If the email is already taken, throws an AlreadyExistsException.
     *
     * @param email the email address to validate
     */
    private void validateEmailAvailability(String email) {
        logger.info("Validating email availability: {}", email);
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new AlreadyExistsException("Email " + email + " is already taken");
        });
    }
}
