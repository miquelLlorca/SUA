package sua.autonomouscar.driving.l2.acc;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;
	protected L2_AdaptiveCruiseControlARC drivingFunctionARC = null;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		this.drivingFunctionARC = new L2_AdaptiveCruiseControlARC(bundleContext, "L2_AdaptiveCruiseControl");
		this.drivingFunctionARC.start();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		this.drivingFunctionARC.stop();
		this.drivingFunctionARC = null;
		Activator.context = null;
	}

}
