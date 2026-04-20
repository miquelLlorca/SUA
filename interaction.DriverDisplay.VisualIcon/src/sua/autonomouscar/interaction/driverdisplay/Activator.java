package sua.autonomouscar.interaction.driverdisplay;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import sua.autonomouscar.infraestructure.interaction.ARC.VisualIconARC;

public class Activator implements BundleActivator {

	private static BundleContext context;
	protected VisualIconARC im_visualiconARC = null;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		String deviceId = "DriverDisplay.VisualIcon";
		
		this.im_visualiconARC = new VisualIconARC(bundleContext, deviceId);
		this.im_visualiconARC.start();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		this.im_visualiconARC.stop();
		this.im_visualiconARC = null;
		Activator.context = null;
	}

}
