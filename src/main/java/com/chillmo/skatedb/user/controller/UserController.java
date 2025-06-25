package com.chillmo.skatedb.user.controller;

import com.chillmo.skatedb.user.domain.User;
import com.chillmo.skatedb.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieve a list of all registered users.
     *
     * @return list of users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Enable a user by id. Only admins may perform this action.
     */
    @PutMapping("/{id}/enable")
    //@PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> enableUser(@PathVariable Long id) {
        userService.enableUser(id);
        return ResponseEntity.noContent().build();
    }
}
