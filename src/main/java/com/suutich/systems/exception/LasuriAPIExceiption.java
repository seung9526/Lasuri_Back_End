package com.suutich.systems.exception;

import org.springframework.http.HttpStatus;

public class LasuriAPIExceiption extends RuntimeException{
    private HttpStatus status;
    private String message;

    public LasuriAPIExceiption(HttpStatus status, String message){
        this.status = status;
        this.message = message;
    }

    // TODO : 예외 메시지와 상태코드를 받아서 message 와 status 멤버 변수에 값을 할당하고, 예외 메시지 설정
    public LasuriAPIExceiption(String message, HttpStatus status, String message1){
        super(message);
        this.status = status;
        this.message = message1;
    }

    public HttpStatus getStatus(){
        return status;
    }

    @Override
    public String getMessage(){
        return message;
    }
}