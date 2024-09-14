package com.security.de.secureService.secureService;


import com.security.de.secureService.secureService.Exception.KeyNotFoundException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public final class KeyManager {
    private static final String keyAlgorithm = "AES";
    private static final String keyPairAlgorithm = "RSA";
    private static final String path = "C:\\Users\\82108\\demo\\src\\main\\resources\\static\\key\\";

    public static Key createKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(keyAlgorithm);
        keyGen.init(256);
        Key key = keyGen.generateKey();
        return key;
    }

    //사용자 id를 받아 키 쌍 생성 및 저장
    public static void createKeyPair(String id){
        String fName = path+id+".keyPair";
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance(keyPairAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyPairGen.initialize(2048);
        KeyPair keyPair;
        try(FileOutputStream fosPrivate = new FileOutputStream(fName);
            ObjectOutputStream os = new ObjectOutputStream(fosPrivate);){
            keyPair = keyPairGen.generateKeyPair();
            os.writeObject(keyPair);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static KeyPair getKeyPair(String id) throws IOException, ClassNotFoundException {
        String fName = path+id+".keyPair";
        try(FileInputStream keyFis = new FileInputStream(fName);
            ObjectInputStream ois = new ObjectInputStream(keyFis);){
            KeyPair keyPair = (KeyPair) ois.readObject();
            return keyPair;
        }
    }

    public static PrivateKey getPrivateKey(String id) throws KeyNotFoundException {
        try {
            return getKeyPair(id).getPrivate();
        } catch (IOException | ClassNotFoundException e) {
            throw new KeyNotFoundException(id);
        }
    }

    public static PublicKey getPublicKey(String id) throws KeyNotFoundException {
        try {
            return getKeyPair(id).getPublic();
        } catch (IOException | ClassNotFoundException e) {
            throw new KeyNotFoundException(id);
        }
    }

    public static Key getKeyFromEncoded(byte[] encoded) {
        return new SecretKeySpec(encoded, keyAlgorithm);
    }
}
