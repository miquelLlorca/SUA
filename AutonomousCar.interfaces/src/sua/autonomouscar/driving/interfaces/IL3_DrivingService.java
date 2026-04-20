package sua.autonomouscar.driving.interfaces;

import sua.autonomouscar.devices.interfaces.IHumanSensors;
import sua.autonomouscar.devices.interfaces.IRoadSensor;

public interface IL3_DrivingService extends IL2_DrivingService {
	
	public void setHumanSensors(IHumanSensors humanSensors);
	public void setRoadSensor(IRoadSensor roadSensors);
	
	public void setFallbackPlan(IFallbackPlan plan);

	public void setReferenceSpeed(int speed);
	public int getReferenceSpeed();
	
	public IL3_DrivingService performTheTakeOver();
	public IL3_DrivingService activateTheFallbackPlan();
	
}
