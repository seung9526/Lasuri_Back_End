package com.suutich.systems.service.impl;

import com.suutich.systems.exception.LasuriAPIExceiption;
import com.suutich.systems.model.User;
import com.suutich.systems.payload.request.AddUserRequest;
import com.suutich.systems.payload.request.LoginRequest;
import com.suutich.systems.repository.UserRepository;
import com.suutich.systems.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User register(AddUserRequest addUserRequest) {

        // TODO: add check for email exists in the database
        if (Boolean.TRUE.equals(userRepository.existsByEmail(addUserRequest.getUserEmail()))) {
            throw new LasuriAPIExceiption(HttpStatus.BAD_REQUEST, "Email is already exists!.");
        }

        User user = addUserRequest.toEntity();
        return userRepository.save(user);
    }

    @Override
    public User modify(AddUserRequest addUserRequest) {
        User existUser = userRepository.findByEmail(addUserRequest.getUserEmail());

        if (existUser == null) {
            // Handle the case when the user does not exist
            throw new LasuriAPIExceiption(HttpStatus.BAD_REQUEST, "User not found");
        }

        // Update the user's fields if they are present in the request
        if (addUserRequest.getUserPassword() != null) {
            existUser.setPassword(addUserRequest.getUserPassword());
        }
        if (addUserRequest.getUserName() != null) {
            existUser.setName(addUserRequest.getUserName());
        }
        if (addUserRequest.getUserAddress() != null) {
            existUser.setAddress(addUserRequest.getUserAddress());
        }

        return userRepository.save(existUser);
    }

    @Override
    public User login(String email) {
        return userRepository.findByEmail(email);
    }
}
