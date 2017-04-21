package odd;

import java.io.File;
import java.net.URL;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.LibraryLocation;


public class OddVMInstallProxy implements IVMInstall {

	private IVMInstall subject;
	public OddVMInstallProxy(IVMInstall subject) {
		this.subject = subject;
	}
	@Override
	public String getId() {
		return subject.getId();
	}

	@Override
	public File getInstallLocation() {
		return subject.getInstallLocation();
	}

	@Override
	public URL getJavadocLocation() {
		return subject.getJavadocLocation();
	}

	@Override
	public LibraryLocation[] getLibraryLocations() {
		return subject.getLibraryLocations();
	}

	@Override
	public String getName() {
		return subject.getName();
	}

	@Override
	public String[] getVMArguments() {
		return subject.getVMArguments();
	}

	@Override
	public IVMInstallType getVMInstallType() {
		return subject.getVMInstallType();
	}

	@Override
	public IVMRunner getVMRunner(String mode) {
		if(ILaunchManager.DEBUG_MODE.equals(mode)){
			System.out.println("getVMRunner");
			return new OddVMDebugger(this);
		} else {
			throw new IllegalStateException("AAAAAA");
		}
	}

	@Override
	public void setInstallLocation(File arg0) {
		subject.setInstallLocation(arg0);
	}

	@Override
	public void setJavadocLocation(URL arg0) {
		subject.setJavadocLocation(arg0);
	}

	@Override
	public void setLibraryLocations(LibraryLocation[] arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setName(String arg0) {
		subject.setName(arg0);
	}

	@Override
	public void setVMArguments(String[] arg0) {
//		subject.setVMArguments(arg0);
//		String str = "";
//		for(String s : arg0){
//			str = str + " " + s;
//		}
		subject.setVMArguments(arg0);
//		((IVMInstall2)subject).setVMArgs("-javaagent:/Users/d4ji/IdeaProjects/ASM1107/out/artifacts/premain/premain.jar");
	}
}
