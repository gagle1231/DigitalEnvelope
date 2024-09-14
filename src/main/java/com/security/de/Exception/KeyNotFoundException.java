package com.security.de.Exception;

public class KeyNotFoundException extends RuntimeException {

    public static String defaultMessage = "Key를 찾을 수 없습니다.";

    public KeyNotFoundException() {
        super(defaultMessage);
    }

    public KeyNotFoundException(String message) {
        super(message+"님의 Key를 찾을 수 없습니다.");
    }

}
