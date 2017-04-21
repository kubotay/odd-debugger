package jdi2diagram.event_info.prepare_event;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.event.ClassPrepareEvent;

import jdi2diagram.event_info.ModifierInfo;



public abstract class PrepareEventInfo extends ModifierInfo {
    protected static final String DumpPrefix = "CP";


    public PrepareEventInfo() {
        super();
    }
//    protected List<ThisVariable> variables = new ArrayList<>();
//    protected List<ThisMethod> methods = new ArrayList<>();

    public PrepareEventInfo(ClassPrepareEvent _event) {

//      this._event = _event;
      try {
            this._stackFrame = _event.thread().frame(0);
        } catch (IncompatibleThreadStateException e) {
            e.printStackTrace();
        }
        this.threadName = _event.thread().name();
        this.threadId = _event.thread().uniqueID();
        this.modifiers =_event.referenceType().modifiers();

    }

    protected void releaseJDIObject(){

        this._stackFrame = null;
    }
}
