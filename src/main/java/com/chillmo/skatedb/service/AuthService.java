package com.chillmo.skatedb.service;

import com.chillmo.skatedb.dto.UserDTO;
import com.chillmo.skatedb.dto.UserLoginRequest;
import com.chillmo.skatedb.dto.UserRegisterRequest;

public interface AuthService {
    UserDTO registerUser(UserRegisterRequest registerRequest);
    String loginUser(UserLoginRequest loginRequest);
}