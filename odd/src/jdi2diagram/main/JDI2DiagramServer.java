package jdi2diagram.main;

import jdi2diagram.watchdog.WatchdogThread;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


public class JDI2DiagramServer extends Observable implements Runnable,Observer {
    List<Serializable> events = new ArrayList<>();
    List<JDI2DiagramWorker> workers = new ArrayList<>();
    WatchdogThread watchdog = WatchdogThread.getInstance();
    ServerSocket serverSocket;
    Socket sock;
    Thread watchdogThread;
    int portNum;

    public JDI2DiagramServer() {

        watchdog.setHostname("localhost");
        watchdog.setPortNum("4321");

        watchdog.addObserver(this);
        watchdogThread = new Thread(watchdog);

    }

    @Override
    public void update(Observable o, Object arg) { //　監視対象に変更があると呼び出される
        events.add((Serializable)arg);
        this.setChanged();
        this.notifyObservers(arg);
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(4444);
            while(true){
                System.out.println("server start!!!!");
                sock = serverSocket.accept();
                JDI2DiagramWorker worker = new JDI2DiagramWorker(workers.size(),sock,this);
                this.addObserver(worker);
                workers.add(worker);
                new Thread(worker).start();
                watchdog.connect(); // VMに接続している
                watchdogThread.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
