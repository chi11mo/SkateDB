package com.chillmo.skatedb.recommendation;


import com.chillmo.skatedb.trick.domain.Trick;
import com.chillmo.skatedb.trick.library.repository.TrickLibraryRepository;
import com.chillmo.skatedb.trick_user.UserTrick;
import com.chillmo.skatedb.trick_user.UserTrickRepository;
import com.chillmo.skatedb.user.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TrickRecommendationService {

    private final TrickLibraryRepository trickLibraryRepository;
    private final UserTrickRepository userTrickRepository;

    public TrickRecommendationService(TrickLibraryRepository trickLibraryRepository,
                                      UserTrickRepository userTrickRepository) {
        this.trickLibraryRepository = trickLibraryRepository;
        this.userTrickRepository = userTrickRepository;
    }

    /**
     * Service method to get a logical list of tricks, that the user can learn next.
     *
     * @param user to get correct trick recommendation.
     * @return a list of tricks.
     */
    public List<Trick> getRecommendedTricksForUser(User user) {
        Set<Trick> learnedTricks = userTrickRepository.findByUser(user)
                .stream()
                .map(UserTrick::getTrick)
                .collect(Collectors.toSet());

        List<Trick> allTricks = trickLibraryRepository.findAll();

        return allTricks.stream()
                .filter(trick -> !learnedTricks.contains(trick))
                .filter(trick -> trick.getPrerequisites().isEmpty() ||
                        trick.getPrerequisites().stream().allMatch(learnedTricks::contains))
                .collect(Collectors.toList());
    }
}
