package com.security.de.service;

import com.security.de.Exception.KeyNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.*;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

@Component
public final class KeyManager {

    @Value("${key.storage.path}")
    private static String path;

    private static final String keyAlgorithm = "AES";  // 대칭키 알고리즘 (AES)
    private static final String keyPairAlgorithm = "RSA";  // 비대칭키 알고리즘 (RSA)

    /**
     * 새로운 AES 대칭 키를 생성합니다.
     *
     * @return 생성된 AES 키
     * @throws NoSuchAlgorithmException 지정된 알고리즘을 사용할 수 없을 때 발생
     */
    public static Key createKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(keyAlgorithm);
        keyGen.init(256); // AES 256비트 키 생성
        return keyGen.generateKey();
    }

    /**
     * 주어진 사용자 ID로 RSA 키 쌍을 생성하여 파일로 저장합니다.
     *
     * @param id 사용자 ID (키 쌍 파일 이름에 사용)
     */
    public static void createKeyPair(String id) {
        String fName = path + id + ".keyPair";
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(keyPairAlgorithm);
            keyPairGen.initialize(2048); // RSA 2048비트 키 쌍 생성
            KeyPair keyPair = keyPairGen.generateKeyPair();

            // 키 쌍을 파일로 저장
            try (FileOutputStream fosPrivate = new FileOutputStream(fName);
                 ObjectOutputStream os = new ObjectOutputStream(fosPrivate)) {
                os.writeObject(keyPair);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("키 파일을 찾을 수 없습니다: " + fName, e);
            } catch (IOException e) {
                throw new RuntimeException("키 파일을 저장하는 중 오류가 발생했습니다.", e);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("알고리즘을 찾을 수 없습니다: " + keyPairAlgorithm, e);
        }
    }

    /**
     * 주어진 사용자 ID에 해당하는 RSA 키 쌍을 파일에서 읽어옵니다.
     *
     * @param id 사용자 ID
     * @return 파일에서 읽어온 KeyPair 객체
     * @throws IOException            파일 입출력 문제 발생 시
     * @throws ClassNotFoundException 객체 직렬화 클래스 오류 시
     */
    public static KeyPair getKeyPair(String id) throws IOException, ClassNotFoundException {
        String fName = path + id + ".keyPair";
        try (FileInputStream keyFis = new FileInputStream(fName);
             ObjectInputStream ois = new ObjectInputStream(keyFis)) {
            return (KeyPair) ois.readObject();
        }
    }

    /**
     * 주어진 사용자 ID에 해당하는 개인 키를 반환합니다.
     *
     * @param id 사용자 ID
     * @return 개인 키 (PrivateKey)
     * @throws KeyNotFoundException 키 파일을 찾을 수 없을 때 발생
     */
    public static PrivateKey getPrivateKey(String id) throws KeyNotFoundException {
        try {
            return getKeyPair(id).getPrivate();
        } catch (IOException | ClassNotFoundException e) {
            throw new KeyNotFoundException(id);
        }
    }

    /**
     * 주어진 사용자 ID에 해당하는 공개 키를 반환합니다.
     *
     * @param id 사용자 ID
     * @return 공개 키 (PublicKey)
     * @throws KeyNotFoundException 키 파일을 찾을 수 없을 때 발생
     */
    public static PublicKey getPublicKey(String id) throws KeyNotFoundException {
        try {
            return getKeyPair(id).getPublic();
        } catch (IOException | ClassNotFoundException e) {
            throw new KeyNotFoundException(id);
        }
    }

    /**
     * 주어진 인코딩된 바이트 배열로부터 AES 대칭 키를 생성합니다.
     *
     * @param encoded 인코딩된 키 값
     * @return 생성된 AES 대칭 키
     */
    public static Key getKeyFromEncoded(byte[] encoded) {
        return new SecretKeySpec(encoded, keyAlgorithm);
    }

}
