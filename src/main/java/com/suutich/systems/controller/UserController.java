package com.suutich.systems.controller;

import com.suutich.systems.exception.LasuriAPIExceiption;
import com.suutich.systems.model.User;
import com.suutich.systems.payload.request.AddUserRequest;
import com.suutich.systems.payload.request.LoginRequest;
import com.suutich.systems.payload.request.UpdateUserRoleRequest;
import com.suutich.systems.payload.response.LoginResponse;
import com.suutich.systems.payload.response.UserProfileResponse;
import com.suutich.systems.security.JwtTokenProvider;
import com.suutich.systems.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.log4j2.Log4J2LoggingSystem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(
        name = "Lasuri REST APIs"
)
@SecurityRequirement(name = "bearer")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 API
    @Operation(summary = "회원가입", description = "회원가입 진행")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "가입 성공"),
            @ApiResponse(responseCode = "400", description = "가입 실패")
    })
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody AddUserRequest request) {
        // 주의: 이미 존재하는 이메일인지 확인
        if (userService.existsByEmail(request.getUserEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // 주의: 비밀번호를 안전하게 저장
        String encodedPassword = passwordEncoder.encode(request.getUserPassword());
        request.setUserPassword(encodedPassword);

        User savedUser = userService.register(request);

        // 사용자의 인증 정보를 가져와서 토큰 생성
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = jwtTokenProvider.createToken(authentication);

        // 주의: 토큰을 응답 헤더에 추가
        return ResponseEntity.status(HttpStatus.CREATED).header("Authorization", "Bearer " + token).body(savedUser);
    }


    // 로그인 API
    // TODO : 해당 부분 임의 적인 로그인이라 보안 관련 코드 짜야 함
    @Operation(summary = "로그인", description = "로그인 진행")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            if (loginRequest == null || loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
                // 요청이 null 또는 필수 필드가 null인 경우 400 Bad Request 반환
                logger.info("400 에러: 요청이 올바르지 않습니다.");
                return ResponseEntity.badRequest().body(null);
            }

            LoginResponse loginResponse = userService.login(loginRequest);

            if (loginResponse != null && loginResponse.getAccessToken() != null) {
                // 로그인 성공 시 200 OK와 토큰 반환
                logger.info("로그인 성공: {}", loginRequest.getEmail());
                return ResponseEntity.ok().body(loginResponse);
            } else {
                // 사용자를 찾을 수 없거나 로그인 실패
                logger.error("로그인 실패 또는 사용자를 찾을 수 없음: {}", loginRequest.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } catch (LasuriAPIExceiption e) {
            // 예외 처리 및 적절한 오류 응답
            logger.error("로그인 오류: {}", e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(null);
        } catch (Exception e) {
            // 예외 처리 및 서버 오류 응답
            logger.error("서버 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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

    // 전문가 권한 신청 API
    @Operation(summary = "일반유저 전문가 권한 업데이트", description = "일반유저 전문가 권한 업데이트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "수정 실패")
    })
    @PutMapping("/pro")
    public ResponseEntity<User> modifyRole(@Valid @RequestBody UpdateUserRoleRequest updateRequest) {
        User update = userService.updateUserRole(updateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(update);
    }

    // 사용자 이름으로 사용자 찾기 API
    @Operation(summary = "사용자 정보를 반환", description = "사용자 정보를 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "사용자 정보를 반환 할수없음")
    })
    @GetMapping("/userInfo/{name}")
    public ResponseEntity<User> findUserByName(@PathVariable String name) {
        User user = userService.getName(name);

        if (user != null) {
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile() {
        // 현재 로그인한 사용자의 정보 가져오기
        Optional<User> userOptional = userService.getMyUserWithAuthorities();

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // UserProfileResponse 객체 생성
            UserProfileResponse userProfileResponse = new UserProfileResponse(
                    user.getEmail(),
                    user.getName(),
                    user.getAddress()
            );

            // 사용자 정보를 담은 응답 객체 반환
            return ResponseEntity.ok(userProfileResponse);
        } else {
            // 사용자 정보를 찾지 못한 경우 404 응답 반환
            return ResponseEntity.notFound().build();
        }
    }

}
