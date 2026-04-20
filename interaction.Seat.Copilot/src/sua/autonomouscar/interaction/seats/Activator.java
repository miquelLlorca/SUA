package sua.autonomouscar.interaction.seats;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import sua.autonomouscar.infraestructure.interaction.ARC.HapticVibrationARC;

public class Activator implements BundleActivator {

	private static BundleContext context;
	protected HapticVibrationARC im_hapticvibration_copilotSeatARC = null;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

		this.im_hapticvibration_copilotSeatARC = new HapticVibrationARC(bundleContext, "CopilotSeat.HapticVibration");
		this.im_hapticvibration_copilotSeatARC.start();

}

	public void stop(BundleContext bundleContext) throws Exception {
		this.im_hapticvibration_copilotSeatARC.stop();
		this.im_hapticvibration_copilotSeatARC = null;
		Activator.context = null;
	}

}
