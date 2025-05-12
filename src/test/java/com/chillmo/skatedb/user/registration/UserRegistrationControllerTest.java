package com.chillmo.skatedb.user.registration;



import com.chillmo.skatedb.security.JwtAuthenticationFilter;
import com.chillmo.skatedb.user.domain.User;
import com.chillmo.skatedb.user.registration.controller.UserRegistrationController;
import com.chillmo.skatedb.user.registration.dto.UserRegistrationDto;
import com.chillmo.skatedb.user.registration.exception.EmailAlreadyExistsException;
import com.chillmo.skatedb.user.registration.exception.InvalidPasswordException;
import com.chillmo.skatedb.user.registration.exception.UsernameAlreadyExistsException;
import com.chillmo.skatedb.user.registration.service.UserRegistrationService;
import com.chillmo.skatedb.util.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.chillmo.skatedb.user.domain.ExperienceLevel.NOVICE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserRegistrationController.class)
@AutoConfigureMockMvc(addFilters = false) // Security-Filter für diesen Test deaktivieren
class UserRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRegistrationService userRegistrationService;
    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;



    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("POST /api/register - Erfolg (201)")
    void whenValidInput_thenReturns201() throws Exception {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("johnDoe");
        dto.setEmail("john@example.com");
        dto.setPassword("Abcd1234!");
        dto.setExperienceLevel(NOVICE);

        User savedUser = User.builder()
                .id(42L)
                .username(dto.getUsername())
                .email(dto.getEmail())
                .build();

        when(userRegistrationService.registerUser(any(UserRegistrationDto.class)))
                .thenReturn(savedUser);

        // When & Then
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.username").value("johnDoe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @DisplayName("POST /api/register - Konflikt bei existierendem Username (409)")
    void whenUsernameExists_thenReturns409() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("johnDoe");
        dto.setEmail("john2@example.com");
        dto.setPassword("Abcd1234!");
        dto.setExperienceLevel(NOVICE);

        when(userRegistrationService.registerUser(any(UserRegistrationDto.class)))
                .thenThrow(new UsernameAlreadyExistsException(dto.getUsername()));

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Username bereits vergeben: " + dto.getUsername()))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("POST /api/register - Konflikt bei existierender E-Mail (409)")
    void whenEmailExists_thenReturns409() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("janeDoe");
        dto.setEmail("jane@example.com");
        dto.setPassword("Abcd1234!");
        dto.setExperienceLevel(NOVICE);

        when(userRegistrationService.registerUser(any(UserRegistrationDto.class)))
                .thenThrow(new EmailAlreadyExistsException(dto.getEmail()));

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Email bereits verwendet: " + dto.getEmail()))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("POST /api/register - Ungültiges Passwort (400)")
    void whenInvalidPassword_thenReturns400() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("janeDoe");
        dto.setEmail("jane2@example.com");
        dto.setPassword("weak"); // zu schwach
        dto.setExperienceLevel(NOVICE);

        when(userRegistrationService.registerUser(any(UserRegistrationDto.class)))
                .thenThrow(new InvalidPasswordException(
                        "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character."
                ));

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character."
                ))
                .andExpect(jsonPath("$.status").value(400));
    }
}