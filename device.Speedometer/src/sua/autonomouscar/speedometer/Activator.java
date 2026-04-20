package sua.autonomouscar.speedometer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import sua.autonomouscar.infraestructure.devices.ARC.SpeedometerARC;

public class Activator implements BundleActivator {

	private static BundleContext context;
	protected SpeedometerARC odometerARC = null;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		this.odometerARC = new SpeedometerARC(bundleContext, "Odometer");
		this.odometerARC.start();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		this.odometerARC.stop();
		this.odometerARC = null;
		Activator.context = null;
	}

}
