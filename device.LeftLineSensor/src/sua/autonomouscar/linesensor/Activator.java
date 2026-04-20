package sua.autonomouscar.linesensor;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import sua.autonomouscar.infraestructure.devices.ARC.LineSensorARC;


public class Activator implements BundleActivator {

	private static BundleContext context;
	protected LineSensorARC sensorARC = null;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		this.sensorARC = new LineSensorARC(bundleContext, "LeftLineSensor");
		this.sensorARC.start();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		this.sensorARC.stop();
		this.sensorARC = null;
		Activator.context = null;
	}

}
