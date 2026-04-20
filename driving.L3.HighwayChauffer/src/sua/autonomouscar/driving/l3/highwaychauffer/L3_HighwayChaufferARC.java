package sua.autonomouscar.driving.l3.highwaychauffer;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.osgi.utils.logger.SmartLogger;
import sua.autonomouscar.infraestructure.driving.ARC.L3_DrivingServiceARC;

public class L3_HighwayChaufferARC extends L3_DrivingServiceARC {
	
	public L3_HighwayChaufferARC(BundleContext context, String id) {
		super(context, context.getBundle().getSymbolicName());
		logger = SmartLogger.getLogger(context.getBundle().getSymbolicName());
		this.setTheDrivingService(new L3_HighwayChauffer(this.context, id));
	}
	
	
}
