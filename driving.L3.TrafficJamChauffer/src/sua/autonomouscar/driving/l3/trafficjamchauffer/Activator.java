
package sua.autonomouscar.driving.l3.trafficjamchauffer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;
	protected L3_TrafficJamChaufferARC drivingFunctionARC = null;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		this.drivingFunctionARC = new L3_TrafficJamChaufferARC(bundleContext, "L3_TrafficJamChauffer");
		this.drivingFunctionARC.start();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		this.drivingFunctionARC.stop();
		this.drivingFunctionARC = null;
		Activator.context = null;
	}

}
