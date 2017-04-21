package odd.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdi.internal.ArrayReferenceImpl;
import org.eclipse.jdi.internal.BooleanValueImpl;
import org.eclipse.jdi.internal.ByteValueImpl;
import org.eclipse.jdi.internal.CharValueImpl;
import org.eclipse.jdi.internal.DoubleValueImpl;
import org.eclipse.jdi.internal.FloatValueImpl;
import org.eclipse.jdi.internal.IntegerValueImpl;
import org.eclipse.jdi.internal.LongValueImpl;
import org.eclipse.jdi.internal.ObjectReferenceImpl;
import org.eclipse.jdi.internal.ShortValueImpl;
import org.eclipse.jdi.internal.StringReferenceImpl;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StringReference;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

import odd.util.Rectangle;

/**
 * Created by d4ji on 2016/01/15.
 */
public class OddObject {

    boolean isDrawn = false;
    List<Tupple4<String, String, Field, Value>> typeFieldValueList;
    VirtualMachine vm;
    javanimation g;
    boolean isOpened = false;
    float x, y;
    float alpha = 0;
    float width = 0;
    float height = 0;
    String cacheFirstLine = null;
    private boolean firstTime = true;
    private ObjectReference objectReference;
    private boolean isCollectionObject = false;
    private long cachedId = -1;

    public OddObject(javanimation g, VirtualMachine vm, ObjectReference objectReference, float x, float y) {
        this.g = g;
        this.vm = vm;
        this.objectReference = objectReference;
        this.x = x;
        this.y = y;
//        isCollectionObject = checkCollectionObject(objectReference.referenceType().name());

        typeFieldValueList = createTypeFieldValueList(objectReference);
    }

    public Rectangle<Float> getPersonalSpace() {
        return new Rectangle<Float>(x - 20, y - 20, width + 40, height + 40);
    }

    public ObjectReference getObjectReferenceType() {
        return this.objectReference;
    }

    public float centerX() {
        return x + width / 2;
    }

    public float centerY() {
        return y + height / 2;
    }

    private List<Tupple4<String, String, Field, Value>> createTypeFieldValueList(ObjectReference objectReference) {
        List<Tupple4<String, String, Field, Value>> list = new ArrayList<>();
        objectReference.referenceType().allFields().forEach(f -> {
            list.add(new Tupple4(f.typeName(), f.name(), f, objectReference.getValue(f)));
        });
        return list;
    }

    public void moveTo(float x, float y) {
        this.x = x;
        this.y = y;
        g.invalidate();
    }

    public List<Tupple4<String, String, Field, Value>> getTypeFieldValueList() {
        return typeFieldValueList;
    }

    public List<ObjectReference> getAllReferringObjects() {
        return this.objectReference.referringObjects(0);
    }

    public boolean isFirstTime() {
        return firstTime;
    }

    public long uniqueId() {
        if (cachedId < 0) {
            cachedId = this.objectReference.uniqueID();
        }
        return cachedId;
    }

    public void drawOn(javanimation g, float x, float y) {
//        System.out.println("Height: " + (g.textDescent() + g.textAscent()));
//        g.line(20, 20, 20, 20 + g.textDescent() + g.textAscent());
        this.x = x;
        this.y = y;

//        g.fill(255);
//        this.drawFirstLine(g, x, y, 0);

        if (isOpened) {

//        this.drawFirstLine(g, x, y, 0);
//            width = this.calcMaxFieldLineLength(g, x, y + 5 + g.textHeight, 0);
//            height = g.textHeight * (1 + typeFieldValueList.size()) + 10;
//            g.fill(255);
            drawPersonalSpace(g, x, y);
//            g.fill(0);
            this.drawFirstLine(g, x, y, 0);
//            g.line(x, y + g.textAscent() + g.textDescent() + 5, x + width, y + g.textAscent() + g.textDescent() + 5);
//        this.drawFieldLines(g, x, y + 5 + g.textHeight, 0);
//            if (alpha < 1.0)
//                alpha += 0.1;
        } else {
            this.drawSimpleObject(g);
        }

        firstTime = false;
    }

    private void drawPersonalSpace(javanimation g, float x, float y) {
        Rectangle<Float> ps = getPersonalSpace();
//        g.rect(ps.getX(), ps.getY(), ps.getWidth(), ps.getHeight());
//        g.rect(x, y, width, height);
    }

    public void drawSimpleObject(javanimation g) {
        width = g.textWidth(this.getFirstLineString());
//        height = g.textHeight + 10;
//        g.fill(255);//memo
        drawPersonalSpace(g, x, y);
        g.fill(0);
        this.drawFirstLine(g, x, y, 0);
//        g.line(x, y + g.textHeight + 5, x + width, y + g.textHeight + 5);
    }

    public void drawOn(javanimation g) {
//        g.line(20, 20, 20, 20 + g.textDescent() + g.textAscent());

        g.fill(255);
        if (isOpened) {
//        System.out.println("Height: " + (g.textDescent() + g.textAscent()));
//            g.line(20, 20, 20, 20 + g.textDescent() + g.textAscent());
//            g.fill(255);
//        this.drawFirstLine(g, x, y, 0);
//        width = this.drawFieldLines(g, x, y + 5 + g.textHeight, 0);
            width = this.calcMaxFieldLineLength(g, x, y + g.textAscent() + g.textDescent(), 0);
//            height = g.textHeight * (1 + typeFieldValueList.size()) + 10;
//            g.fill(255);
            drawPersonalSpace(g, x, y);
//            g.fill(0);
//            this.drawFirstLine(g, x, y, 0);
//            g.line(x, y + g.textAscent() + g.textDescent() + 5, x + width, y + g.textAscent() + g.textDescent() + 5);
            this.drawFieldLines(g, x, y + 5 + g.textAscent() + g.textDescent(), 0);
//            if (alpha < 1.0)
//                alpha += 0.1;
        } else {
            drawSimpleObject(g);
        }

    }

    public boolean containtsPoint(float x, float y) {
//        System.out.println("x: " + x + " y:" + y);
//        System.out.println("this.x: " + this.x + " width:" + width + " this.y: " + this.y + " heidht:" + height);
        return x > this.x && x < this.x + width && y > this.y && y < this.y + height;
    }

    private String convertValueToString(Value value) {
        String str = "";
        try {
            if (value instanceof IntegerValueImpl) {
                IntegerValueImpl integerValue = (IntegerValueImpl) value;
                str += integerValue.value();
            } else if (value instanceof LongValueImpl) {
                LongValueImpl longValue = (LongValueImpl) value;
                str += longValue;
            } else if (value instanceof ShortValueImpl) {
                ShortValueImpl shortValue = (ShortValueImpl) value;
                str += shortValue;
            } else if (value instanceof BooleanValueImpl) {
                BooleanValueImpl booleanValue = (BooleanValueImpl) value;
                str += booleanValue;
            } else if (value instanceof FloatValueImpl) {
                FloatValueImpl floatValue = (FloatValueImpl) value;
                str += floatValue;
            } else if (value instanceof DoubleValueImpl) {
                DoubleValueImpl doubleValue = (DoubleValueImpl) value;
                str += doubleValue;
            } else if (value instanceof ObjectReferenceImpl) {
                if (value instanceof StringReferenceImpl) {
                    StringReferenceImpl stringValue = (StringReferenceImpl) value;
                    str += "\"" + stringValue.value() + "\"";
                } else if (value instanceof ArrayReferenceImpl) {
                    ArrayReferenceImpl arrayReference = (ArrayReferenceImpl) value;
                    str += "[" + arrayReference.uniqueID() + "]";
                } else {
                    ObjectReferenceImpl objectReferenceImpl = (ObjectReferenceImpl) value;
                    str += "<" + objectReferenceImpl.uniqueID() + ">";
                }
            } else if (value instanceof CharValueImpl) {
                CharValueImpl charValue = (CharValueImpl) value;
                str += charValue;
            } else if (value instanceof ByteValueImpl) {
                ByteValueImpl byteValue = (ByteValueImpl) value;
                str += byteValue;
            }
        } catch (VMDisconnectedException e) {
            g.init();
        }
        return str;
    }

    private float drawFieldLines(javanimation g, float x, float nextY, float width) {
        float textMaxWidth[] = {width};
        float nY[] = {nextY};
        float firstStringWidth = this.g.textWidth(this.getFirstLineString());
        if (firstStringWidth > textMaxWidth[0])
            textMaxWidth[0] = this.g.textWidth(this.getFirstLineString());
        typeFieldValueList.forEach(tupple4 -> {
            String type = tupple4.getA();
            String name = tupple4.getB();
            Field field = tupple4.getC();
            Value value = tupple4.getD();
            String fieldString = name + ":" + type + " = ";
            fieldString += convertValueToString(value);
            if (g.textWidth(fieldString) > textMaxWidth[0])
                textMaxWidth[0] = g.textWidth(fieldString);
//            g.text(fieldString, x, nY[0] + g.textHeight);
//            nY[0] += g.textHeight;
        });
        return textMaxWidth[0];
    }

    private float calcMaxFieldLineLength(javanimation g, float x, float nextY, float width) {
        float textMaxWidth[] = {width};
        float nY[] = {nextY};
        float firstStringWidth = this.g.textWidth(this.getFirstLineString());
        if (firstStringWidth > textMaxWidth[0])
            textMaxWidth[0] = this.g.textWidth(this.getFirstLineString());
        typeFieldValueList.forEach(tupple4 -> {
            String type = tupple4.getA();
            String name = tupple4.getB();
            Field field = tupple4.getC();
            Value value = tupple4.getD();
            String fieldString = name + ":" + type + " = ";
            fieldString += convertValueToString(value);
            if (g.textWidth(fieldString) > textMaxWidth[0])
                textMaxWidth[0] = g.textWidth(fieldString);
//            g.text(fieldString, x, nY[0] + g.textHeight, alpha);
//            nY[0] += g.textHeight;
        });
        return textMaxWidth[0];
    }

    public String getObjectsString(){
    	String objectString = "";
    	objectString += objectReference.uniqueID() + "#" + objectReference.referenceType().name() + "#FIELD#";
    	objectString += typeFieldValueList.size();
    	for(Tupple4 tupple : typeFieldValueList){
    		objectString += "#" + tupple.getA() + "#";
    		objectString += tupple.getB() + "#";
    		//objectString += tupple.getD();
    		objectString += convertAll((Value) tupple.getD());
    	}
    	return objectString;
    }

    public String getFirstLineString() {
        if (cacheFirstLine == null) {
            cacheFirstLine = objectReference.uniqueID() + " : " + objectReference.referenceType().name();
        }
        return cacheFirstLine;
    }

    public float drawFirstLine(javanimation g, float x, float y, float width) {
//        g.fill(255);
        String firstLine = getFirstLineString();
        float textWidth = g.textWidth(firstLine);
//        g.rect(x, y, textWidth, g.textHeight);
//        g.fill(0);
//        g.text(firstLine, x, y + g.textHeight, alpha);
//        g.line(x, y + g.textHeight, x + textWidth, y + g.textHeight);

        return 0;
    }

    public void update(VirtualMachine vm, ObjectReference objectReference) {
        this.vm = vm;
        this.objectReference = objectReference;
        this.typeFieldValueList = createTypeFieldValueList(objectReference);

    }

    public void toggleView() {
        isOpened = !isOpened;
    }

    public static String convertAll(Value value){
    	if (value instanceof ClassType) {
            System.out.println("ClassType");
        } else if (value instanceof ArrayReference) {
        	ArrayReference arrayReference = (ArrayReference) value;
            //return "[" + arrayReference.uniqueID() + "]";
        	return convert(arrayReference);
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

    public static String convert(ArrayReference value){
    	String returnStr = "[";
    	for(int i = 0; i < value.length(); i++){
    		returnStr += value.getValue(i);
    		if(i+1 < value.length()){
    			returnStr += ",";
    		}
    	}
    	returnStr += "]";
    	return returnStr;
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

    public class Tupple4<A, B, C, D> {
        A a;
        B b;
        C c;
        D d;

        public Tupple4(A a, B b, C c, D d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }

        public A getA() {
            return a;
        }

        public B getB() {
            return b;
        }

        public C getC() {
            return c;
        }

        public D getD() {
            return d;
        }

    }
}
