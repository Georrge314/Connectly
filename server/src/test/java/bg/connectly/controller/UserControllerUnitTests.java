package bg.connectly.controller;

import bg.connectly.configuration.JwtUtil;
import bg.connectly.dto.UserDto;
import bg.connectly.exception.AuthenticationException;
import bg.connectly.model.User;
import bg.connectly.service.AuthService;
import bg.connectly.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerUnitTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
                    .authorizeHttpRequests(auth -> auth
                            .anyRequest().permitAll()// unrestricted access to all endpoints
                    )
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless session for JWT
                    );

            return http.build();
        }
    }

    @Test
    @Order(1)
    void editUserWithValidDataReturnsUpdatedUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("new-email@abv.bg");
        User updatedUser = new User();
        updatedUser.setEmail("new-email@abv.bg");

        when(authService.getEmailFromToken(anyString())).thenReturn("existing-email");
        when(userService.updateUser(any(UserDto.class), anyString())).thenReturn(updatedUser);

        mockMvc.perform(put("/api/user/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid-token")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new-email@abv.bg"));
    }

    @Test
    @Order(2)
    void editUserWithInvalidDataReturnsBadRequest() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("invalid-mail.com"); //Invalid email

        mockMvc.perform(put("/api/user/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid-token")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    void editUserWithInvalidTokenReturnsUnauthorized() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("new-email@abv.bg");

        when(authService.getEmailFromToken(anyString())).thenThrow(new AuthenticationException("Unauthorized"));

        mockMvc.perform(put("/api/user/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer invalid-token")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(4)
    void searchUsersWithValidSearchTextReturnsUsers() throws Exception {
        User user = new User();
        user.setEmail("test-user@abv.bg");

        User anthorUser = new User();
        anthorUser.setEmail("test-user2@abv.bg");

        Page<User> usersPage = new PageImpl<>(List.of(user, anthorUser));

        when(userService.searchUsers(anyString(), any(Pageable.class))).thenReturn(usersPage);

        mockMvc.perform(get("/api/user/search")
                        .param("searchText", "test")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("test-user@abv.bg"))
                .andExpect(jsonPath("$.content[1].email").value("test-user2@abv.bg"));
    }

    @Test
    @Order(5)
    void searchUsersWithNoResultsReturnsEmptyPage() throws Exception {
        Page<User> emptyPage = new PageImpl<>(Collections.emptyList());

        when(userService.searchUsers(anyString(), any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/api/user/search")
                        .param("searchText", "nonexistent")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

}