package com.suutich.systems.controller;


import com.suutich.systems.model.User;
import com.suutich.systems.payload.request.AddUserRequest;
import com.suutich.systems.payload.request.LoginRequest;
import com.suutich.systems.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(
        name = "Lasuri REST APIs"
)
public class UserController {

    private final UserService userService;

    // 회원가입 API
    @Operation(summary = "회원가입", description = "회원가입 진행")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "가입 성공"),
            @ApiResponse(responseCode = "400", description = "가입 실패")
    })
    @PostMapping({"/register", "/signup"})
    public ResponseEntity<User> register(@Valid @RequestBody AddUserRequest request) {

        User savedUser = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    // 로그인 API
    // TODO : 해당 부분 임의 적인 로그인이라 보안 관련 코드 짜야 함
    @Operation(summary = "로그인", description = "로그인 진행")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.login(loginRequest.getEmail());

            if (user != null && user.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.ok("Login successful");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }


    // 회원 정보 수정 API
    @Operation(summary = "회원 정보 수정", description = "회원 정보 수정 진행")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "수정 실패")
    })
    @PutMapping("/mypage")
    public ResponseEntity<User> modify(@Valid @RequestBody AddUserRequest request) {

        User update = userService.modify(request);
        return ResponseEntity.status(HttpStatus.OK).body(update);
    }
}