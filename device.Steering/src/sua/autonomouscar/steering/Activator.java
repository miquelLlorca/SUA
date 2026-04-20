package sua.autonomouscar.steering;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import sua.autonomouscar.infraestructure.devices.ARC.SteeringARC;

public class Activator implements BundleActivator {

	private static BundleContext context;
	protected SteeringARC steeringARC = null;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		this.steeringARC = new SteeringARC(bundleContext, "Steering");
		this.steeringARC.start();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		this.steeringARC.stop();
		this.steeringARC = null;
		Activator.context = null;
	}

}
