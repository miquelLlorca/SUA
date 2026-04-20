package sua.autonomouscar.driving.l0.manual;

import org.osgi.framework.BundleContext;

import sua.autonomouscar.driving.interfaces.IL0_ManualDriving;
import sua.autonomouscar.infraestructure.driving.ARC.L0_DrivingServiceARC;

public class L0_ManualDrivingARC extends L0_DrivingServiceARC {
	
	public L0_ManualDrivingARC(BundleContext context, String id) {
		super(context, context.getBundle().getSymbolicName());
		this.setTheDrivingService(new L0_ManualDriving(this.context, id));
	}

	protected IL0_ManualDriving getTheL0_ManualDrivingService() {
		return (IL0_ManualDriving) this.getTheDrivingService();
	}
	
	@Override
	public Object getServiceSupply(String serviceSupply) {
		if (serviceSupply.equals(PROVIDED_DRIVINGSERVICE))
			return this.getTheL0_ManualDrivingService();
		
		return super.getServiceSupply(serviceSupply);
	}

}
