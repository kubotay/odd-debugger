package odd;

import org.eclipse.jdi.internal.ArrayReferenceImpl;
import org.eclipse.jdi.internal.ObjectReferenceImpl;
import org.eclipse.jdi.internal.StringReferenceImpl;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ArrayReference;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PrimitiveType;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StringReference;
import com.sun.jdi.Value;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.request.MethodEntryRequest;

import odd.views.MainPanel;


/**
 * Created by d4ji on 2016/06/16.
 */
public class OddMethodEntryEventListener implements OddMethodEventListener {

    private JDIThread jdiThread;
    private MethodEntryRequest request;
    public OddMethodEntryEventListener(MethodEntryRequest request, JDIThread jdiThread) {
        this.request = request;
        this.jdiThread = jdiThread;
        
    }

    public OddMethodEntryEventListener(MethodEntryRequest request) {
        this.request = request;
    }

    @Override
    public void removeFromObservable() {
        this.jdiThread.removeJDIEventListener(this, this.request);
        this.jdiThread.getEventRequestManager().deleteEventRequest(this.request);
    }

    @Override
    public Object getRequest() {
        return this.request;
    }

    @Override
    public boolean handleEvent(Event event, JDIDebugTarget jdiDebugTarget, boolean b, EventSet eventSet) {

        try {
        	MainPanel.getAnimation().suspendAll();
            MethodEntryEvent e = (MethodEntryEvent)event;
            ObjectReference objectReference = e.thread().frame(0).thisObject();
            if(objectReference != null){ // オブジェクト
                System.out.println("OMEnEL:("+ objectReference.referenceType().name() + " " + objectReference.uniqueID() + ") " + ((MethodEntryEvent) event).method());
                String s = "ME#";
                s += objectReference.referenceType().name() + "#" + objectReference.uniqueID() + "#" + e.method().modifiers() + "#";
                s += e.method().returnTypeName() + "#" + e.method().name() + "#FIELD#" + objectReference.referenceType().allFields().size() + "#";
                for(int i = 0; i < objectReference.referenceType().allFields().size(); i++){
                	Field f = objectReference.referenceType().allFields().get(i);
                	if(f.type() instanceof PrimitiveType){
                		s += f.modifiers() + ":" + f.typeName() +":" + f.name() +":" + objectReference.getValue(f) + "#";
                	}else if(f.type() instanceof ReferenceType){
                		Value value = objectReference.getValue(f);
                		if(value != null){
                			System.out.println(value.type().name());
	                		if (value instanceof ArrayReference) {
	                			s += f.modifiers() + ":" + f.typeName() +":" + f.name() +":" +  convertAll(value) + "#";
	                        } else if (value instanceof StringReference) {
	                        	s += f.modifiers() + ":" + f.typeName() +":" + f.name() +":" +  value + "#";//todo この続き
	                        } else if (value instanceof ObjectReference) {
	                        	s += f.modifiers() + ":" + f.typeName() +":" + f.name() +":" +  convertAll(value) + "#";
	                        }
                		}else{
                			s += f.modifiers() + ":" + f.typeName() +":" + f.name() + ":null#";
                		}
                	}
                }
                s += "ARGS#" + e.method().arguments().size() + "#";
                for(int i = 0; i < e.method().arguments().size(); i++){
					s += e.method().arguments().get(i).typeName() + ":";
					s += e.method().arguments().get(i).name() + ":";
					for(int j = 0; j < e.thread().frameCount(); j++){
						if(e.thread().frame(j).location().method().name().equals(e.method().name())){
							s += e.thread().frame(j).getArgumentValues().get(i).toString() + "#";
							break;
						}
					}
				}
                s += e.thread().name() + "#" + e.thread().uniqueID();
                MainPanel.getAnimation().sendMethodEvent(s);
            }else{                       // クラス
                System.out.println("OMEnEL:(class)" + e.method().declaringType().name());
            }
        } catch (IncompatibleThreadStateException e1) {
            e1.printStackTrace();
        } catch (ClassCastException e2){
        	e2.printStackTrace();
        } catch (AbsentInformationException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (ClassNotLoadedException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
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

    @Override
    public void eventSetComplete(Event event, JDIDebugTarget jdiDebugTarget, boolean b, EventSet eventSet) {

    }

}
