package jdi2diagram.object_info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ThisObject implements Serializable{
    private String className;
    private long objectId = -1;
    private List<ThisVariable> fields = new ArrayList<>();

    public ThisObject(long objectId,String className) {
        this.className = className;
        this.objectId = objectId;
    }

    public ThisObject() {
    }

    public String getClassName() {
        return className;
    }

    public long getObjectId() {
        return objectId;
    }

    public int sizeOfFields(){
        return fields.size();
    }

    public ThisVariable getField(int i){
        return fields.get(i);
    }

    public void addField(ThisVariable thisVariable){
        if(!thisVariable.isField()) throw new Error();
        fields.add(thisVariable);
    }
}
