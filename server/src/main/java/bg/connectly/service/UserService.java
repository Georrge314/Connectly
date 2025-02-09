package bg.connectly.service;

import bg.connectly.dto.UserDto;
import bg.connectly.model.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    User updateUser(@Valid UserDto userDto, String username);

    Page<User> searchUsers(String searchText, Pageable pageable);
}
