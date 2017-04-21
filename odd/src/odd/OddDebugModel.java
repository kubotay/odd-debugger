package odd;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;

import com.sun.jdi.VirtualMachine;

@SuppressWarnings("restriction")
public class OddDebugModel {
	public static IDebugTarget newDebugTarget(ILaunch launch, VirtualMachine vm, String name, IProcess process,
			boolean allowTerminate, boolean allowDisconnect) {
		return newDebugTarget(launch, vm, name, process, allowTerminate, allowDisconnect, true);

	}

	public static IDebugTarget newDebugTarget(final ILaunch launch, final VirtualMachine vm, final String name,
			final IProcess process, final boolean allowTerminate, final boolean allowDisconnect, final boolean resume) {
		final IJavaDebugTarget[] target = new IJavaDebugTarget[1];
		// System.out.println("newDebugTarget");
		IWorkspaceRunnable r = m -> target[0] = new OddDebugTarget(launch, vm, name, allowTerminate, allowDisconnect, process, resume);
		try {
			ResourcesPlugin.getWorkspace().run(r, null, 0, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return target[0];
	}

}
