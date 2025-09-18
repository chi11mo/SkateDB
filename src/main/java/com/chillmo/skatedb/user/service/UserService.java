package com.chillmo.skatedb.user.service;
import com.chillmo.skatedb.user.domain.User;
import com.chillmo.skatedb.user.dto.ChangePasswordRequest;
import com.chillmo.skatedb.user.dto.UpdateProfileRequest;
import com.chillmo.skatedb.user.dto.UpdateUserRolesRequest;
import com.chillmo.skatedb.user.exception.InvalidPasswordException;
import com.chillmo.skatedb.user.exception.UserNotFoundException;
import com.chillmo.skatedb.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieve all users from the database.
     *
     * @return list of users
     */
    @Transactional(readOnly = true)
    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Enable a user account by id.
     *
     * @param id user id
     * @return the updated user
     */
    public User enableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setEnabled(true);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    @Transactional
    public User updateProfile(String username, UpdateProfileRequest request) {
        User user = getByUsername(username);

        if (request.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(request.getProfilePictureUrl());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getLocation() != null) {
            user.setLocation(request.getLocation());
        }
        if (request.getExperienceLevel() != null) {
            user.setExperienceLevel(request.getExperienceLevel());
        }
        if (request.getStand() != null) {
            user.setStand(request.getStand());
        }

        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = getByUsername(username);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteAccount(String username) {
        User user = getByUsername(username);
        userRepository.delete(user);
    }

    @Transactional
    public User updateUserRoles(Long id, UpdateUserRolesRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        Set<com.chillmo.skatedb.user.domain.Role> roles = request.getRoles();
        user.setRoles(roles == null ? new HashSet<>() : new HashSet<>(roles));
        return userRepository.save(user);
    }
}
