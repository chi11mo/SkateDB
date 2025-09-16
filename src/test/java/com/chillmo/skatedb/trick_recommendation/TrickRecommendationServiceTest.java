package com.chillmo.skatedb.trick_recommendation;

import com.chillmo.skatedb.recommendation.TrickRecommendationService;
import com.chillmo.skatedb.trick.domain.Difficulty;
import com.chillmo.skatedb.trick.domain.Trick;
import com.chillmo.skatedb.trick.domain.TrickType;
import com.chillmo.skatedb.trick.library.repository.TrickLibraryRepository;
import com.chillmo.skatedb.trick_user.UserTrick;
import com.chillmo.skatedb.trick_user.UserTrickRepository;
import com.chillmo.skatedb.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrickRecommendationServiceTest {

    @Mock
    private TrickLibraryRepository trickLibraryRepository;

    @Mock
    private UserTrickRepository userTrickRepository;

    @InjectMocks
    private TrickRecommendationService recommendationService;

    @Test
    void testGetRecommendedTricksForUser() {
        // Erstelle Beispiel-Tricks
        Trick ollie = Trick.builder()
                .id(1L)
                .name("Ollie")
                .difficulty(Difficulty.BEGINNER)
                .trickType(TrickType.STREET)
                .prerequisites(new ArrayList<>())
                .build();

        Trick kickflip = Trick.builder()
                .id(2L)
                .name("Kickflip")
                .difficulty(Difficulty.INTERMEDIATE)
                .trickType(TrickType.STREET)
                .prerequisites(Arrays.asList(ollie))
                .build();

        Trick heelflip = Trick.builder()
                .id(3L)
                .name("Heelflip")
                .difficulty(Difficulty.INTERMEDIATE)
                .trickType(TrickType.STREET)
                .prerequisites(Arrays.asList(ollie))
                .build();

        Trick varialKickflip = Trick.builder()
                .id(4L)
                .name("Varial Kickflip")
                .difficulty(Difficulty.ADVANCED)
                .trickType(TrickType.STREET)
                .prerequisites(Arrays.asList(ollie, kickflip))
                .build();

        Trick impossible = Trick.builder()
                .id(5L)
                .name("Impossible")
                .difficulty(Difficulty.EXPERT)
                .trickType(TrickType.STREET)
                .prerequisites(Arrays.asList(ollie, kickflip))
                .build();

        // Erstelle einen Test-User
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        // Simuliere, dass der User bereits 'Ollie' und 'Kickflip' gelernt hat
        UserTrick userTrick1 = UserTrick.builder().id(1L).user(user).trick(ollie).build();
        UserTrick userTrick2 = UserTrick.builder().id(2L).user(user).trick(kickflip).build();
        List<UserTrick> learnedTricks = Arrays.asList(userTrick1, userTrick2);

        // Konfiguriere die Mocks
        when(userTrickRepository.findByUser(user)).thenReturn(learnedTricks);
        List<Trick> allTricks = Arrays.asList(ollie, kickflip, heelflip, varialKickflip, impossible);
        when(trickLibraryRepository.findAll()).thenReturn(allTricks);

        // Führe die Empfehlungslogik aus
        List<Trick> recommendations = recommendationService.getRecommendedTricksForUser(user);

        // Erwartet: 'Heelflip', 'Varial Kickflip' und 'Impossible'
        assertFalse(recommendations.contains(ollie), "Ollie sollte nicht empfohlen werden");
        assertFalse(recommendations.contains(kickflip), "Kickflip sollte nicht empfohlen werden");
        assertTrue(recommendations.contains(heelflip), "Heelflip sollte empfohlen werden");
        assertTrue(recommendations.contains(varialKickflip), "Varial Kickflip sollte empfohlen werden");
        assertTrue(recommendations.contains(impossible), "Impossible sollte empfohlen werden");
        assertEquals(3, recommendations.size(), "Es sollten genau 3 Tricks empfohlen werden");
    }

    @Test
    void testTricksWithUnmetPrerequisitesAreFilteredOut() {
        Trick ollie = Trick.builder()
                .id(1L)
                .name("Ollie")
                .difficulty(Difficulty.BEGINNER)
                .trickType(TrickType.STREET)
                .prerequisites(new ArrayList<>())
                .build();

        Trick kickflip = Trick.builder()
                .id(2L)
                .name("Kickflip")
                .difficulty(Difficulty.INTERMEDIATE)
                .trickType(TrickType.STREET)
                .prerequisites(List.of(ollie))
                .build();

        Trick varialKickflip = Trick.builder()
                .id(3L)
                .name("Varial Kickflip")
                .difficulty(Difficulty.ADVANCED)
                .trickType(TrickType.STREET)
                .prerequisites(List.of(ollie, kickflip))
                .build();

        User user = new User();
        user.setId(7L);
        user.setUsername("learner");

        UserTrick ollieTrick = UserTrick.builder().id(11L).user(user).trick(ollie).build();
        when(userTrickRepository.findByUser(user)).thenReturn(List.of(ollieTrick));
        when(trickLibraryRepository.findAll()).thenReturn(List.of(ollie, kickflip, varialKickflip));

        List<Trick> recommendations = recommendationService.getRecommendedTricksForUser(user);

        assertTrue(recommendations.contains(kickflip), "Kickflip sollte empfohlen werden, da alle Voraussetzungen erfüllt sind");
        assertFalse(recommendations.contains(varialKickflip), "Varial Kickflip darf nicht empfohlen werden, da ein Prerequisite fehlt");
        assertEquals(1, recommendations.size(), "Es sollte genau ein Trick empfohlen werden");
    }

    @Test
    void testTricksWithoutPrerequisitesRemainAvailable() {
        Trick shuvit = Trick.builder()
                .id(4L)
                .name("Shuvit")
                .difficulty(Difficulty.BEGINNER)
                .trickType(TrickType.STREET)
                .prerequisites(new ArrayList<>())
                .build();

        Trick kickflip = Trick.builder()
                .id(5L)
                .name("Kickflip")
                .difficulty(Difficulty.INTERMEDIATE)
                .trickType(TrickType.STREET)
                .prerequisites(List.of(shuvit))
                .build();

        User user = new User();
        user.setId(2L);
        user.setUsername("beginner");

        when(userTrickRepository.findByUser(user)).thenReturn(Collections.emptyList());
        when(trickLibraryRepository.findAll()).thenReturn(List.of(shuvit, kickflip));

        List<Trick> recommendations = recommendationService.getRecommendedTricksForUser(user);

        assertTrue(recommendations.contains(shuvit), "Tricks ohne Voraussetzungen sollten empfohlen werden");
        assertFalse(recommendations.contains(kickflip), "Kickflip darf nicht empfohlen werden, wenn 'Shuvit' noch nicht gelernt wurde");
    }
}
