package sua.autonomouscar.driving.l0.manual;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;
	protected L0_ManualDrivingARC drivingFunctionARC = null;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		this.drivingFunctionARC = new L0_ManualDrivingARC(bundleContext, "L0_ManualDriving");
		this.drivingFunctionARC.start();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		this.drivingFunctionARC.stop();
		this.drivingFunctionARC = null;
		Activator.context = null;
	}

}
