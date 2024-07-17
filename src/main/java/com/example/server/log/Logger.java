package com.example.server.log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    public static void error(String text)
    {
        log(text, "ERROR: ");
        System.err.println(text);
    }

    public static void print(String text) {
        log(text, "LOG: ");
        System.out.println(text);
    }

    private static final SimpleDateFormat dateToCalendarFormat = new SimpleDateFormat("dd-MM-yyyy");
    private static final SimpleDateFormat dateToTodayFormat = new SimpleDateFormat("HH:mm:ss");

    /**
     * Логирует сообщение в файл "log текущая дата.txt"
     * @param text текст
     * @param tag тег к тексту
     */
    private static void log(String text, String tag) {
        Date date = new Date();
        File file = new File("./logs/log " + dateToCalendarFormat.format(date) + ".txt");
        if (!file.canWrite() && new File("./logs").mkdirs()) {
            Logger.print("logs directory was created!");
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            String stringBuffer = '[' + dateToTodayFormat.format(date) + ']' + tag + text;
            writer.append(stringBuffer);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("log error: " + e);
        }
    }

}
