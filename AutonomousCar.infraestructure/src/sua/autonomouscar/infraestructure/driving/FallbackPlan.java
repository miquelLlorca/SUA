package sua.autonomouscar.infraestructure.driving;

import org.osgi.framework.BundleContext;

import sua.autonomouscar.devices.interfaces.IEngine;
import sua.autonomouscar.devices.interfaces.ISteering;
import sua.autonomouscar.driving.interfaces.IFallbackPlan;
import sua.autonomouscar.interaction.interfaces.INotificationService;

public abstract class FallbackPlan extends DrivingService implements IFallbackPlan {

	protected IEngine engine = null;
	protected ISteering steering = null;

	protected INotificationService notificationService = null;
	
	public FallbackPlan(BundleContext context, String id) {
		super(context, id);
		this.addImplementedInterface(IFallbackPlan.class.getName());
	}
	
	@Override
	public void setEngine(IEngine engine) {
		this.engine = engine;
		return;
	}
	
	protected IEngine getEngine() {
		return this.engine;
	}
	
	@Override
	public void setSteering(ISteering steering) {
		this.steering = steering;
		return;
	}

	protected ISteering getSteering() {
		return this.steering;
	}

	@Override
	public void setNotificationService(INotificationService service) {
		this.notificationService = service;
		return;
	}
	
	protected INotificationService getNotificationService() {
		return this.notificationService;
	}

	@Override
	protected boolean checkRequirementsToPerfomTheDrivingService() {
		boolean ok = true;
		if ( this.getEngine() == null ) {
			ok = true;
			logger.warn("Required Engine ...");
		}
		if ( this.getSteering() == null ) {
			ok = true;
			logger.warn("Required Steering ...");
		}
		
		return ok;
	}

	
	

}
