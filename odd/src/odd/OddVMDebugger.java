package odd;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.internal.launching.StandardVMDebugger;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

import com.sun.jdi.VirtualMachine;

@SuppressWarnings("restriction")
public class OddVMDebugger extends StandardVMDebugger {

	public OddVMDebugger(IVMInstall vmInstance) {
		super(vmInstance);
	}

	protected IDebugTarget createDebugTarget(VMRunnerConfiguration config, ILaunch launch, int port, IProcess process,
			VirtualMachine vm) {
		System.out.println("launch : " + config.getClassToLaunch());
		return OddDebugModel.newDebugTarget(launch, vm, renderDebugTarget(config.getClassToLaunch(), port), process,
				true, false, config.isResumeOnStartup());

	}

}
