package com.security.de.service;

import com.security.de.Exception.*;
import com.security.de.utils.FileIOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.*;

@Service
@RequiredArgsConstructor
public class SendDataService {

    // 외부 환경에서 경로 설정을 주입받음
    @Value("${sign.storage.path}")
    private String encryptedSignPath;

    @Value("${data.storage.path}")
    private String dataFilePath;

    // 필요한 매니저 클래스들을 주입받음
    private final EncryptManager encryptManager;
    private final SignManager signManager;
    private final KeyManager keyManager;

    /**
     * 데이터를 전자서명 후 암호화하여 전송하는 함수
     *
     * @param senderId   송신자 ID
     * @param receiverId 수신자 ID
     * @param data       전송할 데이터
     * @throws KeyNotFoundException   키를 찾을 수 없는 경우 발생
     * @throws SignException          서명 생성 중 발생
     * @throws SendFailException      전송 실패 시 발생
     */
    public void sendMessage(String senderId, String receiverId, String data) throws KeyNotFoundException, SignException, SendFailException {

        PrivateKey privateKey = keyManager.getPrivateKey(senderId);
        byte[] sign = signManager.createSign(senderId + receiverId, data.getBytes(), privateKey);

        if (sign.length == 0) {
            throw new SignException("서명 생성 실패");
        }

        Key secretKey;
        try {
            secretKey = keyManager.createKey();
        } catch (NoSuchAlgorithmException e) {
            throw new KeyNotFoundException("대칭키 생성 실패");
        }

        byte[] encryptedData = null;
        try {
            encryptedData = encryptManager.encrypt(data.getBytes("UTF-8"), secretKey);
        } catch (UnsupportedEncodingException e) {
            throw new SendFailException(e.getMessage());
        }

        if (encryptedData.length == 0) {
            throw new SendFailException("데이터 암호화 실패");
        }

        String signFileName = encryptedSignPath + senderId + receiverId + ".sig";
        encryptManager.encryptToFile(signFileName, sign, secretKey);

        PublicKey receiverPublicKey = keyManager.getPublicKey(receiverId);
        byte[] envelope = encryptManager.encrypt(secretKey.getEncoded(), receiverPublicKey);

        if (envelope.length == 0) {
            throw new SendFailException("전자봉투 생성 실패");
        }

        SendData sendData = new SendData(envelope, encryptedData, signFileName, senderId);
        String dataFileName = dataFilePath + senderId + receiverId + ".data";
        boolean state = FileIOUtils.writeObjectToFile(dataFileName, sendData);

        if (!state) {
            throw new SendFailException("파일 저장 실패");
        }
    }

    /**
     * 전자봉투를 풀어서 데이터 검증 후 복호화하는 함수
     *
     * @param senderId   송신자 ID
     * @param receiverId 수신자 ID
     * @return 복호화된 원본 데이터
     * @throws KeyNotFoundException       키를 찾을 수 없는 경우 발생
     * @throws InvalidSignException       서명 검증 실패 시 발생
     * @throws ReadFailException          읽기 실패 시 발생
     */
    public String readMessage(String senderId, String receiverId) throws KeyNotFoundException, InvalidSignException, ReadFailException {

        // 1. 저장된 SendData 파일을 불러옴
        String dataFileName = dataFilePath + senderId + receiverId + ".data";
        SendData sendData = (SendData) FileIOUtils.readObjectFromFile(dataFileName);

        if (sendData == null) {
            throw new ReadFailException(senderId);
        }

        // 2. 수신자의 사설키로 대칭키(비밀키) 복호화
        PrivateKey privateKey = keyManager.getPrivateKey(receiverId);
        byte[] keyArr = encryptManager.decrypt(sendData.getEnvelope(), privateKey);

        if (keyArr.length == 0) {
            throw new ReadFailException();
        }

        Key secretKey = keyManager.getKeyFromEncoded(keyArr); // 복호화된 대칭키

        // 3. 대칭키로 암호화된 데이터를 복호화
        byte[] encryptedData = sendData.getEncryptedData();
        byte[] plainByteData = encryptManager.decrypt(encryptedData, secretKey);
        String plainData = null;
        try {
            plainData = new String(plainByteData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new SendFailException(e.getMessage());
        }

        // 4. 복호화된 전자서명 파일을 대칭키로 복호화하여 무결성 검증
        byte[] sign = encryptManager.decryptFromFile(sendData.getEncryptedSignFileName(), secretKey);
        PublicKey publicKey = keyManager.getPublicKey(senderId);

        if (!signManager.verify(plainData, publicKey, sign)) {
            throw new InvalidSignException();
        }

        return plainData;  // 검증 성공 후 복호화된 데이터 반환
    }
}
