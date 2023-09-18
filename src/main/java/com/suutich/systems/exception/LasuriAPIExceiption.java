package com.suutich.systems.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class LasuriAPIExceiption extends RuntimeException{
    @Getter
    private final HttpStatus status;
    private final String message;

    public LasuriAPIExceiption(HttpStatus status, String message){
        this.status = status;
        this.message = message;
    }

    // 예외 메시지와 상태코드를 받아서 message 와 status 멤버 변수에 값을 할당하고, 예외 메시지 설정
    public LasuriAPIExceiption(String message, HttpStatus status, String message1){
        super(message);
        this.status = status;
        this.message = message1;
    }

    @Override
    public String getMessage(){
        return message;
    }

}