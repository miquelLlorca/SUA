package sua.autonomouscar.infraestructure.devices.ARC;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.impl.AdaptiveReadyComponent;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;
import es.upv.pros.tatami.osgi.utils.logger.SmartLogger;
import sua.autonomouscar.devices.interfaces.IHandsOnWheelSensor;
import sua.autonomouscar.infraestructure.devices.HandsOnWheelSensor;

public class HandsOnWheelSensorARC extends AdaptiveReadyComponent implements IAdaptiveReadyComponent {
	
	public static String PROVIDED_SENSOR = "provided_sensor";
	protected IHandsOnWheelSensor sensor = null;
	
	public HandsOnWheelSensorARC(BundleContext context, String bundleId, String id) {
		super(context, bundleId);
//		super(context, context.getBundle().getSymbolicName());
		logger = SmartLogger.getLogger(bundleId);
		this.sensor = new HandsOnWheelSensor(this.context, id);
	}
	
	@Override
	public IAdaptiveReadyComponent deploy() {
		((HandsOnWheelSensor)this.sensor).registerThing();
		return super.deploy();
	}
	
	@Override
	public IAdaptiveReadyComponent undeploy() {
		((HandsOnWheelSensor)this.sensor).unregisterThing();
		this.sensor = null;
		return super.undeploy();
	}
	
	@Override
	public Object getServiceSupply(String serviceSupply) {
		if (serviceSupply.equals(PROVIDED_SENSOR)) {
			super.getServiceSupply(serviceSupply);
			return this.sensor;
		}
		
		return super.getServiceSupply(serviceSupply);
	}

}
