package com.chillmo.skatedb.user.dto;

import com.chillmo.skatedb.user.domain.ExperienceLevel;
import com.chillmo.skatedb.user.domain.Stand;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Payload for updating profile attributes of the currently authenticated user.
 */
@Data
public class UpdateProfileRequest {

    @Size(max = 255)
    private String profilePictureUrl;

    @Size(max = 500)
    private String bio;

    @Size(max = 100)
    private String location;

    private ExperienceLevel experienceLevel;

    private Stand stand;
}
