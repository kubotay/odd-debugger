package jdi2diagram.event_info.prepare_event;

import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.tools.jdi.InterfaceTypeImpl;
import jdi2diagram.object_info.ThisInterface;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class InterfacePrepareEventInfo extends PrepareEventInfo {
    private ThisInterface thisInterface;

    public InterfacePrepareEventInfo(ClassPrepareEvent _event) {
        super(_event);
        InterfaceTypeImpl interfaceTypeImpl = (InterfaceTypeImpl) _event.referenceType();
        thisInterface = new ThisInterface(interfaceTypeImpl);
    }


    public ThisInterface getThisInterface() {
        return thisInterface;
    }

    public void printDump() {

    }

    public String dump() {
        return null;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        //stream.defaultWriteObject();

        stream.writeObject(thisInterface);
        stream.writeObject(threadName);
        stream.writeLong(threadId);

    }
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

        //stream.defaultReadObject();
        thisInterface = (ThisInterface)stream.readObject();
        threadName = (String)stream.readObject();
        threadId = stream.readLong();
    }
}
