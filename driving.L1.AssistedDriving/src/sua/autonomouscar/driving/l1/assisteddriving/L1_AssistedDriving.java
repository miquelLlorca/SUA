package sua.autonomouscar.driving.l1.assisteddriving;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.osgi.utils.logger.SmartLogger;
import sua.autonomouscar.driving.interfaces.IDrivingService;
import sua.autonomouscar.driving.interfaces.IL1_AssistedDriving;
import sua.autonomouscar.infraestructure.driving.L1_DrivingService;

public class L1_AssistedDriving extends L1_DrivingService implements IL1_AssistedDriving {
	
	public static final int DEFAULT_LONGITUDINAL_SECURITY_DISTANCE = 10000;
	
	public L1_AssistedDriving(BundleContext context, String id) {
		super(context, id);
		logger = SmartLogger.getLogger(context.getBundle().getSymbolicName());
		this.addImplementedInterface(IL1_AssistedDriving.class.getName());
		this.setLongitudinalSecurityDistance(DEFAULT_LONGITUDINAL_SECURITY_DISTANCE); // cms
	}


	
	@Override
	public IDrivingService performTheDrivingFunction() {
		
		boolean correction_required = false;
		
		// Reducimos la velocidad un poco si detectamos distancia frontal inferior a distancia de seguridad
		if ( this.getLongitudinalSecurityDistance() > this.getFrontDistanceSensor().getDistance() ) {
			logger.info("Font Distance Warning: ⊼");
			if ( this.notificationService != null )
				this.getNotificationService().notify("Font Distance Warning: ⊼");
				
		}

		// Control de la dirección si nos salimos del carril
		if ( this.getLeftLineSensor().isLineDetected() ) {
			correction_required = true;
			logger.info("Left Line Warning: |<");
			if ( this.notificationService != null )
				this.getNotificationService().notify("Left Line Warning: |<");
		}
		
		if ( this.getRightLineSensor().isLineDetected() ) {
			correction_required = true;
			logger.info("Right Line Warning: >|");
			if ( this.notificationService != null )
				this.getNotificationService().notify("Right Line Warning: >|");
		}
		
		// Si todo va bien, indicamos que seguimos como estamos ...
		if ( !correction_required ) {
			logger.info("Monitoring driving parameters. Nothing to warn ...");
		}
		
		
		return this;
	}	
	
	



}
