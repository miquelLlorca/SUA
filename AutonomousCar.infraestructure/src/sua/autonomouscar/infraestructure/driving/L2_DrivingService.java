package sua.autonomouscar.infraestructure.driving;

import org.osgi.framework.BundleContext;

import sua.autonomouscar.devices.interfaces.IDistanceSensor;
import sua.autonomouscar.devices.interfaces.IEngine;
import sua.autonomouscar.devices.interfaces.ISteering;
import sua.autonomouscar.driving.interfaces.IL2_DrivingService;

public abstract class L2_DrivingService extends L1_DrivingService implements IL2_DrivingService {

	public final static String LATERAL_SECURITY_DISTANCE = "lateral-security-distance";  // expressed in cms

	protected IEngine engine = null;
	protected ISteering steering = null;
	
	protected IDistanceSensor rearDistanceSensor = null;
	protected IDistanceSensor rightDistanceSensor = null;
	protected IDistanceSensor leftDistanceSensor = null;

	
	public L2_DrivingService(BundleContext context, String id) {
		super(context, id);
		this.addImplementedInterface(IL2_DrivingService.class.getName());
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
	public void setLateralSecurityDistance(int distance) {
		this.setProperty(L2_DrivingService.LATERAL_SECURITY_DISTANCE, distance);
	}	
	
	@Override
	public int getLateralSecurityDistance() {
		return (int)this.getProperty(L2_DrivingService.LATERAL_SECURITY_DISTANCE);
	}

	
	
	@Override
	public void setRearDistanceSensor(IDistanceSensor sensor) {
		this.rearDistanceSensor = sensor;
		return;
	}

	protected IDistanceSensor getRearDistanceSensor() {
		return this.rearDistanceSensor;
	}

	@Override
	public void setRightDistanceSensor(IDistanceSensor sensor) {
		this.rightDistanceSensor = sensor;
		return;
	}

	protected IDistanceSensor getRightDistanceSensor() {
		return this.rightDistanceSensor;
	}

	@Override
	public void setLeftDistanceSensor(IDistanceSensor sensor) {
		this.leftDistanceSensor = sensor;
		return;
	}

	protected IDistanceSensor getLeftDistanceSensor() {
		return this.leftDistanceSensor;
	}

	
	@Override
	protected boolean checkRequirementsToPerfomTheDrivingService() {
		boolean ok = true;
		if ( this.getEngine() == null ) {
			ok = false;
			logger.warn("Required Engine ...");
		}
		if ( this.getSteering() == null ) {
			ok = false;
			logger.warn("Required Steering ...");
		}
		if ( this.getLeftDistanceSensor() == null ) {
			ok = false;
			logger.warn("Required Left Distance Sensor ...");
		}
		if ( this.getRightDistanceSensor() == null ) {
			ok = false;
			logger.warn("Required Right Distance Sensor ...");
		}
		if ( this.getRearDistanceSensor() == null ) {
			ok = false;
			logger.warn("Required Rear Distance Sensor ...");
		}
		if ( !super.checkRequirementsToPerfomTheDrivingService() )
			ok = false;
		
		return ok;
	}

}
