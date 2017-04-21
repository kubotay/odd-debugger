package jdi2diagram.main;


public class Main {
    public static void main(String[] args) {
//        WatchdogThread watchdogThread = WatchdogThread.getInstance();
//        watchdogThread.setHostname("localhost");
//        watchdogThread.setPortNum("4321");
//        watchdogThread.connect();
//        Thread thread = new Thread(watchdogThread);
//        thread.start();

        JDI2DiagramServer server = new JDI2DiagramServer();

        Thread serverThread = new Thread(server);
        serverThread.start();

    }
}
