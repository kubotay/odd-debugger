package odd.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.internal.debug.core.IJDIEventListener;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;

import odd.ODDMethodExitEventListener;
import odd.OddClassPrepareEventListener;
import odd.OddMethodEntryEventListener;
import odd.OddMethodEventListener;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.MouseEvent;


public class OddApplet extends PApplet {

    public OddDiagram diagram = null;
    public float textHeight;
    Optional<OddObject> selectedObject = Optional.empty();
    float prevX = 0;
    float prevY = 0;
    private boolean requested = false;

    public OddApplet() {
//        try {
//            java.lang.reflect.Method handleSettingsMethod =
//                    this.getClass().getSuperclass().getDeclaredMethod("handleSettings", null);
//            handleSettingsMethod.setAccessible(true);
//            handleSettingsMethod.invoke(this, null);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        super.initSurface();
//        surface.placeWindow(new int[]{0,0}, new int[]{0, 0});
//        super.startSurface();

//        super.start();
//        super.showSurface();
//        noLoop();
    }

    @Override
    public synchronized void draw() {
        //this.background(255);
        background(0xFFF8FF8F);
        this.fill(0);
        if (diagram == null) {
//            System.out.println("Diagram is null");
            return;
        }
//        text("frame rate: "+frameRate, 0, 10);
        textSize(10);
//        diagram.draw(this);
        if(diagram.getJdiThreads() != null && MainPanel.getAnimation().getPleyFlag() == 1 && MainPanel.getAnimation().getNextSign()){
        	diagram.getJdiThreads().stream().forEach(jdiThread -> {
                try {
                	boolean flag = true;
                	for(Map.Entry<Long, ThreadReference> e : diagram.threadTable.entrySet()){
                		if(e.getValue().isSuspended()){
                			if(e.getValue().name().equals(jdiThread.getName())){
                				try {
									if(e.getValue().frame(0).location().declaringType().name().startsWith("java")){
										flag = false;
										jdiThread.stepReturn();
										break;
									}else if(e.getValue().frame(0).location().declaringType().name().startsWith("com.sun.")){
										flag = false;
										jdiThread.stepReturn();
										break;
									}else if(e.getValue().frame(0).location().declaringType().name().startsWith("sun.")){
										flag = false;
										jdiThread.stepReturn();
										break;
									}
								} catch (Exception e1) {
									// TODO 自動生成された catch ブロック
									e1.printStackTrace();
								}
                			}
                		}
                	}
                	if(flag){
                		jdiThread.stepInto();
                	}
                    redraw();
                } catch (DebugException e1) {
                    e1.printStackTrace();
                }
            });
        }else{
        	redraw();
        }

    }



    //    @Override
//	public void settings() {
//    	this.pixelDensity(displayDensity());
//		this.size(1024,  1024);
//	}

	@Override
    public void setup() {

		this.size(512, 512);
        this.smooth();
//        this.noSmooth();
        textHeight = textAscent() + textDescent();
        //noLoop();
    }

	private static String[] Excludes = { "java.*", "javax.*", "sun.*", "com.sun.*" };
    private List<IJDIEventListener> listenerList;

    @Override
    public void mouseClicked(MouseEvent e) {
//        System.out.println(e.getButton());
    	if (e.getButton() == PConstants.LEFT) {// left button
            System.out.println("BUTTON1(JDIThread size: " + diagram.getJdiThreads().size() + ")");

            if(!requested) {
                listenerList = new ArrayList<>();
                requested = true;
                System.out.println("Event Requested: " + requested);
                diagram.getJdiThreads().stream().forEach(jdiThread -> {
                    //try {
                    //jdiThread.stepInto();

//                    jdiThread.stepOver();

                    EventRequestManager mgr = jdiThread.getEventRequestManager();
                    MethodEntryRequest request = mgr.createMethodEntryRequest();

//                    request.setSuspendPolicy(EventRequest.SUSPEND_NONE);
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
//                    MethodExitRequest requestex = mgr.createMethodExitRequest();
//                    requestex.setSuspendPolicy(EventRequest.SUSPEND_NONE);
//                    Arrays.asList(Excludes).forEach(requestex::addClassExclusionFilter);
//                    requestex.enable();

//                private void requestMethodExitEvent(VirtualMachine vm) {
//                    EventRequestManager mgr = vm.eventRequestManager();
//                    MethodExitRequest request = mgr.createMethodExitRequest();
//                    request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
//                    Arrays.asList(Excludes).forEach(request::addClassExclusionFilter);
//                    request.enable();

//                } catch (DebugException e1) {
//                    e1.printStackTrace();
//                }
                });
//            if(requested) {
//                System.out.println("Thread start");
//                new Thread() {
//                    @Override
//                    public void run() {
//                        EventSet eventSet;
//                        while (true) {
//                            try {
//                                eventSet = queue.remove();
//                                eventSet.stream().forEach(event -> {
//                                    System.out.println("EventXXX: " + event.toString());
//                                });
//                                eventSet.resume();
//                            } catch (InterruptedException ex) {
//                                ex.printStackTrace();
//                            } catch (InternalError err){
//                                err.printStackTrace();
//                            }
//
//                        }
//                    }
//                }.start();
//            }
            } else {
                //memo ここの処理でデバッガの動作があやしくなる 6/20 => 解決(mgrからも削除で解決)
                requested = false;
                System.out.println("Event Request: " + requested + "listener size: " + listenerList.size());
                listenerList.forEach(ijdiEventListener -> {
                    if(ijdiEventListener instanceof OddMethodEventListener){
                        System.out.println("REMOVE Obserer1");
                        ((OddMethodEventListener) ijdiEventListener).removeFromObservable();
                    }else {
                        System.out.println("REMOVE NOTHING");
                    }
                });
//                diagram.jdiThreads.stream().forEach(jdiThread -> {
//                    listenerList.stream().forEach(ijdiEventListener -> {
//                        System.out.println("listener: " + ijdiEventListener);
//                        if(ijdiEventListener instanceof OddMethodEntryEventListener){
//                            jdiThread.removeJDIEventListener(ijdiEventListener, ((OddMethodEntryEventListener) ijdiEventListener).getRequest());
//                        } else if (ijdiEventListener instanceof ODDMethodExitEventListener) {
//                            jdiThread.removeJDIEventListener(ijdiEventListener, ((ODDMethodExitEventListener) ijdiEventListener).getRequest());
//                        }
//                    });
//                });
                listenerList = new ArrayList<>();
            }
        }
        if (e.getButton() == PConstants.RIGHT) {// right button
            System.out.println("BUTTON3(JDIThread size: " + diagram.getJdiThreads().size() + ")");


          //  while (true) {
                diagram.getJdiThreads().stream().forEach(jdiThread -> {
                    try {
//                    Arrays.asList(jdiThread.getStackFrames()).stream().forEach(iStackFrame -> {
//                        try {
//                            jdiThread.stepOver();
//                        } catch (DebugException e1) {
//                            e1.printStackTrace();
//                        }
//                    });
                        //jdiThread.stepOver();

                        jdiThread.stepOver();
                    } catch (DebugException e1) {
                        e1.printStackTrace();
                    }
                });
           // }
        }
        if(e.getCount() == 2){// double click
            selectedObject = pick(e.getX(), e.getY());
            selectedObject.ifPresent(OddObject::toggleView);
            redraw();
        }

    }

    public void setMethodListener(){
    	if(!requested) {
            listenerList = new ArrayList<>();
            requested = true;
            System.out.println("Event Requested: " + requested);
            diagram.getJdiThreads().stream().forEach(jdiThread -> {
                //try {
                //jdiThread.stepInto();

//                jdiThread.stepOver();

                EventRequestManager mgr = jdiThread.getEventRequestManager();
                MethodEntryRequest request = mgr.createMethodEntryRequest();

//                request.setSuspendPolicy(EventRequest.SUSPEND_NONE);
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
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == PConstants.LEFT) {
            selectedObject = pick(e.getX(), e.getY());
            prevX = e.getX();
            prevY = e.getY();
        }
    }

    private Optional<OddObject> pick(int x, int y) {
        return this.diagram.pick(x, y);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        float dx = e.getX() - prevX;
        float dy = e.getY() - prevY;
        selectedObject.ifPresent(oddObject -> oddObject.moveTo(oddObject.x + dx, oddObject.y + dy));
        prevX = e.getX();
        prevY = e.getY();
        redraw();
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        selectedObject = Optional.empty();
    }

    private void updateDiagram() {


    }

    public void clear() {
        diagram = null;
    }

    VirtualMachine vm;
    EventQueue queue;
    public synchronized void update(VirtualMachine vm, DebugEvent[] events) {
    	if(this.vm == null && this.queue == null) {
            this.vm = vm;
            this.queue = vm.eventQueue();
        }
        if (diagram == null){
            //diagram = new OddDiagram(this, vm, events);
            //diagram.update(vm, events);
        }else
            diagram.update(vm, events);

        redraw();
    }
//    public boolean isInitialized(){
//        return initialized;
//    }
//    private boolean initialized = false;
//    @Override
//    public void init() {
//        super.init();
//        initialized = true;
//    }
}
