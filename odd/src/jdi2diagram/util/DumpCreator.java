package jdi2diagram.util;

import jdi2diagram.event_info.method_event.MethodEventInfo;
import jdi2diagram.event_info.prepare_event.ClassPrepareEventInfo;
import jdi2diagram.object_info.*;


public class DumpCreator {
    private StringBuilder builder = new StringBuilder();

    public static String dumpSeparator() {
        return "#";
    }

    public static String dump(MethodEventInfo event) {
        DumpCreator creator = new DumpCreator();

        ThisObject thisObject = event.getThisObject();
        ThisMethod thisMethod = event.getThisMethod();
        creator.append(event.dumpPrefix());
        creator.append(thisObject.getClassName());
        creator.append(thisObject.getObjectId());
        creator.append(thisMethod.getModifiers());
        creator.append(thisMethod.getReturnTypeName());
        creator.append(thisMethod.getMethodName());
        creator.append("FIELD");

        creator.append(thisObject.sizeOfFields());
        for (int i = 0; i < thisObject.sizeOfFields(); i++) {
            ThisVariable thisVariable = thisObject.getField(i);
            creator.append(thisVariable.dump());

        }
        creator.append("ARGS");
        creator.append(thisMethod.sizeOfArguments());
        for (int i = 0; i < thisMethod.sizeOfArguments(); i++) {
            ThisVariable thisVariable = thisMethod.getArgument(i);
            creator.append(thisVariable.dump());
        }
        creator.append(event.getThreadName());
        creator.append(event.getThreadId());
        return creator.getString();

    }

    public static String dump(ClassPrepareEventInfo classPrepareEventInfo) {
        DumpCreator creator = new DumpCreator();
        ThisClass thisClass = classPrepareEventInfo.getThisClass();

        creator.append(classPrepareEventInfo.dumpPrefix());
        creator.append(thisClass.getModifiers());
        creator.append(thisClass.getClassName());
        creator.append("SUPER");
        creator.append(thisClass.getSuperClass().getClassName());
        creator.append("INTERFACES");
        creator.append(thisClass.sizeOfInterfaces());
        for (ThisInterface anInterface : thisClass.getInterfaces()) {
            creator.append(anInterface.getInterfaceName());
        }

//        for (int i = 0; i < thisClass.sizeOfInterfaces(); i++) {
//            ThisInterface thisInterface = thisClass.getInterface(i);
//            creator.append(thisInterface.getInterfaceName());
//        }
        creator.append("FIELDS");
        creator.append(thisClass.sizeOfFields());
        for (ThisVariable variable : thisClass.getFields()) {
            creator.append(variable.dump());
        }

//        for (int i = 0; i < thisClass.sizeOfFields(); i++) {
//            ThisVariable field = thisClass.getField(i);
//            creator.append(field.getModifiers() + ":" + field.getTypeName() + ":" + field.getVarName() + ":" + field.getValueString());
//
//        }
        creator.append("METHODS");
        creator.append(thisClass.sizeOfMethods());
        for (int i = 0; i < thisClass.sizeOfMethods(); i++) {
            ThisMethod method = thisClass.getMethod(i);
            creator.append(method.getModifiers() + ":" + method.getReturnTypeName() + ":" + method.getMethodName());
            creator.append("ARG");
            for (int j = 0; j < method.sizeOfArguments(); j++) {
                ThisVariable argument = method.getArgument(j);
                creator.append(argument.getTypeName()+":"+argument.getVarName()+":"+argument.getValueString());
            }
        }
        creator.append(classPrepareEventInfo.getThreadName());
        creator.append(classPrepareEventInfo.getThreadId());

        return creator.getString();
    }

    private StringBuilder append(int i) {
        builder.append(i);
        builder.append(dumpSeparator());
        return builder;
    }

    private StringBuilder append(long l) {
        builder.append(l);
        builder.append(dumpSeparator());
        return builder;
    }

    private StringBuilder append(String s) {
        builder.append(s);
        builder.append(dumpSeparator());
        return builder;
    }

    private String getString() {
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
