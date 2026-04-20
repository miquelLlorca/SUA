package sua.autonomouscar.interaction.steeringwheel;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import sua.autonomouscar.infraestructure.interaction.ARC.HapticVibrationARC;

public class Activator implements BundleActivator {

	private static BundleContext context;
	protected HapticVibrationARC im_hapticvibrationARC = null;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

		String deviceId = "SteeringWheel";
		
		this.im_hapticvibrationARC = new HapticVibrationARC(bundleContext, deviceId);
		this.im_hapticvibrationARC.start();

}

	public void stop(BundleContext bundleContext) throws Exception {
		this.im_hapticvibrationARC.stop();
		this.im_hapticvibrationARC = null;
		Activator.context = null;
	}

}
