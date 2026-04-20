package sua.autonomouscar.infraestructure.driving.ARC;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;
import sua.autonomouscar.devices.interfaces.IDistanceSensor;
import sua.autonomouscar.devices.interfaces.IEngine;
import sua.autonomouscar.devices.interfaces.ISteering;
import sua.autonomouscar.driving.interfaces.IL2_DrivingService;

public abstract class L2_DrivingServiceARC extends L1_DrivingServiceARC {
	
	public static String REQUIRED_ENGINE = "required_engine";
	public static String REQUIRED_STEERING = "required_steering";
	
	public static String REQUIRED_REARDISTANCESENSOR = "required_reardistancesensor";
	public static String REQUIRED_RIGHTDISTANCESENSOR = "required_rightdistancesensor";
	public static String REQUIRED_LEFTDISTANCESENSOR = "required_leftdistancesensor";
	
	public static String PARAMETER_LATERALSECURITYDISTANCE = "parameter_lateralsecuritydistance";
	
	public L2_DrivingServiceARC(BundleContext context, String bundleId) {
		super(context, bundleId);
    }
	
	protected IL2_DrivingService getTheL2DrivingService() {
		return (IL2_DrivingService) this.getTheDrivingService();
	}
	
	@Override
	public IAdaptiveReadyComponent bindService(String req, Object value) {
		if (req.equals(REQUIRED_ENGINE))
			this.getTheL2DrivingService().setEngine((IEngine) value);
		else if ( req.equals(REQUIRED_STEERING) )
			this.getTheL2DrivingService().setSteering((ISteering) value);
		else if (req.equals(REQUIRED_REARDISTANCESENSOR))
			this.getTheL2DrivingService().setRearDistanceSensor((IDistanceSensor) value);
		else if (req.equals(REQUIRED_RIGHTDISTANCESENSOR))
			this.getTheL2DrivingService().setRightDistanceSensor((IDistanceSensor) value);
		else if (req.equals(REQUIRED_LEFTDISTANCESENSOR))
			this.getTheL2DrivingService().setLeftDistanceSensor((IDistanceSensor) value);

		return super.bindService(req, value);
	}
	
	@Override
	public IAdaptiveReadyComponent unbindService(String req, Object value) {
		if (req.equals(REQUIRED_ENGINE))
			this.getTheL2DrivingService().setEngine(null);
		else if ( req.equals(REQUIRED_STEERING) )
			this.getTheL2DrivingService().setSteering(null);
		else if (req.equals(REQUIRED_REARDISTANCESENSOR))
			this.getTheL2DrivingService().setRearDistanceSensor(null);
		else if (req.equals(REQUIRED_RIGHTDISTANCESENSOR))
			this.getTheL2DrivingService().setRightDistanceSensor(null);
		else if (req.equals(REQUIRED_LEFTDISTANCESENSOR))
			this.getTheL2DrivingService().setLeftDistanceSensor(null);

		return super.unbindService(req, value);
	}
	
	@Override
	public IAdaptiveReadyComponent setParameter(String parameter, Object value) {

		if (parameter.equals(PARAMETER_LATERALSECURITYDISTANCE)) {
			this.getTheL2DrivingService().setLateralSecurityDistance((int) value);
		}
		return super.setParameter(parameter, value);
	}
	

}
