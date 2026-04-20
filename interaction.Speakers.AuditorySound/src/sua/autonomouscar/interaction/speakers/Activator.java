package sua.autonomouscar.interaction.speakers;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import sua.autonomouscar.infraestructure.interaction.ARC.AuditorySoundARC;

public class Activator implements BundleActivator {

	private static BundleContext context;
	protected AuditorySoundARC im_soundauditoryARC = null;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		String deviceId = "Speakers.AuditorySound";
		
		this.im_soundauditoryARC = new AuditorySoundARC(bundleContext, deviceId);
		this.im_soundauditoryARC.start();

	}

	public void stop(BundleContext bundleContext) throws Exception {
		this.im_soundauditoryARC.stop();
		this.im_soundauditoryARC = null;
		Activator.context = null;
	}

}
