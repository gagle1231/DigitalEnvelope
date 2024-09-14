package com.security.de.secureService.secureService.Exception;

public class SendFailException extends Exception{
    private static String defaultMessage="메세지 전송에 실패하였습니다. ";
    public SendFailException(){
        super(defaultMessage);
    }

    public SendFailException(String message){
        super(message);
    }

}
