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
}