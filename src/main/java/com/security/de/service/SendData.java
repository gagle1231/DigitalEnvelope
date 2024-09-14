package com.security.de.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
final class SendData implements Serializable{

    static final long serialVersionUID = 1L;

    private byte[] envelope; //전자봉투

    private byte[] encryptedData; //암호화한 원본 데이터

    private String encryptedSignFileName; // 암호화된 사인 파일명

    private String senderId; //송신자 id

}

