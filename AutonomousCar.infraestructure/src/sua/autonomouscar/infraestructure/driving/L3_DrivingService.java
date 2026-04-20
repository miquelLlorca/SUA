package sua.autonomouscar.infraestructure.driving;

import org.osgi.framework.BundleContext;

import sua.autonomouscar.devices.interfaces.IHumanSensors;
import sua.autonomouscar.devices.interfaces.IRoadSensor;
import sua.autonomouscar.driving.interfaces.IFallbackPlan;
import sua.autonomouscar.driving.interfaces.IL3_DrivingService;

public abstract class L3_DrivingService extends L2_DrivingService implements IL3_DrivingService {

	public final static String REFERENCE_SPEED = "reference-speed";
	
	protected IHumanSensors humanSensors = null;
	protected IRoadSensor roadSensor = null;
	protected IFallbackPlan fallbackPlan = null;
	
	protected int lateralSecurityDistance = 1;
	
	public L3_DrivingService(BundleContext context, String id) {
		super(context, id);
		this.addImplementedInterface(IL3_DrivingService.class.getName());
	}
	
	@Override
	public void setHumanSensors(IHumanSensors humanSensors) {
		this.humanSensors = humanSensors;
		return;
	}

	protected IHumanSensors getHumanSensors() {
		return this.humanSensors;
	}

	@Override
	public void setRoadSensor(IRoadSensor sensor) {
		this.roadSensor = sensor;
	}
	
	protected IRoadSensor getRoadSensor() {
		return this.roadSensor;
	}
	
	@Override
	public void setFallbackPlan(IFallbackPlan plan) {
		this.fallbackPlan = plan;
		return;
	}
	
	protected IFallbackPlan getFallbackPlan() {
		return this.fallbackPlan;
	}

	
	@Override
	public void setReferenceSpeed(int speed) {
		this.setProperty(L3_DrivingService.REFERENCE_SPEED, speed);
	}
	
	@Override
	public int getReferenceSpeed() {
		return (int)this.getProperty(L3_DrivingService.REFERENCE_SPEED);
	}

	@Override
	public IL3_DrivingService performTheTakeOver() {
		this.stopDriving();
		this.getNotificationService().notify("Exited Autonomous Mode");
		return this;
	}

	@Override
	public IL3_DrivingService activateTheFallbackPlan() {
		this.stopDriving();
		this.getFallbackPlan().startDriving();

		return this;
	}
	
	@Override
	protected boolean checkRequirementsToPerfomTheDrivingService() {
		boolean ok = true;
		if ( this.getHumanSensors() == null ) {
			ok = false;
			logger.warn("Required Human Sensors ...");
		}
		if ( this.getRoadSensor() == null ) {
			ok = false;
			logger.warn("Required Road Sensor ...");
		}
		if ( this.getFallbackPlan() == null ) {
			ok = false;
			logger.warn("Required Fallback Plan ...");
		}
		if ( !super.checkRequirementsToPerfomTheDrivingService() )
			ok = false;
		
		return ok;
	}
}
