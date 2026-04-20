package sua.autonomouscar.driving.l2.acc;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.osgi.utils.logger.SmartLogger;
import sua.autonomouscar.driving.interfaces.IDrivingService;
import sua.autonomouscar.driving.interfaces.IL2_AdaptiveCruiseControl;
import sua.autonomouscar.infraestructure.devices.Engine;
import sua.autonomouscar.infraestructure.driving.L2_DrivingService;

public class L2_AdaptiveCruiseControl extends L2_DrivingService implements IL2_AdaptiveCruiseControl {
	
	public static final int DEFAULT_LONGITUDINAL_SECURITY_DISTANCE = 10000;
	
	public L2_AdaptiveCruiseControl(BundleContext context, String id) {
		super(context, id);
		logger = SmartLogger.getLogger(context.getBundle().getSymbolicName());
		this.addImplementedInterface(IL2_AdaptiveCruiseControl.class.getName());
		this.setLongitudinalSecurityDistance(DEFAULT_LONGITUDINAL_SECURITY_DISTANCE); // cms
	}
	
	@Override
	public IDrivingService performTheDrivingFunction() {
		
		boolean correction_performed = false;
		
		// Reducimos la velocidad un poco si detectamos distancia frontal inferior a distancia de seguridad
		if ( this.getLongitudinalSecurityDistance() > this.getFrontDistanceSensor().getDistance() ) {
			
				this.getEngine().decelerate(Engine.MEDIUM_ACCELERATION_RPM);
				correction_performed = true;
				logger.info("Font Distance Warning: ⊼");
				this.getNotificationService().notify("Font Distance Warning: Braking ...");
		}
		
		// Si todo va bien, indicamos que seguimos como estamos ...
		if ( !correction_performed ) {
			logger.info("Monitoring driving parameters. Nothing to warn ...");
		}
				
		return this;
	}	
	
	



}
