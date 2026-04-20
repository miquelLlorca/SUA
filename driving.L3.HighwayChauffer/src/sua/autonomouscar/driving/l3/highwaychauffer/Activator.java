
package sua.autonomouscar.driving.l3.highwaychauffer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;
	protected L3_HighwayChaufferARC drivingFunctionARC = null;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		this.drivingFunctionARC = new L3_HighwayChaufferARC(bundleContext, "L3_HighwayChauffer");
		this.drivingFunctionARC.start();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		this.drivingFunctionARC.stop();
		this.drivingFunctionARC = null;
		Activator.context = null;
	}

}
