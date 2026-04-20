package sua.autonomouscar.infraestructure.driving.ARC;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.impl.AdaptiveReadyComponent;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;
import sua.autonomouscar.driving.interfaces.IDrivingService;
import sua.autonomouscar.driving.interfaces.IL0_DrivingService;
import sua.autonomouscar.infraestructure.Thing;

public abstract class DrivingServiceARC extends AdaptiveReadyComponent implements IAdaptiveReadyComponent {
	
	public static String PROVIDED_DRIVINGSERVICE = "provided_drivingservice";
	protected IDrivingService theDrivingService = null;

	public DrivingServiceARC(BundleContext context, String bundleId) {
		super(context, bundleId);
	}
	
	protected DrivingServiceARC setTheDrivingService(IDrivingService theDrivingService) {
		this.theDrivingService = theDrivingService;
		return this;
	}
	
	protected IDrivingService getTheDrivingService() {
		return theDrivingService;
	}
		
	@Override
	public IAdaptiveReadyComponent deploy() {
		((Thing) this.getTheDrivingService()).registerThing();
		this.getTheDrivingService().startDriving();
		return super.deploy();
	}
	
	@Override
	public IAdaptiveReadyComponent undeploy() {
		this.getTheDrivingService().stopDriving();
		((Thing) this.getTheDrivingService()).unregisterThing();
		return super.undeploy();
	}
	
	@Override
	public Object getServiceSupply(String serviceSupply) {

		if (serviceSupply.equals(PROVIDED_DRIVINGSERVICE)) {
			super.getServiceSupply(serviceSupply);
			return (IL0_DrivingService) this.getTheDrivingService();
		}
		
		return super.getServiceSupply(serviceSupply);
	}


}
