package jdi2diagram.main;

import jdi2diagram.event_info.prepare_event.ClassPrepareEventInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.*;


public class JDI2DiagramWorker implements Runnable,Observer {
    Socket sock;

    ObjectOutputStream out;
    BufferedReader in;
    JDI2DiagramServer jdi2DiagramServer;
    List<Serializable> events = Collections.synchronizedList(new ArrayList<>());
    int n;

    public JDI2DiagramWorker( int n, Socket sock, JDI2DiagramServer jdi2DiagramServer) {
        this.sock = sock;
        this.jdi2DiagramServer = jdi2DiagramServer;
        this.n = n;
        this.out = null;
        this.in = null;
    }

    @Override
    public void run() { //リストの先頭のイベントを送信している？
        System.out.println("Thread running" + Thread.currentThread());
        try {

            out = new ObjectOutputStream(sock.getOutputStream());
            while(true) {
                if (events.size() > 1) {
                    Serializable serializable = events.remove(0);
                    if(serializable instanceof ClassPrepareEventInfo) {
                        System.out.println("send CP");

                        out.writeObject(serializable);
                        out.flush();

                    }
                }
            }
//            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void update(Observable o, Object arg) {
        System.out.println("worker update");
        events.add((Serializable)arg);
    }
}
