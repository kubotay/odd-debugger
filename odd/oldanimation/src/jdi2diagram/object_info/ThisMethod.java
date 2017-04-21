package jdi2diagram.object_info;




import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;


public class ThisMethod implements Serializable {
    int modifiers;
    String returnTypeName;
    String methodName;
    ArrayList<ThisVariable> arguments = new ArrayList<>();

    public ThisMethod() {
    }

    public ThisMethod(int modifiers, String returnTypeName, String methodName) {
        this.modifiers = modifiers;
        this.returnTypeName = returnTypeName;
        this.methodName = methodName;
    }

    public void addArgument(ThisVariable arg){
        this.arguments.add(arg);
    }

    public ThisVariable getArgument(int i){
        return this.arguments.get(i);
    }

    public int sizeOfArguments(){
        return this.arguments.size();
    }
    public int getModifiers() {
        return modifiers;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getReturnTypeName() {
        return returnTypeName;
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

}
