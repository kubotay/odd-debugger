package javanimation;

import processing.core.PApplet;

/**
 * Created by shou on 2014/11/02.
 */
public class Field {
    javanimation papplet;
    private String type = ""; //変数なら型、メソッドなら返却値の型
    private String name = "";
    private String access = ""; //publicとかprivateとか、名前調べて書き直す
    private String value = "";
    private float iValue = 0;
    private boolean isMethod = false; // 0なら変数、1ならメソッド
    private String[] arg; // 引数
    private int numArgs = 0;
    private int isCurrent = 0;
    private float textLength = 0;
    private int textColor = 0;
    private int textAlpha = 255;
    private long threadId = 0; // そのメソッドを実行しているスレッド　0なら変数
    private boolean clickFlag = false;

    public Field(MEE mee, javanimation p){ // メソッドのコンストラクタ
        papplet = p;
        this.name = mee.getMethodName();
        this.type = mee.getReturnType();
        this.isMethod = true;
        this.arg = mee.getArgs();
        this.numArgs = mee.getNumArgs();
        if(mee.isPublic()){
            this.access = "+";
        }else if(mee.isPrivate()){
            this.access = "-";
        }else if(mee.isProtected()){
            this.access = "#";
        }
        String s = this.access + this.name+"(";
        for(int j = 0; j < this.numArgs; j++){
            s += this.getArgs(j) + " ";
        }
        s = s.trim();
        s += "):" + this.type;
        papplet.textSize(12);
        this.textLength = papplet.textWidth(s);
        this.threadId = mee.getThreadId();
        papplet.textSize(javanimation.getFontSize()*papplet.getMagnification()/100);
    }

    public Field(String name, String access, javanimation p){
        papplet = p;
        this.name = name;
        this.value = "null";
        this.access = access;
        papplet.textSize(12);
        this.textLength = papplet.textWidth(this.access + this.type + this.name+"=" + this.getValue());
        papplet.textSize(javanimation.getFontSize()*papplet.getMagnification()/100);
    }

    public String getName(){
        return this.name;
    }
    public int getNumArgs(){
        return this.numArgs;
    }
    public String getArgs(int i){
        return this.arg[i];
    }
    public int getArgsLength(){
        int i;
        for(i = 0; i < this.numArgs; i++){
            i += arg[i].length() + 1;
        }
        i--;
        return i;
    }
    public String getType(){
        return this.type;
    }
    public String getAccess(){
        return this.access;
    }
    public String getValue(){
        if(this.value.length() <=10) {
            return this.value;
        }else{
            return this.value.substring(0,5) + "..." + this.value.substring(this.value.length()-5);
        }
    }
    public String getFullValue(){
        return this.value;
    }
    public void setValue(String value){
        if(this.clickFlag){
            javanimation.play_flag = 0;
        }
        this.value = value;
        papplet.textSize(12);
        this.textLength = papplet.textWidth(this.access + this.type + " " + this.name+"=" + this.getValue());
        papplet.textSize(javanimation.getFontSize()*papplet.getMagnification()/100);
    }
    public boolean getIsMethod(){
        return this.isMethod;
    }
    public void setIsCurrent(int i){
        this.isCurrent = i;
    }
    public int getIsCurrent(){
        return this.isCurrent;
    }
    public float getTextLength(){
        return this.textLength;
    }
    public int getTextColor() { return this.textColor;}
    public void setTextColor(int c){ this.textColor = c;}
    public void changeTextAlpha(boolean f){ // trueなら濃く、falseなら薄く
        if(f){
            if(this.textAlpha < 205){
                this.textAlpha += 51;
            }
        }else {
            if(this.textAlpha > 51){
                this.textAlpha -= 51;
            }
        }
    }
    public int getTextAlpha(){ return this.textAlpha;}
    public long getThreadId(){ return this.threadId;}

    public boolean getClickFlag(){ return this.clickFlag;}

    public void checkFieldClick(){
        if(!this.isMethod) {
            this.clickFlag = !this.clickFlag;
        }
    }
}
