package com.example.server.builders;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

    /**
     * @param text текст, который будет преобразован в хеш
     * @return хеш
     */
    public static String get(String text) {
        //MessageDigest не потоко безопасный, потому его экземпляр создаётся на каждом потоке
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] textBytes = text.getBytes();
        md.update(textBytes);
        byte[] digest = md.digest();
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : digest) {
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
    }

    /**
     * Получает случайный хеш
     * @return хеш
     */
    //TODO: обезопасить
    public static String getRandom() {
        //MessageDigest не потоко безопасный, потому его экземпляр создаётся на каждом потоке
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            byte[] timeBytes = ByteBuffer.allocate(Long.BYTES).putLong(System.nanoTime()).array();
            md.update(timeBytes);
            byte[] digest = md.digest();
            for (byte b : digest) {
                stringBuilder.append(String.format("%02x", b));
            }
        }
        int randStart = (int) (32*Math.random());
        return stringBuilder.substring(randStart,32 + randStart)
            .replace((char)(48+Math.random()*10),(char)(48+Math.random()*10))
            .replace((char)(97+Math.random()*26), (char)(97+Math.random()*26));
    }

}
