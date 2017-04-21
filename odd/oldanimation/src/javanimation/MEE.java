package javanimation;

import java.lang.reflect.Modifier;
import java.util.*;

import jdi2diagram.event_info.method_event.MethodEntryEventInfo;
import jdi2diagram.object_info.*;
import processing.core.PApplet;

/**
 * Created by shou on 2014/11/02.
 */
public class MEE implements Event{
    javanimation papplet;
    MethodEntryEventInfo methodEntryEventInfo;

    long id = 0;
    String className = "";
    String returnType = "";
    String methodName = "";
    int modifiers = 0;
    int numArgs = 0;
    String[] Args;
    boolean isStatic = false;
    boolean isConstructor = false;
    boolean isAbstract = false;
    boolean isVarArgs = false;
    boolean isPrivate = false;
    boolean isPublic = false;
    boolean isProtected = false;
    String[] Fields;
    String[] fieldValue;
    int numFields;
    private long threadId;
    private String threadName;
    List<Instance> instanceList;
    ArrayList<Event> methodList;
    ArrayList<Token> tokenList;

    ArrayList<Tuple<String, String, String>> args = new ArrayList<Tuple<String, String, String>>();
    ArrayList<Tuple<String, String, String>> fieldValues = new ArrayList<Tuple<String, String, String>>();


    public MEE(String[] dumpData, List<Instance> listI, ArrayList<Event> listM, ArrayList<Token> listT, javanimation p){
        papplet = p;
        this.instanceList = listI;
        this.methodList = listM;
        this.tokenList = listT;

        this.className = dumpData[1];
        this.id = Long.parseLong(dumpData[2]);
        this.returnType = dumpData[4];
        this.methodName = dumpData[5];
        this.modifiers = Integer.parseInt(dumpData[3]);
        int index = 8;
        if(dumpData[6].equals("FIELD")){
            int fieldLen = Integer.parseInt(dumpData[7]);
            for(int i=0;i<fieldLen;i++){

                String fieldString = dumpData[index];
                String fieldData[] = fieldString.split(":");
                if(fieldData.length == 2){
                    fieldValues.add(new Tuple<String,String,String>(fieldData[1], fieldData[2], ""));
                }else {
                    if(fieldData[3].startsWith("\"")){
                        fieldValues.add(new Tuple<String, String, String>(fieldData[1], fieldData[2], fieldData[3].substring(1,fieldData[3].length()-1)));
                    }else {
                        fieldValues.add(new Tuple<String, String, String>(fieldData[1], fieldData[2], fieldData[3]));
                    }
                }
                index += 1;
            }
        }
        if(dumpData[index].equals("ARGS") || dumpData[index].equals("ARG")){
            index++;
            int argLen = Integer.parseInt(dumpData[index]);
            index++;
            for(int i=0;i<argLen;i++){

                String argString  = dumpData[index];
                String argData[] = argString.split(":");
                args.add(new Tuple<String,String,String>(argData[0],argData[1],argData[2]));
                index++;
            }
        }
        if(this.methodName.equals("<init>")){
            this.isConstructor = true;
        }
        this.threadName = dumpData[index];
        index++;
        this.threadId = Long.parseLong(dumpData[index]);
        this.isStatic = this.isStatic();
    }

    public MEE(MethodEntryEventInfo meei, List<Instance> listI, ArrayList<Event> listM, ArrayList<Token> listT, javanimation p){
        papplet = p;
        this.instanceList = listI;
        this.methodList = listM;
        this.tokenList = listT;
        this.methodEntryEventInfo = meei;

        this.className = meei.getThisObject().getClassName();
        this.id = meei.getThisObject().getObjectId();
        this.modifiers = meei.getThisMethod().getModifiers();
        this.returnType = meei.getThisMethod().getReturnTypeName();
        this.methodName = meei.getThisMethod().getMethodName();

        int size = meei.getThisObject().sizeOfFields();
        for (int i = 0; i < size; i++) {
            ThisVariable thisVariable = meei.getThisObject().getField(i);
            if (!thisVariable.isArgument()) {
                if(thisVariable.getValueString() == null) {
                    fieldValues.add(new Tuple<String, String, String>(thisVariable.getTypeName(), thisVariable.getVarName(), ""));
                } else {
                    if(thisVariable.getValueString().startsWith("\"")){
                        fieldValues.add(new Tuple<String, String, String>(thisVariable.getTypeName(), thisVariable.getVarName(), thisVariable.getValueString().substring(1, thisVariable.getValueString().length()-1)));
                    }else {
                        fieldValues.add(new Tuple<String, String, String>(thisVariable.getTypeName(), thisVariable.getVarName(), thisVariable.getValueString()));
                    }
                }
            }
        }

        size = meei.getThisMethod().sizeOfArguments();
        System.out.println("fieldSize:"+size);
        for (int i = 0; i < size; i++) {
            ThisVariable thisVariable = meei.getThisMethod().getArgument(i);
            if (thisVariable.isArgument()) {
                if(thisVariable.getValueString() == null)
                    args.add(new Tuple<String, String, String>(thisVariable.getTypeName(), thisVariable.getVarName(), ""));
                else
                    args.add(new Tuple<String, String, String>(thisVariable.getTypeName(), thisVariable.getVarName(), thisVariable.getValueString()));
            }
        }

        if(this.methodName.equals("<init>")){
            this.isConstructor = true;
        }
        this.threadName = meei.getThreadName();
        this.threadId = meei.getThreadId();
        this.isStatic = this.isStatic();
    }

    public long getThreadId() {
        return this.threadId;
    }

    public String getThreadName() {
        return threadName;
    }
    public int getNumFields() {
        return this.fieldValues.size();
    }

    public String getFieldType(int i) {
        return this.fieldValues.get(i).getX();
    }

    public String getFieldName(int i) {
        return this.fieldValues.get(i).getY();
    }

    public String getFieldValue(int i) {
        return this.fieldValues.get(i).getZ();
    }

    public String getClassName() {
        return this.className;
    }

    /*public ObjectReference getThisObject() {

        if (stackFrame == null) return null;
        return this.thisObject;

    }*/  // stackFrameが無いと出る

    public boolean isConstructor() {
        return this.isConstructor;
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

    public boolean isVarArgs() {
        return this.isVarArgs;
    }

    public int getNumArgs() {
        return this.args.size();
    }

    public String getArgType(int i) {
        return this.args.get(i).getX();
    }

    public String getArgName(int i) {
        return this.args.get(i).getY();
    }

    public String getArgValue(int i) {
        return this.args.get(i).getZ();
    }

    public String getReturnType() {
        return this.returnType;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public long getId(){
        return this.id;
    }

    public String[] getArgs(){
        String[] a = new String[this.getNumArgs()];
        for(int i=0; i < this.getNumArgs(); i++){
            a[i] = this.getArgValue(i);
        }
        return a;
    }
    public String getFields(int n){return null;}
    public String getPrev(){return null;}
    public String getNew(){return null;}
    public boolean getIs(int i){return false;}
    public String getFieldValues(int num){return null;}
    public int getFieldAccess(int num){return Integer.parseInt(fieldValues.get(num).getX());}
    public String getName(){return this.methodName;}

    public void drawEvent() {
        javanimation.currentEvent = "Method Entry";
        int flag = 1;
        if(this.isConstructor || this.isStatic){ // コンストラクタ
            for(Instance instance : instanceList) {
                if (instance.getId() == this.id) {
                    flag = 0;
                    break;
                }
            }
            if(flag==1) {
                if (this.isStatic)
                    papplet.setCurrentInstance(this.id);
                methodList.add(this);
                Instance ins = new Instance(this, papplet);
                instanceList.add(ins);
                Instance parent;
                if (!this.isStatic || this.isConstructor) {
                    for (int i = 0; i < instanceList.size(); i++) {
                        parent = instanceList.get(i);
                        if (papplet.getTokenInstanceID(this.threadId) == parent.getId()) {
                            ins.setPList(instanceList.get(i));
                            break;
                        }
                    }
                } else {
                    Field fi = new Field(this, papplet);
                    ins.addField(fi);
                }
                papplet.setCurrentInstance(this.id);
                setToken(ins);
            }else{
                if (this.isStatic)
                    papplet.setCurrentInstance(this.id);
                methodList.add(this);
                Instance ins;
                for(int i = 0; i < instanceList.size(); i++){
                    ins = instanceList.get(i);
                    if(this.id == ins.getId()){
                        setToken(ins);
                        break;
                    }
                }
            }

        }else{
            papplet.setCurrentInstance(this.id);
            methodList.add(this);
            Instance ins;
            boolean f = true;
            Field fi = new Field(this, papplet);
            for(int i = 0; i < instanceList.size(); i++){
                ins = instanceList.get(i);
                if(this.id == ins.getId()){
                    Token t = setToken(ins);
                    for(Instance instance : instanceList){
                        instance.setTextColor(this.threadId, false); // 薄くしている
                    }
                    fi.setTextColor(t.getTokenColor());
                    ins.addField(fi);
                    ins.updateField(this);
                    f = false;
                    break;
                }
            }
            if(f){
                Instance in = new Instance(this, papplet);
                instanceList.add(in);
                Instance parent;
                if (!this.isStatic || this.isConstructor) {
                    for (int i = 0; i < instanceList.size(); i++) {
                        parent = instanceList.get(i);
                        if (papplet.getTokenInstanceID(this.threadId) == parent.getId()) {
                            in.setPList(instanceList.get(i));
                            break;
                        }
                    }
                } else {
                    Field field = new Field(this, papplet);
                    in.addField(fi);
                }
            }
        }
        javanimation.animation_flag = 1;
    }
    public Token setToken(Instance ins){
        Token returnToken = null;
        if (tokenList.isEmpty()) {
            returnToken = new Token(this, papplet);
            tokenList.add(returnToken);
        }

        boolean flag = true;
        for (Token token : tokenList) {
            if (token.getThreadId() == this.threadId) {
                token.setInstansId(ins.getId());
                returnToken = token;
                flag = false;
            }
        }
        if(flag){
            returnToken = new Token(this, papplet);
            tokenList.add(returnToken);
        }
        return returnToken;
    }
}
