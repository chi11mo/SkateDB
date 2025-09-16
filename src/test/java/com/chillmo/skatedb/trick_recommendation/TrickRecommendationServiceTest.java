package com.chillmo.skatedb.trick_recommendation;

import com.chillmo.skatedb.recommendation.TrickRecommendationService;
import com.chillmo.skatedb.trick.domain.Difficulty;
import com.chillmo.skatedb.trick.domain.Trick;
import com.chillmo.skatedb.trick.domain.TrickType;
import com.chillmo.skatedb.trick.library.repository.TrickLibraryRepository;
import com.chillmo.skatedb.trick_user.UserTrick;
import com.chillmo.skatedb.trick_user.UserTrickRepository;
import com.chillmo.skatedb.user.domain.ExperienceLevel;
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
        user.setExperienceLevel(ExperienceLevel.PRO);

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
        user.setExperienceLevel(ExperienceLevel.PRO);

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
        user.setExperienceLevel(ExperienceLevel.PRO);

        when(userTrickRepository.findByUser(user)).thenReturn(Collections.emptyList());
        when(trickLibraryRepository.findAll()).thenReturn(List.of(shuvit, kickflip));

        List<Trick> recommendations = recommendationService.getRecommendedTricksForUser(user);

        assertTrue(recommendations.contains(shuvit), "Tricks ohne Voraussetzungen sollten empfohlen werden");
        assertFalse(recommendations.contains(kickflip), "Kickflip darf nicht empfohlen werden, wenn 'Shuvit' noch nicht gelernt wurde");
    }

    @Test
    void testNoviceUsersOnlyReceiveBeginnerRecommendations() {
        Trick beginner = Trick.builder()
                .id(10L)
                .name("Beginner Trick")
                .difficulty(Difficulty.BEGINNER)
                .trickType(TrickType.STREET)
                .prerequisites(new ArrayList<>())
                .build();

        Trick intermediate = Trick.builder()
                .id(11L)
                .name("Intermediate Trick")
                .difficulty(Difficulty.INTERMEDIATE)
                .trickType(TrickType.STREET)
                .prerequisites(new ArrayList<>())
                .build();

        Trick advanced = Trick.builder()
                .id(12L)
                .name("Advanced Trick")
                .difficulty(Difficulty.ADVANCED)
                .trickType(TrickType.STREET)
                .prerequisites(new ArrayList<>())
                .build();

        Trick expert = Trick.builder()
                .id(13L)
                .name("Expert Trick")
                .difficulty(Difficulty.EXPERT)
                .trickType(TrickType.STREET)
                .prerequisites(new ArrayList<>())
                .build();

        User user = new User();
        user.setId(3L);
        user.setUsername("novice");
        user.setExperienceLevel(ExperienceLevel.NOVICE);

        when(userTrickRepository.findByUser(user)).thenReturn(Collections.emptyList());
        when(trickLibraryRepository.findAll()).thenReturn(List.of(beginner, intermediate, advanced, expert));

        List<Trick> recommendations = recommendationService.getRecommendedTricksForUser(user);

        assertTrue(recommendations.contains(beginner), "Beginner tricks should be recommended to novice users");
        assertFalse(recommendations.contains(intermediate), "Intermediate tricks should not be recommended to novice users");
        assertFalse(recommendations.contains(advanced), "Advanced tricks should not be recommended to novice users");
        assertFalse(recommendations.contains(expert), "Expert tricks should not be recommended to novice users");
        assertEquals(1, recommendations.size(), "Only beginner tricks should be recommended to novice users");
    }

    @Test
    void testIntermediateUsersReceiveBeginnerAndIntermediateRecommendations() {
        Trick beginner = Trick.builder()
                .id(14L)
                .name("Beginner Trick")
                .difficulty(Difficulty.BEGINNER)
                .trickType(TrickType.STREET)
                .prerequisites(new ArrayList<>())
                .build();

        Trick intermediate = Trick.builder()
                .id(15L)
                .name("Intermediate Trick")
                .difficulty(Difficulty.INTERMEDIATE)
                .trickType(TrickType.STREET)
                .prerequisites(new ArrayList<>())
                .build();

        Trick advanced = Trick.builder()
                .id(16L)
                .name("Advanced Trick")
                .difficulty(Difficulty.ADVANCED)
                .trickType(TrickType.STREET)
                .prerequisites(new ArrayList<>())
                .build();

        User user = new User();
        user.setId(4L);
        user.setUsername("intermediate");
        user.setExperienceLevel(ExperienceLevel.INTERMEDIATE);

        when(userTrickRepository.findByUser(user)).thenReturn(Collections.emptyList());
        when(trickLibraryRepository.findAll()).thenReturn(List.of(beginner, intermediate, advanced));

        List<Trick> recommendations = recommendationService.getRecommendedTricksForUser(user);

        assertTrue(recommendations.contains(beginner), "Beginner tricks should be available to intermediate users");
        assertTrue(recommendations.contains(intermediate), "Intermediate tricks should be available to intermediate users");
        assertFalse(recommendations.contains(advanced), "Advanced tricks should not be available to intermediate users");
        assertEquals(2, recommendations.size(), "Intermediate users should receive beginner and intermediate tricks");
    }

    @Test
    void testSkilledUsersReceiveUpToAdvancedRecommendations() {
        Trick beginner = Trick.builder()
                .id(17L)
                .name("Beginner Trick")
                .difficulty(Difficulty.BEGINNER)
                .trickType(TrickType.STREET)
                .prerequisites(new ArrayList<>())
                .build();

        Trick intermediate = Trick.builder()
                .id(18L)
                .name("Intermediate Trick")
                .difficulty(Difficulty.INTERMEDIATE)
                .trickType(TrickType.STREET)
                .prerequisites(new ArrayList<>())
                .build();

        Trick advanced = Trick.builder()
                .id(19L)
                .name("Advanced Trick")
                .difficulty(Difficulty.ADVANCED)
                .trickType(TrickType.STREET)
                .prerequisites(new ArrayList<>())
                .build();

        Trick expert = Trick.builder()
                .id(20L)
                .name("Expert Trick")
                .difficulty(Difficulty.EXPERT)
                .trickType(TrickType.STREET)
                .prerequisites(new ArrayList<>())
                .build();

        User user = new User();
        user.setId(5L);
        user.setUsername("skilled");
        user.setExperienceLevel(ExperienceLevel.SKILLED);

        when(userTrickRepository.findByUser(user)).thenReturn(Collections.emptyList());
        when(trickLibraryRepository.findAll()).thenReturn(List.of(beginner, intermediate, advanced, expert));

        List<Trick> recommendations = recommendationService.getRecommendedTricksForUser(user);

        assertTrue(recommendations.contains(beginner), "Beginner tricks should be available to skilled users");
        assertTrue(recommendations.contains(intermediate), "Intermediate tricks should be available to skilled users");
        assertTrue(recommendations.contains(advanced), "Advanced tricks should be available to skilled users");
        assertFalse(recommendations.contains(expert), "Expert tricks should not be available to skilled users");
        assertEquals(3, recommendations.size(), "Skilled users should receive up to advanced tricks");
    }

}
