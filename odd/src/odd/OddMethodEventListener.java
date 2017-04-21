package odd;

import org.eclipse.jdt.internal.debug.core.IJDIEventListener;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;

/**
 * Created by d4ji on 2016/06/21.
 */
public abstract interface OddMethodEventListener extends IJDIEventListener {

    JDIThread jdiThread = null;

    public void removeFromObservable();

    public Object getRequest();

    @Override
    public abstract boolean handleEvent(Event event, JDIDebugTarget jdiDebugTarget, boolean b, EventSet eventSet);

    @Override
    public void eventSetComplete(Event event, JDIDebugTarget jdiDebugTarget, boolean b, EventSet eventSet);
}
