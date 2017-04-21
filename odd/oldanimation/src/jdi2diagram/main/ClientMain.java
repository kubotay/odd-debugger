package jdi2diagram.main;


public class ClientMain implements Runnable {
    ConnectorUser connectorUser ;
    public static void main(String[] args) {
        Thread thread = new Thread(new ClientMain());
        thread.start();


    }

    @Override
    public void run() {
        connectorUser = new ConnectorUser("localhost",4444);

    }
}
