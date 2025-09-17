package com.chillmo.skatedb.user.controller;

import com.chillmo.skatedb.user.domain.User;
import com.chillmo.skatedb.user.dto.ChangePasswordRequest;
import com.chillmo.skatedb.user.dto.UpdateProfileRequest;
import com.chillmo.skatedb.user.dto.UpdateUserRolesRequest;
import com.chillmo.skatedb.user.dto.UserProfileResponse;
import com.chillmo.skatedb.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        List<UserProfileResponse> users = userService.getAllUsers().stream()
                .map(UserProfileResponse::from)
                .toList();
        return ResponseEntity.ok(users);
    }

    /**
     * Enable a user by id. Only admins may perform this action.
     */
    @PutMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> enableUser(@PathVariable Long id) {
        userService.enableUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileResponse> getCurrentUser(Authentication authentication) {
        User user = userService.getByUsername(authentication.getName());
        return ResponseEntity.ok(UserProfileResponse.from(user));
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileResponse> updateProfile(@RequestBody @Valid UpdateProfileRequest request,
                                                             Authentication authentication) {
        User updated = userService.updateProfile(authentication.getName(), request);
        return ResponseEntity.ok(UserProfileResponse.from(updated));
    }

    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request,
                                               Authentication authentication) {
        userService.changePassword(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteAccount(Authentication authentication) {
        userService.deleteAccount(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> updateUserRoles(@PathVariable Long id,
                                                               @RequestBody @Valid UpdateUserRolesRequest request) {
        User user = userService.updateUserRoles(id, request);
        return ResponseEntity.ok(UserProfileResponse.from(user));
    }
}
