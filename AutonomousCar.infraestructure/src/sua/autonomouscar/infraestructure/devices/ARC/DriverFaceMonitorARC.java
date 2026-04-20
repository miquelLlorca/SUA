package sua.autonomouscar.infraestructure.devices.ARC;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.impl.AdaptiveReadyComponent;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;
import es.upv.pros.tatami.osgi.utils.logger.SmartLogger;
import sua.autonomouscar.devices.interfaces.IFaceMonitor;
import sua.autonomouscar.infraestructure.devices.DriverFaceMonitor;
import sua.autonomouscar.interfaces.EFaceStatus;

public class DriverFaceMonitorARC extends AdaptiveReadyComponent implements IAdaptiveReadyComponent {
	
	public static String PROVIDED_SENSOR = "provided_sensor";
	protected IFaceMonitor sensor = null;
	
	public DriverFaceMonitorARC(BundleContext context, String bundleId, String id) {
		super(context, bundleId);
		logger = SmartLogger.getLogger(bundleId);
		this.sensor = new DriverFaceMonitor(this.context, id);
	}
	
	@Override
	public IAdaptiveReadyComponent deploy() {
		this.sensor.setFaceStatus(EFaceStatus.LOOKING_FORWARD);
		((DriverFaceMonitor)this.sensor).registerThing();
		return super.deploy();
	}
	
	@Override
	public IAdaptiveReadyComponent undeploy() {
		((DriverFaceMonitor)this.sensor).unregisterThing();
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
