package sua.autonomouscar.driving.interfaces;

import sua.autonomouscar.devices.interfaces.IDistanceSensor;
import sua.autonomouscar.devices.interfaces.ILineSensor;
import sua.autonomouscar.interaction.interfaces.INotificationService;

public interface IL1_DrivingService extends IL0_DrivingService {
	
	public void setFrontDistanceSensor(IDistanceSensor sensor);
	public void setRightLineSensor(ILineSensor sensor);
	public void setLeftLineSensor(ILineSensor sensor);
	
	public void setLongitudinalSecurityDistance(int distance);
	public int getLongitudinalSecurityDistance();
	
	public void setNotificationService(INotificationService service);


}
