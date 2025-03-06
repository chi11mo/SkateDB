package com.chillmo.skatedb.recommendation;


import com.chillmo.skatedb.trick.domain.Trick;
import com.chillmo.skatedb.user.domain.User;
import com.chillmo.skatedb.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tricks")
public class TrickRecommendationController {

    private final TrickRecommendationService recommendationService;
    private final UserRepository userRepository;

    @Autowired
    public TrickRecommendationController(TrickRecommendationService recommendationService,
                                         UserRepository userRepository) {
        this.recommendationService = recommendationService;
        this.userRepository = userRepository;
    }

    /**
     * Get a list of Recommendation of tricks the user have to learn next.
     *
     * @param userId of user to get the recommendation.
     * @return a list of tricks user have to learn next.
     */
    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<List<Trick>> getRecommendedTricks(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        List<Trick> recommendations = recommendationService.getRecommendedTricksForUser(user);

        return ResponseEntity.ok(recommendations);
    }
}
