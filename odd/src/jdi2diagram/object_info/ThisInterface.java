package jdi2diagram.object_info;


import com.sun.jdi.InterfaceType;
import com.sun.jdi.Method;
import jdi2diagram.event_info.ModifierInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ThisInterface extends ModifierInfo implements Serializable{
    private String interfaceName;
    private List<ThisMethod> methods = new ArrayList<>();
    public String getInterfaceName() {
        return interfaceName;
    }

    public ThisInterface() {
    }

    public ThisInterface(InterfaceType interfaceType) {
        modifiers = interfaceType.modifiers();
        interfaceName = interfaceType.name();
        for (Method method : interfaceType.methods()) {
            ThisMethod thisMethod = new ThisMethod(method.modifiers(),method.returnTypeName(),method.name());
            methods.add(thisMethod);
        }
    }

    public List<ThisMethod> getMethods(){
        return new ArrayList<>(methods);
    }
}
