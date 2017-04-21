package jdi2diagram.event_info;

import java.io.Serializable;

/**
 * Created by shou on 2015/06/20.
 */
public class FinishInfo  extends ModifierInfo implements Serializable {
    public FinishInfo(){

    }
    public void setLastEvent(boolean last){
        lastEvent = last;
    }
}
