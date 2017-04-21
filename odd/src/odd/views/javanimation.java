package odd.views;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.jdt.internal.debug.core.IJDIEventListener;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.request.AccessWatchpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.ModificationWatchpointRequest;

import jdi2diagram.event_info.ModifierInfo;
import jdi2diagram.event_info.method_event.MethodEntryEventInfo;
import jdi2diagram.event_info.method_event.MethodExitEventInfo;
import jdi2diagram.event_info.prepare_event.ClassPrepareEventInfo;
import odd.ODDMethodExitEventListener;
import odd.OddAccessWatchpointEventListener;
import odd.OddClassPrepareEventListener;
import odd.OddMethodEntryEventListener;
import odd.OddMethodEventListener;
import odd.OddModificationWatchpointEventListener;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;


/**
 * Created by shou on 2014/11/02.
 */
public class javanimation extends PApplet implements ChangeListener{
    int mode = 0;
    int filePage = 0;
    int nowPage = 0;

    MainPanel mainPanel;
    JSlider slider;
    File file;
    String selectedFile;
    Option option;
    PImage[] bar = new PImage[2];
    PImage circle;
    PFont font;
    static float textHeight;
    static int play_flag = 0;  // １なら実行中,0なら停止中
    static int animation_flag = 0; //アニメーションが動いている最中は1
    boolean barMoveFlag = false;
    static int lastEventFlag = 0;
    int exeFlag = 1;

    static float[] origin = {0,0};
    float[] prevPoint = {0,0};
    int cameraNo = -1; // 0は通常描画、1～はそれぞれのトークンを中心に据える
    boolean dMoveFlag = false;
    static float fontsize = 12;
    static int classPrepare_flag = 1;
    public static String currentEvent = "";
    static float[] drawingArea = {0,0,0,0}; // 描画処理を行う範囲 x1,x2,y1,y2


    List<Instance> instanceList = Collections.synchronizedList(new ArrayList<Instance>());
    ArrayList<Field> fieldList;
    ArrayList<Event> methodList;
    ArrayList<Event> eventList;
    ArrayList<Token> tokenList;
    ArrayList<CPE> classList;
    static ArrayList<String> oddObjects;
    static ArrayList<String> oddMethods;
    static ArrayList<String> oddLinks;
    public OddDiagram diagram = null;
    private static String[] Excludes = { "java.*", "javax.*", "sun.*", "com.sun.*", "jdk.*" };
    private List<IJDIEventListener> listenerList;
    private List<IJDIEventListener> fieldListenerList;
    private boolean requested = false;

    int count = 0;
    int delayTime = 40;
    int delayTmp = delayTime;
    Integrator integrator = new Integrator(delayTime);
    int step = 0;
    boolean nextSign = false;
    boolean maximumFlag = false; // slider.setMaximum()を呼んだ時にtrueにしてGoBack()を回避

    String data = "";
    PrintWriter pw;
    private boolean logFlag = false;

    public javanimation(MainPanel mainPanel){
        this.mainPanel = mainPanel;
    }

    public void setup() {
        size(800,630);
        drawingArea[0] = drawingArea[2] = 0;
        drawingArea[1] = 800;
        drawingArea[3] = 630;

        file = new File("C:\\Users\\shou\\IdeaProjects\\javanimation\\src\\data\\log\\workerthredlog" +".txt");//dump.txtまでの絶対パス
        option = new Option(this);
        font = createFont("MS-Gothic", fontsize);
        textFont(font,fontsize);
        textHeight = textAscent();

        //instanceList = new ArrayList<Instance>();
        fieldList = new ArrayList<Field >();
        methodList = new ArrayList<Event>();
        eventList = new ArrayList<Event>();
        tokenList = new ArrayList<Token>();
        classList = new ArrayList<CPE>();
        fieldListenerList = new ArrayList<>();

        background(0xFFF8FF8F);
        noLoop();
    }

    public synchronized void draw(){
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
                //text(delayTime,600,450);

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
            	long start = System.nanoTime();
                if (dMoveFlag) {
                    origin[0] += mouseX - prevPoint[0];
                    origin[1] += mouseY - prevPoint[1];
                    prevPoint[0] = mouseX;
                    prevPoint[1] = mouseY;
                }
                /* オブジェクト情報更新 */
                if(diagram == null){
                	return;
                }
                //diagram.draw(this); // 一応残しておく

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
                //text(delayTime, 720, 590);
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
                        resumeAll();
                    }
                }
                long end = System.nanoTime();
                //System.out.println("############DrawTime ########## : " + (end - start));
                break;
            case 3:   // バッチファイル作成モード
                drawBack();
                break;
            case 4:   // ブレークポイント設定モード
            	if (dMoveFlag) {
                    origin[0] += mouseX - prevPoint[0];
                    origin[1] += mouseY - prevPoint[1];
                    prevPoint[0] = mouseX;
                    prevPoint[1] = mouseY;
                }
                for (Instance ins : instanceList) {
                    ins.setDrawFlag();
                }
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
        }
    }

    public void drawBack(){
        background(0xFFF8FF8F);
    }
    public void drawBack2(){//通常の描画部分(800x600)の外の部分
        fill(0xFFF8FF8F);
        stroke(0);
        textSize(12);
        textAlign(LEFT,CENTER);

        //image(button[play_flag], 680, 15);
        //image(setting, 740,15,50,50);

        fill(0);
        imageMode(CORNER);
    }

    public void drawBox(){
        float[] wh = {0,0};
        if(instanceList.size() >= 1){
        	if(mode == 2 || mode == 4){
        		ArrayList<Instance> copy = new ArrayList<>();
        		copy.addAll(instanceList);
        		for(Instance ins : copy){
        			if(ins.isRoot()){
        				wh = ins.drawBox(wh, 0);
        			}
        			if(ins.getDrawFlag()){
        				wh = ins.drawBox(wh, 0);
        			}
        		}
        	}else{
                Instance ins;
	            ins = instanceList.get(0);
	            ins.drawBox(wh,0);
	            ins.setMode(0);
        	}
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
                    list[i] = token.drawToken(p, i);
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
                		barMoveFlag = false;
                		play_flag = 0;
                		count = 0;
                		return;
                	}
                    Event ev = eventList.get(step);
                    if(mode == 2){// ここにブレークポイント判定処理を追加 引っかかったらplay_flagを0に
                    	checkBreakPoint(ev);
                    }
                    ev.drawEvent();
                    if(ev instanceof MEE) {
                        setToken(ev);
                    }
                    if((ev instanceof MXE) && !((MXE) ev).isConstructor())
                        System.out.print("");
                        //checkInstance(ev.getId());
                    if(tokenList.isEmpty()){
                    	Token.countToken = 0;
                    }
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

    public boolean checkBreakPoint(Event ev){
    	ArrayList<String[]> breakList;
    	if(ev instanceof MEE){ // メソッドブレークの判定
        	breakList = mainPanel.getMethodBreakPoint();
        	for(String[] breakInfo : breakList){
        		if(methodList.isEmpty() && breakInfo[0].equals("*****")){
        			String[] methodInfo = {};
        			if(!breakInfo[1].equals("*****")){
        				methodInfo = breakInfo[1].split("\\(")[0].split(".");
        			}
        			if(breakInfo[1].equals("*****") || ev.getMethodName().equals(methodInfo[methodInfo.length-1])){
            			if(breakInfo[2].equals("*****") || ev.getClassName().equals(breakInfo[2]) || ev.getId() == Long.parseLong(breakInfo[2])){//呼び出し先チェック
            				if(!breakInfo[3].equals("*****")){
                				methodInfo = breakInfo[3].split("\\(")[0].split("\\.");
                			}
            				if(breakInfo[3].equals("*****") || ev.getMethodName().equals(methodInfo[methodInfo.length-1])){
                				if(breakInfo[4].equals("-----")){
                					play_flag = 0;
                					return true;
                				}
                				for(int i = 0; i < ev.getNumArgs(); i++){
                					String className = ((MEE) ev).getArgType(i);
                					if(className.equals(breakInfo[4])){
                						play_flag = 0;
                    					return true;
                					}
                				}
                				for(int i = 0; i < ev.getNumArgs(); i++){
                					String argsValue = ((MEE) ev).getArgValue(i);
                					if(argsValue.contains("id=" + breakInfo[4])){
                						play_flag = 0;
                    					return true;
                					}
                				}
            				}
            			}
        			}
        		}else if(breakInfo[0].equals("*****") || methodList.get(methodList.size()-1).getClassName().equals(breakInfo[0]) || methodList.get(methodList.size()-1).getId() == Long.parseLong(breakInfo[0])){// 呼び出し元チェック
        			String[] methodInfo = null;
        			if(!breakInfo[1].equals("*****")){
        				methodInfo = breakInfo[1].split("\\(")[0].split(".");
        			}
        			if(breakInfo[1].equals("*****") || ev.getMethodName().equals(methodInfo[methodInfo.length-1])){
            			if(breakInfo[2].equals("*****") || ev.getClassName().equals(breakInfo[2]) || ev.getId() == Long.parseLong(breakInfo[2])){//呼び出し先チェック
            				if(!breakInfo[3].equals("*****")){
            					String tmp = breakInfo[3].split("\\(")[0];
                				methodInfo = tmp.split("\\.");
                			}
            				if(breakInfo[3].equals("*****") || ev.getMethodName().equals(methodInfo[methodInfo.length-1])){
                				if(breakInfo[4].equals("-----")){
                					play_flag = 0;
                					return true;
                				}
                				for(int i = 0; i < ev.getNumArgs(); i++){
                					String className = ((MEE) ev).getArgType(i);
                					if(className.equals(breakInfo[4])){
                						play_flag = 0;
                						return true;
                					}
                				}
                				for(int i = 0; i < ev.getNumArgs(); i++){
                					String argsValue = ((MEE) ev).getArgValue(i);
                					if(argsValue.contains("id=" + breakInfo[4])){
                						play_flag = 0;
                						return true;
                					}
                				}
            				}
            			}
        			}
        		}
        	}
    	}/*else if(ev instanceof MXE){ // フィールドブレークの判定
    		breakList = mainPanel.getFieldBreakPoint();
    		for(String[] breakInfo : breakList){
    			if(breakInfo[4].equals("Object") && Long.parseLong(breakInfo[0].split(":")[1]) == ev.getId() && breakInfo[2].equals("true")){ // ターゲットがオブジェクト
        			for(Instance ins : instanceList){
        				if(ins.getId() == ev.getId()){
        					for(Field insField : ins.getFieldList()){
        						if(insField.getName().equals(breakInfo[0].split(":")[0])){
            						for(int i = 0; i < ev.getNumFields(); i++){
            							if(((MXE) ev).getFieldName(i).equals(insField.getName()) && !((MXE) ev).getFieldValue(i).equals(insField.getFullValue())){
            								play_flag = 0;
            								return true;
            							}
            						}
            						break;
        						}
        					}
        					break;
        				}
        			}
        		}else if(breakInfo[4].equals("Class") && breakInfo[0].split(":")[1].equals(ev.getClassName()) && breakInfo[2].equals("true")){ // ターゲットがクラス
        			for(Instance ins : instanceList){
        				if(ins.getId() == ev.getId()){
        					for(Field insField : ins.getFieldList()){
        						if(insField.getName().equals(breakInfo[0].split(":")[0])){
            						for(int i = 0; i < ev.getNumFields(); i++){
            							if(((MXE) ev).getFieldName(i).equals(insField.getName()) && !((MXE) ev).getFieldValue(i).equals(insField.getFullValue())){
            								play_flag = 0;
            								return true;
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
    	}*/
    	return false;
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
            ev.drawEvent();
            if((ev instanceof MXE) && !((MXE) ev).isConstructor())
                System.out.print("");
                //checkInstance(ev.getId());
        }
        slider.setValue(step);
        play_flag = 1;
        barMoveFlag = true;
    }

    public void goBack(int n){
        reset();

        if(mode == 2){
        	for(int i = oddMethods.size()-1; i >= 0; i--){
        		methodList.add(new MEE(oddMethods.get(i), instanceList, methodList, tokenList, this));
        	}
        	for(String oddObject : oddObjects) {
        		String[] objectData = oddObject.split("#");
        		Instance ins = new Instance(objectData, this);
        		instanceList.add(ins);
        	}
        	for(int i = 0; i < methodList.size(); i++){
        		MEE mee = (MEE)methodList.get(i);
        		for(Instance ins : instanceList){
    				if(ins.getId() == mee.getId()){
    					Field fi = new Field(mee, this);
    					Token t = mee.setToken(ins);
    	                for(Instance instance : instanceList){
    	                    instance.setTextColor(mee.getThreadId(), false); // 薄くしている
    	                }
    	                fi.setTextColor(t.getTokenColor());
    	                ins.addField(fi);
    	                ins.updateField(mee);
    				}
        		}
        	}
        	for(String s : oddLinks){
        		String[] linkData = s.split("->");
        		for(Instance ins : instanceList){
        			if(ins.getId() == Long.parseLong(linkData[1])){
        				ins.setRoot(false);
        				for(Instance parent : instanceList){
        					if(parent.getId() == Long.parseLong(linkData[0])){
        						ins.setPList(parent);
        						break;
        					}
        				}
        				break;
        			}
        		}
        	}
        }

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
        slider.setValue(step);
        play_flag = 1;
        barMoveFlag = true;
    }

    public void reset(){
        //Instance ins = instanceList.get(0);
        //ins.setMode(0);
        step=0;
        slider.setValue(0);
        count =0;
        methodList.clear();
        instanceList.clear();
        fieldList.clear();
        Token.countToken = 0;
        tokenList.clear();
        animation_flag=0;
        play_flag=1;
        lastEventFlag=0;
        option.setCurrentInstance(0);
    }

    public void returnMenu(){
    	reset();
    	removeMethodListener();
    	eventList.clear();
    }

    public void mousePressed(){
        // ボタンからの距離が25以下、つまりボタン上でクリックされたら
        // 再生、停止を切り替える
        System.out.println(mouseX + ", " + mouseY);
        boolean flag = true;

        for(Token token : tokenList){
            if(token.checkTokenClick(mouseX, mouseY)){
                return;
            }
        }

        for(Instance ins : instanceList){
            if(ins.checkInstanceClick(mouseX, mouseY)){
            	if(mode == 4){
            		if(mainPanel.getWateState() == 4){
            			return;
            		}
            		mainPanel.setObjectName(String.valueOf(ins.getId()));
            	}
                return;
            }
        }

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
            cameraNo = 0;
            drawingArea[0] = drawingArea[2] = 0;
            //drawingArea[1] = 800;
            //drawingArea[3] = 630;
            changeFrameSize(width, height);
            option.setMagnification();
        }
        if (key == '\n') {
            lastEventFlag = 2;
        }
        if (key == 'p') {
            save("image.png");
        }
        if (keyCode == SHIFT) {
            cameraNo++;
            if(cameraNo < tokenList.size()){
            	Token token = tokenList.get(cameraNo);
            	origin[0] = width / 2 - token.getPoint()[0];
            	origin[1] = height / 2 - token.getPoint()[1];
            }else{
            	cameraNo = -1;
            	origin[0] = 0;
            	origin[1] = 0;
            }
        }
        if(keyCode == DELETE){
            reset();
            size(800,630);
            mode = 0;
            eventList.clear();
            //ap.pc();
        }
        if (key == 'a') {
            for (Instance ins : instanceList) {
                System.out.print(ins.getId() + " : ");
                for(Field fi : ins.getFieldList()){
                	System.out.print(fi.getName() + "  ");
                }
                System.out.println();
            }
            for(Event event : eventList){
            	System.out.println(event.getId() + "#" + event.getName());
            }
            System.out.println(slider.getValue() + ":" + slider.getMaximum());
        }
    }

    public void loadLogFile(File file){
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
                }else if(str[0].equals("CP")){
                	classList.add(new CPE(str, classList, this));
                	mainPanel.addClassName(str[2]);
                }
            }
            play_flag = 1;
            maximumFlag = true;
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

    public int pressPlayButton(){
        if (play_flag == 0 && !requested){
            play_flag = 1;
            setMethodListener();
        }else{
            play_flag = 0;
            removeMethodListener();
        }
        return play_flag;
    }

    public int getPleyFlag(){return play_flag;}

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
                System.out.println("Go Forward");
            }else if(step > source.getValue()){
                goBack(source.getValue());
                System.out.println("Go Back");
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
    public int getMode(){ return this.mode;}

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
        maximumFlag = true;
        slider.setMaximum(eventList.size());
        loop();
    }



    public void createBFile(){
        try{
            /*Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            file = new File("C:\\Users\\shou\\IdeaProjects\\javanimation\\src\\data\\log\\"+ sdf.format(date) + ".txt");
            System.out.println("C:\\Users\\shou\\IdeaProjects\\javanimation\\src\\data\\log\\"+ sdf.format(date) + ".txt");*/
        	//file = Panel2.getSelected();

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

    public void changeFrameSize(int x, int y){
    	if(x > 0 && y > 0){
    		size(x, y);
        	drawingArea[1] = width;
        	drawingArea[3] = height;
    	}
    }

    public ArrayList<String> getObjectMethods(long id){
    	ArrayList<String> methods = new ArrayList<String>();
    	String className = "";
    	for(Instance ins : instanceList){
    		if(ins.getId() == id){
    			className = ins.getFullClassName();
    			break;
    		}
    	}
    	for(CPE cpe : classList){
    		if(className.equals(cpe.getName())){
    			methods = cpe.getMethodsName();
    			break;
    		}
    	}

    	return methods;
    }

    public int getWateState(){
    	if(mode ==4){
    		return mainPanel.getWateState();
    	}
    	return 0;
    }

    public boolean getbg3(){return mainPanel.getbg3();}
    public void setFieldName(String name){mainPanel.setObjectName(name);}

    public javanimation getAnimation(){return this;}

    public void updateOddObjects(ArrayList<String> objects, ArrayList<String> methods, ArrayList<String> links, ArrayList<String> classes){
    	if(objects.isEmpty()){
    		return;
    	}
    	play_flag = 0;
    	nextSign = false;
        noLoop();
    	reset();
    	background(0xFFF8FF8F);
    	oddObjects = objects;
    	oddMethods = methods;
    	oddLinks = links;
		for(int i = oddMethods.size()-1; i >= 0; i--){
    		methodList.add(new MEE(oddMethods.get(i), instanceList, methodList, tokenList, this));
    	}
    	for(String oddObject : oddObjects) {
    		String[] objectData = oddObject.split("#");
    		if(!objectData[1].matches("java.*|javax.*|sun.*|com.sun.*")){
    			Instance ins = new Instance(objectData, this);
    			instanceList.add(ins);
    		}
    	}
    	for(Instance ins : instanceList){
    		ins.presetSList(instanceList);
    	}
    	for(int i = 0; i < methodList.size(); i++){
    		MEE mee = (MEE)methodList.get(i);
    		for(Instance ins : instanceList){
				if(ins.getId() == mee.getId()){
					Field fi = new Field(mee, this);
					Token t = mee.setToken(ins);
	                for(Instance instance : instanceList){
	                    instance.setTextColor(mee.getThreadId(), false); // 薄くしている
	                }
	                fi.setTextColor(t.getTokenColor());
	                ins.addField(fi);
	                ins.updateField(mee);
				}
    		}
    	}
    	for(String s : links){
    		String[] linkData = s.split("->");
    		for(Instance ins : instanceList){
    			if(ins.getId() == Long.parseLong(linkData[1])){
    				ins.setRoot(false);
    				for(Instance parent : instanceList){
    					if(parent.getId() == Long.parseLong(linkData[0])){
    						ins.setPList(parent);
    						break;
    					}
    				}
    				break;
    			}
    		}
    	}
    	for(String s : classes){
    		String str[] = s.split("#");
    		classList.add(new CPE(str, classList, this));
        	mainPanel.addClassName(str[2]);
    	}
    	mode = 2;
    	while(animation_flag == 1){
    		redraw();
    	}
    	play_flag = 0;
    	loop();
    	mainPanel.setClearedFlag(false);
    }

    public void sendMethodEvent(String eventString){
        nextSign = false;
        String str[] = eventString.split("#");

        System.out.println(eventString);
        if(str[0].equals("ME")){
        	Event event = new MEE(str, instanceList, methodList, tokenList, this);
            eventList.add(event);
            play_flag = 1;
        }else if(str[0].equals("MX")){
        	Event event = new MXE(str, instanceList, methodList, tokenList, this);
            eventList.add(event);
            play_flag = 1;
        }else if(str[0].equals("MW")){
            eventList.add(new MWE(str, instanceList, this));
            //play_flag = 1;
        }else if(str[0].equals("AW")){
        	eventList.add(new AWE(str, instanceList, this));
        	//
        	//
        }else if(str[0].equals("CP")){
        	//classList.add(new CPE(str, classList, this));
        	//mainPanel.addClassName(str[2]);
        	//loop();
        }
        slider.setMaximum(eventList.size());
    }

	public void setMethodListener() {
		if(!requested) {
            listenerList = new ArrayList<>();
            requested = true;
            System.out.println("Event Requested: " + requested);
            diagram.getJdiThreads().stream().forEach(jdiThread -> {

                EventRequestManager mgr = jdiThread.getEventRequestManager();
                MethodEntryRequest request = mgr.createMethodEntryRequest();

                Arrays.asList(Excludes).forEach(request::addClassExclusionFilter);
                request.enable();
                OddMethodEntryEventListener oddMethodEntryEventListener = new OddMethodEntryEventListener(request, jdiThread);
                jdiThread.addJDIEventListener(oddMethodEntryEventListener, request);
                listenerList.add(oddMethodEntryEventListener);
                MethodExitRequest exitRequest = mgr.createMethodExitRequest();
                Arrays.asList(Excludes).forEach(exitRequest::addClassExclusionFilter);
                exitRequest.enable();
                ODDMethodExitEventListener oddMethodExitEventListener = new ODDMethodExitEventListener(exitRequest, jdiThread);
                jdiThread.addJDIEventListener(oddMethodExitEventListener, exitRequest);
                listenerList.add(oddMethodExitEventListener);
                ClassPrepareRequest classRequest = mgr.createClassPrepareRequest();
                Arrays.asList(Excludes).forEach(classRequest::addClassExclusionFilter);
                classRequest.enable();
                OddClassPrepareEventListener oddClassPrepareEventListener = new OddClassPrepareEventListener(classRequest, jdiThread);
                jdiThread.addJDIEventListener(oddClassPrepareEventListener, classRequest);
                listenerList.add(oddClassPrepareEventListener);

            });
            diagram.getJdiThreads().forEach(jdiThread -> {
            	try {
					jdiThread.resume();
				} catch (Exception e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
            });
        }

	}

	public void removeMethodListener(){
    	if(requested) {
            //memo ここの処理でデバッガの動作があやしくなる 6/20 => 解決(mgrからも削除で解決)
            requested = false;
            System.out.println("Event Request: " + requested + "listener size: " + listenerList.size());
            listenerList.forEach(ijdiEventListener -> {
                if(ijdiEventListener instanceof OddMethodEventListener){
                    System.out.println("REMOVE Obserer1");
                    ((OddMethodEventListener) ijdiEventListener).removeFromObservable();
                }else if(ijdiEventListener instanceof OddClassPrepareEventListener){
                	System.out.println("REMOVE Obserer1");
                    ((OddClassPrepareEventListener) ijdiEventListener).removeFromObservable();
                }else{
                    System.out.println("REMOVE NOTHING");
                }
            });
            listenerList = new ArrayList<>();
            diagram.getJdiThreads().forEach(jdiThread -> {
            	try {
					jdiThread.suspend();
				} catch (Exception e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
            });
        }
    }

	VirtualMachine vm;
    EventQueue queue;
    public synchronized void update(VirtualMachine vm, DebugEvent[] events) {
    	if(this.vm == null && this.queue == null) {
            this.vm = vm;
            this.queue = vm.eventQueue();
        }
        if (diagram == null){
            diagram = new OddDiagram(this, vm, events);
            diagram.update(vm, events);
        }else
            diagram.update(vm, events);

        redraw();
    }

    public void suspendAll(){
    	diagram.getJdiThreads().forEach(jdiThread -> {
        	try {
				jdiThread.suspend();
			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
        });
    }

    public void resumeAll(){
    	diagram.getJdiThreads().forEach(jdiThread -> {
        	try {
				jdiThread.resume();
			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
        });
    }

    public void setModificationWatchpointListener(long id, String fieldName, String target){ // Accessも同様
    	// ブレークポイントをかけるFieldオブジェクトを取得
    	ObjectReference targetObject = null;
    	com.sun.jdi.Field targetField = null;
    	for(ReferenceType referenceType : diagram.getVM().allClasses()){
    		for(ObjectReference objectReference : referenceType.instances(0)){
    			if(id == objectReference.uniqueID()){
    				targetObject = objectReference;
    				break;
    			}
    		}
    		if(targetObject != null){
    			break;
    		}
    	}
    	for(com.sun.jdi.Field field : targetObject.referenceType().allFields()){
    		if(field.name().equals(fieldName)){
    			targetField = field;
    			break;
    		}
    	}

    	for(JDIThread jdiThread : diagram.getJdiThreads()){
    		EventRequestManager mgr = jdiThread.getEventRequestManager();
            ModificationWatchpointRequest request = mgr.createModificationWatchpointRequest(targetField);
            boolean flag = false;
            if(target.equals("Class")){
            	flag = true;
            	request.addClassFilter(targetObject.referenceType());
            }else{
            	request.addInstanceFilter(targetObject);// memo 対象のインスタンスにのみ
            }
            request.enable();
            OddModificationWatchpointEventListener oddModificationWatchpointEventListener = new OddModificationWatchpointEventListener(request, jdiThread, id, fieldName, flag);
            jdiThread.addJDIEventListener(oddModificationWatchpointEventListener, request);
            fieldListenerList.add(oddModificationWatchpointEventListener);
    	}
    }

    public void removeModificationWatchpointListener(long id, String fieldName){
    	Iterator<IJDIEventListener> itr = fieldListenerList.iterator();
    	while(itr.hasNext()){
    		IJDIEventListener listener = itr.next();
    		if(listener instanceof OddModificationWatchpointEventListener){
	    		OddModificationWatchpointEventListener mweListener = (OddModificationWatchpointEventListener) listener;
	    		if(mweListener.getId() == id && fieldName.equals(mweListener.getFieldName())){
	    			mweListener.removeFromObservable();
	    			itr.remove();
	    		}
    		}
    	}
    }

    public void setAccessWatchpointListener(long id, String fieldName, String target){ // Accessも同様
    	// ブレークポイントをかけるFieldオブジェクトを取得
    	ObjectReference targetObject = null;
    	com.sun.jdi.Field targetField = null;
    	for(ReferenceType referenceType : diagram.getVM().allClasses()){
    		for(ObjectReference objectReference : referenceType.instances(0)){
    			if(id == objectReference.uniqueID()){
    				targetObject = objectReference;
    				break;
    			}
    		}
    		if(targetObject != null){
    			break;
    		}
    	}
    	for(com.sun.jdi.Field field : targetObject.referenceType().allFields()){
    		if(field.name().equals(fieldName)){
    			targetField = field;
    			break;
    		}
    	}

    	for(JDIThread jdiThread : diagram.getJdiThreads()){
    		EventRequestManager mgr = jdiThread.getEventRequestManager();
            AccessWatchpointRequest request = mgr.createAccessWatchpointRequest(targetField);
            boolean flag = false;
            if(target.equals("Class")){
            	flag = true;
            	request.addClassFilter(targetObject.referenceType());
            }else{
            	request.addInstanceFilter(targetObject);// memo 対象のインスタンスにのみ
            }
            request.enable();
            OddAccessWatchpointEventListener oddAccessWatchpointEventListener = new OddAccessWatchpointEventListener(request, jdiThread, id, fieldName, flag);
            jdiThread.addJDIEventListener(oddAccessWatchpointEventListener, request);
            fieldListenerList.add(oddAccessWatchpointEventListener);
    	}
    }

    public void removeAccessWatchpointListener(long id, String fieldName){
    	Iterator<IJDIEventListener> itr = fieldListenerList.iterator();
    	while(itr.hasNext()){
    		IJDIEventListener listener = itr.next();
    		if(listener instanceof OddAccessWatchpointEventListener){
	    		OddAccessWatchpointEventListener aweListener = (OddAccessWatchpointEventListener) listener;
	    		if(aweListener.getId() == id && fieldName.equals(aweListener.getFieldName())){
	    			aweListener.removeFromObservable();
	    			itr.remove();
	    		}
    		}
    	}
    }
}
