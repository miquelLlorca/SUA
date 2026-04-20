package sua.autonomouscar.driving.interfaces;

import sua.autonomouscar.devices.interfaces.IEngine;
import sua.autonomouscar.devices.interfaces.ISteering;
import sua.autonomouscar.interaction.interfaces.INotificationService;

public interface IFallbackPlan extends IDrivingService {
	
	public void setEngine(IEngine engine);
	public void setSteering(ISteering steering);
	
	public void setNotificationService(INotificationService service);


}
