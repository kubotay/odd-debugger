package jdi2diagram.main;

import jdi2diagram.event_info.method_event.MethodEntryEventInfo;
import jdi2diagram.event_info.method_event.MethodExitEventInfo;
import jdi2diagram.event_info.prepare_event.ClassPrepareEventInfo;
import jdi2diagram.event_info.prepare_event.InterfacePrepareEventInfo;
import jdi2diagram.watchdog.WatchdogThread;

import java.util.Observable;
import java.util.Observer;


public class TestServer implements Runnable,Observer {

    public static void main(String[] args) {


        new Thread(new TestServer(WatchdogThread.getInstance(), "localhost", "4321")).start();
    }


    WatchdogThread watchdogThread;

    public TestServer(WatchdogThread watchdogThread,String watchdogHostname, String watchdogPortNum) {
        this.watchdogThread = watchdogThread;
        this.watchdogThread.setHostname(watchdogHostname);
        this.watchdogThread.setPortNum(watchdogPortNum);
        this.watchdogThread.addObserver(this);
    }

    @Override
    public void run() {
        this.watchdogThread.connect();
        new Thread(this.watchdogThread).start();
        while(this.watchdogThread.isConnected())
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof ClassPrepareEventInfo) {
            System.out.println("ClassPrepareEventInfo");
            ((ClassPrepareEventInfo) arg).printDump();

        } else if (arg instanceof InterfacePrepareEventInfo) {
            ((InterfacePrepareEventInfo) arg).printDump();
        } else if(arg instanceof MethodEntryEventInfo){
            ((MethodEntryEventInfo) arg).printDump();
        } else if(arg instanceof MethodExitEventInfo){
            ((MethodExitEventInfo) arg).printDump();
        }
    }
}
