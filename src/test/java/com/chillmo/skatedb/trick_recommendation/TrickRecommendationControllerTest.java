package com.chillmo.skatedb.trick_recommendation;

import com.chillmo.skatedb.recommendation.TrickRecommendationController;
import com.chillmo.skatedb.recommendation.TrickRecommendationService;
import com.chillmo.skatedb.trick.domain.Trick;
import com.chillmo.skatedb.user.domain.User;
import com.chillmo.skatedb.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrickRecommendationController.class)
class TrickRecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrickRecommendationService recommendationService;

    @MockBean
    private UserRepository userRepository;

    // Mock für den JwtUtils, damit die Sicherheitskonfiguration vollständig ist
    @MockBean
    private com.chillmo.skatedb.util.JwtUtils jwtUtils;

    @Test
    @WithMockUser(username = "testuser")  // Fügt einen fiktiven, authentifizierten User hinzu
    void testGetRecommendedTricks() throws Exception {
        // Erstelle einen Beispiel-User
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        // Erstelle Beispiel-Tricks als Empfehlungen
        Trick trick1 = Trick.builder().id(2L).name("Kickflip").build();
        Trick trick2 = Trick.builder().id(3L).name("Heelflip").build();
        List<Trick> recommendations = Arrays.asList(trick1, trick2);

        // Mocks konfigurieren
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(recommendationService.getRecommendedTricksForUser(user))
                .thenReturn(recommendations);

        // Führe den GET-Request aus und überprüfe das Ergebnis
        mockMvc.perform(get("/api/tricks/recommendations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Kickflip"))
                .andExpect(jsonPath("$[1].name").value("Heelflip"));
    }
}