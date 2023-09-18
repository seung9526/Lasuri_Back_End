package com.suutich.systems.service.impl;

import com.suutich.systems.controller.UserController;
import com.suutich.systems.exception.LasuriAPIExceiption;
import com.suutich.systems.model.Role;
import com.suutich.systems.model.RoleType;
import com.suutich.systems.model.User;
import com.suutich.systems.payload.request.AddUserRequest;
import com.suutich.systems.payload.request.LoginRequest;
import com.suutich.systems.payload.request.UpdateUserRoleRequest;
import com.suutich.systems.payload.response.LoginResponse;
import com.suutich.systems.repository.RoleRepository;
import com.suutich.systems.repository.UserRepository;
import com.suutich.systems.security.JwtTokenProvider;
import com.suutich.systems.security.SecurityUtil;
import com.suutich.systems.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public User register(AddUserRequest addUserRequest) {
        if (Boolean.TRUE.equals(userRepository.existsByEmail(addUserRequest.getUserEmail()))) {
            throw new LasuriAPIExceiption(HttpStatus.BAD_REQUEST, "Email is already exists!.");
        }

        Role userRole = roleRepository.findByName(RoleType.USER)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(RoleType.USER);
                    return roleRepository.save(newRole);
                });

        User user = User.builder()
                .email(addUserRequest.getUserEmail())
                .password(passwordEncoder.encode(addUserRequest.getUserPassword()))
                .name(addUserRequest.getUserName())
                .address(addUserRequest.getUserAddress())
                .build();

        user.setRoles(Collections.singleton(userRole));


        return userRepository.save(user);
    }

    // 유저,권한 정보를 가져오는 메소드
    public Optional<User> getUserWithAuthorities(String email) {
        return Optional.ofNullable(userRepository.findOneWithAuthoritiesByEmail(email));
    }

    // 보안 컨텍스트에 현재 사용자 이름 정보만 검색하는 메서드
    @Transactional(readOnly = true)
    public Optional<User> getMyUserWithAuthorities() {
        return SecurityUtil.getCurrentUsername()
                .flatMap(email -> Optional.ofNullable(userRepository.findOneWithAuthoritiesByEmail(email)));
    }

    @Override
    public boolean existsByEmail(String userEmail) {
        // 이메일 주소를 이용하여 사용자를 데이터베이스에서 찾습니다.
        User user = userRepository.findByEmail(userEmail);

        // 사용자가 존재하면 true를 반환하고, 그렇지 않으면 false를 반환합니다.
        return user != null;
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

        existUser.getRoles().clear(); // 기존 authorities를 모두 지우고
        existUser.getRoles().add(userRole); // 새로운 권한을 추가합니다.

        return userRepository.save(existUser);
    }



    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        logger.debug(user.getEmail());
        logger.debug(user.getPassword());
        if (user == null) {
            // 사용자를 찾을 수 없음
            logger.error("로그인 실패: 사용자를 찾을 수 없음 - {}", loginRequest.getEmail());
            throw new LasuriAPIExceiption(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다.");
        }
        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            // 비밀번호 일치 - 로그인 성공
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String token = jwtTokenProvider.createToken(authentication);

            // 로그 메시지 출력
            logger.debug("로그인 성공: 사용자 이메일 - {}", loginRequest.getEmail());

            // 토큰을 반환
            return new LoginResponse(token);
        }
        // 로그인 실패 - 비밀번호가 일치하지 않음
        logger.error("로그인 실패: 사용자 이메일 - {}", loginRequest.getEmail());
        throw new LasuriAPIExceiption(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
    }



    @Override
    public User getName(String name) {
        return userRepository.findByName(name);
    }
}
