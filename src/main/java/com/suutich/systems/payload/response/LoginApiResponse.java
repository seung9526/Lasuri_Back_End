package com.suutich.systems.payload.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginApiResponse<T> {
    private boolean success;
    private T data;
    private String message;

    public LoginApiResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}

