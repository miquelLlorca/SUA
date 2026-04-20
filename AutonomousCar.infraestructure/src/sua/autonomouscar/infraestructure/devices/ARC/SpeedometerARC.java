package sua.autonomouscar.infraestructure.devices.ARC;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.impl.AdaptiveReadyComponent;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;
import es.upv.pros.tatami.osgi.utils.logger.SmartLogger;
import sua.autonomouscar.devices.interfaces.ISpeedometer;
import sua.autonomouscar.infraestructure.devices.Speedometer;

public class SpeedometerARC extends AdaptiveReadyComponent implements IAdaptiveReadyComponent {
	
	public static String PROVIDED_SENSOR = "provided_sensor";
	protected ISpeedometer sensor = null;
	
	public SpeedometerARC(BundleContext context, String id) {
		super(context, context.getBundle().getSymbolicName());
		logger = SmartLogger.getLogger(context.getBundle().getSymbolicName());
		this.sensor = new Speedometer(this.context, id);
	}
	
	@Override
	public IAdaptiveReadyComponent deploy() {
		((Speedometer)this.sensor).registerThing();
		return super.deploy();
	}
	
	@Override
	public IAdaptiveReadyComponent undeploy() {
		((Speedometer)this.sensor).unregisterThing();
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
