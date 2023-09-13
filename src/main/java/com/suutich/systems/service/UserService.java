package com.suutich.systems.service;

import com.suutich.systems.model.User;
import com.suutich.systems.payload.request.AddUserRequest;
import com.suutich.systems.payload.request.UpdateUserRoleRequest;

public interface UserService {
    User register(AddUserRequest addUserRequest);
    User modify(AddUserRequest addUserRequest);

    User updateUserRole(UpdateUserRoleRequest updateRequest);
    User login(String email);

    User getName(String name);
}