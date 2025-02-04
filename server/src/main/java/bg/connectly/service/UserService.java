package bg.connectly.service;

import bg.connectly.dto.EditUserDto;
import bg.connectly.exception.AlreadyExistsException;
import bg.connectly.exception.NotFoundException;
import bg.connectly.model.User;
import bg.connectly.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<User> searchUsers(String searchText, Pageable pageable) {
        logger.info("Searching users with text: {}", searchText);
        return userRepository.findAll((root, query, criteriaBuilder) -> {
            String likeSearch = "%" + searchText.toLowerCase() + "%";

            Predicate usernamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), likeSearch);
            Predicate emailPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likeSearch);
            Predicate firstNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), likeSearch);
            Predicate lastNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), likeSearch);

            return criteriaBuilder.or(usernamePredicate, emailPredicate, firstNamePredicate, lastNamePredicate);
        }, pageable);
    }

    public User updateUser(EditUserDto userDto, String username) {
        logger.info("Updating user: {}", username);
        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Username " + username + " not found"));

        updateExistingUserInfo(existingUser, userDto);
        existingUser.setUpdatedAt(LocalDateTime.now());

        logger.info("User updated successfully: {}", username);
        return userRepository.save(existingUser);
    }

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

    private void validateUsernameAvailability(String newUsername) {
        logger.info("Validating username availability: {}", newUsername);
        userRepository.findByUsername(newUsername).ifPresent(user -> {
            throw new AlreadyExistsException("Username " + newUsername + " is already taken");
        });
    }

    private void validateEmailAvailability(String email) {
        logger.info("Validating email availability: {}", email);
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new AlreadyExistsException("Email " + email + " is already taken");
        });
    }
}
