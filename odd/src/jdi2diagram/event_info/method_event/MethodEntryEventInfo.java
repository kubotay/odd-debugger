package jdi2diagram.event_info.method_event;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.sun.jdi.event.MethodEntryEvent;

import jdi2diagram.object_info.ThisMethod;
import jdi2diagram.object_info.ThisObject;


public class MethodEntryEventInfo extends MethodEventInfo implements Serializable{

    private static final String DumpPrefix = "ME";
    public MethodEntryEventInfo(MethodEntryEvent event) {
        super(event);
    }

    public MethodEntryEventInfo(String[] dumpData) {
        super(dumpData);
    }

    @Override
    public String dumpPrefix() {
        return DumpPrefix;
    }


    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();

        stream.writeBoolean(isConstructor);
        stream.writeBoolean(isVarArgs);
        stream.writeObject(thisObject);
        stream.writeObject(thisMethod);
        stream.writeObject(threadName);
        stream.writeLong(threadId);

    }
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

        stream.defaultReadObject();
        isConstructor = stream.readBoolean();
        isVarArgs = stream.readBoolean();
        thisObject = (ThisObject)stream.readObject();
        thisMethod = (ThisMethod)stream.readObject();
        threadName = (String)stream.readObject();
        threadId = stream.readLong();
    }

}
