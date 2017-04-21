package jdi2diagram.event_info.method_event;


import com.sun.jdi.*;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.tools.jdi.ArrayReferenceImpl;
import com.sun.tools.jdi.ObjectReferenceImpl;
import com.sun.tools.jdi.StringReferenceImpl;
import jdi2diagram.event_info.ModifierInfo;
import jdi2diagram.object_info.ThisMethod;
import jdi2diagram.object_info.ThisObject;
import jdi2diagram.object_info.ThisVariable;
import jdi2diagram.util.DumpCreator;
import jdi2diagram.util.ObjectToStringConverter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


public abstract class MethodEventInfo extends ModifierInfo implements Serializable{
    protected boolean isConstructor = false;
    protected boolean isVarArgs = false;

    protected Method _method;
    protected ObjectReference _thisObject;
    protected StackFrame _stackFrame = null;


    protected ThisObject thisObject;
    protected ThisMethod thisMethod;

    public MethodEventInfo() {
    }

    public MethodEventInfo(MethodEntryEvent event) {
        this.fetchPreMethodEntryEventInfo(event);
        this.fetchInfo();
        //this.setArguments();
        this.releaseJDIObjects();
    }

    public MethodEventInfo(MethodExitEvent event) {
        this.fetchPreEventInfo(event);
        this.fetchInfo();
        //this.setArguments();
        this.releaseJDIObjects();
    }

    public MethodEventInfo(String[] dumpData) {


    }

    private void fetchPreEventInfo(Event event) {
        if (event instanceof MethodExitEvent)
            fetchPreMethodExitEventInfo((MethodExitEvent) event);
        else if (event instanceof MethodEntryEvent)
            fetchPreMethodEntryEventInfo((MethodEntryEvent) event);
    }

    private void fetchPreMethodExitEventInfo(MethodExitEvent event) {
        this._method = event.method();
        try {
            this._stackFrame = event.thread().frame(0);
        } catch (IncompatibleThreadStateException e) {
            e.printStackTrace();
        }
        this._thisObject = _stackFrame.thisObject();
        this.threadId = event.thread().uniqueID();
        this.threadName = event.thread().name();
    }

    private void fetchPreMethodEntryEventInfo(MethodEntryEvent event) {
        this._method = event.method();
        try {
            this._stackFrame = event.thread().frame(0);
        } catch (IncompatibleThreadStateException e) {
            e.printStackTrace();
        }
        this._thisObject = _stackFrame.thisObject();
        this.threadId = event.thread().uniqueID();
        this.threadName = event.thread().name();
    }

    private void releaseJDIObjects() {
        this._thisObject = null;
        this._stackFrame = null;
        this._method = null;
    }

    public long getThreadId() {
        return threadId;
    }

    public String getThreadName() {
        return threadName;
    }

    public int getNumFields() {
        return thisObject.sizeOfFields();//this.fieldValues.size();
    }

    public String getFieldType(int i) {
        return thisObject.getField(i).getTypeName();//this.fieldValues.get(i).getX();
    }

    public String getFieldName(int i) {
        return thisObject.getField(i).getVarName();//this.fieldValues.get(i).getY();
    }

    public String getFieldValue(int i) {
        return thisObject.getField(i).getValueString();//this.fieldValues.get(i).getZ();
    }

    public String getClassName() {
        return this.thisObject.getClassName();
    }


    public void fetchInfo() {
        this.isConstructor = this._method.isConstructor();
        this.isVarArgs = this._method.isVarArgs();
        fetchThisObjectInfo();
        fetchThisMethodInfo();
    }

    public boolean isVarArgs() {
        return this.isVarArgs;
    }


    private void fetchThisMethodInfo() {
        int modifiers = this._method.modifiers();
        String methodName = this._method.name();
        String returnTypeName = this._method.returnTypeName();
        this.thisMethod = new ThisMethod(modifiers, returnTypeName, methodName);
        try {
            for (LocalVariable lv : this._method.arguments()) {
                Value value = this._stackFrame.getValue(lv);
                ThisVariable thisVariable = new ThisVariable(lv.typeName(), lv.name(), createValueString(lv, value), ThisVariable.ARGUMENT);
                this.thisMethod.addArgument(thisVariable);
            }
        } catch (AbsentInformationException e) {
            e.printStackTrace();
        }


    }

    private void fetchFieldsOfThisObject() {
        if (this._thisObject != null) {
            List<Field> fields = this._thisObject.referenceType().fields();
            Map<Field, Value> fieldValueMap = this._thisObject.getValues(fields);
            for (Map.Entry<Field, Value> fieldValueEntry : fieldValueMap.entrySet()) {
                Field field = fieldValueEntry.getKey();
                String fieldName = field.name();
                String fieldTypeName = field.typeName();
                int fieldModifiers = field.modifiers();
                Value value = fieldValueEntry.getValue();
                String valueString = this.createValueString(field, value);
                ThisVariable thisVariable = new ThisVariable(fieldModifiers, fieldTypeName, fieldName, valueString, ThisVariable.FIELD);
                this.thisObject.addField(thisVariable);
            }
        }
    }

    private String createValueString(Field field, Value value) {

        try {
            if (field.type() instanceof PrimitiveType) {
                PrimitiveValue primitiveValue = (PrimitiveValue) value;
                return primitiveValue.toString();
            } else if (field.type() instanceof ReferenceType) {
                if (value == null) return null;
                return objectToString(value);
            }
        } catch (ClassNotLoadedException e) {
            //e.printStackTrace();
        }

        return null;
    }

    private String createValueString(LocalVariable localVariable, Value value) {
        try {
            if (localVariable.type() instanceof PrimitiveType) {
                PrimitiveValue primitiveValue = (PrimitiveValue) value;
                return primitiveValue.toString();
            } else if (localVariable.type() instanceof ReferenceType) {
                if (value == null) return null;
                return objectToString(value);
            }
        } catch (ClassNotLoadedException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String objectToString(Value value) {
        if (value instanceof ClassType) {
            System.out.println("ClassType");
        } else if (value instanceof ArrayReference) {
            return ObjectToStringConverter.convert((ArrayReferenceImpl) value);
        } else if (value instanceof StringReference) {
            return ObjectToStringConverter.convert((StringReferenceImpl) value);//todo この続き
        } else if (value instanceof ObjectReference) {
            return ObjectToStringConverter.covert((ObjectReferenceImpl) value);
        }
        return null;
    }


    private void fetchThisObjectInfo() {

        String cName = this._method.declaringType().name();
        if (this._thisObject != null)
            this.thisObject = new ThisObject(this._thisObject.uniqueID(), cName);
        else {
            this.thisObject = new ThisObject(-1, cName);
            return;
        }
        fetchFieldsOfThisObject();


    }


    public boolean isConstructor() {
        return this.isConstructor;
    }


    public int getNumArgs() {
        return 0;//this.args.size();
    }

    public String getArgType(int i) {
        return null;//this.args.get(i).getX();
    }

    public String getArgName(int i) {
        return null;//this.args.get(i).getY();
    }

    public String getArgValue(int i) {
        return null;//this.args.get(i).getZ();
    }

    public String getReturnTypeName() {
        return this.thisMethod.getReturnTypeName();
    }

    public String getMethodName() {
        return this.thisMethod.getMethodName();
    }

    public long getObjectId() {
        return this.thisObject.getObjectId();
    }


    public abstract String dumpPrefix();

    public void printDump() {
        System.out.println(dump());
    }

    public ThisObject getThisObject() {
        return thisObject;
    }

    public ThisMethod getThisMethod() {
        return thisMethod;
    }

    public String dump() {
        return DumpCreator.dump(this);
    }

}
