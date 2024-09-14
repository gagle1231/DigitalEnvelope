package com.security.de.service;


import com.security.de.Exception.EncryptionException;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

@Component
class EncryptManager {

    /**
     * 주어진 모드(ENCRYPT 또는 DECRYPT)에 따라 Cipher 객체를 초기화합니다.
     *
     * @param mode 암호화/복호화 모드 (Cipher.ENCRYPT_MODE 또는 Cipher.DECRYPT_MODE)
     * @param key  암호화/복호화에 사용할 키
     * @return 초기화된 Cipher 객체
     * @throws EncryptionException 알고리즘, 패딩, 키가 잘못된 경우 발생
     */
    private static Cipher initCipher(int mode, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(key.getAlgorithm());
            cipher.init(mode, key);
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new EncryptionException("Cipher 초기화 실패", e);
        }
    }

    /**
     * 주어진 데이터를 키를 사용해 암호화합니다.
     *
     * @param data 암호화할 데이터
     * @param key  암호화에 사용할 키
     * @return 암호화된 바이트 배열
     * @throws EncryptionException 암호화 실패 시 발생
     */
    public static byte[] encrypt(byte[] data, Key key) {
        try {
            Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new EncryptionException("암호화 실패", e);
        }
    }

    /**
     * 주어진 암호화된 데이터를 키를 사용해 복호화합니다.
     *
     * @param encryptedData 복호화할 암호화된 데이터
     * @param key           복호화에 사용할 키
     * @return 복호화된 바이트 배열
     * @throws EncryptionException 복호화 실패 시 발생
     */
    public static byte[] decrypt(byte[] encryptedData, Key key) {
        try {
            Cipher cipher = initCipher(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(encryptedData);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new EncryptionException("복호화 실패", e);
        }
    }

    /**
     * 주어진 데이터를 암호화하여 파일에 저장합니다.
     *
     * @param fName 저장할 파일 이름
     * @param data  암호화할 데이터
     * @param key   암호화에 사용할 키
     * @throws EncryptionException 암호화 또는 파일 쓰기 실패 시 발생
     */
    public static void encryptToFile(String fName, byte[] data, Key key) {
        try (CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(fName), initCipher(Cipher.ENCRYPT_MODE, key))) {
            cos.write(data);
        } catch (IOException e) {
            throw new EncryptionException("파일에 암호화된 데이터를 쓰는 데 실패: " + fName, e);
        }
    }

    /**
     * 파일에서 암호화된 데이터를 읽어와 복호화하여 반환합니다.
     *
     * @param fName 복호화할 파일 이름
     * @param key   복호화에 사용할 키
     * @return 복호화된 바이트 배열
     * @throws EncryptionException 복호화 또는 파일 읽기 실패 시 발생
     */
    public static byte[] decryptFromFile(String fName, Key key) {
        try (FileInputStream fis = new FileInputStream(fName);
             CipherInputStream cis = new CipherInputStream(fis, initCipher(Cipher.DECRYPT_MODE, key));
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[256];
            int length;
            while ((length = cis.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            throw new EncryptionException("파일에서 데이터를 복호화하는 데 실패: " + fName, e);
        }
    }
}
