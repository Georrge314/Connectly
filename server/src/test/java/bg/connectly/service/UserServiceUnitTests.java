package bg.connectly.service;


import bg.connectly.dto.EditUserDto;
import bg.connectly.exception.AlreadyExistsException;
import bg.connectly.exception.NotFoundException;
import bg.connectly.model.User;
import bg.connectly.repository.UserRepository;
import bg.connectly.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private EditUserDto editUserDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");

        editUserDto = new EditUserDto();
        editUserDto.setUsername("newusername");
        editUserDto.setEmail("newemail@example.com");
        editUserDto.setPassword("newpassword");
    }

    @Test
    void searchUsersSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));
        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(userPage);

        Page<User> result = userService.searchUsers("test", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void updateUserSuccess() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user), Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUser(editUserDto, "testuser");

        assertNotNull(updatedUser);
        assertEquals("newusername", updatedUser.getUsername());
        assertEquals("newemail@example.com", updatedUser.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(editUserDto, "testuser"));
    }

    @Test
    void updateUserUsernameExists() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("newusername")).thenReturn(Optional.of(new User()));

        assertThrows(AlreadyExistsException.class, () -> userService.updateUser(editUserDto, "testuser"));
    }

    @Test
    void updateUserEmailExists() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user), Optional.empty());
        when(userRepository.findByEmail("newemail@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(AlreadyExistsException.class, () -> userService.updateUser(editUserDto, "testuser"));
    }
}
