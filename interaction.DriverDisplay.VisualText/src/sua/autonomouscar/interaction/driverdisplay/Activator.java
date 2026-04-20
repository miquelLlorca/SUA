package sua.autonomouscar.interaction.driverdisplay;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import sua.autonomouscar.infraestructure.interaction.ARC.VisualTextARC;

public class Activator implements BundleActivator {

	private static BundleContext context;
	protected VisualTextARC im_visualtextARC = null;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		String deviceId = "DriverDisplay.VisualText";
		
		this.im_visualtextARC = new VisualTextARC(bundleContext, deviceId);
		this.im_visualtextARC.start();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		this.im_visualtextARC.stop();
		this.im_visualtextARC = null;
		Activator.context = null;
	}

}
