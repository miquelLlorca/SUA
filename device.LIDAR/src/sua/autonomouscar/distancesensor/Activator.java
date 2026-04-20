package sua.autonomouscar.distancesensor;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import sua.autonomouscar.infraestructure.devices.ARC.DistanceSensorARC;


public class Activator implements BundleActivator {

	private static BundleContext context;
	protected DistanceSensorARC frontDistance_sensorARC = null;
	protected DistanceSensorARC rearDistance_sensorARC = null;
	protected DistanceSensorARC leftDistance_sensorARC = null;
	protected DistanceSensorARC rightDistance_sensorARC = null;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		String bundleId = bundleContext.getBundle().getSymbolicName();
		
		this.frontDistance_sensorARC = new DistanceSensorARC(bundleContext, bundleId + ".FrontDistanceSensor", "LIDAR-FrontDistanceSensor");
		this.frontDistance_sensorARC.start();

		this.rearDistance_sensorARC = new DistanceSensorARC(bundleContext, bundleId + ".RearDistanceSensor", "LIDAR-RearDistanceSensor");
		this.rearDistance_sensorARC.start();

		this.leftDistance_sensorARC = new DistanceSensorARC(bundleContext, bundleId + ".LeftDistanceSensor", "LIDAR-LeftDistanceSensor");
		this.leftDistance_sensorARC.start();

		this.rightDistance_sensorARC = new DistanceSensorARC(bundleContext, bundleId + ".RightDistanceSensor", "LIDAR-RightDistanceSensor");
		this.rightDistance_sensorARC.start();
}

	public void stop(BundleContext bundleContext) throws Exception {

		this.frontDistance_sensorARC.stop();	this.frontDistance_sensorARC = null;
		this.rearDistance_sensorARC.stop();		this.rearDistance_sensorARC = null;
		this.leftDistance_sensorARC.stop();		this.leftDistance_sensorARC = null;
		this.rightDistance_sensorARC.stop();	this.rightDistance_sensorARC = null;
		Activator.context = null;
	}

}
