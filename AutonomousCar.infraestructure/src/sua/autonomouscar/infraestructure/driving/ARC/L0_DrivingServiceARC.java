package sua.autonomouscar.infraestructure.driving.ARC;

import org.osgi.framework.BundleContext;

import sua.autonomouscar.driving.interfaces.IL0_DrivingService;

public abstract class L0_DrivingServiceARC extends DrivingServiceARC {
	

	public L0_DrivingServiceARC(BundleContext context, String bundleId) {
		super(context, bundleId);
	}
	
	protected IL0_DrivingService getTheL0DrivingService() {
		return (IL0_DrivingService) this.getTheDrivingService();
	}

	

}
