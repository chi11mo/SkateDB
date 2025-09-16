package com.chillmo.skatedb.trick_user;

import lombok.Data;

@Data
public class UpdateTrickStatusRequestDto {
    private TrickStatus status;
}
