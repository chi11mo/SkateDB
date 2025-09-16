package com.chillmo.skatedb.trick_user;


import com.chillmo.skatedb.trick.domain.Trick;
import com.chillmo.skatedb.user.domain.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserTrickService {

    private final UserTrickRepository userTrickRepository;

    public UserTrickService(UserTrickRepository userTrickRepository) {
        this.userTrickRepository = userTrickRepository;
    }

    /**
     * Start a trick learning session for a user.
     *
     * @param user  who starts a trick learning session.
     * @param trick who get started from a user.
     * @return the trick is started.
     */
    public UserTrick startTrick(User user, Trick trick) {

        Optional<UserTrick> existingEntry = userTrickRepository.findByUserAndTrick(user, trick);
        if (existingEntry.isPresent()) {
            return existingEntry.get();
        }
        UserTrick newEntry = UserTrick.builder()
                .user(user)
                .trick(trick)
                .dateLearned(LocalDateTime.now())
                .status(TrickStatus.IN_PROGRESS)
                .build();
        return userTrickRepository.save(newEntry);
    }

    public List<UserTrick> getUserTricks(Long userId) {
        return userTrickRepository.findAllByUserId(userId);
    }

    public UserTrick updateTrickStatus(Long id, TrickStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("TrickStatus must not be null");
        }

        UserTrick userTrick = userTrickRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserTrick not found with id: " + id));

        userTrick.setStatus(status);

        return userTrickRepository.save(userTrick);
    }
}
