package sua.autonomouscar.interaction.seats;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import sua.autonomouscar.infraestructure.interaction.ARC.HapticVibrationARC;

public class Activator implements BundleActivator {

	private static BundleContext context;
	protected HapticVibrationARC im_hapticvibration_driverSeatARC = null;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
				
		this.im_hapticvibration_driverSeatARC = new HapticVibrationARC(bundleContext, "DriverSeat.HapticVibration");
		this.im_hapticvibration_driverSeatARC.start();
}

	public void stop(BundleContext bundleContext) throws Exception {
		this.im_hapticvibration_driverSeatARC.stop();
		this.im_hapticvibration_driverSeatARC = null;
		Activator.context = null;
	}

}
