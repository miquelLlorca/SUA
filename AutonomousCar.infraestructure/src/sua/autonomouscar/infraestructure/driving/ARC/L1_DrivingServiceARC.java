package sua.autonomouscar.infraestructure.driving.ARC;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;
import sua.autonomouscar.devices.interfaces.IDistanceSensor;
import sua.autonomouscar.devices.interfaces.ILineSensor;
import sua.autonomouscar.driving.interfaces.IL1_DrivingService;
import sua.autonomouscar.interaction.interfaces.INotificationService;

public abstract class L1_DrivingServiceARC extends L0_DrivingServiceARC {
	
	
	public static String REQUIRED_FRONTDISTANCESENSOR = "required_frontdistancesensor";
	
	public static String REQUIRED_RIGHTLINESENSOR = "required_rightlinesensor";
	public static String REQUIRED_LEFTLINESENSOR = "required_leftlinesensor";
	
	public static String REQUIRED_NOTIFICATIONSERVICE = "required_notificationservice";
	
	public static String PARAMETER_LONGITUDINALSECURITYDISTANCE = "parameter_longitudinalsecuritydistance";


	public L1_DrivingServiceARC(BundleContext context, String bundleId) {
		super(context, bundleId);
	}
	
	protected IL1_DrivingService getTheL1DrivingService() {
		return (IL1_DrivingService) this.getTheDrivingService();
	}
	

	
	@Override
	public IAdaptiveReadyComponent bindService(String req, Object value) {
		if (req.equals(REQUIRED_FRONTDISTANCESENSOR))
			this.getTheL1DrivingService().setFrontDistanceSensor((IDistanceSensor)value);
		else if (req.equals(REQUIRED_RIGHTLINESENSOR))
			this.getTheL1DrivingService().setRightLineSensor((ILineSensor) value);
		else if (req.equals(REQUIRED_LEFTLINESENSOR))
			this.getTheL1DrivingService().setLeftLineSensor((ILineSensor) value);
		else if (req.equals(REQUIRED_NOTIFICATIONSERVICE))
			this.getTheL1DrivingService().setNotificationService((INotificationService)value);
		return super.bindService(req, value);
	}
	
	@Override
	public IAdaptiveReadyComponent unbindService(String req, Object value) {
		if (req.equals(REQUIRED_FRONTDISTANCESENSOR))
			this.getTheL1DrivingService().setFrontDistanceSensor(null);
		else if (req.equals(REQUIRED_RIGHTLINESENSOR))
			this.getTheL1DrivingService().setRightLineSensor(null);
		else if (req.equals(REQUIRED_LEFTLINESENSOR))
			this.getTheL1DrivingService().setLeftLineSensor(null);
		else if (req.equals(REQUIRED_NOTIFICATIONSERVICE))
			this.getTheL1DrivingService().setNotificationService(null);
		return super.unbindService(req, value);
	}
	
	@Override
	public IAdaptiveReadyComponent setParameter(String parameter, Object value) {

		if (parameter.equals(PARAMETER_LONGITUDINALSECURITYDISTANCE)) {
			this.getTheL1DrivingService().setLongitudinalSecurityDistance((int) value);
		}
		return super.setParameter(parameter, value);
	}

}
