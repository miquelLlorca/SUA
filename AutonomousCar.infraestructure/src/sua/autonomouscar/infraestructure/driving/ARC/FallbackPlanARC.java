package sua.autonomouscar.infraestructure.driving.ARC;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;
import sua.autonomouscar.devices.interfaces.IEngine;
import sua.autonomouscar.devices.interfaces.ISteering;
import sua.autonomouscar.driving.interfaces.IFallbackPlan;
import sua.autonomouscar.infraestructure.Thing;
import sua.autonomouscar.interaction.interfaces.INotificationService;

public abstract class FallbackPlanARC extends DrivingServiceARC {
	
	public static String REQUIRED_ENGINE = "required_engine";
	public static String REQUIRED_STEERING = "required_steering";
	public static String REQUIRED_NOTIFICATIONSERVICE = "required_notificationservice";

	public FallbackPlanARC(BundleContext context, String bundleId) {
		super(context, bundleId);
	}
		
	@Override
	public IAdaptiveReadyComponent deploy() {
		// super.deploy();
		((Thing) this.getTheDrivingService()).registerThing();
		// theService.startDriving();       // inhabilitamos la activación inicial automática para este tipo de servicios de conducción
		return this;
	}
	
	protected IFallbackPlan getTheFallbackPlanDrivingService() {
		return (IFallbackPlan) this.getTheDrivingService();
	}
	
	@Override
	public IAdaptiveReadyComponent bindService(String req, Object value) {
		if (req.equals(REQUIRED_ENGINE))
			this.getTheFallbackPlanDrivingService().setEngine((IEngine) value);
		else if ( req.equals(REQUIRED_STEERING) )
			this.getTheFallbackPlanDrivingService().setSteering((ISteering) value);
		else if (req.equals(REQUIRED_NOTIFICATIONSERVICE))
			this.getTheFallbackPlanDrivingService().setNotificationService((INotificationService) value);
		return super.bindService(req, value);
	}

	@Override
	public IAdaptiveReadyComponent unbindService(String req, Object value) {
		if (req.equals(REQUIRED_ENGINE))
			this.getTheFallbackPlanDrivingService().setEngine(null);
		else if ( req.equals(REQUIRED_STEERING) )
			this.getTheFallbackPlanDrivingService().setSteering(null);
		else if (req.equals(REQUIRED_NOTIFICATIONSERVICE))
			this.getTheFallbackPlanDrivingService().setNotificationService(null);
		return super.unbindService(req, value);
	}
	

}
