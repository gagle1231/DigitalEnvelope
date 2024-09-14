package com.security.de.secureService.secureService;


import com.security.de.secureService.secureService.Exception.InvalidSignException;
import com.security.de.secureService.secureService.Exception.SignException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

class SignManager {
    private static final String signPath = "C:\\Users\\82108\\demo\\src\\main\\resources\\static\\sign\\";
    private static final String algorithm ="SHA1withRSA";

    //전자서명 생성 후 파일에 저장&리턴
    static byte[] createSign(String fileName, byte[] plainData, PrivateKey key) throws SignException {
        byte[] sign;
        Signature sig = null;
        try {
            sig = Signature.getInstance(algorithm);
            sig.initSign(key);
            sig.update(plainData);
            sign = sig.sign(); //전자서명 생성

            String fName = signPath + fileName+".sign";
            FileIOUtils.writeToFile(sign, fName);
            return sign;
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new SignException();
        }
    }

    //무결성 검증
     static boolean verify(String plainData, PublicKey key, byte[] sign) throws InvalidSignException {
        Signature sig = null;
        try {
            sig = Signature.getInstance(algorithm);
            sig.initVerify(key);
            sig.update(plainData.getBytes());
            return sig.verify(sign);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return false;
    }
}
