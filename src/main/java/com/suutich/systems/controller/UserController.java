package com.suutich.systems.controller;


import com.suutich.systems.model.User;
import com.suutich.systems.payload.request.AddUserRequest;
import com.suutich.systems.payload.request.LoginRequest;
import com.suutich.systems.payload.request.UpdateUserRoleRequest;
import com.suutich.systems.payload.response.LoginApiResponse;
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
            @ApiResponse(responseCode = "401", description = "로그인 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.login(loginRequest.getEmail());

            if (user != null && user.getPassword().equals(loginRequest.getPassword())) {
                // 로그인 성공
                return ResponseEntity.ok(new LoginApiResponse<>(true, user, "로그인 성공"));
            } else {
                // 사용자를 찾을 수 없을 때
                if (user == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new LoginApiResponse<>(false, null, "사용자를 찾을 수 없음"));
                }
                // 로그인 실패
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginApiResponse<>(false, null, "로그인 실패"));
            }
        } catch (Exception e) {
            // 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginApiResponse<>(false, null, "서버 오류 발생"));
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

    // TODO : JWT 토큰으로 바꿔주는 작업 전반적으로 해줘야 함
    // 사용자 이름으로 사용자 찾기 API
    @Operation(summary = "사용자 정보를 반환", description = "사용자 정보를 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "사용자 정보를 반환 할수없음")
    })
    @GetMapping("/userInfo")
    public ResponseEntity<User> findUserByName(@RequestParam String name) {
        User user = userService.getName(name);

        if (user != null) {
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}