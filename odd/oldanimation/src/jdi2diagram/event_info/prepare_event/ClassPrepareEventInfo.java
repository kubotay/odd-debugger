package jdi2diagram.event_info.prepare_event;

import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.tools.jdi.ClassTypeImpl;
import jdi2diagram.object_info.ThisClass;
import jdi2diagram.util.DumpCreator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class ClassPrepareEventInfo extends PrepareEventInfo implements Serializable{
    private ThisClass thisClass;

    public ClassPrepareEventInfo(ClassPrepareEvent _event) {
        super(_event);
        ClassTypeImpl classTypeImpl = (ClassTypeImpl)_event.referenceType();
        thisClass = new ThisClass(classTypeImpl);
    }


    public ThisClass getThisClass() {
        return thisClass;
    }


    public String dumpPrefix() {
        return DumpPrefix;
    }

    public void printDump() {
        System.out.println(dump());
    }

    public String dump() {
        return DumpCreator.dump(this);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        //stream.defaultWriteObject();

        stream.writeObject(thisClass);
        stream.writeObject(threadName);
        stream.writeLong(threadId);

    }
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

        //stream.defaultReadObject();
        thisClass = (ThisClass)stream.readObject();
        threadName = (String)stream.readObject();
        threadId = stream.readLong();
    }
}
