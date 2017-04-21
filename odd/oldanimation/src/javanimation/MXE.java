package javanimation;

import java.lang.reflect.Modifier;
import java.util.*;

import jdi2diagram.event_info.method_event.MethodExitEventInfo;
import jdi2diagram.object_info.ThisVariable;
import processing.core.PApplet;

/**
 * Created by shou on 2014/11/02.
 */
public class MXE implements Event {
    javanimation papplet;
    MethodExitEventInfo methodExitEventInfo;

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
    //String[] fieldValues;
    int numFields;
    private long threadId;
    private String threadName;
    List<Instance> instanceList;
    ArrayList<Event> methodList;
    ArrayList<Token> tokenList;

    ArrayList<Tuple<String, String, String>> args = new ArrayList<Tuple<String, String, String>>();
    ArrayList<Tuple<String, String, String>> fieldValues = new ArrayList<Tuple<String, String, String>>();

    public MXE(String[] dumpData, List<Instance> listI, ArrayList<Event> listM, ArrayList<Token> listT, javanimation p) {
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
        if(dumpData[index].equals("ARG") || dumpData[index].equals("ARGS")){
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
        this.threadName = dumpData[index];
        index++;
        this.threadId = Long.parseLong(dumpData[index]);
        this.isStatic = this.isStatic();
        if(this.methodName.equals("<init>")){
            this.isConstructor = true;
        }
    }

    public MXE(MethodExitEventInfo mxei, List<Instance> listI, ArrayList<Event> listM, ArrayList<Token> listT, javanimation p){
        papplet = p;
        this.instanceList = listI;
        this.methodList = listM;
        this.tokenList = listT;
        this.methodExitEventInfo = mxei;

        this.className = mxei.getThisObject().getClassName();
        this.id = mxei.getThisObject().getObjectId();
        this.modifiers = mxei.getThisMethod().getModifiers();
        this.returnType = mxei.getThisMethod().getReturnTypeName();
        this.methodName = mxei.getThisMethod().getMethodName();

        int size = mxei.getThisObject().sizeOfFields();
        for (int i = 0; i < size; i++) {
            ThisVariable thisVariable = mxei.getThisObject().getField(i);
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

        size = mxei.getThisMethod().sizeOfArguments();
        for (int i = 0; i < size; i++) {
            ThisVariable thisVariable = mxei.getThisMethod().getArgument(i);
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
        this.threadName = mxei.getThreadName();
        this.threadId = mxei.getThreadId();
        this.isStatic = this.isStatic();
    }

    public String getThreadName() {
        return threadName;
    }

    public long getThreadId() {
        return this.threadId;
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

    }*/

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

    public long getId() {
        return this.id;
    }

    public String[] getArgs(){
        String[] a = new String[this.numArgs];
        for(int i=0; i < this.numArgs; i++){
            a[i] = this.getArgValue(i);
        }
        return a;
    }
    public String getFields(int n){return null;}
    public String getPrev(){return null;}
    public String getNew(){return null;}
    public boolean getIs(int i){return false;}
    public String getFieldValues(int num){return null;}
    public String getName(){return this.methodName;}

    public void drawEvent() {
        javanimation.currentEvent = "Method Exit";
        if(this.isConstructor){ // コンストラクタ
            for(int i = 0; i < instanceList.size(); i++){
                Instance ins = instanceList.get(i);
                if(this.id == ins.getId()){
                    String list = "[";
                    for(int j = 0; j < this.getNumFields(); j++){
                        String s = this.getFieldValue(j);
                        if(s.startsWith("\"[<i") || s.startsWith("[<i")){
                            list += s.substring(s.indexOf("[")+1, s.indexOf("]"));
                            list += ",";
                        }else if(s.startsWith("<id") || s.startsWith("\"<id")){
                            list += s.substring(s.indexOf("<"), s.indexOf(">")+1);
                            list += s + ",";
                        }else if(this.getFieldType(j).equals("java.util.HashMap")){
                            String[] hash = s.split(",");
                            for(int k = 1; k < hash.length; k=k+2){
                                if(hash[k].startsWith("<id="))
                                    list += hash[k].substring(0,hash[k].length()-1) + ",";
                            }
                        }
                    }

                    for(Field field : ins.getFieldList()){
                        String s = field.getFullValue();
                        if(s.startsWith("\"[<i") || s.startsWith("[<i")){
                            list += s.substring(s.indexOf("[")+1, s.indexOf("]"));
                            list += ",";
                        }else if(s.startsWith("<id") || s.startsWith("\"<id")){
                            list += s.substring(s.indexOf("<"), s.indexOf(">")+1);
                            list += s + ",";
                        }else if(field.getType().equals("java.util.HashMap")){
                            String[] hash = s.split(",");
                            for(int k = 1; k < hash.length; k=k+2){
                                if(hash[k].startsWith("<id="))
                                    list += hash[k].substring(0,hash[k].length()-1) + ",";
                            }
                        }
                    }
                    if(list.endsWith(","))
                        list = list.substring(0, list.length()-1);
                    if(list.startsWith("[<")){
                        ins.removeSList();
                        ins.setSList(instanceList, list);
                    }
                    ins.updateField(this);
                    break;
                }
            }
            for(int i = methodList.size()-1; i >= 0; i--){
                Event mee = methodList.get(i);
                if(mee.getThreadId() == this.threadId){
                    methodList.remove(i);
                    break;
                }
            }
            for(Instance ins : instanceList){
                if(this.id == ins.getId()){
                    ins.updateField(this);
                    break;
                }
            }
            Event mee;
            if(methodList.isEmpty() == false) {
                for(int i = methodList.size()-1; i >= 0; i--) {
                    mee=methodList.get(i);
                    long thread = mee.getThreadId();
                    if(thread == this.threadId) {
                        for (Instance in : instanceList) {
                            if (in.getId() == mee.getId()) {
                                //in.setTextColor();
                                papplet.setCurrentInstance(in.getId());
                                setToken(in.getId());
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }else{
            Event mee;
            Instance ins;
            for(int i = 0; i < instanceList.size(); i++){
                ins = instanceList.get(i);
                if(this.id == ins.getId()){
                    String list = "[";
                    for(int j = 0; j < this.getNumFields(); j++){
                        String s = this.getFieldValue(j);
                        if(s.startsWith("\"[<i") || s.startsWith("[<i")){
                            list += s.substring(s.indexOf("[")+1, s.indexOf("]"));
                            list += ",";
                        }else if(s.startsWith("<id") || s.startsWith("\"<id")){
                            list += s.substring(s.indexOf("<"), s.indexOf(">")+1);
                            list += s + ",";
                        }else if(this.getFieldType(j).equals("java.util.HashMap")){
                            String[] hash = s.split(",");
                            for(int k = 1; k < hash.length; k=k+2){
                                if(hash[k].startsWith("<id="))
                                    list += hash[k].substring(0,hash[k].length()-1) + ",";
                            }
                        }
                    }

                    for(Field field : ins.getFieldList()){
                        String s = field.getFullValue();
                        if(s.startsWith("\"[<i") || s.startsWith("[<i")){
                            list += s.substring(s.indexOf("[")+1, s.indexOf("]"));
                            list += ",";
                        }else if(s.startsWith("<id") || s.startsWith("\"<id")){
                            list += s.substring(s.indexOf("<"), s.indexOf(">")+1);
                            list += s + ",";
                        }else if(field.getType().equals("java.util.HashMap")){
                            String[] hash = s.split(",");
                            for(int k = 1; k < hash.length; k=k+2){
                                if(hash[k].startsWith("<id="))
                                    list += hash[k].substring(0,hash[k].length()-1) + ",";
                            }
                        }
                    }
                    if(list.endsWith(","))
                        list = list.substring(0, list.length()-1);
                    if(list.startsWith("[<")){
                        ins.removeSList();
                        ins.setSList(instanceList, list);
                    }
                    ins.updateField(this);
                    ins.removeField(this.methodName);
                    break;
                }
            }
            for(int i = methodList.size()-1; i >= 0; i--){
                mee = methodList.get(i);
                if(mee.getThreadId() == this.threadId){
                    methodList.remove(i);
                    break;
                }
            }
            if(methodList.isEmpty() == false) {
                boolean f = true;
                for(int i = methodList.size()-1; i >= 0; i--) {
                    mee=methodList.get(i);
                    long thread = mee.getThreadId();
                    if(thread == this.threadId) {
                        for (Instance in : instanceList) {
                            if (in.getId() == mee.getId()) {
                                //in.setTextColor();    //ここでエラーが発生した
                                papplet.setCurrentInstance(mee.getId());
                                setToken(mee.getId());
                                f = false;
                                break;
                            }
                        }
                        break;
                    }
                }
                if(f)
                    deleteToken(this.threadId);
            }else {
                deleteToken(this.threadId);
            }
        }
    }
    public void setToken(long id){
        for (Token token : tokenList) {
            if (token.getThreadId() == this.threadId) {
                token.setInstansId(id);
                for(Instance instance : instanceList){
                    instance.setTextColor(this.threadId, true); // 薄くしている
                }
            }
        }
    }
    public void deleteToken(long id){
        for(int i = 0; i < tokenList.size(); i++){
            Token token = tokenList.get(i);
            if (token.getThreadId() == this.threadId) {
                tokenList.remove(i);
                break;
            }
        }
    }
}
