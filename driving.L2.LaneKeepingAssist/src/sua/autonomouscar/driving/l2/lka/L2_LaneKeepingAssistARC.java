package sua.autonomouscar.driving.l2.lka;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;
import es.upv.pros.tatami.osgi.utils.logger.SmartLogger;
import sua.autonomouscar.infraestructure.driving.ARC.L2_DrivingServiceARC;

public class L2_LaneKeepingAssistARC extends L2_DrivingServiceARC {
	
	public L2_LaneKeepingAssistARC(BundleContext context, String id) {
		super(context, context.getBundle().getSymbolicName());
		logger = SmartLogger.getLogger(context.getBundle().getSymbolicName());
		this.setTheDrivingService(new L2_LaneKeepingAssist(this.context, id));
	}
	
	@Override
	public IAdaptiveReadyComponent bindService(String req, Object value) {
		
		if (req.equals(L2_DrivingServiceARC.REQUIRED_ENGINE) ) {
			logger.warn("L2 Lane Keeping Assist: Cannot handle de Engine!");
			return this;
		}

		return super.bindService(req, value);
	}

	@Override
	public IAdaptiveReadyComponent unbindService(String req, Object value) {
		
		if (req.equals(L2_DrivingServiceARC.REQUIRED_ENGINE) ) {
			logger.warn("L2 Lane Keeping Assist: Cannot handle de Engine!");
			return this;
		}

		return super.bindService(req, value);
	}
}
