package javanimation;

import jdi2diagram.event_info.ModifierInfo;
import jdi2diagram.event_info.method_event.MethodEntryEventInfo;
import jdi2diagram.event_info.method_event.MethodExitEventInfo;
import jdi2diagram.event_info.prepare_event.ClassPrepareEventInfo;
import processing.core.*;
import java.util.*;
import java.io.*;
import javax.swing.event.*;
import javax.swing.*;
import swingAnime.*;
import java.text.SimpleDateFormat;

/**
 * Created by shou on 2014/11/02.
 */
public class javanimation extends PApplet implements ChangeListener{
    int mode = 0;
    int filePage = 0;
    int nowPage = 0;

    AnimePanel ap;
    JSlider slider;
    File file;
    String selectedFile;
    Option option;
    PImage[] button = new PImage[4];
    PImage[] bar = new PImage[2];
    PImage circle;
    PImage setting;
    PFont font;
    static float textHeight;
    static int play_flag = 1;  // １なら実行中,0なら停止中
    static int animation_flag = 0; //アニメーションが動いている最中は1
    boolean barMoveFlag = false;
    static int lastEventFlag = 0;
    int exeFlag = 1;

    static float[] origin = {0,0};
    float[] prevPoint = {0,0};
    boolean dMoveFlag = false;
    static float fontsize = 12;
    static int classPrepare_flag = 1;
    public static String currentEvent = "";
    static float[] drawingArea = {0,0,0,0}; // 描画処理を行う範囲 x1,x2,y1,y2

    //ArrayList<Instance> instanceList;
    List<Instance> instanceList = Collections.synchronizedList(new ArrayList<Instance>());
    ArrayList<Field> fieldList;
    ArrayList<Event> methodList;
    ArrayList<Event> eventList;
    ArrayList<Token> tokenList;

    int count = 0;
    int delayTime = 40;
    int delayTmp = delayTime;
    Integrator integrator = new Integrator(delayTime);
    int step = 0;
    boolean nextSign = true;

    String data = "";
    PrintWriter pw;
    private boolean logFlag = false;

    public javanimation(AnimePanel ap){
        this.ap = ap;
    }

    public void setup() {
        size(800,630);
        drawingArea[0] = drawingArea[2] = 0;
        drawingArea[1] = 800;
        drawingArea[3] = 630;
        System.out.println(drawingArea[3]);

        file = new File("C:\\Users\\shou\\IdeaProjects\\javanimation\\src\\data\\log\\workerthredlog" +".txt");//dump.txtまでの絶対パス
        option = new Option(this);
        button[0] = loadImage("play.png");
        button[1] = loadImage("stop.png");
        //button[2] = loadImage("btn055_01.gif");
        //button[3] = loadImage("btn055_06.gif");
        //bar[0] = loadImage("white.png");
        //bar[1] = loadImage("red.png");
        //circle = loadImage("point.png");
        setting = loadImage("setting.png");
        font = createFont("MS-Gothic", fontsize);
        textFont(font,fontsize);
        textHeight = textAscent();

        instanceList = new ArrayList<Instance>();
        fieldList = new ArrayList<Field >();
        methodList = new ArrayList<Event>();
        eventList = new ArrayList<Event>();
        tokenList = new ArrayList<Token>();

        noLoop();
    }

    public void draw(){
        long start = System.nanoTime();
        switch (mode) {
            case 0:
                break;
            case 1: // バッチモード
                if (dMoveFlag) {
                    origin[0] += mouseX - prevPoint[0];
                    origin[1] += mouseY - prevPoint[1];
                    prevPoint[0] = mouseX;
                    prevPoint[1] = mouseY;
                }
                for (Instance ins : instanceList) {
                    ins.setDrawFlag();
                }

                event();
                drawBack();
                textSize(fontsize * option.getMagnification() / 100);
                drawBox();
                drawToken();
                drawBack2();

                fill(0);
                textAlign(LEFT, CENTER);
                textSize(12);
                text(currentEvent, 720, 590);
                //text(Boolean.toString(dMoveFlag),600,450);

                animation_flag = 0;
                for (int i = 0; i < instanceList.size(); i++) {
                    Instance ins = instanceList.get(i);
                    if (ins.getAnimation_flag()==1 && !ins.getDrawFlag()) {
                        animation_flag = 1;
                    }
                    if ((ins.getPListSize() >= 3) || (ins.getPListSize() == 2) && (ins.getPList().get(0).getId() != -1)) {
                        ins.setMode(1);
                    }
                }
                if (animation_flag == 0 && play_flag == 1) {
                    exeFlag = 1;
                }
                break;
            case 2: // リアルタイムモード
                if (dMoveFlag) {
                    origin[0] += mouseX - prevPoint[0];
                    origin[1] += mouseY - prevPoint[1];
                    prevPoint[0] = mouseX;
                    prevPoint[1] = mouseY;
                }
                for (Instance ins : instanceList) {
                    ins.setDrawFlag();
                }
                event();
                background(0xFFF8FF8F);
                textSize(fontsize * option.getMagnification() / 100);
                drawBox();
                drawToken();
                drawBack2();
                fill(0);
                textAlign(LEFT, CENTER);
                textSize(12);
                text(currentEvent, 720, 590);
                //text(Boolean.toString(nextSign), 720, 590);
                animation_flag = 0;
                for (int i = 0; i < instanceList.size(); i++) {
                    Instance ins = instanceList.get(i);
                    if (ins.getAnimation_flag()==1 && !ins.getDrawFlag()) {
                        animation_flag = 1;
                    }
                    if ((ins.getPListSize() >= 3) || (ins.getPListSize() == 2) && (ins.getPList().get(0).getId() != -1)) {
                        ins.setMode(1);
                    }
                }
                if (animation_flag == 0 && play_flag == 1) {
                    exeFlag = 1;
                    if(step == eventList.size()) {
                        this.nextSign = true;
                    }
                }
                break;
            case 3:   // バッチファイル作成モード
                drawBack();
                break;
        }
        //System.out.println((System.nanoTime() - start)  + "ns");
    }

    public void drawBack(){
        background(0xFFF8FF8F);
        fill(0xffff0000);
        //rect(0,0,800,630);
    }
    public void drawBack2(){//通常の描画部分(800x600)の外の部分
        fill(0xFFF8FF8F);
        stroke(0);
        textSize(12);
        textAlign(LEFT,CENTER);
        //rect(0,600,800,630);

        image(button[play_flag], 15, 460);
        image(setting, 15,520,50,50);

        fill(0);
        imageMode(CORNER);
    }

    public void drawBox(){
        Instance ins;
        float[] wh = {0,0};
        if(instanceList.size() >= 1){
            ins = instanceList.get(0);
            ins.drawBox(wh,0);
            ins.setMode(0);
        }
    }
    public void drawToken(){
        long[] list;
        list = new long[tokenList.size()];
        for(int i = 0; i<tokenList.size(); i++) {
            list[i] = 0;
        }
        int i =0;
        for(Token token : tokenList) {
            for (Instance ins : instanceList) {
                if (token.getInstansId() == ins.getId()) {
                    float[] p = ins.getPoint();
                    int cnt =0;
                    for(int j = 0; j < i; j++) {
                        if(token.getInstansId() == list[j]) {
                            if((cnt % 2) == 0){
                                p[1] += 3*getMagnification() / 10;
                            }else {
                                p[0] += 3*getMagnification() / 10;
                                p[1] -= 3*getMagnification() / 10;
                            }
                            cnt++;
                        }
                    }
                    list[i] = token.drawToken(p);
                    i++;
                }
            }
        }
    }
    public void setToken(Event ev){
        if(ev instanceof MXE){
            for (Token token : tokenList) {
                if (token.getThreadId() == ev.getThreadId()) {
                    Event method;
                    for(int i = methodList.size()-1; i >= 0; i--){
                        method = methodList.get(i);
                        if(ev.getThreadId() == token.getThreadId()){
                            for (Instance ins : instanceList) {
                                if (ins.getId() == method.getId()) {
                                    token.setInstansId(ins.getId());
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    public void event(){
        if((exeFlag == 1)&&(animation_flag == 0)&&(play_flag==1)){
            if(step < eventList.size()){
                if(count >= delayTime){
                    if(barMoveFlag){
                        barMoveFlag=false;
                        play_flag=0;
                        count=0;
                        return;
                    }
                    Event ev = eventList.get(step);
                    ev.drawEvent();
                    if(ev instanceof MEE) {
                        setToken(ev);
                    }
                    if((ev instanceof MXE) && !((MXE) ev).isConstructor())
                        System.out.print("");
                        //checkInstance(ev.getId());
                    count=0;
                    exeFlag=0;
                    step++;
                    slider.setValue(step);
                }else{
                    count++;
                }
            }else {
                currentEvent = "Finished";
            }
        }
    }

    public void checkInstance(long mxId){
        for(Instance ins : instanceList){
            if(ins.getId() == mxId) {
                boolean f = false;
                for(int i = 0; i < ins.getPList().size(); i++){
                    Instance pIns = ins.getPList().get(i);
                    if((ins.getPList().size() ==1) && (pIns.getId() == -1)) {
                        f = false;
                        break;
                    }
                    if(pIns.getMethodCount()!=0) {
                        f = false;
                        break;
                    }
                    else
                        f = true;
                }
                if(f)
                    ins.setShowFlag(false);
                break;
            }
        }
    }

    public void goForward(int n){
        for(; step < n; step++){
            Event ev = eventList.get(step);
            if(ev.getName().equals("main") && (ev instanceof MXE) && lastEventFlag == 0)
                lastEventFlag = 1;
            //if(lastEventFlag != 1)
            ev.drawEvent();
            if((ev instanceof MXE) && !((MXE) ev).isConstructor())
                System.out.print("");
                //checkInstance(ev.getId());
        }
        barMoveFlag=true;
    }

    public void goBack(int n){
        reset();

        for(; step < n; step++) {
            Event ev = eventList.get(step);
            if (ev.getName().equals("main") && (ev instanceof MXE) && lastEventFlag == 0)
                lastEventFlag = 1;
            if (lastEventFlag != 1)
                ev.drawEvent();
            if ((ev instanceof MXE) && !((MXE) ev).isConstructor())
                System.out.print("");
            //checkInstance(ev.getId());
        }
        barMoveFlag=true;
    }

    public void reset(){
        Instance ins = instanceList.get(0);
        ins.setMode(0);
        step=0;
        slider.setValue(0);
        count =0;
        methodList.clear();
        instanceList.clear();
        fieldList.clear();
        tokenList.clear();
        animation_flag=0;
        play_flag=1;
        lastEventFlag=0;
        option.setCurrentInstance(0);
    }

    public void mousePressed(){
        // ボタンからの距離が25以下、つまりボタン上でクリックされたら
        // 再生、停止を切り替える
        System.out.println(mouseX + ", " + mouseY);
        boolean flag = true;
        float d = dist(40, 485, mouseX, mouseY);
        if (d <= 25) {
            flag = false;
            if (play_flag == 0)
                play_flag = 1;
            else
                play_flag = 0;
        }

        for(Token token : tokenList){
            if(token.checkTokenClick(mouseX, mouseY)){
                return;
            }
        }

        for(Instance ins : instanceList){
            if(ins.checkInstanceClick(mouseX, mouseY)){
                return;
            }
        }

        flag = option.optionPressed(flag);

        if(flag){
            prevPoint[0] = mouseX;
            prevPoint[1] = mouseY;
            dMoveFlag = true;
        }
    }

    public void mouseReleased(){
        dMoveFlag = false;
    }

    public void keyPressed(){
        if (key == ' ') {
            origin[0] = 0;
            origin[1] = 0;
            drawingArea[0] = drawingArea[2] = 0;
            drawingArea[1] = 800;
            drawingArea[3] = 630;
            option.setMagnification();
        }
        if (key == '\n') {
            lastEventFlag = 2;
        }
        if (key == 'p') {
            save("image.png");
        }
        if (keyCode == SHIFT) {
            reset();
        }
        if(keyCode == DELETE){
            reset();
            size(800,630);
            mode = 0;
            eventList.clear();
            ap.pc();
        }
        if (key == 'a') {
            for (Instance ins : instanceList) {
                System.out.print(ins.getId() + " : ");
                for (int i = 0; i < ins.getPList().size(); i++) {
                    Instance in = ins.getPList().get(i);
                    System.out.print(in.getId() + ",");
                }
                System.out.println();
            }
        }
    }

    public void loadLogFile(String fileName){
        file = new File("C:\\Users\\shou\\IdeaProjects\\javanimation\\src\\data\\log\\"+fileName);
        mode = 1;

        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String line;
            while((line = bufferedReader.readLine()) != null){
                String str[] = line.split("#");

                if(str[0].equals("ME")){
                    eventList.add(new MEE(str, instanceList, methodList, tokenList, this));
                }else if(str[0].equals("MX")){
                    eventList.add(new MXE(str, instanceList, methodList, tokenList, this));
                }else if(str[0].equals("MW")){
                    eventList.add(new MWE(str, instanceList, this));
                }
            }
            play_flag = 1;
            slider.setMaximum(eventList.size());
            loop();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try{
                bufferedReader.close();
                fileReader.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void pressPlayButton(){
        if (play_flag == 0)
            play_flag = 1;
        else
            play_flag = 0;
    }

    public void pressClassButton(){option.pressClassButton();}
    public void pressTokenButton(){option.pressTokenButton();}
    public void pressZoomUp(){option.pressZoomUp();}
    public void pressZoomDown(){option.pressZoomDown();}
    public void changeDelay(int n){delayTime = n;}

    public long getTokenInstanceID(long thredId){
        long id = 0;
        for(Token token : tokenList){
            if(token.getThreadId() == thredId){
                id = token.getInstansId();
                break;
            }
        }
        return id;
    }

    public static float getFontSize() {
        return fontsize;
    }
    public boolean getTokenFlag(){ return option.getTokenFlag();}
    public float getMagnification(){ return option.getMagnification();}
    public boolean getClassNameFlag(){ return option.getClassNameFlag();}
    public void setCurrentInstance(long i){ option.setCurrentInstance(i);}
    public int getAnimation_flag(){ return animation_flag;}
    public void setAnimation_flag(int i){ animation_flag = i;}
    public long getCurrentInstance(){ return option.getCurrentInstance();}
    public void getSlider(JSlider s){
        slider = s;
    }

    public void stateChanged(ChangeEvent e){
        noLoop();
        JSlider source = (JSlider)e.getSource();
        if (!slider.getValueIsAdjusting()) {
            if(step < source.getValue()){
                goForward(source.getValue());
            }else if(step > source.getValue()){
                goBack(source.getValue());
            }
            redraw();
            loop();
        }
    }

    public boolean getNextSign(){
        return this.nextSign;
    }
    public void setMode(int n){
        this.mode = n;
    }

    public void sentString(String line){
        if(line != null) {
            nextSign = false;
            //data += line + "\n";
            //loop();
            pw.println(line);
            System.out.println(line);
            nextSign = true;
        }
    }
    public void sentObject(ModifierInfo modifierInfo){
        if (modifierInfo instanceof ClassPrepareEventInfo) {
        } else if (modifierInfo instanceof MethodEntryEventInfo) {
            eventList.add(new MEE((MethodEntryEventInfo) modifierInfo, instanceList, methodList, tokenList, this));
            nextSign = false;
        } else if (modifierInfo instanceof MethodExitEventInfo) {
            eventList.add(new MXE((MethodExitEventInfo) modifierInfo, instanceList, methodList, tokenList, this));
            nextSign = false;
        }
        play_flag = 1;
        slider.setMaximum(eventList.size());
        loop();
    }



    public void createBFile(){
        try{
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            file = new File("C:\\Users\\shou\\IdeaProjects\\javanimation\\src\\data\\log\\"+ sdf.format(date) + ".txt");
            System.out.println("C:\\Users\\shou\\IdeaProjects\\javanimation\\src\\data\\log\\"+ sdf.format(date) + ".txt");

            pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            this.logFlag = true;

        }catch(IOException e){
            System.out.println(e);
        }
    }
    public void closeBFile(){
        if(this.logFlag){
            pw.close();
        }
    }
}
