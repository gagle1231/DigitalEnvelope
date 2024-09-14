package com.security.de.secureService.secureService;

import java.io.Serializable;

final class SendData implements Serializable{
    static final long serialVersionUID = 1L;
    private byte[] envelope; //전자봉투
    private byte[] encryptedData; //암호화한 원본 데이터
    private String encryptedSignFileName;
    private String senderId; //송신자 id

    SendData(){}
    SendData(byte[] envelope, byte[] encryptedData, String encryptedSignFileName, String senderId) {
        this.envelope = envelope;
        this.encryptedData = encryptedData;
        this.encryptedSignFileName = encryptedSignFileName;
        this.senderId = senderId;
    }

    String getEncryptedSignFileName() {
        return encryptedSignFileName;
    }
    byte[] getEnvelope() {
        return envelope;
    }
    byte[] getEncryptedData() {
        return encryptedData;
    }
}

