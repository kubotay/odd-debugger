package odd.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class OddCorePlugin extends Plugin {

    public static final String PLUGIN_ID = "jp.ac.dendai.sie.odd";

    private static OddCorePlugin plugin;

    public OddCorePlugin() {
        if(plugin != null)
            throw new IllegalStateException("The Object Diagram core plug-in class already exists.");
    }

    public void start(BundleContext context) throws Exception{
        super.start(context);
        plugin  = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    public static OddCorePlugin getDefault(){
        return plugin;
    }
}

