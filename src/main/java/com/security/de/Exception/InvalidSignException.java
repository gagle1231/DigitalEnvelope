package com.security.de.secureService.secureService.Exception;

public class InvalidSignException extends Exception{
    public static String defaultMessage = "전자서명이 유효하지 않습니다.";
    public InvalidSignException() {
        super(defaultMessage);
    }

    public InvalidSignException(String message) {
        super(message);
    }
}
