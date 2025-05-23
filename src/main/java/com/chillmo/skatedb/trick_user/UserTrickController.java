package com.chillmo.skatedb.trick_user;


import com.chillmo.skatedb.trick.domain.Trick;
import com.chillmo.skatedb.trick.library.repository.TrickLibraryRepository;
import com.chillmo.skatedb.user.domain.User;
import com.chillmo.skatedb.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-tricks")
public class UserTrickController {

    private final UserTrickService userTrickService;
    private final UserRepository userRepository;
    private final TrickLibraryRepository trickLibraryRepository;

    public UserTrickController(UserTrickService userTrickService,
                               UserRepository userRepository,
                               TrickLibraryRepository trickLibraryRepository) {
        this.userTrickService = userTrickService;
        this.userRepository = userRepository;
        this.trickLibraryRepository = trickLibraryRepository;
    }

    /**
     * Post mapping to start a learning phase for a single trick.
     *
     * @param dto startTrickDto to connect trick with user.
     * @return error status or okay.
     */
    @PostMapping("/start")
    public ResponseEntity<?> startTrick(@RequestBody StartTrickRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));
        Trick trick = trickLibraryRepository.findById(dto.getTrickId())
                .orElseThrow(() -> new RuntimeException("Trick not found with id: " + dto.getTrickId()));

        UserTrick userTrick = userTrickService.startTrick(user, trick);

        return ResponseEntity.ok(userTrick);
    }
}