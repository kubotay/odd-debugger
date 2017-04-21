package javanimation;

import java.lang.reflect.Modifier;
import java.util.*;

import processing.core.PApplet;

/**
 * Created by shou on 2014/11/02.
 */
public class MWE implements Event {
    javanimation papplet;
    private long id = 0;
    int modifiers = 0;
    private String name = "";
    private String type;
    private String prevV = "";
    private String newV = "";
    List<Instance> instanceList;
    long threadId;
    String threadName;

    public MWE(String[] dumpData, List<Instance> list, javanimation p) {
        papplet = p;
        this.instanceList = list;

        this.id = Long.parseLong(dumpData[1]);
        this.modifiers = Integer.parseInt(dumpData[2]);
        this.type = dumpData[3];
        this.name = dumpData[4];
        if(dumpData[6] != null) {
            this.prevV = dumpData[6];
        }else {
            this.prevV = "";
        }
        if(dumpData[8] != null) {
            this.newV = dumpData[8];
        }else {
            this.newV = "";
        }
        this.threadName = dumpData[9];
        this.threadId = Long.parseLong(dumpData[10]);
    }


    public void drawEvent(){
        javanimation.currentEvent = "Change Field";
        Instance ins;
        for(int i = 0; i < instanceList.size(); i++){
            ins = instanceList.get(i);
            if(ins.getId() == this.id){
                ins.modificationWatchpoint(this);
                break;
            }
        }
        javanimation.animation_flag = 1;
    }

    public long getId(){
        return this.id;
    }
    public String getName(){
        return this.name;
    }
    public String getPrev(){
        return this.prevV;
    }
    public String getNew(){
        return this.newV;
    }
    public boolean getIs(int i){return false;}
    public String getReturnType(){return null;}
    public String getMethodName(){return null;}
    public int getNumArgs(){return 0;}
    public String[] getArgs(){return null;}
    public String getClassName(){return null;}
    public int getNumFields(){return 0;}
    public String getFields(int num){return null;}
    public String getFieldValues(int num){return null;}
    public long getThreadId(){return this.threadId;}
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
}