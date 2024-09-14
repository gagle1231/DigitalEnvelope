package com.security.de.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileIOUtils {

    private static final Logger logger = Logger.getLogger(FileIOUtils.class.getName());

    /**
     * 객체를 파일에 저장하는 메서드
     *
     * @param fileName 저장할 파일 경로
     * @param obj      저장할 객체
     * @return 저장 성공 여부
     */
    public static boolean writeObjectToFile(String fileName, Object obj) {
        try (FileOutputStream fos = new FileOutputStream(fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(obj);
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "파일 쓰기 실패: " + fileName, e);
        }
        return false;
    }


    /**
     * 파일에서 객체를 읽어오는 메서드
     *
     * @param fileName 읽을 파일 경로
     * @return 읽어온 객체, 실패 시 null 반환
     */
    public static Object readObjectFromFile(String fileName) {
        try (FileInputStream fis = new FileInputStream(fileName);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            return ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            logger.log(Level.SEVERE, "파일 읽기 실패: " + fileName, e);
        }
        return null;
    }

    /**
     * 바이트 데이터를 파일에 저장하는 메서드
     *
     * @param data     저장할 바이트 데이터
     * @param fileName 저장할 파일 경로
     */
    public static void writeToFile(byte[] data, String fileName) {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(data);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "파일 쓰기 실패: " + fileName, e);
        }
    }
}
