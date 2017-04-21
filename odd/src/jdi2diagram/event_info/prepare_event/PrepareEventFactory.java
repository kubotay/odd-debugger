package jdi2diagram.event_info.prepare_event;

import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.tools.jdi.ClassTypeImpl;
import com.sun.tools.jdi.InterfaceTypeImpl;


public class PrepareEventFactory {
    public static ClassPrepareEventInfo createClassPrepareEventInfo(ClassPrepareEvent _event){
        return new ClassPrepareEventInfo(_event);
    }

    public static InterfacePrepareEventInfo createInterfacePrepareEventInfo(ClassPrepareEvent _event){
        return new InterfacePrepareEventInfo(_event);
    }

    public static PrepareEventInfo create(ClassPrepareEvent _event){
        if(_event.referenceType() instanceof ClassTypeImpl){
            return createClassPrepareEventInfo(_event);
        }else if(_event.referenceType() instanceof InterfaceTypeImpl){
            return createInterfacePrepareEventInfo(_event);
        }
        return null;
    }
}
