package com.suutich.systems.payload.request;

import com.suutich.systems.model.User;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class AddUserRequest {
    private String userEmail;
    private String userPassword;
    private String userName;
    private String userAddress;

    @Builder
    public User toEntity(){
        return User.builder()
                .email(userEmail)
                .password(userPassword)
                .name(userName)
                .build();
    }
}