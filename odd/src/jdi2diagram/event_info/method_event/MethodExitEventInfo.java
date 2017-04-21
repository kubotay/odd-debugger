package jdi2diagram.event_info.method_event;

import com.sun.jdi.event.MethodExitEvent;
import jdi2diagram.object_info.ThisMethod;
import jdi2diagram.object_info.ThisObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class MethodExitEventInfo extends MethodEventInfo {
    private static final String DumpPrefix = "MX";
    public MethodExitEventInfo(MethodExitEvent event) {
        super(event);
    }

    public MethodExitEventInfo(String[] dumpData) {
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
