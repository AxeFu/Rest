package com.example.server;

import io.vertx.core.Launcher;

import java.util.Scanner;

public class VertxLauncher implements Runnable {
    public static void main(String[] args) {
        // Запускаем вертиклы
        new Thread(new VertxLauncher()).start();
        Launcher.executeCommand("run", ServerVerticle.class.getName());
    }


    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if (input.equals("exit")) {
                System.exit(0);
            }
        }
    }
}
