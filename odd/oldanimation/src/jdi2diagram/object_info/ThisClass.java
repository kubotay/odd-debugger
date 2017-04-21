package jdi2diagram.object_info;

import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.Method;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.tools.jdi.ClassTypeImpl;
import com.sun.tools.jdi.InterfaceTypeImpl;
import jdi2diagram.event_info.ModifierInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ThisClass extends ModifierInfo implements Serializable {
    private List<ThisInterface> interfaces = new ArrayList<>();
    private List<ThisVariable> fields = new ArrayList<>();
    private List<ThisMethod> methods = new ArrayList<>();
    private String className;
    private ThisClass superClass;


    private int modifiers;
    private boolean isLoadClass = false;

    public ThisClass(ClassPrepareEvent _event) {
        super();
        try {
            this._stackFrame = _event.thread().frame(0);
        } catch (IncompatibleThreadStateException e) {
            e.printStackTrace();
        }
        fetchClassInfo((ClassTypeImpl) _event.referenceType());
    }

    public ThisClass(ClassTypeImpl classTypeImpl) {
        super();
        fetchClassInfo(classTypeImpl);
    }

    public ThisClass(InterfaceTypeImpl interfaceTypeImpl) {
        super();
        fetchClassInfo(interfaceTypeImpl);
    }

    private void fetchClassInfo(InterfaceTypeImpl interfaceTypeImpl) {
        if (interfaceTypeImpl == null) return;
        className = interfaceTypeImpl.name();
        modifiers = interfaceTypeImpl.modifiers();
        fetchFieldInfo(interfaceTypeImpl.fields());
        fetchMethodInfo(interfaceTypeImpl.methods());
        fetchInterfaceInfo(interfaceTypeImpl.superinterfaces());
    }

    private void fetchClassInfo(ClassTypeImpl classTypeImpl) {
        if (classTypeImpl == null) return;
        className = classTypeImpl.name();
        modifiers = classTypeImpl.modifiers();
        superClass = new ThisClass((ClassTypeImpl) classTypeImpl.superclass());
        fetchFieldInfo(classTypeImpl.fields());
        fetchMethodInfo(classTypeImpl.methods());
        fetchInterfaceInfo(classTypeImpl.interfaces());
    }

    private void fetchInterfaceInfo(List<InterfaceType> interfaceTypeList) {
        for (InterfaceType interfaceType : interfaceTypeList) {
            ThisInterface thisInterface = new ThisInterface(interfaceType);
            interfaces.add(thisInterface);
        }
    }

    private void fetchFieldInfo(List<Field> fieldList) {
        for (Field field : fieldList) {
            //Value value = classTypeImpl.getValue(field);
            ThisVariable thisVariable = new ThisVariable(field.modifiers(), field.typeName(), field.name(), "test", ThisVariable.FIELD);
            fields.add(thisVariable);
        }
    }

    private void fetchMethodInfo(List<Method> methodList) {
        for (Method method : methodList) {
            ThisMethod thisMethod = new ThisMethod(method.modifiers(), method.returnTypeName(), method.name());
            methods.add(thisMethod);
        }
    }

    public String getClassName() {

        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ThisClass getSuperClass() {
        return superClass;
    }

    public void setSuperClass(ThisClass superClass) {
        this.superClass = superClass;
    }

    public int sizeOfFields() {
        return fields.size();
    }

    public int sizeOfMethods() {
        return methods.size();
    }

    public void addField(ThisVariable thisVariable) {
        fields.add(thisVariable);
    }

    public void addMethod(ThisMethod method) {
        methods.add(method);
    }

    public ThisVariable getField(int i) {
        return fields.get(i);
    }

    public ThisMethod getMethod(int i) {
        return methods.get(i);
    }

    public int getModifiers() {
        return modifiers;
    }

    public int sizeOfInterfaces() {
        return interfaces.size();
    }

    public ThisInterface getInterface(int i) {
        return interfaces.get(i);
    }

    public List<ThisInterface> getInterfaces() {
        return new ArrayList(this.interfaces);
    }

    public List<ThisVariable> getFields() {
        return new ArrayList(this.fields);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();


    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

        stream.defaultReadObject();

    }
}
