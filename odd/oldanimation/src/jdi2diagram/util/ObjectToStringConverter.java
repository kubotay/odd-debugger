package jdi2diagram.util;

import com.sun.jdi.Field;
import com.sun.tools.jdi.ArrayReferenceImpl;
import com.sun.tools.jdi.ObjectReferenceImpl;
import com.sun.tools.jdi.StringReferenceImpl;

public class ObjectToStringConverter {

    public static String convert(ArrayReferenceImpl value) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("[");
        if(value.length()!=0) {
            for (int i = 0; i < value.length(); i++) {
                stringBuffer.append(value.getValue(i));
                stringBuffer.append(",");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        }

        stringBuffer.append("]");
        return stringBuffer.toString();
    }

    public static String convert(StringReferenceImpl value) {
        return value.value();
    }

    public static String covert(ObjectReferenceImpl value) {
        if (value.type().name().equals("java.util.ArrayList")) {
            return convertArrayListToString(value);
        } else if(value.type().name().equals("java.util.HashMap")){
            return convertHashMapToString(value);
        } else {
            return "<id=" + value.uniqueID() + ">";
        }
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
}
