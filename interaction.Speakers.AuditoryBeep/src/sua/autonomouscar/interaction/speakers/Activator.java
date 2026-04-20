package sua.autonomouscar.interaction.speakers;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import sua.autonomouscar.infraestructure.interaction.ARC.AuditoryBeepARC;

public class Activator implements BundleActivator {

	private static BundleContext context;
	protected AuditoryBeepARC im_soundbeepARC = null;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		String deviceId = "Speakers.AuditoryBeep";
		this.im_soundbeepARC = new AuditoryBeepARC(bundleContext, deviceId);
		this.im_soundbeepARC.start();

}

	public void stop(BundleContext bundleContext) throws Exception {
		this.im_soundbeepARC.stop();
		this.im_soundbeepARC = null;
		Activator.context = null;
	}

}
