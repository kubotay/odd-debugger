package jdi2diagram.main;


import jdi2diagram.event_info.prepare_event.ClassPrepareEventInfo;

import java.util.Observable;
import java.util.Observer;


public class ConnectorUser implements Observer {
    JDI2DiagramConnector connector;
    Thread connectorThread;
    String hostName;
    int portNum;

    public ConnectorUser(String hostName, int portNum) {
        this.hostName = hostName;
        this.portNum = portNum;
        connector = new JDI2DiagramConnector(hostName, portNum);
        connector.addObserver(this);
        connectorThread = new Thread(connector);
        connectorThread.start();
    }

    @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof ClassPrepareEventInfo){
            System.out.println("ConnectorUser:UPDATE");
            ClassPrepareEventInfo classPrepareEventInfo = (ClassPrepareEventInfo)arg;
            classPrepareEventInfo.printDump();

        }
    }
}
