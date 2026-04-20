package sua.autonomouscar.infraestructure.driving;

import org.osgi.framework.BundleContext;

import sua.autonomouscar.devices.interfaces.IDistanceSensor;
import sua.autonomouscar.devices.interfaces.ILineSensor;
import sua.autonomouscar.driving.interfaces.IDrivingService;
import sua.autonomouscar.driving.interfaces.IL1_DrivingService;
import sua.autonomouscar.interaction.interfaces.INotificationService;

public abstract class L1_DrivingService extends L0_DrivingService implements IL1_DrivingService {

	public final static String LONGITUDINAL_SECURITY_DISTANCE = "longitudinal-security-distance";   // expressed in cms

	protected IDistanceSensor frontDistanceSensor = null;
	protected ILineSensor rightLineSensor = null;
	protected ILineSensor leftLineSensor = null;

	protected INotificationService notificationService = null;
	
	public L1_DrivingService(BundleContext context, String id) {
		super(context, id);
		this.addImplementedInterface(IL1_DrivingService.class.getName());
	}

	@Override
	public void setLongitudinalSecurityDistance(int distance) {
		this.setProperty(L1_DrivingService.LONGITUDINAL_SECURITY_DISTANCE, distance);
		return;
	}
	
	@Override
	public int getLongitudinalSecurityDistance() {
		return (int)this.getProperty(L1_DrivingService.LONGITUDINAL_SECURITY_DISTANCE);
	}
	
	
	@Override
	public void setFrontDistanceSensor(IDistanceSensor sensor) {
		this.frontDistanceSensor = sensor;
		return;
	}
	
	protected IDistanceSensor getFrontDistanceSensor() {
		return this.frontDistanceSensor;
	}

	
	@Override
	public void setRightLineSensor(ILineSensor sensor) {
		this.rightLineSensor = sensor;
		return;
	}

	protected ILineSensor getRightLineSensor() {
		return this.rightLineSensor;
	}

	@Override
	public void setLeftLineSensor(ILineSensor sensor) {
		this.leftLineSensor = sensor;
		return;
	}
	
	protected ILineSensor getLeftLineSensor() {
		return this.leftLineSensor;
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
	public IDrivingService stopTheDrivingFunction() {
		return this;
	}
	
	@Override
	protected boolean checkRequirementsToPerfomTheDrivingService() {
		boolean ok = true;
		if ( this.getFrontDistanceSensor() == null ) {
			ok = false;
			logger.warn("Required Front Distance Sensor ...");
		}
		if ( this.getLeftLineSensor() == null ) {
			ok = false;
			logger.warn("Required Left Line Sensor ...");
		}
		if ( this.getRightLineSensor() == null ) {
			ok = false;
			logger.warn("Required Right Line Sensor ...");
		}
		if ( !super.checkRequirementsToPerfomTheDrivingService() )
			ok = false;
		
		return ok;
	}




}
