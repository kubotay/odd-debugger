package odd;

import org.eclipse.jdt.internal.debug.core.IJDIEventListener;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.request.ClassPrepareRequest;

import odd.views.MainPanel;

public class OddClassPrepareEventListener implements IJDIEventListener{
	private JDIThread jdiThread;
	private ClassPrepareRequest request;

	public OddClassPrepareEventListener(ClassPrepareRequest request, JDIThread jdiThread){
		this.request = request;
		this.jdiThread = jdiThread;
	}

	public OddClassPrepareEventListener(ClassPrepareRequest request){
		this.request = request;
	}

	public void removeFromObservable(){
		this.jdiThread.removeJDIEventListener(this, this.request);
		this.jdiThread.getEventRequestManager().deleteEventRequest(this.request);
	}

	public Object getRequest(){
		return this.request;
	}

	public boolean handleEvent(Event event, JDIDebugTarget jdiDebugTarget, boolean b, EventSet eventSet){
		ClassPrepareEvent e = (ClassPrepareEvent)event;
		ReferenceType referenceType = e.referenceType();
		String s = "CP#";
		s += referenceType.modifiers() + "#" + referenceType.name() + "#SUPER#" + ((ClassType)referenceType).superclass().name() + "#INTERFACES#0#FIELDS#";
		//((ClassType)e.getValue()).allInterfaces();
		int fieldSize = referenceType.fields().size();
		s += fieldSize;
		for(Field field : referenceType.fields()){
			try {
				s += "#" + field.modifiers() + ":" + field.type().name() + ":" + field.name() + ":\"\"";
			} catch (ClassNotLoadedException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}
		}
		s += "#METHODS#" + referenceType.methods().size();
		for(Method method : referenceType.methods()){
			s += "#" + method.modifiers() + ":" + method.returnTypeName() + ":" + method.name() + "#ARG#0";
		}
		s += "#main#1"; // 暫定
		MainPanel.getAnimation().sendMethodEvent(s);
		return true;
	}

	public void eventSetComplete(Event event, JDIDebugTarget jdiDebugTarget, boolean b, EventSet eventSet){

	}

}
