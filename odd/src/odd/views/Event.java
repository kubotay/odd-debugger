package odd.views;

/**
 * Created by shou on 2014/11/02.
 */
public interface Event {
    public String getName();
    public long getId();
    public boolean getIs(int i);
    public String getReturnType();
    public String getMethodName();
    public int getNumArgs();
    public String[] getArgs();
    public String getClassName();
    public int getNumFields();
    public String getFields(int num);
    public String getFieldValues(int num);
    public String getPrev();
    public String getNew();
    public void drawEvent();
    public long getThreadId();
}
