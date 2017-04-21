package odd.views;

import java.lang.reflect.Modifier;
import java.util.ArrayList;


/**
 * Created by shou on 2014/11/02.
 */
public class CPE implements Event {
    javanimation papplet;
    private String myName = "";
    private String superName = "";
    private ArrayList<String> interfaces = new ArrayList<String>();
    private ArrayList<Tuple<String, String, String>> fieldValues = new ArrayList<Tuple<String, String, String>>();
    private ArrayList<String> methods = new ArrayList<String>();
    private int modifiers = 0;
    private String threadName = "";
    private long threadId;
    ArrayList<CPE> classList;

    public CPE(String[] dumpData, ArrayList<CPE> list, javanimation p){
        papplet = p;
        this.classList = list;

        this.modifiers = Integer.parseInt(dumpData[1]);
        this.myName = dumpData[2];
        this.superName = dumpData[4];
        int index = 7;
        if(dumpData[5].equals("INTERFACES")){
        	int interfaceLen = Integer.parseInt(dumpData[6]);
        	for(int i = 0; i < interfaceLen; i++){
        		interfaces.add(dumpData[index]);
        		index++;
        	}
        }
        if(dumpData[index].equals("FIELDS")){
        	index++;
        	int fieldLen = Integer.parseInt(dumpData[index]);
        	index++;
        	for(int i = 0; i < fieldLen; i++){
        		String fieldString = dumpData[index];
                String fieldData[] = fieldString.split(":");
                if(fieldData.length <= 3){
                    fieldValues.add(new Tuple<String,String,String>(fieldData[1], fieldData[2], ""));
                }else {
                	if(fieldData[3].startsWith("\"")){
                        fieldValues.add(new Tuple<String, String, String>(fieldData[1], fieldData[2], fieldData[3].substring(1,fieldData[3].length()-1)));
                    }else {
                        fieldValues.add(new Tuple<String, String, String>(fieldData[1], fieldData[2], fieldData[3]));
                    }
                }
                index++;
        	}
        }
        if(dumpData[index].equals("METHODS")){
        	index++;
        	int methodLen = Integer.parseInt(dumpData[index]);
        	index++;
        	for(int i = 0; i < methodLen; i++){
        		String methodString = dumpData[index] + "#";
        		index++;
        		if(dumpData[index].equals("ARG") || dumpData[index].equals("ARGS")){
        			index++;
        			int argLen = Integer.parseInt(dumpData[index]);
        			methodString += "ARG#" + argLen + "#";
                    index++;
                    for(int j = 0; j < argLen; j++){
                    	methodString += dumpData[index] + "#";
                    	index++;
                    }
        		}
        		methods.add(methodString);
        	}
        }
        this.threadName = dumpData[index];
        index++;
        this.threadId = Long.parseLong(dumpData[index]);
    }

    public void drawEvent(){
        /*javanimation.currentEvent = "Class Prepare";
        if(javanimation.classPrepare_flag == 1){
            //instanceList.add(new Instance(this.myName, papplet));
        }
        javanimation. classPrepare_flag = 0;
        javanimation. animation_flag = 1;*/
    }

    public String getName(){
        return this.myName;
    }
    public long getId(){return 0;}
    public boolean getIs(int i){return false;}
    public String getReturnType(){return null;}
    public String getMethodName(){return null;}
    public ArrayList<String> getMethodsName(){
    	ArrayList<String> superMethods = new ArrayList<String>();
    	ArrayList<String> returnMethods = new ArrayList<String>();
    	for(CPE cpe : classList){
    		if(superName.equals(cpe.getName())){
    			superMethods = cpe.getMethodsName();
    			returnMethods.addAll(superMethods);
    			break;
    		}
    	}
    	for(String method : this.methods){
    		String[] allData = method.split("#");
    		String[] methodData = allData[0].split(":");
    		String addString;
    		if(Modifier.isStatic(Integer.parseInt(methodData[0])) == false){
    			addString = this.myName + "." + methodData[2] + "(";
    			int numArgs = Integer.parseInt(allData[2]);
    			if(numArgs != 0){
    				for(int i = 0; i < numArgs; i++){
    					String[] argsData = allData[3+i].split(":");
    					addString += argsData[1] + ":" + argsData[0];
    					if(i+1 < numArgs){
    						addString += ",";
    					}
    				}
    			}
    			addString += "):" + methodData[1];
    			returnMethods.add(addString);
    		}
    	}
    	return returnMethods;
    }
    public int getNumArgs(){return 0;}
    public String[] getArgs(){return null;}
    public String getClassName(){return null;}
    public int getNumFields(){return 0;}
    public String getFields(int num){return null;}
    public String getFieldValues(int num){return null;}
    public String getPrev(){return null;}
    public String getNew(){return null;}
    public long getThreadId(){return this.threadId;}
}
