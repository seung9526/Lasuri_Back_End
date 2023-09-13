package com.suutich.systems.service.impl;

import com.suutich.systems.exception.LasuriAPIExceiption;
import com.suutich.systems.model.Role;
import com.suutich.systems.model.RoleType;
import com.suutich.systems.model.User;
import com.suutich.systems.payload.request.AddUserRequest;
import com.suutich.systems.payload.request.UpdateUserRoleRequest;
import com.suutich.systems.repository.RoleRepository;
import com.suutich.systems.repository.UserRepository;
import com.suutich.systems.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public User register(AddUserRequest addUserRequest) {

        // TODO: add check for email exists in the database
        if (Boolean.TRUE.equals(userRepository.existsByEmail(addUserRequest.getUserEmail()))) {
            throw new LasuriAPIExceiption(HttpStatus.BAD_REQUEST, "Email is already exists!.");
        }

        Role userRole = roleRepository.findByName(RoleType.USER)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(RoleType.USER);
                    return roleRepository.save(newRole);
                });

        User user = addUserRequest.toEntity();
        user.getRoles().add(userRole);

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
    public User updateUserRole(UpdateUserRoleRequest updateRequest) {
        User existUser = userRepository.findByEmail(updateRequest.getUserEmail());

        if (existUser == null) {
            throw new LasuriAPIExceiption(HttpStatus.BAD_REQUEST, "User not found");
        }

        RoleType requestedRole = RoleType.valueOf(updateRequest.getRole());

        if (requestedRole == null) {
            throw new LasuriAPIExceiption(HttpStatus.BAD_REQUEST, "Invalid role");
        }

        Role userRole = roleRepository.findByName(requestedRole)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(requestedRole);
                    return roleRepository.save(newRole);
                });

        existUser.getRoles().clear();
        existUser.getRoles().add(userRole);

        return userRepository.save(existUser);
    }

    @Override
    public User login(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User getName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //User user = userRepository.findByEmail(email)
        //        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
        return null;
    }
}
