package com.security.de.service;

import com.security.de.Exception.InvalidSignException;
import com.security.de.Exception.SignException;
import com.security.de.utils.FileIOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.*;

@Component
public class SignManager {

    @Value("${sign.storage.path}")
    private String signPath;

    private static final String algorithm = "SHA1withRSA";  // 서명에 사용될 알고리즘

    /**
     * 전자서명을 생성한 후 파일에 저장하고 서명을 반환합니다.
     *
     * @param fileName  서명 파일명
     * @param plainData 서명할 데이터
     * @param key       서명에 사용할 개인 키
     * @return 생성된 전자 서명
     * @throws SignException 전자서명 생성 과정에서 발생하는 예외
     */
    public byte[] createSign(String fileName, byte[] plainData, PrivateKey key) throws SignException {
        byte[] sign;
        try {
            Signature sig = Signature.getInstance(algorithm);
            sig.initSign(key);
            sig.update(plainData);
            sign = sig.sign();  // 전자서명 생성

            // 생성된 서명을 파일로 저장
            String fName = signPath + fileName + ".sign";
            FileIOUtils.writeToFile(sign, fName);

            return sign;

        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new SignException();
        }
    }

    /**
     * 주어진 서명과 원본 데이터를 사용하여 무결성을 검증합니다.
     *
     * @param plainData 원본 데이터
     * @param key       검증에 사용할 공개 키
     * @param sign      검증할 서명
     * @return 서명이 유효하면 true, 그렇지 않으면 false
     * @throws InvalidSignException 서명 검증 중 오류가 발생할 경우 던짐
     */
    public boolean verify(String plainData, PublicKey key, byte[] sign) throws InvalidSignException {
        try {
            Signature sig = Signature.getInstance(algorithm);
            sig.initVerify(key);
            sig.update(plainData.getBytes());

            return sig.verify(sign);

        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new InvalidSignException();
        }
    }

}
