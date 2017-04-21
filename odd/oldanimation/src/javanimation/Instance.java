package javanimation;

import java.util.*;
import java.lang.reflect.Modifier;

import processing.core.PApplet;

/**
 * Created by shou on 2014/11/02.
 */
public class Instance {
    javanimation papplet;

    static int mode = 0;
    static int netCount = 0;

    private float[] point = {0, 0}; // 描画する枠の中心の座標
    private float[] wh = {0, 0};
    private float[] fPoint = {0, 0};
    private float fHeight = 0;
    private long id = 0;
    private int level = 0;
    private String className = "";
    private float maxLength = 0; // フィールドと名前の最大長さ
    private int isClass = 0; // 0ならインスタンス、1ならクラス
    private int fieldCount = 0;
    private ArrayList<Field> fieldList = new ArrayList<Field>();
    private ArrayList<Instance> subList = new ArrayList<Instance>();//子リスト
    private ArrayList<Instance> parentList = new ArrayList<Instance>(); // 親リスト
    private Integrator[] integrator;
    private int animation_flag = 1;
    private boolean showFlag = true; // trueなら描画される
    private boolean drawFlag = true; // trueならそのループでまだ描画されていない
    private boolean cNameFlag; // 各インスタンスのクラスネームフラグ
    private boolean cUpdateFlag = false;
    private boolean clickFlag = false; // 選択されるとtrueになる

    public Instance(String name, javanimation p){ // クラス用のコンストラクタ
        papplet = p;

        this.isClass = 1;
        this.id = -1;
        this.cNameFlag = papplet.getClassNameFlag();
        this.className = name;
        this.animation_flag = 1;
        papplet.textSize(12);
        this.maxLength = papplet.textWidth("+" + name);
        papplet.textSize(javanimation.getFontSize()*papplet.getMagnification()/100);
        this.point[0] = 75;
        this.point[1] = 20;

        integrator = new Integrator[5];
        integrator[0] = new Integrator(this.point[0]);
        integrator[1] = new Integrator(this.point[1]);
        integrator[2] = new Integrator(this.wh[0]);
        integrator[3] = new Integrator(this.wh[1]);
        integrator[4] = new Integrator(this.fHeight);

        integrator[0].target(75);
        integrator[1].target(20);
        integrator[2].target(this.maxLength);
        integrator[3].target(javanimation.textHeight);

        integrator[2].update();
        integrator[3].update();
    }
    public Instance(MEE mee, javanimation p){ // インスタンス用のコンストラクタ
        papplet = p;

        this.id = mee.getId();
        this.className = mee.getClassName();
        this.animation_flag = 1;
        this.point[0] = 75 + this.level*150;
        this.point[1] = 65 + 20;
        this.fPoint[0] = this.point[0];
        this.fPoint[1] = this.point[1] + this.wh[1]/2;

        integrator = new Integrator[5];
        integrator[0] = new Integrator(this.point[0]);
        integrator[1] = new Integrator(this.point[1]);
        integrator[2] = new Integrator(this.wh[0]);
        integrator[3] = new Integrator(this.wh[1]);
        integrator[4] = new Integrator(this.fHeight);
        papplet.textSize(12);
        this.maxLength = papplet.textWidth("<id=" + this.id + ">:" + this.getClassName());
        this.level = 1;
        for(int i = 0; i < mee.getNumFields(); i++){
            String access = "";
            if(mee.isPublic()){
                access = "+";
            }else if(mee.isPrivate()){
                access = "-";
            }else if(mee.isProtected()){
                access = "#";
            }
            Field fi = new Field(mee.getFieldName(i),access, papplet);
            this.fieldList.add(fi);
            fieldCount++;

            if(fi.getTextLength() >= this.maxLength){
                this.maxLength = fi.getTextLength();
            }
        }
        papplet.textSize(javanimation.getFontSize()*papplet.getMagnification()/100);

        integrator[0].target(75 + this.level*150);
        integrator[1].target(65 + 20);
        integrator[2].target(this.maxLength);
        integrator[3].target(13);
        integrator[4].target(javanimation.textHeight*this.fieldCount);

        integrator[2].update();
        integrator[3].update();
    }

    public float[] drawBox(float[] maxWH, float num){
        if(this.drawFlag) {
            float[] tmp = {0, 0};
            int count =0;
            if(papplet.getClassNameFlag() != this.cNameFlag){
                changeClassNameFlag();
                this.cUpdateFlag = true;
            }
            if(cUpdateFlag)
                integrator[2].cUpdate();

            mode = 0;
            if(mode == 0) { // ツリーモード
                this.drawFlag = false;
                tmp[0] = maxWH[0];
                tmp[1] = maxWH[1];

                integrator[0].target(maxWH[0] + 30 + this.wh[0] / 2);
                integrator[0].update();
                this.point[0] = integrator[0].getValue();
                this.fPoint[0] = this.point[0];

                integrator[1].target(maxWH[1] + javanimation.textHeight + this.wh[1] / 2);
                integrator[1].update();
                this.point[1] = integrator[1].getValue();
                this.animation_flag = 1;

                this.fPoint[1] = this.point[1];
                maxWH[0] = this.point[0] + this.wh[0] / 2;

                for (int i = 0; i < this.subList.size(); i++) {
                    Instance ins = this.subList.get(i);
                    if (ins.getDrawFlag()) {
                        if ((this.id != -1) || (ins.getPList().size() == 1) && (ins.getPList().get(0).getId() == -1)) {
                            float[] subPoint = ins.getPoint();
                            papplet.stroke(0);
                            float[] cross = ins.crossPoint(this.getPoint());
                            papplet.line(movedPoint(1), movedPoint(2) + movedPoint(7) / 2, cross[0], cross[1]);
                            if(papplet.getMagnification() >= 70)
                                ins.drawArrow(cross);
                            maxWH = ins.drawBox(maxWH, num + 1);
                        } else {
                            maxWH = ins.drawBox(maxWH, num + 1);
                        }
                    } else {
                        if ((this.id != -1) || (ins.getPList().size() == 1) && (ins.getPList().get(0).getId() == -1)) {
                            float[] subPoint = ins.getPoint();
                            papplet.stroke(0);
                            float[] cross = ins.crossPoint(this.getPoint());
                            papplet.line(movedPoint(1), movedPoint(2) + movedPoint(7) / 2, cross[0], cross[1]);
                            if(papplet.getMagnification() >= 70)
                                ins.drawArrow(cross);
                        }
                    }

                }
            }else if(mode == 1){ // ネットワークモード
                this.drawFlag = false;
                tmp[0] = maxWH[0];
                tmp[1] = maxWH[1];
                float[] p = {0,0};
                float lineLemgth = 30+(float)Math.sqrt((Math.pow((double)this.wh[0],2.0) + Math.pow((double)(this.wh[1]+this.fHeight),2.0)));

                if(num ==0){
                    integrator[0].target(30 + this.wh[0] / 2);
                    integrator[0].update();
                    this.point[0] = integrator[0].getValue();
                    this.fPoint[0] = this.point[0];

                    integrator[1].target(javanimation.textHeight + this.wh[1] / 2);
                    integrator[1].update();
                    this.point[1] = integrator[1].getValue();
                    this.animation_flag = 1;

                    this.fPoint[1] = this.point[1];
                }else {
                    count = 1 + this.subList.size();

                    integrator[0].target(lineLemgth*papplet.cos((float)(num/180.0*papplet.PI)) +tmp[0]);
                    integrator[0].update();
                    this.point[0] = integrator[0].getValue();
                    this.fPoint[0] = this.point[0];

                    integrator[1].target(lineLemgth*papplet.sin((float)(num/180.0*papplet.PI)) + tmp[1]);
                    integrator[1].update();
                    this.point[1] = integrator[1].getValue();
                    this.animation_flag = 1;

                    this.fPoint[1] = this.point[1];
                }
                p[0] = this.point[0];
                p[1] = this.point[1] + this.fHeight/2;
                for(int i = 0; i < subList.size(); i++){
                    Instance ins = subList.get(i);
                    float[] subWH = ins.getWH();
                    lineLemgth += (float)Math.sqrt((Math.pow((double)subWH[0]/2,2.0) + Math.pow((double)(subWH[1]+ ins.getfHeight() )/2,2.0))) + 20;
                    if (ins.getDrawFlag()) {
                        if ((this.id != -1) || (ins.getPList().size() == 1) && (ins.getPList().get(0).getId() == -1)) {
                            float[] subPoint = ins.getPoint();
                            papplet.stroke(0);
                            float[] cross = ins.crossPoint(this.getPoint());
                            papplet.line(movedPoint(1), movedPoint(2) + movedPoint(7) / 2, cross[0], cross[1]);
                            if(papplet.getMagnification() >= 70)
                                ins.drawArrow(cross);
                            ins.drawBox(p, (float)(360.0/(float)(this.subList.size()+1)*(i+1))+num);
                        } else {
                            ins.drawBox(p, (float)(360.0/(float)(this.subList.size()+1)*(i+1))+num);
                        }
                    } else {
                        if ((this.id != -1) || (ins.getPList().size() == 1) && (ins.getPList().get(0).getId() == -1)) {
                            float[] subPoint = ins.getPoint();
                            papplet.stroke(0);
                            float[] cross = ins.crossPoint(this.getPoint());
                            papplet.line(movedPoint(1), movedPoint(2) + movedPoint(7) / 2, cross[0], cross[1]);
                            if(papplet.getMagnification() >= 70)
                                ins.drawArrow(cross);
                        }
                    }
                }
            }

            if(this.checkArea()) {
                if(this.clickFlag){
                    papplet.stroke(0xFFFF0000);
                }else{
                    papplet.stroke(0);
                }
                papplet.textAlign(papplet.CENTER);
                papplet.fill(255);
                papplet.rect(movedPoint(1) - movedPoint(5) / 2, movedPoint(2) - movedPoint(6) / 2, movedPoint(5), movedPoint(6));
                papplet.rect(movedPoint(3) - movedPoint(5) / 2, movedPoint(4) + movedPoint(6) / 2, movedPoint(5), movedPoint(7));
                papplet.fill(0);
                this.cUpdateFlag = false;
                String str;
                if (this.isClass == 1) {
                    str = "+" + this.getClassName();
                } else {
                    str = "<id=" + this.id + ">:" + this.getClassName();
                }
                if (papplet.textWidth(str) <= movedPoint(5) + 1)
                    papplet.text(str, movedPoint(1), movedPoint(2) + papplet.textDescent());
                if (this.fieldCount != 0) {
                    papplet.textAlign(papplet.LEFT);
                    for (int i = 0; i < this.fieldCount; i++) {
                        Field fi = this.fieldList.get(i);
                        String s;
                        if (fi.getIsMethod()) {
                            switch (fi.getTextColor()) {
                                case 0:
                                    papplet.fill(0xE6, 0x00, 0x12, fi.getTextAlpha());
                                    break;
                                case 1:
                                    papplet.fill(0x1D, 0x20, 0x88, fi.getTextAlpha());
                                    break;
                                case 2:
                                    papplet.fill(0x00, 0x99, 0x44, fi.getTextAlpha());
                                    break;
                                case 3:
                                    papplet.fill(0xF3, 0x98, 0x00, fi.getTextAlpha());
                                    break;
                                case 4:
                                    papplet.fill(0x00, 0x9E, 0x96, fi.getTextAlpha());
                                    break;
                                case 5:
                                    papplet.fill(0x92, 0x07, 0x83, fi.getTextAlpha());
                                    break;
                                case 6:
                                    papplet.fill(0xE5, 0x00, 0x4F, fi.getTextAlpha());
                                    break;
                                case 7:
                                    papplet.fill(0x00, 0x68, 0xB7, fi.getTextAlpha());
                                    break;
                                case 8:
                                    papplet.fill(0x8F, 0xC3, 0x1F, fi.getTextAlpha());
                                    break;
                            }
                            s = fi.getAccess() + fi.getName() + "(";
                            for (int j = 0; j < fi.getNumArgs(); j++) {
                                s += fi.getArgs(j) + " ";
                            }
                            s = s.trim();
                            s += "):" + fi.getType();
                        } else {
                            if(fi.getClickFlag()){
                                papplet.fill(0xFFFF0000);
                            }else{
                                papplet.fill(0);
                            }
                            s = fi.getAccess() + fi.getType() + fi.getName() + "=" + fi.getValue();
                        }
                        if ((papplet.textWidth(str) <= movedPoint(5) + 1) && (this.fHeight + 1 >= javanimation.textHeight * (i + 1)))
                            papplet.text(s, movedPoint(3) - movedPoint(5) / 2, (movedPoint(4) + movedPoint(6) + 13 * i * papplet.getMagnification() / 100 + papplet.textDescent()));
                    }
                }
            }
            this.animation_flag = 0;


            integrator[0].cUpdate();
            integrator[1].cUpdate();
            integrator[2].cUpdate();
            integrator[3].cUpdate();
            integrator[4].cUpdate();

            this.point[0] = integrator[0].getValue();
            this.point[1] = integrator[1].getValue();
            this.wh[0] = integrator[2].getValue();
            this.wh[1] = integrator[3].getValue();
            this.fHeight = integrator[4].getValue();

            maxWH[0] = tmp[0];
            if (maxWH[1] <= this.point[1] + this.wh[1] / 2 + this.fHeight) {
                maxWH[1] = this.point[1] + this.wh[1] / 2 + this.fHeight;
            }
        }else{
            this.animation_flag = 0;
        }

        return maxWH;
    }

    public boolean checkArea(){
        // オブジェクト図の左辺
        if(this.movedPoint(1)-this.movedPoint(5)/2 > javanimation.drawingArea[1]){
            return false;
        }
        // 右辺
        if(this.movedPoint(1)+this.movedPoint(5)/2 < javanimation.drawingArea[0]){
            return false;
        }
        // 上辺
        if(this.movedPoint(2)-this.movedPoint(6)/2 > javanimation.drawingArea[3]){
            return false;
        }
        // 下辺
        if(this.movedPoint(4)+this.movedPoint(6)/2+this.movedPoint(7) < javanimation.drawingArea[2]){
            return false;
        }
        return true;
    }

    public float movedPoint(int i){
        float returnFloat = 0;
        if(i >= 1 && i <= 7){
            switch(i){
                case 1:
                    returnFloat = this.point[0] + javanimation.origin[0];
                    break;
                case 2:
                    returnFloat = this.point[1] + javanimation.origin[1];
                    break;
                case 3:
                    returnFloat = this.fPoint[0] + javanimation.origin[0];
                    break;
                case 4:
                    returnFloat = this.fPoint[1] + javanimation.origin[1];
                    break;
                case 5:
                    returnFloat = this.wh[0];
                    break;
                case 6:
                    returnFloat = this.wh[1];
                    break;
                case 7:
                    returnFloat = this.fHeight;
                    break;
            }
        }else{
            returnFloat = 0;
        }
        return returnFloat * (papplet.getMagnification()/100);
    }

    public void addField(Field fi){
        papplet.textSize(12);
        this.fieldList.add(fi);
        this.fieldCount++;
        this.fPoint[0] = this.point[0];
        this.fPoint[1] = this.point[1] + this.wh[1]/2;
        integrator[4].target(javanimation.textHeight*this.fieldCount);
        this.animation_flag = 1;
        if(fi.getTextLength() >= this.maxLength){
            maxLength = fi.getTextLength();
            integrator[2].target(this.maxLength);
        }
        integrator[2].update();
        integrator[4].update();
        papplet.textSize(javanimation.getFontSize()*papplet.getMagnification()/100);
    }
    public void removeField(String s){
        Field fi;
        for(int i = this.fieldCount-1; i >= 0; i--){
            fi = this.fieldList.get(i);
            if(s.equals(fi.getName())){
                fieldCount--;
                integrator[4].target(javanimation.textHeight*this.fieldCount);
                this.animation_flag = 1;
                if(fi.getTextLength() == this.maxLength){
                    papplet.textSize(12);
                    this.maxLength = papplet.textWidth("<id=" + this.id + ">:" + this.getClassName());
                    papplet.textSize(javanimation.getFontSize()*papplet.getMagnification()/100);
                    integrator[2].target(this.maxLength);
                }
                fieldList.remove(i);

                break;
            }
        }
        papplet.textSize(12);
        if(this.maxLength == papplet.textWidth("<id=" + this.id + ">:" + this.getClassName())){
            papplet.textSize(12);
            for(int i = 0; i < this.fieldList.size(); i++){
                fi = this.fieldList.get(i);
                if(fi.getTextLength() >= this.maxLength){
                    this.maxLength = fi.getTextLength();
                }
            }
            integrator[2].target(this.maxLength);
        }
        papplet.textSize(javanimation.getFontSize()*papplet.getMagnification()/100);
        integrator[2].update();
        integrator[4].update();
    }

    public void addVariable(String name, String access, String value){
        Field fi = new Field(name, access, papplet);
        this.fieldList.add(fi);
        fi.setValue(value);
        this.fieldCount++;
        this.fPoint[0] = this.point[0];
        this.fPoint[1] = this.point[1] + this.wh[1]/2;
        integrator[4].target(javanimation.textHeight*this.fieldCount);
        this.animation_flag = 1;

        if(fi.getTextLength() >= this.maxLength){
            maxLength = fi.getTextLength();
            integrator[2].target(this.maxLength);
        }
        integrator[2].update();
        integrator[4].update();
    }

    public void modificationWatchpoint(MWE mwe){
        Field fi;
        this.animation_flag = 1;
        for(int i = 0; i < this.fieldCount; i++){
            fi = this.fieldList.get(i);
            if(fi.getName().equals(mwe.getName())){
                fi.setValue(mwe.getNew());
                if(fi.getTextLength() >= this.maxLength){
                    maxLength = fi.getTextLength();
                    integrator[2].target(this.maxLength);
                }
                integrator[2].update();
                return;
            }
        }
        String access = "";
        if(mwe.isPublic()){
            access = "+";
        }else if(mwe.isPrivate()){
            access = "-";
        }else if(mwe.isProtected()){
            access = "#";
        }
        this.addVariable(mwe.getName(),access, mwe.getNew());
    }
    public void setAnimation_flag(){
        this.animation_flag = 1;
    }
    public int getAnimation_flag(){
        return this.animation_flag;
    }
    public long getId(){
        return this.id;
    }
    public float[] getPoint(){
        float[] mPoint = {movedPoint(1),movedPoint(2)+movedPoint(7)/2};
        return mPoint;
    }
    public void setPoint(float x, float y){
        integrator[0].target(x + this.wh[0]/2 + 15);
        integrator[1].target(y);

        integrator[0].update();
        integrator[1].update();
    }
    public float[] getWH(){
        return this.wh;
    }
    public  float getfHeight(){return this.fHeight;}
    public String getClassName(){
        if(this.cNameFlag){
            return this.className;
        }else{
            String[] s = this.className.split("\\.");
            return s[s.length-1];
        }
    }
    public ArrayList<Field> getFieldList(){
        return this.fieldList;
    }
    public void changeClassNameFlag(){
        this.cNameFlag = papplet.getClassNameFlag();
        papplet.textSize(12);
        String[] s = this.className.split("\\.");
        if(papplet.getClassNameFlag()){
            if(this.maxLength <= papplet.textWidth("<id=" + this.id + ">:" + this.className)){
                this.maxLength = papplet.textWidth("<id=" + this.id + ">:" + this.className);
                integrator[2].target(this.maxLength);
                integrator[2].cUpdate();
            }
        }else {
            if(this.maxLength == papplet.textWidth("<id=" + this.id + ">:" + this.className)){
                this.maxLength = papplet.textWidth("<id=" + this.id + ">:" + s[s.length-1]);
                for(Field fi : this.fieldList){
                    if(fi.getTextLength() >= this.maxLength){
                        this.maxLength = fi.getTextLength();
                    }
                }
                integrator[2].target(this.maxLength);
                integrator[2].cUpdate();
            }
        }
        papplet.textSize(javanimation.getFontSize()*papplet.getMagnification()/100);
    }

    public int getLevel(){
        return this.level;
    }
    public void setLevel(int i){
        this.level = i;
    }
    public void setPList(Instance ins){
        ins.setSList(this);
        this.parentList.add(ins);
    }
    public int getPListSize(){
        return this.parentList.size();
    }
    public void removePList(int x){
        Instance ins;
        for(int i = 0; i < this.parentList.size(); i++){
            ins = this.parentList.get(i);
            if(ins.getId() == x){
                this.parentList.remove(i);
                break;
            }
        }
    }
    public void setSList(List<Instance> list, String value){
        Instance ins;
        String[] valueAry = value.split(",");
        for(int i = 0; i < valueAry.length; i++){
            valueAry[i] = valueAry[i].substring(valueAry[i].indexOf("=") +1, valueAry[i].indexOf(">"));
            int a;
            a = Integer.parseInt(valueAry[i]);
            for(int j = 0; j < list.size(); j++){
                ins = list.get(j);
                if(a == ins.getId()){
                    boolean f = true;
                    for(Instance in : this.subList){
                        if(a == in.getId()){
                            f = false;
                            break;
                        }
                    }
                    if(f) {
                        ins.setPList(this);
                        ins.setLevel(ins.getLevel() + 1);
                    }
                    break;
                }
            }
        }
    }
    public void setSList(Instance ins){
        this.subList.add(ins);
    }
    public void removeSList(){
        Instance ins;
        for(int i = 0; i < this.subList.size(); i++){
            ins = this.subList.get(i);
            ins.removePList((int)this.id);
        }
        this.subList.clear();
    }
    public ArrayList getSList(){
        return this.subList;
    }
    public ArrayList<Instance> getPList(){
        return this.parentList;
    }
    public void setShowFlag (boolean f){
        this.showFlag = f;
        if(!this.showFlag)
            this.animation_flag=0;
    }
    public  boolean getShowFlag(){
        return this.showFlag;
    }
    public int getMethodCount(){
        int count=0;
        for (Field fi : fieldList){
            if(fi.getIsMethod())
                count++;
        }
        return count;
    }
    public void setTextColor(long threadId, boolean f){ // fがtrueなら色を濃く、falseなら薄く
        for(Field fi : fieldList){
            if(threadId == fi.getThreadId()){
                fi.changeTextAlpha(f);
            }
        }
    }
    public void setTextColor(){
        Field fi = fieldList.get(fieldList.size()-1);
        fi.setTextColor(0xFFFF0000);
    }
    public void setDrawFlag() { this.drawFlag = true;}
    public boolean getDrawFlag() { return this.drawFlag;}
    public float[] crossPoint(float[] a){ //親の点
        float[] cross = {0,0};
        if((a[0] > this.movedPoint(1)) && ((a[1] > this.liner(a[0],1)) && (a[1] < this.liner(a[0],2)))){ // 点が箱の右
            cross[0] = this.movedPoint(1)+this.movedPoint(5)/2;
            float y1 = (this.movedPoint(5)/2)*(a[1]-this.movedPoint(2)-this.movedPoint(7)/2)/(a[0]-this.movedPoint(1));
            cross[1] = this.movedPoint(2)+this.movedPoint(7)/2+y1;
        }else if((a[1] < this.liner(a[0],1)) && (a[1] > this.liner(a[0],2))){ // 左
            cross[0] = this.movedPoint(1)-this.movedPoint(5)/2;
            float y1 = (this.movedPoint(5)/2)*(a[1]-this.movedPoint(2)-this.movedPoint(7)/2)/(this.movedPoint(1)-a[0]);
            cross[1] = this.movedPoint(2)+this.movedPoint(7)/2+y1;
        }else if(a[1] > this.movedPoint(2)+this.movedPoint(7)/2){ // 下
            cross[1] = this.movedPoint(2)+this.movedPoint(6)/2+this.movedPoint(7);
            float x1 = ((this.movedPoint(6)+this.movedPoint(7))/2)*(a[0]-this.movedPoint(1))/(a[1]-this.movedPoint(2)-this.movedPoint(7)/2);
            cross[0] = this.movedPoint(1)+x1;
        }else { // 上
            cross[1] = this.movedPoint(2)-this.movedPoint(6)/2;
            float x1 = ((this.movedPoint(6)+this.movedPoint(7))/2)*(a[0]-this.movedPoint(1))/(this.movedPoint(2)+this.movedPoint(7)/2-a[1]);
            cross[0] = this.movedPoint(1)+x1;
        }

        return cross;
    }
    public float liner(float x, int f){ // 中点と頂点を結ぶ直線の方程式
        float x2,y2;
        float[] p1 = this.getPoint();
        if(f == 1) { // 傾き＋
            y2 = this.movedPoint(2) - this.movedPoint(6) / 2;
            x2 = this.movedPoint(1) + this.movedPoint(5) / 2;
            return ((y2-p1[1])/(x2-p1[0])*(x-p1[0])+p1[1]);
        }else if(f == 2){ //  傾きー
            y2 = this.movedPoint(2) + this.movedPoint(6)/2 + this.movedPoint(7);
            x2 = this.movedPoint(1)+ this.movedPoint(5)/2;
            return ((y2-p1[1])/(x2-p1[0])*(x-p1[0])+p1[1]);
        }
        return 0;
    }
    public void drawArrow(float[] cross) { // 矢印の描画
        float dx = cross[0] - this.movedPoint(1);
        float dy = cross[1] - (this.movedPoint(2)+movedPoint(7)/2);
        float theta = papplet.atan2(dy,dx);
        papplet.line(cross[0],cross[1],10*papplet.cos(theta+papplet.radians(20))+cross[0],10*papplet.sin(theta+papplet.radians(20))+cross[1]);
        papplet.line(cross[0],cross[1],10*papplet.cos(theta-papplet.radians(20))+cross[0],10*papplet.sin(theta-papplet.radians(20))+cross[1]);
    }
    public void setMode(int i){
        mode = i;
    }

    public void updateField(MEE mee){
        for(int i = 0; i < mee.getNumFields(); i++){
            if(!(mee.getFieldValue(i).equals(this.fieldList.get(i).getValue()))){
                this.fieldList.get(i).setValue(mee.getFieldValue(i));
            }
        }
    }

    public void updateField(MXE mxe){
        for(Field field : fieldList){
            for(int i = 0; i < mxe.getNumFields(); i++){
                if(mxe.getFieldName(i).equals(field.getName())){
                    if(!(mxe.getFieldValue(i).equals(field.getValue()))){ // ここにOCDのブレークポイント処理を追加する
                        field.setValue(mxe.getFieldValue(i));
                        this.fPoint[0] = this.point[0];
                        this.fPoint[1] = this.point[1] + this.wh[1]/2;
                        integrator[4].target(javanimation.textHeight*this.fieldCount);
                        this.animation_flag = 1;

                        if(field.getTextLength() >= this.maxLength){
                            maxLength = field.getTextLength();
                            integrator[2].target(this.maxLength);
                        }
                        integrator[2].update();
                        integrator[4].update();
                    }
                }
            }
        }
    }

    public boolean checkInstanceClick(float mouseX, float mouseY){
        if(this.checkArea()) {
            if ((mouseX >= movedPoint(1)-movedPoint(5)/2) && (mouseX <= movedPoint(1)+movedPoint(5)/2)) {
                if((mouseY >= movedPoint(2)-movedPoint(6)/2) && (mouseY <= movedPoint(4)+movedPoint(6)/2+movedPoint(7))) {
                    System.out.println("インスタンス：" + this.id);
                    this.clickFlag = !this.clickFlag;
                    float y = mouseY - (movedPoint(2)+movedPoint(6)/2);
                    if(y <= 0){ // id部分をクリック
                        return true;
                    } else{
                        this.fieldList.get((int)(y / (movedPoint(7)/this.fieldCount))).checkFieldClick();
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
