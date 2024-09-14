package com.security.de.Exception;

public class SignException extends RuntimeException{

    public static String defaultMessage = "사인 생성에 실패하였습니다.";

    public SignException() {
        super(defaultMessage);
    }

    public SignException(String message) {
        super(message);
    }

}
