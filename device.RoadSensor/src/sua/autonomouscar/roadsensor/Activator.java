package sua.autonomouscar.roadsensor;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import sua.autonomouscar.infraestructure.devices.ARC.RoadSensorARC;

public class Activator implements BundleActivator {

	private static BundleContext context;
	protected RoadSensorARC roadSensorARC = null;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		this.roadSensorARC = new RoadSensorARC(bundleContext, "RoadSensor");
		this.roadSensorARC.start();
		
		// Activamos este servicio por defecto (para facilitar la simulación)
		this.roadSensorARC.deploy();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		this.roadSensorARC.stop();
		this.roadSensorARC = null;
		Activator.context = null;
	}

}
