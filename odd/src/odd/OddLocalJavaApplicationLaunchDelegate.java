package odd;



import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.JavaLaunchDelegate;




public class OddLocalJavaApplicationLaunchDelegate extends JavaLaunchDelegate {

	@SuppressWarnings("static-access")
	@Override
	public IVMInstall verifyVMInstall(ILaunchConfiguration configuration) throws CoreException {
//		String[] args = {"-javaagent:/Users/d4ji/IdeaProjects/ASM1107/out/artifacts/premain/premain.jar"};//, "-classpath /Users/d4ji/IdeaProjects/asm-5.0.4/lib/all/asm-debug-all-5.0.4.jar"};
		IVMInstall subject = super.verifyVMInstall(configuration);
		System.out.println("Configuration: " + configuration);
		/*((IVMInstall2)subject).setVMArgs("-javaagent:/Users/d4ji/IdeaProjects/ASM1107/out/artifacts/premain/premain.jar "
				+ "-cp .:/Users/d4ji/IdeaProjects/ASM1107/out/artifacts/premain/premain.jar:" + 
				"/Users/d4ji/IdeaProjects/asm-5.0.4/lib/all/asm-debug-all-5.0.4.jar");*/
		System.out.println("CP: "+System.getenv("CLASSPATH"));
		//((IVMInstall2)subject).setVMArgs("-javaagent:/Users/d4ji/IdeaProjects/ASM1107/out/artifacts/premain/premain.jar");

		//((IVMInstall2)subject).setVMArgs(((IVMInstall2)subject).getVMArgs() + " -javaagent:/Users/d4ji/IdeaProjects/ASM1107/out/artifacts/premain/premain.jar");
		System.out.println("VMArgs:"+((IVMInstall2)subject).getVMArgs());
		System.out.println("CP2: "+System.getenv("CLASSPATH"));
		//		Arrays.stream(subject.getVMArguments()).forEach(System.out::println);
//		((IVMInstall2)subject).setVMArgs(null);
//		subject.setVMArguments(null);
//	    JFrame frame =new JFrame();
//	    frame.setSize(400,400);
//	    app.frame(frame).

//	    JFrame frame = new JFrame();
//	    frame.setSize(400, 400);
//	    System.out.println("applet" + views.DynamicObjectDiagramWindow.class);
	    

	    System.out.println("AIUEO");
	    System.err.println("aiueo");
//	    PApplet applet = new PApplet();
//	    System.out.println(applet);
//	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//	    frame.setVisible(true);
//	    System.out.println("DIAPLAYHEIGHT" + views.DynamicObjectDiagramWindow$.MODULE$.getClass().getName());
//	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//	    frame.setVisible(true);
		return new OddVMInstallProxy(subject);
	}

	
}
