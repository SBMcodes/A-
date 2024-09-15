package main;

public class Main {

    public static void main(String[] args) {

        Thread windowThread = new Thread(new Window());
        windowThread.start();

    }
}
