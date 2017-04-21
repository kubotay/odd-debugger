package jdi2diagram.main;

import jdi2diagram.event_info.FinishInfo;
import jdi2diagram.event_info.ModifierInfo;
import jdi2diagram.event_info.method_event.MethodEntryEventInfo;
import jdi2diagram.event_info.method_event.MethodExitEventInfo;
import jdi2diagram.event_info.prepare_event.ClassPrepareEventInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;


public class MiniClient implements Runnable {
    public static void main(String[] args) {
        new Thread(new MiniClient()).start();
    }
    Socket socket;
    @Override
    public void run() {
        try {
            this.socket = new Socket("localhost",4444);
            while(this.socket.isConnected()){
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                try {
                    ModifierInfo modifierInfo = (ModifierInfo)in.readObject();
                    if(modifierInfo instanceof ClassPrepareEventInfo){
                        ((ClassPrepareEventInfo) modifierInfo).printDump();
                    }else if(modifierInfo instanceof MethodEntryEventInfo){
                        ((MethodEntryEventInfo) modifierInfo).printDump();
                    }else if(modifierInfo instanceof MethodExitEventInfo){
                        ((MethodExitEventInfo) modifierInfo).printDump();
                    }else if(modifierInfo instanceof FinishInfo){
                        System.out.println("finish");
                        in.close();
                        socket.close();
                        break;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
