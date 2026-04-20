package sua.autonomouscar.driving.interfaces;

import sua.autonomouscar.devices.interfaces.IDistanceSensor;
import sua.autonomouscar.devices.interfaces.IEngine;
import sua.autonomouscar.devices.interfaces.ISteering;

public interface IL2_DrivingService extends IL1_DrivingService {
		
	public void setEngine(IEngine engine);
	public void setSteering(ISteering steering);
	
	public void setRearDistanceSensor(IDistanceSensor sensor);
	public void setRightDistanceSensor(IDistanceSensor sensor);
	public void setLeftDistanceSensor(IDistanceSensor sensor);

	public void setLateralSecurityDistance(int distance);
	public int getLateralSecurityDistance();
	
}
