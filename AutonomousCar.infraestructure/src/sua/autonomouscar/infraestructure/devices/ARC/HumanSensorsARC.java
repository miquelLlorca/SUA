package sua.autonomouscar.infraestructure.devices.ARC;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.impl.AdaptiveReadyComponent;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;
import es.upv.pros.tatami.osgi.utils.logger.SmartLogger;
import sua.autonomouscar.devices.interfaces.IFaceMonitor;
import sua.autonomouscar.devices.interfaces.IHandsOnWheelSensor;
import sua.autonomouscar.devices.interfaces.IHumanSensors;
import sua.autonomouscar.devices.interfaces.ISeatSensor;
import sua.autonomouscar.infraestructure.devices.HumanSensors;

public class HumanSensorsARC extends AdaptiveReadyComponent implements IAdaptiveReadyComponent {
	
	public static String PROVIDED_SENSOR = "provided_sensor";
	public static String REQUIRED_FACEMONITOR = "required_facemonitor";
	public static String REQUIRED_DRIVERSEATSENSOR = "required_driverseatsensor";
	public static String REQUIRED_COPILOTSEATSENSOR = "required_copilotseatsensor";
	public static String REQUIRED_HANDSONWHEELSENSOR = "required_handsonwheelsensor";
	
	protected IHumanSensors sensors = null;
	
	public HumanSensorsARC(BundleContext context, String bundleId, String id) {
		super(context, bundleId);
		logger = SmartLogger.getLogger(context.getBundle().getSymbolicName());
		this.sensors = new HumanSensors(this.context, id);
	}
	
	@Override
	public IAdaptiveReadyComponent deploy() {
		((HumanSensors)this.sensors).registerThing();
		return super.deploy();
	}
	
	@Override
	public IAdaptiveReadyComponent undeploy() {
		((HumanSensors)this.sensors).unregisterThing();
		this.sensors = null;
		return super.undeploy();
	}
	
	@Override
	public Object getServiceSupply(String serviceSupply) {
		if (serviceSupply.equals(PROVIDED_SENSOR)) {
			super.getServiceSupply(serviceSupply);
			return this.sensors;
		}
		
		return super.getServiceSupply(serviceSupply);
	}

	
	@Override
	public IAdaptiveReadyComponent bindService(String req, Object value) {
		if ( req.equals(REQUIRED_FACEMONITOR) )
			this.sensors.setFaceMonitor((IFaceMonitor) value);
		else if ( req.equals(REQUIRED_DRIVERSEATSENSOR) )
			this.sensors.setDriverSeatSensor((ISeatSensor) value);
		else if ( req.equals(REQUIRED_COPILOTSEATSENSOR) )
			this.sensors.setCopilotSeatSensor((ISeatSensor) value);
		else if ( req.equals(REQUIRED_HANDSONWHEELSENSOR) )
			this.sensors.setHandsOnWheelSensor((IHandsOnWheelSensor) value);
		return super.bindService(req, value);
	}
	

	@Override
	public IAdaptiveReadyComponent unbindService(String req, Object value) {
		if ( req.equals(REQUIRED_FACEMONITOR) )
			this.sensors.setFaceMonitor(null);
		else if ( req.equals(REQUIRED_DRIVERSEATSENSOR) )
			this.sensors.setDriverSeatSensor(null);
		else if ( req.equals(REQUIRED_COPILOTSEATSENSOR) )
			this.sensors.setCopilotSeatSensor(null);
		else if ( req.equals(REQUIRED_HANDSONWHEELSENSOR) )
			this.sensors.setHandsOnWheelSensor(null);

		return super.unbindService(req, value);
	}
	
}
