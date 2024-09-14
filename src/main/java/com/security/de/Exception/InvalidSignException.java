package com.security.de.Exception;

public class InvalidSignException extends RuntimeException{

    public static String defaultMessage = "전자서명이 유효하지 않습니다.";

    public InvalidSignException() {
        super(defaultMessage);
    }

    public InvalidSignException(String message) {
        super(message);
    }

}
