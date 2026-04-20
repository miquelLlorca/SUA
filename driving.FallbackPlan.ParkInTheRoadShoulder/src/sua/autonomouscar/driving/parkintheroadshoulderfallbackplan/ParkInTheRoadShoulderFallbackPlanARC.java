package sua.autonomouscar.driving.parkintheroadshoulderfallbackplan;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;
import es.upv.pros.tatami.osgi.utils.logger.SmartLogger;
import sua.autonomouscar.devices.interfaces.IDistanceSensor;
import sua.autonomouscar.devices.interfaces.ILineSensor;
import sua.autonomouscar.driving.interfaces.IParkInTheRoadShoulderFallbackPlan;
import sua.autonomouscar.infraestructure.driving.ARC.FallbackPlanARC;

public class ParkInTheRoadShoulderFallbackPlanARC extends FallbackPlanARC {
	
	public static String REQUIRED_RIGHTLINESENSOR = "required_rightlinesensor";
	public static String REQUIRED_RIGHTDISTANCESENSOR = "required_rightdistancesensor";
		
	public ParkInTheRoadShoulderFallbackPlanARC(BundleContext context, String id) {
		super(context, context.getBundle().getSymbolicName());
		logger = SmartLogger.getLogger(context.getBundle().getSymbolicName());
		this.setTheDrivingService(new ParkInTheRoadShoulderFallbackPlan(this.context, id));
	}
	
	protected IParkInTheRoadShoulderFallbackPlan getTheParkInTheRoadShoulderFallbackPlanDrivingService() {
		return (IParkInTheRoadShoulderFallbackPlan) this.getTheFallbackPlanDrivingService();
	}
	
	@Override
	public IAdaptiveReadyComponent bindService(String req, Object value) {
		if (req.equals(REQUIRED_RIGHTLINESENSOR))
			this.getTheParkInTheRoadShoulderFallbackPlanDrivingService().setRightLineSensor((ILineSensor) value);
		else if (req.equals(REQUIRED_RIGHTDISTANCESENSOR))
			this.getTheParkInTheRoadShoulderFallbackPlanDrivingService().setRightDistanceSensor((IDistanceSensor) value);
		
		return super.bindService(req, value);
	}

	@Override
	public IAdaptiveReadyComponent unbindService(String req, Object value) {
		if (req.equals(REQUIRED_RIGHTLINESENSOR))
			this.getTheParkInTheRoadShoulderFallbackPlanDrivingService().setRightLineSensor(null);
		else if (req.equals(REQUIRED_RIGHTDISTANCESENSOR))
			this.getTheParkInTheRoadShoulderFallbackPlanDrivingService().setRightDistanceSensor(null);
		
		return super.unbindService(req, value);
	}

		
	@Override
	public Object getServiceSupply(String serviceSupply) {
		if (serviceSupply.equals(PROVIDED_DRIVINGSERVICE))
			return this.getTheParkInTheRoadShoulderFallbackPlanDrivingService();
		
		return super.getServiceSupply(serviceSupply);
	}

}
