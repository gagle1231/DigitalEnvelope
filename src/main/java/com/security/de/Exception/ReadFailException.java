package com.security.de.secureService.secureService.Exception;

public class ReadFailException extends Exception{
    private static String defaultMessage="메세지를 읽던 중 오류가 발생하였습니다.";
    public ReadFailException(){
        super(defaultMessage);
    }

    public ReadFailException(String senderId){
        super(senderId+"로부터 온 메세지가 없습니다.");
    }
}
