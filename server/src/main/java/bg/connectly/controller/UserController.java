package bg.connectly.controller;

import bg.connectly.dto.UserDto;
import bg.connectly.model.User;
import bg.connectly.service.AuthService;
import bg.connectly.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for managing user-related operations.
 * This class provides endpoints for editing user details and searching users.
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "User", description = "Endpoints for managing users")
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    /**
     * Endpoint for editing user details.
     *
     * @param userDto the user data transfer object containing updated information
     * @param token   the authorization token
     * @return the updated user
     */
    @Operation(summary = "Edit user details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/edit")
    public ResponseEntity<User> editUser(@Valid @RequestBody UserDto userDto,
                                         @RequestHeader("Authorization") String token) {
        String email = authService.getEmailFromToken(token);
        User user = this.userService.updateUser(userDto, email);
        return ResponseEntity.ok(user);
    }

    /**
     * Endpoint for searching users.
     *
     * @param searchText the text to search for
     * @param pageable   the pagination information
     * @return a page of users matching the search criteria
     */
    @Operation(summary = "Search users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<User>> searchUsers(@RequestParam String searchText,
                                                  Pageable pageable) {
        Page<User> users = this.userService.searchUsers(searchText, pageable);
        return ResponseEntity.ok(users);
    }


}
