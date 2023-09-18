package com.suutich.systems.service;

import com.suutich.systems.model.User;
import com.suutich.systems.payload.request.AddUserRequest;
import com.suutich.systems.payload.request.LoginRequest;
import com.suutich.systems.payload.request.UpdateUserRoleRequest;
import com.suutich.systems.payload.response.LoginResponse;

import java.util.Optional;

public interface UserService {
    User register(AddUserRequest addUserRequest);
    User modify(AddUserRequest addUserRequest);

    User updateUserRole(UpdateUserRoleRequest updateRequest);
    LoginResponse login(LoginRequest loginRequest);

    User getName(String name);

    Optional<User> getMyUserWithAuthorities();

    boolean existsByEmail(String userEmail);
}