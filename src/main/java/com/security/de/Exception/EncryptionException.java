package com.security.de.Exception;

public class EncryptionException extends RuntimeException {

    private static String defaultMessage = "암호화에 실패하였습니다.";

    public EncryptionException() {
        super(defaultMessage);
    }

    public EncryptionException(String message) {
        super(message);
    }

    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }

}
