package sua.autonomouscar.driving.emergencyfallbackplan;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.osgi.utils.logger.SmartLogger;
import sua.autonomouscar.driving.interfaces.IEmergencyFallbackPlan;
import sua.autonomouscar.infraestructure.driving.ARC.FallbackPlanARC;

public class EmergencyFallbackPlanARC extends FallbackPlanARC {
	

	public EmergencyFallbackPlanARC(BundleContext context, String id) {
		super(context, context.getBundle().getSymbolicName());
		logger = SmartLogger.getLogger(context.getBundle().getSymbolicName());
		this.setTheDrivingService(new EmergencyFallbackPlan(this.context, id));
	}
	
	protected IEmergencyFallbackPlan getTheEmergencyFallbackPlanDrivingService() {
		return (IEmergencyFallbackPlan) this.getTheFallbackPlanDrivingService();
	}
	
	
	@Override
	public Object getServiceSupply(String serviceSupply) {
		if (serviceSupply.equals(PROVIDED_DRIVINGSERVICE))
			return this.getTheEmergencyFallbackPlanDrivingService();
		
		return super.getServiceSupply(serviceSupply);
	}

}
