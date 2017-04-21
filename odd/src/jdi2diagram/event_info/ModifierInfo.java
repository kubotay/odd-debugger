 package jdi2diagram.event_info;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Modifier;

import com.sun.jdi.StackFrame;

public abstract class ModifierInfo implements Serializable{
    protected String threadName;
    protected long threadId;
    protected int modifiers;
    protected StackFrame _stackFrame;
    public boolean lastEvent = false;

    public ModifierInfo() {
    }

    public String getThreadName() {
        return threadName;
    }

    public long getThreadId() {
        return threadId;
    }
    public boolean isAbstract() {
        return Modifier.isAbstract(this.modifiers);
    }

    public boolean isFinal() {
        return Modifier.isFinal(this.modifiers);
    }

    public boolean isInterface() {
        return Modifier.isInterface(this.modifiers);
    }

    public boolean isNative() {
        return Modifier.isNative(this.modifiers);
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(this.modifiers);
    }

    public boolean isProtected() {
        return Modifier.isProtected(this.modifiers);
    }

    public boolean isPublic() {
        return Modifier.isPublic(this.modifiers);
    }

    public boolean isStatic() {
        return Modifier.isStatic(this.modifiers);
    }

    public boolean isStrict() {
        return Modifier.isStrict(this.modifiers);
    }

    public boolean isSynchronized() {
        return Modifier.isSynchronized(this.modifiers);
    }

    public boolean isTransient() {
        return Modifier.isTransient(this.modifiers);
    }

    public boolean isVolatile() {
        return Modifier.isVolatile(this.modifiers);
    }

    public boolean getLastEvent(){ return lastEvent;}

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeObject(threadName);
        stream.writeLong(threadId);
        stream.writeBoolean(lastEvent);

    }
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        threadName = (String)stream.readObject();
        threadId = stream.readLong();
        lastEvent = stream.readBoolean();
    }

}
