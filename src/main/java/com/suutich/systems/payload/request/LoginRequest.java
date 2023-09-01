package com.suutich.systems.payload.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}