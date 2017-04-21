package javanimation;

import java.util.*;

import processing.core.PApplet;

/**
 * Created by shou on 2014/11/02.
 */
public class CPE implements Event {
    javanimation papplet;
    private String myName = "";
    private long threadId;
    ArrayList<Instance> instanceList;

    public CPE(String name, ArrayList<Instance> list, javanimation p){
        papplet = p;

        this.instanceList = list;
        this.myName = name;
    }

    public void drawEvent(){
        javanimation.currentEvent = "Class Prepare";
        if(javanimation.classPrepare_flag == 1){
            instanceList.add(new Instance(this.myName, papplet));
        }
        javanimation. classPrepare_flag = 0;
        javanimation. animation_flag = 1;
    }

    public String getName(){
        return this.myName;
    }
    public long getId(){return 0;}
    public boolean getIs(int i){return false;}
    public String getReturnType(){return null;}
    public String getMethodName(){return null;}
    public int getNumArgs(){return 0;}
    public String[] getArgs(){return null;}
    public String getClassName(){return null;}
    public int getNumFields(){return 0;}
    public String getFields(int num){return null;}
    public String getFieldValues(int num){return null;}
    public String getPrev(){return null;}
    public String getNew(){return null;}
    public long getThreadId(){return this.threadId;}
}
