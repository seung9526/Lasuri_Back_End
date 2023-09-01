package com.suutich.systems.service;

import com.suutich.systems.model.User;
import com.suutich.systems.payload.request.AddUserRequest;
import com.suutich.systems.payload.request.LoginRequest;

public interface UserService {
    User register(AddUserRequest addUserRequest);
    User modify(AddUserRequest addUserRequest);
    User login(String email);
}