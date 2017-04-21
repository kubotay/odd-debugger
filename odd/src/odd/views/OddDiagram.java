package odd.views;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;


public class OddDiagram {
    public static ArrayList<String> targetPackages = new ArrayList<String>();
    VirtualMachine vm;
    javanimation g;
    DebugEvent[] events;
    HashMap<Long, OddObject> oddObjects;
    List<OddLink> links;
    HashMap<Long, ThreadReference> threadTable;
    HashMap<String, ReferenceType> classTable;
    HashMap<String, ReferenceType> usedClassesTable;
    HashMap<Long, OddObject> objectTable;
    ArrayList<String> sendObjects;
    ArrayList<String> sendMethods;
    ArrayList<String> sendLinks;
    ArrayList<String> sendClasses;

    public OddDiagram(javanimation javanimation, VirtualMachine vm, DebugEvent[] events) {
        this.g = javanimation;
        this.vm = vm;
        this.events = events;
        setTargetPackages();
        this.init();
    }

    public void setTargetPackages(){
    	try {
			BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\shou\\work_plag\\odd\\src\\data\\targets.txt"));
			String line;
			while((line = br.readLine()) != null){
				targetPackages.add(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
    }

    private void init() {
        threadTable = new HashMap<>();
        classTable = new HashMap<>();
        objectTable = new HashMap<>();
        oddObjects = new HashMap<>();
        links = new ArrayList<>();
        usedClassesTable = new HashMap<>();
        sendObjects = new ArrayList<String>();
        sendMethods = new ArrayList<String>();
        sendLinks = new ArrayList<String>();
        sendClasses = new ArrayList<String>();
        this.createTables();
        this.createLinks();
        this.createSendObjects();
        g.updateOddObjects(sendObjects, sendMethods, sendLinks, sendClasses);
    }

    private void createLinks() {
        for (Map.Entry<Long, OddObject> entry : oddObjects.entrySet()) {
            OddObject oddObject = entry.getValue();
            oddObject.getAllReferringObjects().forEach(objectReference -> {
//                System.out.println("REF: from" + objectReference.uniqueID() + " to: "+ entry.getKey());
                links.add(new OddLink(objectReference.uniqueID(), entry.getKey()));
            });

        }
    }

    public void drawTree(javanimation g) {
        try {
            int minRefSize = oddObjects.values().size();
            int maxRefSize = -1;

            Collection<OddObject> list = oddObjects.values();


            for (OddObject oddObject : list) {
                if(oddObject.getObjectReferenceType().referringObjects(0).size() < minRefSize){
                    minRefSize = oddObject.getAllReferringObjects().size();
                }
//                if(oddObject.getObjectReferenceType().referringObjects(0).size() > maxRefSize){
//                    maxRefSize = oddObject.getAllReferringObjects().size();
//                }
            }
            List<OddObject> minRefObjects = new ArrayList<>();
//            List<OddObject> maxRefObjects = new ArrayList<>();
            for(OddObject oddObject : list){
                if(oddObject.getAllReferringObjects().size() == minRefSize){
                    minRefObjects.add(oddObject);
                }
//                if(oddObject.getAllReferringObjects().size() == maxRefSize){
//                    maxRefObjects.add(oddObject);
//                }

            }

            System.out.println("Min Ref Obj: " + minRefSize + " @ ");

            for (int i = 0; i < minRefObjects.size() ; i++) {
                OddObject minRefObject = minRefObjects.get(i);
                System.out.print(minRefObject.getObjectReferenceType().uniqueID() + ", ");
                minRefObject.moveTo(10 , 10 + i * 100);
            }
            System.out.println();
//            System.out.println("Max Ref Obj: " + maxRefSize + " @ ");
//            for (OddObject maxRefObject : maxRefObjects) {
//                System.out.print(maxRefObject.getObjectReferenceType().uniqueID() + ", ");
//            }
//            System.out.println();
        } catch (VMDisconnectedException e) {
            this.init();
        }
    }

    public void draw(javanimation g) {
        long start = System.nanoTime();
        circleArrangement(g);

        drawLinks(g);
        Long end = System.nanoTime();
//        drawTree(g);
        //System.out.println("Draw Time: " + (end - start));
    }

    private void circleArrangement(javanimation g) {
        int size = oddObjects.size();
        int centerX = g.width / 2;
        int centerY = g.height / 2;
        int radius = g.height / 2 - 20;
        double dr = 2 * Math.PI / size;

        double prevX = radius;
        double prevY = 0;
        int i = 0;
        for (OddObject oddObject : oddObjects.values()) {
            double x = prevX * Math.cos(dr * i) - prevY * Math.sin(dr * i) + centerX;
            double y = prevX * Math.sin(dr * i) + prevY * Math.cos(dr * i) + centerY;
            if (oddObject.isFirstTime())
                oddObject.drawOn(g, (float) x, (float) y);
            else
                oddObject.drawOn(g);
            i++;
        }
    }

    private void drawLinks(javanimation g) {
        for (OddLink link : links) {
            link.drawOn(g);
        }
    }

    public Optional<OddObject> pick(int x, int y) {
        return oddObjects.values().stream().filter(oddObject -> oddObject.containtsPoint(x, y)).findFirst();
    }

    public OddObject getById(Long id) {
        return oddObjects.get(id);
    }


    private void createTables() {

        try {
            createThreadTable();
            createClassesTable();
            createUsedClassesTable();
        } catch (VMDisconnectedException e) {
            g.clear();
        }
    }

    private void createUsedClassesTable() {
        usedClassesTable.values().forEach(referenceType -> {
            referenceType.instances(0).forEach(inst -> {
                storeChainedObject(inst);
//                oddObjects.put(inst.uniqueID(), new OddObject(g, vm, inst, 0, 0));
            });

        });
    }

    private void createClassesTable() {
        vm.allClasses().forEach(referenceType -> {
            classTable.put(referenceType.name(), referenceType);
            if(targetPackages.stream().anyMatch(s -> referenceType.name().startsWith(s))){
            //if (referenceType.name().startsWith(targetPackage1) || referenceType.name().startsWith(targetPackage2)) {   // TODO パッケージの指定をハードコードしている
                usedClassesTable.put(referenceType.name(), referenceType);
            }
        });
    }

    private void createThreadTable() {
        vm.allThreads().forEach(threadReference -> {
            threadTable.put(threadReference.uniqueID(), threadReference);
        });
    }

    private void storeChainedObject(ObjectReference obj) {

        if (oddObjects.get(obj.uniqueID()) == null && !obj.referenceType().name().contains("$"))// $クラスを排除
            oddObjects.put(obj.uniqueID(), new OddObject(g, vm, obj, 0, 0));
        if (obj.referringObjects(0).size() == 0) return;
        obj.referringObjects(0).forEach(inst -> {
            OddObject oddObject = oddObjects.get(inst.uniqueID());
            if (oddObject == null) {
                if (!inst.referenceType().name().contains("$")) {
                    oddObjects.put(inst.uniqueID(), new OddObject(g, vm, inst, 0, 0));
//                    System.out.println(inst.uniqueID() + " :rec storeChaindedObject");
                    storeChainedObject(inst);
                }


            } else {
                oddObject.update(this.vm, inst);

            }

        });

    }

    public void update(VirtualMachine vm, DebugEvent[] events) {
    	System.out.println("events length: " + events.length);
        System.out.println("call UPDATE<<");
//  memo      (e.getDetail() == DebugEvent.BREAKPOINT && e.getKind() == DebugEvent.SUSPEND
//        for (DebugEvent event : events) {
//            System.out.println(event);
        for (DebugEvent event : events) {
            System.out.println(event.getDetail() + "@UPDATE@" + event.getKind());
        }
//        for (DebugEvent event : events) {
//            if(event.getDetail() == DebugEvent.BREAKPOINT && event.getKind() == DebugEvent.SUSPEND) {

//        }

        long start = System.nanoTime();
        this.vm = vm;
        this.events = events;
        DebugEvent[] e = new DebugEvent[1];
        for (DebugEvent event : events) {
            if(privateCheckEvents(event)){
                System.out.println("このイベントは必要");
                e[0] = event;
                checkEvents(e);
                checkThreads(vm.allThreads());
            }

        }
        updateTables();
         links = new ArrayList<>();
        createLinks();
        long end = System.nanoTime();
        System.out.println("Update Time: " + (end - start));
        if(this.events[0].getDetail() == DebugEvent.BREAKPOINT){
	        this.createSendObjects();
	        g.updateOddObjects(sendObjects, sendMethods, sendLinks, sendClasses);
	        g.removeMethodListener();
        }

//            }
//        }
        System.out.println("call UPDATE>>");
    }

    private boolean privateCheckEvents(DebugEvent event) {
        System.out.println("DebugEvent.SUSPEND:" + DebugEvent.SUSPEND);
        System.out.println("DebugEvent.BREAKPOINT:" + DebugEvent.BREAKPOINT);
        if(event.getKind() == DebugEvent.SUSPEND &&
                (event.getDetail() == DebugEvent.BREAKPOINT || event.getDetail() == DebugEvent.STEP_END)
                && (event.getSource() instanceof JDIThread))
            return true;
        return false;
    }

    private void methodEventRequest(VirtualMachine vm) {
        System.out.println("Method event Request start");
        requestMethodEntryEvent(vm);
        requestMethodExitEvent(vm);
        System.out.println("Method event Request end");
    }
    private static String[] Excludes = { "java.*", "javax.*", "sun.*", "com.sun.*" };
    private void requestMethodExitEvent(VirtualMachine vm) {
        EventRequestManager mgr = vm.eventRequestManager();
        MethodExitRequest request = mgr.createMethodExitRequest();
        request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
        Arrays.asList(Excludes).forEach(request::addClassExclusionFilter);
        request.enable();
    }

    private void requestMethodEntryEvent(VirtualMachine vm) {
        EventRequestManager mgr = vm.eventRequestManager();
        MethodEntryRequest request = mgr.createMethodEntryRequest();
        request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
        Arrays.asList(Excludes).forEach(request::addClassExclusionFilter);
        request.enable();
    }


    private void checkThreads(List<ThreadReference> threadReferences) {
        System.out.println("OddDiagram << checkThreads");
// stack frame の先頭から実行中のメソッドをみつける
        threadReferences.forEach(threadReference -> {
            try {
                System.out.println("Thread.uniqueId: " + threadReference.uniqueID() + " name:" + threadReference.name());
                if(threadReference.isSuspended() && threadReference.frameCount() > 0) {
                    System.out.println("Thread frameCount:" + threadReference.frameCount());
//                    System.out.println(threadReference.frame(threadReference.frameCount()-1).thisObject());// top of stackframes
                    for(int i = 0; i < threadReference.frameCount(); i++){
                        if(threadReference.frameCount() > 0) {
                            StackFrame stackFrame = threadReference.frame(i);
                            System.out.println("method: (" + i + ")" + stackFrame.location().method().name());
                            System.out.println("class: " + stackFrame.location().declaringType().name());
                            ObjectReference objectReference = stackFrame.thisObject();
                            if (objectReference != null)
                                System.out.println(objectReference.uniqueID() + " " + objectReference.referenceType().name());
                            else
                                System.out.println("static method");
                        } else {
                            System.out.println("Frame Count: 0");
                        }
                    }

                }
//                ObjectReference objectReference =  threadReference.frameCount();

            } catch (IncompatibleThreadStateException e) {
                e.printStackTrace();
            }
        });
        System.out.println("OddDiagram >> checkThreads");
    }
    private List<JDIThread> jdiThreads;
    private void checkEvents(DebugEvent[] events) {
        System.out.println("OddDiagram << checkEvents");
        System.out.println("evnets: " + events);
        System.out.println("checkEvents start");
        for (DebugEvent event : events) {
//            System.out.println("Event: " + event);// memo MethodEntryとExitを要求する条件を調査すること 5/30
//            if(event.getSource() instanceof OddDebugTarget){
//            System.out.println("check: " + privateCheckEvents(event));
//            if(!privateCheckEvents(event)){
//                System.out.println("a対象外Event");
//                System.out.println("SOURCE: " + event.getSource() + " kind:" + event.getKind() + " detail:" + event.getDetail() + " stepstart: " + event.isStepStart());
//                System.out.println("OddDiagram >> checkEvents");
//                return;
//            }
            setJdiThreads(new ArrayList<>());
            if(event.getSource() instanceof JDIThread){
//                OddDebugTarget target = (OddDebugTarget)event.getSource();
                JDIThread target = (JDIThread)event.getSource();
                // memo JDIThread に stepInto()等がある
                getJdiThreads().add(target);
//                try {
//                    target.stepOver();
//                } catch (DebugException e) {
//                    e.printStackTrace();
//                }
                try {
                    System.out.println("Thread name: " + target.getName());
                    System.out.println("Target@@@ :" + target.getJavaDebugTarget());
                } catch (DebugException e) {
                    e.printStackTrace();
                }
//                this.methodEventRequest(target.getEventRequestManager().virtualMachine());
//                target.createMethodExitHander();
//                target.createMethodEntryHander();
            }
            System.out.println("SOURCE: " + event.getSource() + " kind:" + event.getKind() + " detail:" + event.getDetail() + " stepstart: " + event.isStepStart());
        }
        //Arrays.asList(events).stream().forEach(eve
        // nt -> System.out.println(event));
        System.out.println("checkEvents end");
        System.out.println("OddDiagram >> checkEvents");

    }

    private void updateTables() {
        try {
            updateThreadTable();
            updateClassesTable();
            this.usedClassesTable.values().forEach(referenceType -> {
                referenceType.instances(0).forEach(objectReference -> { // memo instances(0) => gets all instances
                    OddObject oddObject = oddObjects.get(objectReference.uniqueID());
                    if (oddObject == null) {
                        if (!objectReference.referenceType().name().contains("$")) // 内部クラスの排除
                            this.oddObjects.put(objectReference.uniqueID(), new OddObject(g, vm, objectReference, 0, 0));
                    } else {
                        oddObject.update(this.vm, objectReference);
                    }
                    storeChainedObject(objectReference);
                });
            });
        } catch (VMDisconnectedException e) {
            this.init();
        }
    }

    private void updateClassesTable() {

        this.vm.allClasses().forEach(referenceType -> {
            if (!classTable.containsKey(referenceType.name()))
                classTable.put(referenceType.name(), referenceType);
            // TODO パッケージの指定をハードコードしている
            if(targetPackages.stream().anyMatch(s ->referenceType.name().startsWith(s))
            /*if (referenceType.name().startsWith(targetPackage1) || referenceType.name().startsWith(targetPackage2) */
            		&& !usedClassesTable.containsKey(referenceType.name())) // $クラスを排除
                usedClassesTable.put(referenceType.name(), referenceType);
        });
    }

    private void updateThreadTable() {
        this.vm.allThreads().forEach(threadReference -> {
            if (!threadTable.containsKey(threadReference))
                threadTable.put(threadReference.uniqueID(), threadReference);
        });
    }

    private void createSendObjects(){
    	sendObjects.clear();
    	for(Map.Entry<Long, OddObject> e : oddObjects.entrySet()) {
    		sendObjects.add(e.getValue().getObjectsString());
    	}
    	sendMethods.clear();
    	for(Map.Entry<Long, ThreadReference> e : threadTable.entrySet()){
    		String s = "";
    		try {
    			if(e.getValue().isSuspended()){
					for(int i = 0; i < e.getValue().frameCount(); i++){
						Method method = e.getValue().frame(i).location().method();
						if(!method.isStatic() && e.getValue().frame(i).thisObject() != null){
							s += e.getValue().frame(i).thisObject().uniqueID() + "#";
						} else {
							s += "-1#";
						}
						s += method.name() + "#" + method.returnTypeName() + "#" + method.modifiers() + "#ARGS#";
						s += method.arguments().size();
						for(int j = 0; j < method.arguments().size(); j++){
							s += "#" + method.arguments().get(j).typeName() + "#";
							s += method.arguments().get(j).name() + "#";
							s += e.getValue().frame(i).getArgumentValues().get(j).toString();
						}
						s += "#" + e.getValue().name() + "#" + e.getValue().uniqueID();
						sendMethods.add(s);
						s = "";
					}
    			}
			} catch (IncompatibleThreadStateException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			} catch (AbsentInformationException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}

    	}
    	sendLinks.clear();
    	for(OddLink link : links){
    		sendLinks.add(link.getLinkString());
    	}
    	sendClasses.clear();
    	for(Map.Entry<String, ReferenceType> e : usedClassesTable.entrySet()){
    		if(!(e.getValue() instanceof ClassType)){
    			continue;
    		}
    		String s = "CP#";
    		s += e.getValue().modifiers() + "#" + e.getValue().name() + "#SUPER#" + ((ClassType)e.getValue()).superclass().name() + "#INTERFACES#0#FIELDS#";
    		//((ClassType)e.getValue()).allInterfaces();
    		int fieldSize = e.getValue().fields().size();
    		s += fieldSize;
    		for(Field field : e.getValue().fields()){
    			try {
					s += "#" + field.modifiers() + ":" + field.type().name() + ":" + field.name() + ":\"\"";
				} catch (ClassNotLoadedException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
				}
    		}
    		s += "#METHODS#" + e.getValue().methods().size();
    		for(Method method : e.getValue().methods()){
    			s += "#" + method.modifiers() + ":" + method.returnTypeName() + ":" + method.name() + "#ARG#0";
    		}
    		s += "#main#1"; // 暫定
    		sendClasses.add(s);
    	}
    }

	public List<JDIThread> getJdiThreads() {
		return jdiThreads;
	}

	public void setJdiThreads(List<JDIThread> jdiThreads) {
		this.jdiThreads = jdiThreads;
	}

	public void getNewDiagramData(){
		this.createSendObjects();
        g.updateOddObjects(sendObjects, sendMethods, sendLinks, sendClasses);
	}

	public VirtualMachine getVM(){return this.vm;}
}
