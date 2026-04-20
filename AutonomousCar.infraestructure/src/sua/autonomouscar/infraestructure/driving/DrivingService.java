package sua.autonomouscar.infraestructure.driving;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.osgi.utils.logger.SmartLogger;
import sua.autonomouscar.driving.interfaces.IDrivingService;
import sua.autonomouscar.infraestructure.Thing;
import sua.autonomouscar.simulation.interfaces.ISimulationElement;


public abstract class DrivingService extends Thing implements IDrivingService, ISimulationElement {
	
	protected SmartLogger logger = SmartLogger.getLogger(DrivingService.class);
	public static final String ACTIVE = "active";

	public DrivingService(BundleContext context, String id) {
		super(context, id);
		this.addImplementedInterface(IDrivingService.class.getName());
		this.addImplementedInterface(ISimulationElement.class.getName());
		this.setProperty(DrivingService.ACTIVE, false);
	}

	@Override
	public IDrivingService startDriving() {
		if ( this.isDriving() )
			return this;
		
		logger.debug("Starting the driving function ...");
		this.setProperty(DrivingService.ACTIVE, true);
		this.drive();
		return this;
	}

	@Override
	public IDrivingService stopDriving() {
		if ( !this.isDriving() )
			return this;
		
		logger.debug("Ending the driving function ...");
		this.setProperty(DrivingService.ACTIVE, false);
		this.stopTheDrivingFunction();
		return this;
	}

	@Override
	public boolean isDriving() {
		return (boolean) this.getProperty(DrivingService.ACTIVE);
	}
	
	protected void drive() {
		if ( this.checkRequirementsToPerfomTheDrivingService() )
			this.performTheDrivingFunction();
	}


	
	public abstract IDrivingService performTheDrivingFunction();
	public abstract IDrivingService stopTheDrivingFunction();
	
	
	protected abstract boolean checkRequirementsToPerfomTheDrivingService();


	
	
	// ISimulationElement
	@Override
	public void onSimulationStep(Integer step, long time_lapse_millis) {
		if ( this.isDriving() && this.checkRequirementsToPerfomTheDrivingService() )
				this.performTheDrivingFunction();
	}

	
}
