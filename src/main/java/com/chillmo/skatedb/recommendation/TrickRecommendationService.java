package com.chillmo.skatedb.recommendation;


import com.chillmo.skatedb.trick.domain.Difficulty;
import com.chillmo.skatedb.trick.domain.Trick;
import com.chillmo.skatedb.trick.library.repository.TrickLibraryRepository;
import com.chillmo.skatedb.trick_user.UserTrick;
import com.chillmo.skatedb.trick_user.UserTrickRepository;
import com.chillmo.skatedb.user.domain.User;
import com.chillmo.skatedb.user.domain.ExperienceLevel;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
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
                .filter(trick -> isWithinDifficultyThreshold(trick.getDifficulty(), user.getExperienceLevel()))
                .collect(Collectors.toList());
    }

    private boolean isWithinDifficultyThreshold(Difficulty trickDifficulty, ExperienceLevel experienceLevel) {
        if (trickDifficulty == null) {
            return true;
        }

        return allowedDifficultiesFor(experienceLevel).contains(trickDifficulty);
    }

    private EnumSet<Difficulty> allowedDifficultiesFor(ExperienceLevel experienceLevel) {
        if (experienceLevel == null) {
            return EnumSet.allOf(Difficulty.class);
        }

        return EnumSet.range(Difficulty.BEGINNER, maxDifficultyFor(experienceLevel));
    }

    private Difficulty maxDifficultyFor(ExperienceLevel experienceLevel) {
        return switch (experienceLevel) {
            case NOVICE -> Difficulty.BEGINNER;
            case INTERMEDIATE -> Difficulty.INTERMEDIATE;
            case SKILLED -> Difficulty.ADVANCED;
            case PRO -> Difficulty.EXPERT;
        };
    }
}
