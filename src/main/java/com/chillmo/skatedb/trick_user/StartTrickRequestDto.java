package com.chillmo.skatedb.trick_user;


import lombok.Data;

@Data
public class StartTrickRequestDto {
    private Long userId;
    private Long trickId;
}