package odd;

import org.eclipse.jdi.internal.ArrayReferenceImpl;
import org.eclipse.jdi.internal.ObjectReferenceImpl;
import org.eclipse.jdi.internal.StringReferenceImpl;
import org.eclipse.jdt.internal.debug.core.IJDIEventListener;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PrimitiveType;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StringReference;
import com.sun.jdi.Value;
import com.sun.jdi.event.AccessWatchpointEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.request.AccessWatchpointRequest;

import odd.views.MainPanel;

public class OddAccessWatchpointEventListener implements IJDIEventListener{
	private JDIThread jdiThread;
	private AccessWatchpointRequest request;
	private Long id;
	private String fieldName;
	private boolean classFlag = false;

	public OddAccessWatchpointEventListener(AccessWatchpointRequest request, JDIThread jdiThread, Long id, String fieldName, boolean flag){
		this.request = request;
		this.jdiThread = jdiThread;
		this.id = id;
		this.fieldName = fieldName;
		this.classFlag = flag;
	}

	public OddAccessWatchpointEventListener(AccessWatchpointRequest request){
		this.request = request;
	}

	public void removeFromObservable(){
		this.jdiThread.removeJDIEventListener(this, this.request);
		this.jdiThread.getEventRequestManager().deleteEventRequest(this.request);
	}

	public Object getRequest(){
		return this.request;
	}

	public Long getId(){return this.id;}
	public String getFieldName(){return this.fieldName;}
	public boolean getClassFlag(){return this.classFlag;}

	public boolean handleEvent(Event event, JDIDebugTarget jdiDebugTarget, boolean b, EventSet eventSet){
		MainPanel.getAnimation().suspendAll();
		AccessWatchpointEvent e = (AccessWatchpointEvent)event;
		ObjectReference objectReference = e.object();
		String s = "AW#";
		s += objectReference.uniqueID() + "#" + objectReference.referenceType().modifiers() + "#";
		try {
			s += e.field().type().name() + "#" + e.field().name() + "#CURRENT#";
		} catch (ClassNotLoadedException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		try {
			if(e.field().type() instanceof PrimitiveType){
				s += e.valueCurrent() + "#";
			}else if(e.field().type() instanceof ReferenceType){
				Value value = e.valueCurrent();
				if(value != null){
					if (value instanceof ArrayReference) {
						s += convertAll(value) + "#";
			        } else if (value instanceof StringReference) {
			        	s += value + "#";//todo この続き
			        } else if (value instanceof ObjectReference) {
			        	s += convertAll(value) + "#";
			        }
				}else{
					s += "null#";
				}
			}
		} catch (ClassNotLoadedException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}

		s += e.thread().name() + "#" + e.thread().uniqueID();

		System.out.println("***********************");
		System.out.println("***********************");
		System.out.println(s);
		System.out.println("***********************");
		System.out.println("***********************");
		MainPanel.getAnimation().sendMethodEvent(s);
		return true;
	}

	 public static String convertAll(Value value){
	    	if (value instanceof ClassType) {
	            System.out.println("ClassType");
	        } else if (value instanceof ArrayReference) {
	        	ArrayReferenceImpl arrayReference = (ArrayReferenceImpl) value;
	            return "[" + arrayReference.uniqueID() + "]";
	        }else if (value instanceof StringReference) {
	        	StringReferenceImpl stringReferenceImpl = (StringReferenceImpl)value;//todo この続き
	        	return stringReferenceImpl.value();
	        } else if (value instanceof ObjectReference) {
	            return convert((ObjectReferenceImpl) value);
	        }
	        return null;
	    }

	    public static String convert(ObjectReferenceImpl value) {
	        if (value.type().name().equals("java.util.ArrayList")) {
	            return convertArrayListToString(value);
	        } else if(value.type().name().equals("java.util.HashMap")){
	            return convertHashMapToString(value);
	        } else {
	            return "<id=" + value.uniqueID() + ">";
	        }
	    }

	    private static String convertArrayListToString(ObjectReferenceImpl value) {
	    	Field field = value.referenceType().fieldByName("elementData");
	    	ArrayReferenceImpl arrayReferenceImpl = (ArrayReferenceImpl) value.getValue(field);
	    	StringBuilder builder = new StringBuilder("[");
	        for (int i = 0; i < arrayReferenceImpl.length(); i++) {
	            ObjectReferenceImpl obj = (ObjectReferenceImpl) arrayReferenceImpl.getValue(i);
	            if (obj != null)
	                builder.append(createObjectId(obj)).append(",");
	        }
	        if (builder.length() != 1)
	            builder.deleteCharAt(builder.length() - 1);
	        builder.append("]");
			return builder.toString();
		}

	    private static String createObjectId(ObjectReferenceImpl objectReferenceImpl) {
	        return "<id=" + objectReferenceImpl.uniqueID() + ">";
	    }

		private static String convertHashMapToString(ObjectReferenceImpl value) {
			Field field = value.referenceType().fieldByName("table");
	        ArrayReferenceImpl arrayReferenceImpl = (ArrayReferenceImpl)value.getValue(field);
	        StringBuilder builder = new StringBuilder("[");
	        if(arrayReferenceImpl != null) {
	            for (int i = 0; i < arrayReferenceImpl.length(); i++) {
	                ObjectReferenceImpl obj = (ObjectReferenceImpl) arrayReferenceImpl.getValue(i);
	                if (obj != null) {
	                    Field keyField = obj.referenceType().fieldByName("key");
	                    Field valueField = obj.referenceType().fieldByName("value");
	                    ObjectReferenceImpl keyValue = (ObjectReferenceImpl) obj.getValue(keyField);
	                    if(keyValue.referenceType().name().equals("java.lang.String")){
	                        builder.append("<").append(((StringReferenceImpl) keyValue).value()).append(",");
	                    }else{
	                        builder.append("<<id=").append(keyValue.uniqueID()).append(">,");
	                    }

	                    ObjectReferenceImpl valueValue = (ObjectReferenceImpl) obj.getValue(valueField);
	                    if(valueValue.referenceType().name().equals("java.lang.String")){
	                        builder.append(((StringReferenceImpl)valueValue).value()).append(">,");
	                    }else{
	                        builder.append("<id=").append(valueValue.uniqueID()).append(">>,");
	                    }
	                }
	            }
	            builder.deleteCharAt(builder.length() - 1);
	        }

	        builder.append("]");
	        return builder.toString();
		}

	public void eventSetComplete(Event event, JDIDebugTarget jdiDebugTarget, boolean b, EventSet eventSet){

	}
}
