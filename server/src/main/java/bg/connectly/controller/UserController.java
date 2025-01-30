package bg.connectly.controller;

import bg.connectly.configuration.JwtUtil;
import bg.connectly.dto.EditUserDto;
import bg.connectly.model.User;
import bg.connectly.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;


    @PutMapping("/edit")
    public ResponseEntity<User> editUser(@Valid @RequestBody EditUserDto userDto,
                                         @RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User user = this.userService.updateUser(userDto, username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<User>> searchUsers(@RequestParam String searchText, Pageable pageable) {
        Page<User> users = this.userService.searchUsers(searchText, pageable);
        return ResponseEntity.ok(users);
    }


}
