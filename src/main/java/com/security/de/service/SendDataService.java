package com.security.de.secureService.secureService;


import com.security.de.secureService.secureService.Exception.*;

import java.io.UnsupportedEncodingException;
import java.security.*;

public class SendDataService {
    private static String encryptedSignPath = "C:\\Users\\82108\\demo\\src\\main\\resources\\static\\sign\\";
    private static String dataFilePath = "C:\\Users\\82108\\demo\\src\\main\\resources\\static\\data\\";

    //전자봉투 생성해서 데이터 보내는 함수
    public static void sendMessage(String senderId, String receiverId, String data) throws KeyNotFoundException, SignException, SendFailException, UnsupportedEncodingException {
      //1. 전자서명 만들기
        PrivateKey privateKey = null;
        privateKey = KeyManager.getPrivateKey(senderId);


        byte[] sign = SignManager.createSign(senderId+receiverId, data.getBytes(), (PrivateKey) privateKey);
        if(sign.length==0){
            throw new SignException();
        }

        //2. 원본+전자서명+publicKey 암호화
        Key secretKey = null;
        try {
            secretKey = KeyManager.createKey();
        } catch (NoSuchAlgorithmException e) {
            throw new KeyNotFoundException();
        }

        byte[] encryptedData; //암호화한 원본
        encryptedData = EncryptManager.encrypt(data.getBytes("UTF-8"), secretKey);
        if(encryptedData.length==0){
            throw new SendFailException();
        }


        String fileName = encryptedSignPath + senderId+receiverId+".sig";
        EncryptManager.encryptToFile(fileName, sign, secretKey); //암호화한 전자서명 파일에 저장


        //3. 암호화에 사용한 대칭키 수신자의 publicKey로 암호화(전자봉투 생성)
        PublicKey receiverPublicKey = null;
        receiverPublicKey = KeyManager.getPublicKey(receiverId);

        byte[] envelope = EncryptManager.encrypt(secretKey.getEncoded(), receiverPublicKey);
        if(envelope.length==0){
            throw new SendFailException();
        }

        //4. 암호문과 전자봉투 묶어서 저장
        SendData sendData = new SendData(envelope, encryptedData, fileName, senderId);
        fileName = dataFilePath+senderId+receiverId+".data";
        boolean state = FileIOUtils.writeObjectToFile(fileName, sendData);
        if(!state){ //파일 생성 오류(전송 오류)
            throw new SendFailException();
        }
    }

    //전자봉투 풀어서 데이터 읽는 함수
    public static String readMessage(String senderId, String receiverId) throws KeyNotFoundException, InvalidSignException, ReadFailException, UnsupportedEncodingException {
        //1. 받은 데이터 불러오기
        String fileName = dataFilePath+senderId+receiverId+".data";
        SendData sendData = (SendData) FileIOUtils.readObjectFromFile(fileName);

        if(sendData==null){
            throw new ReadFailException(senderId);
        }
        //2. 사설키로 비밀키 획득
        byte[] envelope = sendData.getEnvelope();
        PrivateKey privateKey = null;
        privateKey = KeyManager.getPrivateKey(receiverId);

        byte[] keyArr = EncryptManager.decrypt(envelope, privateKey);
        if(keyArr.length==0){
            throw new ReadFailException();
        }
        Key secretKey = KeyManager.getKeyFromEncoded(keyArr); //비밀키 복호화

        //3. 비밀키로 암호문 복호화
        byte[] encryptedData = sendData.getEncryptedData(); //암호화된 원본 데이터
        byte[] plainByteData = EncryptManager.decrypt(encryptedData, secretKey); //원본 데이터 복구
        String plainData = null;
        plainData = new String(plainByteData, "UTF-8");

        fileName = sendData.getEncryptedSignFileName();
        byte[] sign = EncryptManager.decryptFromFile(fileName, secretKey);
        PublicKey publicKey = null;
        publicKey = KeyManager.getPublicKey(senderId);


        if(!SignManager.verify(plainData, publicKey, sign)){
            throw new InvalidSignException();
        }


        return plainData;
    }
}
