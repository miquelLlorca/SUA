package sua.autonomouscar.notificationservice;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import sua.autonomouscar.infraestructure.interaction.ARC.NotificationServiceARC;

public class Activator implements BundleActivator {

	private static BundleContext context;
	protected NotificationServiceARC serviceARC = null;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		this.serviceARC = new NotificationServiceARC(bundleContext, "NotificationService");
		this.serviceARC.start();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		this.serviceARC.stop();
		this.serviceARC = null;
		Activator.context = null;
	}

}
