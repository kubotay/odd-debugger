package jdi2diagram.watchdog;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.event.*;
import com.sun.jdi.request.*;
import jdi2diagram.event_info.FinishInfo;
import jdi2diagram.event_info.method_event.MethodEntryEventInfo;
import jdi2diagram.event_info.method_event.MethodExitEventInfo;
import jdi2diagram.event_info.prepare_event.ClassPrepareEventInfo;
import jdi2diagram.event_info.prepare_event.InterfacePrepareEventInfo;
import jdi2diagram.event_info.prepare_event.PrepareEventFactory;
import jdi2diagram.event_info.prepare_event.PrepareEventInfo;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Observable;


public class WatchdogThread extends Observable implements Runnable {
    private static String[] excludes = {"com.intellij*", "java.*", "javax.*", "sun.*", "com.sun.*", "com.apple.*", "jdk.internal.*", "apple.laf.*", "processing.core.*"};
    private static WatchdogThread soleInstance = new WatchdogThread();
    private VirtualMachine vm;
    private boolean connected = false;
    private String hostname;
    private String portNum;
    private EventQueue queue;


    private WatchdogThread() {

    }

    public static WatchdogThread getInstance() {
        return soleInstance;
    }

    private static void setMethodExitRequest(EventRequestManager mgr) {
        MethodExitRequest request = mgr.createMethodExitRequest();
        for (String exclude : excludes) {
            request.addClassExclusionFilter(exclude);
        }
        request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
        request.enable();
    }

    public boolean isConnected() {
        return connected;
    }

    public void vmSuspend() {
        vm.suspend();
    }

    public void vmResume() {
        vm.resume();
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPortNum(String portNum) {
        this.portNum = portNum;
    }

    public void connect() {

        //VMと接続
        AttachingConnector connector = (AttachingConnector) findConnector("com.sun.jdi.SocketAttach");
        Map arguments = connector.defaultArguments();
        //((Connector.Argument) arguments.get("hostname")).setValue("localhost");
        ((Connector.Argument) arguments.get("hostname")).setValue(this.hostname);
        //((Connector.Argument) arguments.get("port")).setValue("4321");
        ((Connector.Argument) arguments.get("port")).setValue(this.portNum);
        try {
            this.vm = connector.attach(arguments);
            this.queue = this.vm.eventQueue();
            this.connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalConnectorArgumentsException e) {
            e.printStackTrace();
        }
        EventRequestManager mgr = vm.eventRequestManager();
        setEventRequests(mgr);

    }

    public static boolean waitFlag = true;

    @Override
    public void run() {
        while (connected) {
            EventSet events = null;//リクエストを待つ
            try {
                events = queue.remove(); // 次のイベントが発生するまで待ち、そのイベントを格納
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                for (EventIterator it = events.eventIterator(); it.hasNext(); ) {
                    Event e = it.nextEvent();
                    handleEvent(e);
                }
            } catch (VMDisconnectedException e) {
                handleDisconnectedException();
            }

            //mgr.deleteEventRequest(request);
            events.resume(); //中断されたスレッドの再開
            waitNext();
        }
    }

    public void waitNext(){
        waitFlag = true;
        while(waitFlag) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleEvent(Event event) {

        if (event instanceof MethodEntryEvent)
            methodEntryEvent((MethodEntryEvent) event);
        else if (event instanceof MethodExitEvent)
            methodExitEvent((MethodExitEvent) event);
        else if (event instanceof ClassPrepareEvent)
            classPrepareEvent((ClassPrepareEvent) event);
        else if (event instanceof ClassUnloadEvent)
            ;
            //classUnloadEvent((ClassUnloadEvent) event);
        else if (event instanceof BreakpointEvent)
            ;
            //testBreakPointEvent((BreakpointEvent) event);
        else if (event instanceof StepEvent)
            ;
            //stepEvent((StepEvent) event);
        else if (event instanceof ThreadStartEvent)
            ;
            //threadStartEvent((ThreadStartEvent) event);
        else if (event instanceof ThreadDeathEvent)
            ;
            //threadDeathEvent((ThreadDeathEvent) event);
        else if (event instanceof ModificationWatchpointEvent)
            ;
            //fieldWatchEvent((ModificationWatchpointEvent) event);
        else if (event instanceof VMStartEvent)
            ;
            //vmStartEvent((VMStartEvent) event);
        else if (event instanceof VMDeathEvent)

            vmDeathEvent((VMDeathEvent) event);
        else if (event instanceof VMDisconnectEvent)
            vmDisconnectEvent((VMDisconnectEvent) event);

        else
            throw new Error("Unexpected event type");

    }
    private boolean vmDied;
    private void vmDeathEvent(VMDeathEvent event) {
        vmDied = true;
        connected = false;
        FinishInfo finishInfo = new FinishInfo();
        finishInfo.setLastEvent(true);
        this.setChanged();
        this.notifyObservers(finishInfo);
    }
    public boolean getVMDied(){
        return vmDied;
    }
    private void vmDisconnectEvent(VMDisconnectEvent event) {
        connected = false;
//        if (!vmDied)
//            System.out.println("- The application has been disconnected");
    }

    private synchronized void handleDisconnectedException() {
        EventQueue queue = this.vm.eventQueue();
        while (connected) {
            try {
                EventSet events = queue.remove();
                for (Event event : events) {
                    if (event instanceof VMDeathEvent)
                        ;
                        //vmDeathEvent((VMDeathEvent) event);
                    else if (event instanceof VMDisconnectEvent)
                        ;
                    //vmDisconnectEvent((VMDisconnectEvent) event);
                }

            } catch (InterruptedException ignored) {
            }
        }
    }

    private void setEventRequests(EventRequestManager mgr) {
        setClassPrepareRequest(mgr);
        setMethodExitRequest(mgr);
        setMethodEntryRequest(mgr);
        //setThreadDeathRequest(mgr);
        //setThreadStartRequest(mgr);

    }

    private void setClassPrepareRequest(EventRequestManager mgr) {
        ClassPrepareRequest request = mgr.createClassPrepareRequest();
        for (String exclude : excludes) {
            request.addClassExclusionFilter(exclude);
        }
        request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
        request.enable();
    }

    private void setMethodEntryRequest(EventRequestManager mgr) {
        MethodEntryRequest request = mgr.createMethodEntryRequest();
        for (String exclude : excludes) {
            request.addClassExclusionFilter(exclude);
        }
        request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
        request.enable();
    }

    private void classPrepareEvent(ClassPrepareEvent event) {
        PrepareEventInfo prepareEventInfo = PrepareEventFactory.create(event);

        if (prepareEventInfo instanceof ClassPrepareEventInfo) {
            ClassPrepareEventInfo classPrepareEventInfo = (ClassPrepareEventInfo) prepareEventInfo;

            this.setChanged();
            this.notifyObservers(classPrepareEventInfo);
        } else if (prepareEventInfo instanceof InterfacePrepareEventInfo) {
            InterfacePrepareEventInfo interfacePrepareEventInfo = (InterfacePrepareEventInfo) prepareEventInfo;

            this.setChanged();
            this.notifyObservers(interfacePrepareEventInfo);
        }


    }

    private void methodEntryEvent(MethodEntryEvent event) {
        MethodEntryEventInfo methodEntryEventInfo = new MethodEntryEventInfo(event);
        this.setChanged();
        this.notifyObservers(methodEntryEventInfo);
    }

    private void methodExitEvent(MethodExitEvent event) {
        MethodExitEventInfo methodExitEventInfo = new MethodExitEventInfo(event);
        this.setChanged();
        this.notifyObservers(methodExitEventInfo);
    }


    private Connector findConnector(String connector) {
        List connectors = Bootstrap.virtualMachineManager().allConnectors();
        for (Object connector1 : connectors) {
            Connector con = (Connector) connector1;
            if (con.name().equals(connector)) {
                return con;
            }
        }

        throw new RuntimeException("No connector : " + connector);
    }
}
