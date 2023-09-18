package com.suutich.systems.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.suutich.systems.model.User;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor
public class AddUserRequest {



    @NotNull
    private String userEmail;

    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String userPassword;

    @NotNull
    private String userName;

    private String userAddress;

    public User toEntity() {

        return User.builder()
                .email(userEmail)
                .password(userPassword) // 비밀번호 암호화
                .name(userName)
                .build();
    }
}
