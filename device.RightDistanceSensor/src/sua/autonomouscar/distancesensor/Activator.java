package sua.autonomouscar.distancesensor;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import sua.autonomouscar.infraestructure.devices.ARC.DistanceSensorARC;


public class Activator implements BundleActivator {

	private static BundleContext context;
	protected DistanceSensorARC sensorARC = null;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		String bundleId = bundleContext.getBundle().getSymbolicName();
		this.sensorARC = new DistanceSensorARC(bundleContext, bundleId, "RightDistanceSensor");
		this.sensorARC.start();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		this.sensorARC.stop();
		this.sensorARC = null;
		Activator.context = null;
	}

}
