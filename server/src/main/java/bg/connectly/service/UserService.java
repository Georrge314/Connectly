package bg.connectly.service;

import bg.connectly.dto.EditUserDto;
import bg.connectly.exception.UserNotFoundException;
import bg.connectly.exception.UsernameAlreadyExistsException;
import bg.connectly.model.User;
import bg.connectly.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Page<User> searchUsers(String searchText, Pageable pageable) {
        return userRepository.findAll((root, query, criteriaBuilder) -> {
            String likeSearch = "%" + searchText.toLowerCase() + "%";

            Predicate usernamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), likeSearch);
            Predicate emailPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likeSearch);
            Predicate firstNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), likeSearch);
            Predicate lastNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), likeSearch);

            // Combine the predicates with OR
            return criteriaBuilder.or(usernamePredicate, emailPredicate, firstNamePredicate, lastNamePredicate);
        }, pageable);
    }

    public User updateUser(EditUserDto userDto, String username) {
        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (userDto.getUsername() != null && !userDto.getUsername().equals(username)) {
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

        existingUser.setUpdatedAt(LocalDateTime.now());


        return userRepository.save(existingUser);
    }

    private void validateUsernameAvailability(String newUsername) {
        if (userRepository.findByUsername(newUsername).isPresent()) {
            throw new UsernameAlreadyExistsException("Username is already taken");
        }
    }

    private void validateEmailAvailability(String email) {
        if (userRepository.findByUsername(email).isPresent()) {
            throw new UsernameAlreadyExistsException("Email is already taken");
        }
    }

}
