package odd.ui;

import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class OddUIPlugin extends AbstractUIPlugin implements IPropertyChangeListener {


    public static final String PLUGIN_ID = "jp.ac.dendai.sie.odd";

    private static OddUIPlugin plugin;

    public OddUIPlugin(){
        if(plugin != null){
            throw new IllegalStateException("The Object Diagram UI plug-in class already exists");
        }
    }
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// TODO Auto-generated method stub


	}

    public static OddUIPlugin getDefault(){
        return plugin;
    }

}
