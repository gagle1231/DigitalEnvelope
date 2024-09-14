package com.security.de.secureService.secureService.Exception;

public class SignException extends Exception{
    public static String defaultMessage = "사인 생성에 실패하였습니다.";
    public SignException() {
        super(defaultMessage);
    }

    public SignException(String message) {
        super(message);
    }
}
