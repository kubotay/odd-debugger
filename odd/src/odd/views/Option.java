package odd.views;

import processing.core.PImage;

/**
 * Created by shou on 2014/12/17.
 */
public class Option {
    javanimation papplet;

    boolean settingFlag = false; // trueなら設定部分を開く
    boolean classNameFlag = false; //trueならプロジェクト名から、falseならクラス名のみ
    long currentInstance = 0; //現在メソッド実行中のインスタンスのID
    boolean tokenFlag = true;
    boolean delayBarPressed = false;
    float magnification = 100;

    PImage[] button = new PImage[2];
    PImage bar;
    PImage circle;

    public Option(javanimation javanimation){
        this.papplet = javanimation;
        button[0] = papplet.loadImage("plus.png");
        button[1] = papplet.loadImage("minus.png");
        bar = papplet.loadImage("white.png");
        circle = papplet.loadImage("point.png");
    }

    public boolean optionPressed(boolean flag){
        boolean f = flag;
        float d = papplet.dist(40,545,papplet.mouseX,papplet.mouseY);
        if(d <= 25){
            f = false;
            if(settingFlag) {
                settingFlag = false;
                papplet.size(800, 630);
            }else {
                settingFlag = true;
                papplet.size(1000, 630);

            }
        }
        if(settingFlag){
            d = papplet.dist(820+papplet.delayTime, 580, papplet.mouseX, papplet.mouseY);
            if(d <= 10) {
                f = false;
                delayBarPressed = true;
            }
        }
        return f;
    }

    public void pressClassButton(){
        if(classNameFlag)
            classNameFlag = false;
        else
            classNameFlag = true;
    }

    public void pressTokenButton(){
        if(tokenFlag)
            tokenFlag = false;
        else
            tokenFlag = true;
    }
    public void pressZoomUp(){
        magnification += 10;
    }
    public void pressZoomDown(){
        magnification -= 10;
    }

    public float getMagnification() { return  this.magnification;}
    public void setMagnification(){ this.magnification = 100;}
    public long getCurrentInstance(){ return this.currentInstance;}
    public  void setCurrentInstance(long i){ this.currentInstance = i;}
    public boolean getTokenFlag(){ return this.tokenFlag;}
    public boolean getClassNameFlag(){ return this.classNameFlag;}
}
