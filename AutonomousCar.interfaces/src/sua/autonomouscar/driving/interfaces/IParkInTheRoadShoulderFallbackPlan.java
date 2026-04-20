package sua.autonomouscar.driving.interfaces;

import sua.autonomouscar.devices.interfaces.IDistanceSensor;
import sua.autonomouscar.devices.interfaces.ILineSensor;

public interface IParkInTheRoadShoulderFallbackPlan extends IFallbackPlan {
	
	public void setRightLineSensor(ILineSensor sensor);
	public void setRightDistanceSensor(IDistanceSensor sensor);


}
