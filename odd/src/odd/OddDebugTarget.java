package odd;

import java.util.Arrays;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.internal.debug.core.IJDIEventListener;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTargetAdapter;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.StepRequest;
import com.sun.jdi.request.VMDeathRequest;

import odd.views.OddView;

@SuppressWarnings("restriction")
public class OddDebugTarget extends JDIDebugTargetAdapter implements IJavaDebugTarget{
    private static String[] excludes = { "java.*", "javax.*", "sun.*", "com.sun.*" };
    public OddDebugTarget(ILaunch launch, VirtualMachine jvm, String name, boolean supportTerminate,
                          boolean supportDisconnect, IProcess process, boolean resume) {
        super(launch, jvm, name, supportTerminate, supportDisconnect, process, resume);

        // TODO Auto-generated constructor stub
    }

    @Override
    protected synchronized void initialize() {
        // new ClassPrepareHandler();
        // new MethodEntryHandler();
        super.initialize();
    }

    @Override
    public void handleDebugEvents(DebugEvent[] events) {
        System.out.println("handleDebugEvents");
        // TODO Auto-generated method stub
//        super.handleDebugEvents(events);

        // System.out.println("handleDebugEvents"+events);
        VirtualMachine vm = getVM();
//        if(OddView.applet == null || !OddView.applet.isInitialized()) return;
        if (OddView.getMainPanel().getAnimation() == null) return;
        OddView.getMainPanel().getAnimation().update(vm, events); // ������Porcessing�֘A�̃N���X��VM��C�x���g��n���Ă���
        System.out.println("breakpoints: " + this.getBreakpoints());
        System.out.println();
        System.out.println();


//		System.out.println("CAN? " + vm.canGetInstanceInfo());

//		Arrays.stream(events).forEach(e -> {
//
//			if (e.getDetail() == DebugEvent.BREAKPOINT && e.getKind() == DebugEvent.SUSPEND) {
//				vm.allClasses().forEach(cls -> {
//					/*
//					 * cls.instances(0).forEach(inst -> {
//					 * System.out.println(cls.name() + " inst: " + inst); });
//					 */
//					for (Field f : cls.allFields()) {
//						if (f.name().equals("$instances$")) {
//							System.out.println("CLASS: " + cls.name());
//							Value v = cls.getValue(f);
//							if (f.typeName().equals("java.util.WeakHashMap")) {
//								System.out.println(f.typeName());
//								ObjectReference o = (ObjectReference) v;
//								Value v2 = o.getValue(o.referenceType().fieldByName("table"));
//								ArrayReference ar = (ArrayReference) v2;
//								ar.getValues().forEach(elem -> {
//									if (elem != null) {
//										ObjectReference or2 = (ObjectReference) elem;
//										// System.out.println("ID: " +
//										// or2.uniqueID());
//										// System.out.println(or2);
//										Field field = or2.referenceType().fieldByName("referent");
//										Value v3 = or2.getValue(field);
//										ObjectReference keyObject = (ObjectReference) v3;
//										System.out.println("ID: " + keyObject.uniqueID());
//										keyObject.getValues(keyObject.referenceType().allFields()).forEach((fa, va) -> {
//
//											System.out.println("Field: " + fa + " Value: " + va);
//										});
//
//										// System.out.println("Entry Key: " +
//										// field);
//
//										// Value v3 =
//										// or2.getValue(or2.referenceType().fieldByName("key"));
//										// if(v3 != null)
//										// System.out.println("Entry key: " +
//										// v3.type());
//									}
//								});
//							}
//
//							break;
//						}
//					}
//				});
//			}
//		});

        // VirtualMachine vm = getVM();
        // vm.allClasses().stream().forEach(cls -> {
        // System.out.println(cls.name());
        // });
    }

    @Override
    protected void initializeRequests() {

    	setThreadStartHandler(new OddThreadStartHandler());
        // super.initializeRequests();
        // new ClassPrepareHandler();
        // new MethodEntryHandler();
        // new MethodExitHandler();
        new VMDeathEventHandler();
    }

    protected class VMDeathEventHandler implements IJDIEventListener {
        public VMDeathEventHandler() {
            createRequest();
        }

        protected void createRequest() {
            EventRequestManager mgr = getEventRequestManager();
            VMDeathRequest request = mgr.createVMDeathRequest();
            request.setSuspendPolicy(EventRequest.SUSPEND_NONE);
            request.enable();
            addJDIEventListener(this, request);
        }

        @Override
        public boolean handleEvent(Event event, JDIDebugTarget jdiDebugTarget, boolean b, EventSet eventSet) {
            VMDeathEvent evt = (VMDeathEvent) event;
            System.out.println("VMDeath");
//            OddView.applet.clear();
            return false;
        }

        @Override
        public void eventSetComplete(Event event, JDIDebugTarget jdiDebugTarget, boolean b, EventSet eventSet) {

        }
    }

    public void createMethodExitHander(){
        new MethodExitHandler();
    }
    protected class MethodExitHandler implements IJDIEventListener {
        protected MethodExitHandler() {
            createRequest();
        }

        protected void createRequest() {
            EventRequestManager mgr = getEventRequestManager();
            MethodExitRequest request = mgr.createMethodExitRequest();
            request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
            Arrays.asList(excludes).forEach(request::addClassExclusionFilter);
            request.enable();
            addJDIEventListener(this, request);
        }

        @Override
        public boolean handleEvent(Event event, JDIDebugTarget target, boolean suspendVote, EventSet eventSet) {
            System.out.println("Method Exit");
        	MethodExitEvent evt = (MethodExitEvent) event;
            try {
                System.out.println(
                        evt.location().sourceName() + "@" + evt.location().lineNumber() + " << " + evt.method().name());
            } catch (AbsentInformationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public void eventSetComplete(Event event, JDIDebugTarget target, boolean suspend, EventSet eventSet) {
            // TODO Auto-generated method stub

        }
    }

    public void createMethodEntryHander(){
        new MethodEntryHandler();
    }
    protected class MethodEntryHandler implements IJDIEventListener {
        StepEventHandler step = null;

        protected MethodEntryHandler() {
            createRequest();
        }

        protected void createRequest() {
            EventRequestManager mgr = getEventRequestManager();
            MethodEntryRequest request = mgr.createMethodEntryRequest();
            request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
            Arrays.asList(excludes).forEach(request::addClassExclusionFilter);
            request.enable();
            addJDIEventListener(this, request);
        }

        @Override
        public boolean handleEvent(Event event, JDIDebugTarget target, boolean suspendVote, EventSet eventSet) {
            System.out.println("Method Entry");
            MethodEntryEvent evt = (MethodEntryEvent) event;
            // if(step==null){
            // step = new StepEventHandler(evt.thread());
            // }
			try {
				System.out.println(
						evt.location().sourceName() + "@" + evt.location().lineNumber() + " >> " + evt.method().name());
			} catch (AbsentInformationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            return true;
        }

        @Override
        public void eventSetComplete(Event event, JDIDebugTarget target, boolean suspend, EventSet eventSet) {
            // TODO Auto-generated method stub

        }

        protected class StepEventHandler implements IJDIEventListener {
            protected StepEventHandler(ThreadReference t) {
                createRequest(t);
            }

            private void createRequest(ThreadReference thread) {
                EventRequestManager mgr = getEventRequestManager();
                StepRequest request = mgr.createStepRequest(thread, StepRequest.STEP_MIN, StepRequest.STEP_OVER);
                request.enable();

            }

            @Override
            public boolean handleEvent(Event event, JDIDebugTarget target, boolean suspendVote, EventSet eventSet) {
                StepEvent evt = (StepEvent) event;
                StackFrame frame;
                try {
                    frame = evt.thread().frame(0);
                    frame.visibleVariables().stream().forEach(lv -> {
                        Value v = frame.getValue(lv);
                        if (v instanceof IntegerValue) {
                            IntegerValue iv = (IntegerValue) v;
//							System.out.println(lv.name() + "=" + iv.intValue());
                        }
                    });
                } catch (IncompatibleThreadStateException | AbsentInformationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                return true;
            }

            @Override
            public void eventSetComplete(Event event, JDIDebugTarget target, boolean suspend, EventSet eventSet) {
                // TODO Auto-generated method stub

            }

        }

    }

    protected class ClassPrepareHandler implements IJDIEventListener {

        protected ClassPrepareHandler() {
            createRequest();
        }

        protected void createRequest() {
            EventRequestManager mgr = getEventRequestManager();
            ClassPrepareRequest request = mgr.createClassPrepareRequest();
            request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
            request.enable();
            addJDIEventListener(this, request);
        }

        @Override
        public boolean handleEvent(Event event, JDIDebugTarget target, boolean suspendVote, EventSet eventSet) {
            ClassPrepareEvent evt = (ClassPrepareEvent) event;
//			System.out.println(evt.referenceType().name());

            return true;
        }

        @Override
        public void eventSetComplete(Event event, JDIDebugTarget target, boolean suspend, EventSet eventSet) {
            // TODO Auto-generated method stub

        }

    }

    class OddThreadStartHandler extends JDIDebugTargetAdapter.ThreadStartHandlerAdapter{
        protected OddThreadStartHandler() {
            super();
        }

        @Override
        protected void createRequest() {
            EventRequestManager manager = getEventRequestManager();
            if (manager != null) {
                try {
                    EventRequest request = manager.createThreadStartRequest();
                    request.setSuspendPolicy(EventRequest.SUSPEND_NONE);
                    request.enable();
                    addJDIEventListener(this, request);
                    setRequest(request);
                } catch (RuntimeException e) {
                    logError(e);
                }
            }

        }

        @Override
        public boolean handleEvent(Event event, JDIDebugTarget target, boolean suspendVote, EventSet eventSet) {
            return super.handleEvent(event, target, suspendVote, eventSet);
        }
    }

}
