package bg.connectly.service.impl;

import bg.connectly.dto.UserDto;
import bg.connectly.exception.AlreadyExistsException;
import bg.connectly.exception.NotFoundException;
import bg.connectly.mapper.UserMapper;
import bg.connectly.model.User;
import bg.connectly.repository.UserRepository;
import bg.connectly.service.UserService;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


/**
 * Service class for managing user-related operations.
 * This class provides methods for searching, updating, and validating users.
 * It interacts with the UserRepository to perform database operations.
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, UserRepository userRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    /**
     * Searches for users based on the provided search text.
     * The search is performed on email, first name, and last name fields.
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
            Predicate emailPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likeSearch);
            Predicate firstNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), likeSearch);
            Predicate lastNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), likeSearch);

            // Combine all the predicates using a logical OR
            return criteriaBuilder.or(emailPredicate, firstNamePredicate, lastNamePredicate);
        }, pageable);
    }

    /**
     * Updates the user information based on the provided EditUserDto.
     * Validates the availability of the new email if they are changed.
     *
     * @param userDto  the user data transfer object containing updated information
     * @param email the email of the user to update
     * @return the updated user
     */
    @Override
    public User updateUser(UserDto userDto, String email) {
        logger.info("Updating user: {}", email);
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Email " + email + " not found"));

        // Validate the availability of the new email if they are changed
        if (userDto.getEmail() != null && !userDto.getEmail().equals(existingUser.getEmail())) {
            validateEmailAvailability(userDto.getEmail());
        }

        existingUser = userMapper.updateUserFromDto(userDto, existingUser);
        logger.info("User updated successfully: {}", email);
        return userRepository.save(existingUser);
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
