package jdi2diagram.main;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Observable;


public class JDI2DiagramConnector extends Observable implements Runnable {
    String hostName;
    int portNum;
    Socket socket;
    ObjectInputStream in;
    public JDI2DiagramConnector(String hostName, int portNum) {
        this.hostName = hostName;
        this.portNum = portNum;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(hostName,portNum);
            Object inObject;
            System.out.println("run:JDI2DiagramConnector");
            while(true){
                System.out.println("in loop");
                try {
                    in = new ObjectInputStream(socket.getInputStream());
                    inObject = in.readObject();
                    this.setChanged();
                    this.notifyObservers(inObject);
                }catch (EOFException e){
                    e.printStackTrace();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
