package sua.autonomouscar.driving.l1.assisteddriving;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.osgi.utils.logger.SmartLogger;
import sua.autonomouscar.driving.interfaces.IL1_AssistedDriving;
import sua.autonomouscar.infraestructure.driving.ARC.L1_DrivingServiceARC;

public class L1_AssistedDrivingARC extends L1_DrivingServiceARC {
	
	public L1_AssistedDrivingARC(BundleContext context, String id) {
		super(context, context.getBundle().getSymbolicName());
		logger = SmartLogger.getLogger(context.getBundle().getSymbolicName());
		this.setTheDrivingService(new L1_AssistedDriving(this.context, id));
	}

	protected IL1_AssistedDriving getTheL1_AssistedDrivingService() {
		return (IL1_AssistedDriving) this.getTheDrivingService();
	}
	

}
