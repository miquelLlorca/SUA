package sua.autonomouscar.infraestructure.driving.ARC;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;
import sua.autonomouscar.devices.interfaces.IHumanSensors;
import sua.autonomouscar.devices.interfaces.IRoadSensor;
import sua.autonomouscar.driving.interfaces.IFallbackPlan;
import sua.autonomouscar.driving.interfaces.IL3_DrivingService;

public abstract class L3_DrivingServiceARC extends L2_DrivingServiceARC {
	
	
	public static String REQUIRED_HUMANSENSORS = "required_humansensors";
	public static String REQUIRED_ROADSENSOR = "required_roadsensor";
	
	public static String REQUIRED_FALLBACKPLAN = "required_fallbackplan";
	
	public static String PARAMETER_REFERENCESPEED = "parameter_referencespeed";
	
	public L3_DrivingServiceARC(BundleContext context, String bundleId) {
		super(context, bundleId);
    }
	
	protected IL3_DrivingService getTheL3_DrivingService() {
        return (IL3_DrivingService) this.getTheDrivingService();
    }
	
	@Override
	public IAdaptiveReadyComponent bindService(String req, Object value) {
		if (req.equals(REQUIRED_HUMANSENSORS))
			this.getTheL3_DrivingService().setHumanSensors((IHumanSensors) value);
		else if ( req.equals(REQUIRED_ROADSENSOR) )
			this.getTheL3_DrivingService().setRoadSensor((IRoadSensor) value);
		else if (req.equals(REQUIRED_FALLBACKPLAN))
			this.getTheL3_DrivingService().setFallbackPlan((IFallbackPlan) value);

		return super.bindService(req, value);
	}
	
	@Override
	public IAdaptiveReadyComponent unbindService(String req, Object value) {
		if (req.equals(REQUIRED_HUMANSENSORS))
			this.getTheL3_DrivingService().setHumanSensors(null);
		else if ( req.equals(REQUIRED_ROADSENSOR) )
			this.getTheL3_DrivingService().setRoadSensor(null);
		else if (req.equals(REQUIRED_FALLBACKPLAN))
			this.getTheL3_DrivingService().setFallbackPlan(null);

		return super.unbindService(req, value);
	}
	
	@Override
	public IAdaptiveReadyComponent setParameter(String parameter, Object value) {

		if (parameter.equals(PARAMETER_REFERENCESPEED)) {
			this.getTheL3_DrivingService().setReferenceSpeed((int) value);
		}

		return super.setParameter(parameter, value);
	}
	
	@Override
	public Object getServiceSupply(String serviceSupply) {
		if (serviceSupply.equals(PROVIDED_DRIVINGSERVICE)) {
			super.getServiceSupply(serviceSupply);
			return this.getTheL3_DrivingService();
		}
		
		return super.getServiceSupply(serviceSupply);
			
	}


}
