package jdi2diagram.main;

import jdi2diagram.event_info.ModifierInfo;
import jdi2diagram.watchdog.WatchdogThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class MiniServer implements Runnable, Observer {
    public static void main(String[] args) {
        new Thread(new MiniServer(WatchdogThread.getInstance(),"localhost", "4321")).start();
        //new Thread(new MiniServer(WatchdogThread.getInstance(),"192.168.1.2", "4321")).start();
    }
    ArrayList<ModifierInfo> events = new ArrayList<>();
    List<ModifierInfo> queue = Collections.synchronizedList(new ArrayList<>());

    WatchdogThread watchdog;
    ServerSocket serverSocket;
    ObjectOutputStream out;

    public MiniServer(WatchdogThread watchdog, String hostName , String portNum) {
        this.watchdog = watchdog;
        this.watchdog.setHostname(hostName);
        this.watchdog.setPortNum(portNum);
        this.watchdog.addObserver(this);
        try {
            this.serverSocket = new ServerSocket(4444);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("update");

        this.events.add((ModifierInfo) arg);
        this.queue.add((ModifierInfo) arg);

    }

    @Override
    public void run() {
        Socket socket;
        try {
            socket = this.serverSocket.accept();
            System.out.println("connected");
            this.watchdog.connect();
            new Thread(watchdog).start();


            while(true){
                //System.out.println(this.queue.size());
                if(!this.queue.isEmpty()) {
                    ModifierInfo modifierInfo = this.queue.remove(0);
                    System.out.println(modifierInfo);
                    try {
                        out = new ObjectOutputStream(socket.getOutputStream());
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        out.writeObject(modifierInfo);
                        out.flush();
                        if(modifierInfo.getLastEvent()){
                            out.close();
                            WatchdogThread.waitFlag = false;
                            break;
                        }
                        //WatchdogThread.resumeFlag = false;
                        while ( !in.readLine().equals("next")){
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        WatchdogThread.waitFlag = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else {
                    WatchdogThread.waitFlag = false;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
