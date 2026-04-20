package sua.autonomouscar.driving.l2.acc;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;
import es.upv.pros.tatami.osgi.utils.logger.SmartLogger;
import sua.autonomouscar.infraestructure.driving.ARC.L2_DrivingServiceARC;

public class L2_AdaptiveCruiseControlARC extends L2_DrivingServiceARC {
	
	
	public L2_AdaptiveCruiseControlARC(BundleContext context, String id) {
		super(context, context.getBundle().getSymbolicName());
		logger = SmartLogger.getLogger(context.getBundle().getSymbolicName());
		this.setTheDrivingService(new L2_AdaptiveCruiseControl(this.context, id));
	}
	
	@Override
	public IAdaptiveReadyComponent bindService(String req, Object value) {
		
		if (req.equals(L2_DrivingServiceARC.REQUIRED_STEERING) ) {
			logger.warn("L2 Adaptive Cruise Control: Cannot handle de Steering!");
			return this;
		}

		return super.bindService(req, value);
	}

	@Override
	public IAdaptiveReadyComponent unbindService(String req, Object value) {
		
		if (req.equals(L2_DrivingServiceARC.REQUIRED_STEERING) ) {
			logger.warn("L2 Adaptive Cruise Control: Cannot handle de Steering!");
			return this;
		}

		return super.bindService(req, value);
	}


}
