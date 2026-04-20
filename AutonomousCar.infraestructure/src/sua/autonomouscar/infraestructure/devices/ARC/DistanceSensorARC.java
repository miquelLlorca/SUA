package sua.autonomouscar.infraestructure.devices.ARC;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.impl.AdaptiveReadyComponent;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;
import es.upv.pros.tatami.osgi.utils.logger.SmartLogger;
import sua.autonomouscar.devices.interfaces.IDistanceSensor;
import sua.autonomouscar.infraestructure.devices.DistanceSensor;

public class DistanceSensorARC extends AdaptiveReadyComponent implements IAdaptiveReadyComponent {
	
	public static String PROVIDED_SENSOR = "provided_sensor";
	protected IDistanceSensor sensor = null;
	
	public DistanceSensorARC(BundleContext context, String bundleId, String id) {
		super(context, bundleId);
//		logger = SmartLogger.getLogger(DistanceSensorARC.class.getName());
		logger = SmartLogger.getLogger(context.getBundle().getSymbolicName());
		this.sensor = new DistanceSensor(this.context, id);
	}
	
	@Override
	public IAdaptiveReadyComponent deploy() {
		((DistanceSensor)this.sensor).registerThing();
		return super.deploy();
	}
	
	@Override
	public IAdaptiveReadyComponent undeploy() {
		((DistanceSensor)this.sensor).unregisterThing();
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
