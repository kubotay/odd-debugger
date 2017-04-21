package jdi2diagram.object_info;



import java.io.Serializable;
import java.lang.reflect.Modifier;


public class ThisVariable implements Serializable {
    public final static boolean FIELD = false;
    public final static boolean ARGUMENT = true;
    int modifiers;
    String typeName;
    String varName;
    String valueString;
    boolean isArgument = false;

    public ThisVariable() {
    }

    public ThisVariable(String typeName, String name, String valueString, boolean varType) {
        // argument
        this.modifiers = -1;
        this.typeName = typeName;
        this.varName = name;
        this.valueString = valueString;
        this.isArgument = varType;

    }

    public ThisVariable(int modifiers, String typeName, String name, String valueString, boolean varType) {
        // field
        this.modifiers = modifiers;
        this.typeName = typeName;
        this.varName = name;
        this.valueString = valueString;
        this.isArgument = varType;

    }

//    public static String dumpSeparator() {
//        return ":";
//    }
    public static final String DumpSeparator = ":";

    public String getVarName() {
        return varName;
    }

    public boolean isArgument() {
        return isArgument == ARGUMENT;
    }

    public boolean isField() {
        return !isArgument();
    }

    public int getModifiers() {
        return modifiers;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getValueString() {
        return valueString;
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

    public String dump() {

        String dumpString;
        if (!this.isArgument())
            dumpString = modifiers + DumpSeparator + wrap("",typeName) + DumpSeparator + wrap("",varName) + DumpSeparator;
        else
            dumpString = wrap("",typeName) + DumpSeparator + wrap("",varName) + DumpSeparator;
        if(valueString == null)
            dumpString += null;
        else
            dumpString += wrap("\"", valueString);
        return dumpString;
    }

    private String wrap(String wrapper, String string){
        return wrapper + string + wrapper;
    }
}

