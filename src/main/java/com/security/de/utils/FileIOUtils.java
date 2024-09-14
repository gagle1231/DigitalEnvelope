package com.security.de.secureService.secureService;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileIOUtils {
    public static boolean writeObjectToFile(String fileName, Object obj) {
        try(FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.writeObject(obj);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Object readObjectFromFile(String fileName) {
        try(FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis)){
            return ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void writeToFile(byte[] data, String fileName) {

        try(FileOutputStream bos = new FileOutputStream(fileName);){
            bos.write(data);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
