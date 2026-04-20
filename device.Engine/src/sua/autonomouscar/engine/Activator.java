package sua.autonomouscar.engine;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import sua.autonomouscar.infraestructure.devices.ARC.EngineARC;


public class Activator implements BundleActivator {

	private static BundleContext context;
	private EngineARC engineARC = null;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		this.engineARC = new EngineARC(bundleContext, "Engine");
		this.engineARC.start();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		this.engineARC.stop();
		this.engineARC = null;
		Activator.context = null;
	}

}
