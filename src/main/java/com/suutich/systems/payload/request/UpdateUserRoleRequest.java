package com.suutich.systems.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UpdateUserRoleRequest {
    @NotBlank
    private String userEmail;

    @NotBlank
    private String role;

}