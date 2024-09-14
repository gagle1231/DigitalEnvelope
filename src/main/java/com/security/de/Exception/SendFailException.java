package com.security.de.Exception;

public class SendFailException extends RuntimeException {

    private static String defaultMessage="메세지 전송에 실패하였습니다. ";

    public SendFailException(){
        super(defaultMessage);
    }

    public SendFailException(String message){
        super(message);
    }

}
