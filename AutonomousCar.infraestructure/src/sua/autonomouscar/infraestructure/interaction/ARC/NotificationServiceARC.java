package sua.autonomouscar.infraestructure.interaction.ARC;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.impl.AdaptiveReadyComponent;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;
import es.upv.pros.tatami.osgi.utils.logger.SmartLogger;
import sua.autonomouscar.infraestructure.interaction.NotificationService;
import sua.autonomouscar.interaction.interfaces.IInteractionMechanism;
import sua.autonomouscar.interaction.interfaces.INotificationService;

public class NotificationServiceARC extends AdaptiveReadyComponent implements IAdaptiveReadyComponent {
	
	public static String PROVIDED_SERVICE = "provided_service";
	public static String REQUIRED_SERVICE = "required_mechanisms";
	protected INotificationService service = null;
	
	public NotificationServiceARC(BundleContext context, String id) {
		super(context, context.getBundle().getSymbolicName());
		logger = SmartLogger.getLogger(context.getBundle().getSymbolicName());
		this.service = new NotificationService(this.context, id);
	}
	
	@Override
	public IAdaptiveReadyComponent deploy() {
		((NotificationService)this.service).registerThing();
		return super.deploy();
	}
	
	@Override
	public IAdaptiveReadyComponent undeploy() {
		((NotificationService)this.service).unregisterThing();
		this.service = null;
		return super.undeploy();
	}
	
	@Override
	public Object getServiceSupply(String serviceSupply) {
		if (serviceSupply.equals(PROVIDED_SERVICE)) {
			super.getServiceSupply(serviceSupply);
			return this.service;
		}
		
		return super.getServiceSupply(serviceSupply);
	}

	@Override
	public IAdaptiveReadyComponent bindService(String req, Object value) {
		if ( req.equals(REQUIRED_SERVICE) )
			this.service.addInteractionMechanism(((IInteractionMechanism)value).getId());
		
		return super.bindService(req, value);
	}
	

	@Override
	public IAdaptiveReadyComponent unbindService(String req, Object value) {
		if ( req.equals(REQUIRED_SERVICE) )
			this.service.removeInteractionMechanism(((IInteractionMechanism)value).getId());

		return super.unbindService(req, value);
	}
		
	
	
}
