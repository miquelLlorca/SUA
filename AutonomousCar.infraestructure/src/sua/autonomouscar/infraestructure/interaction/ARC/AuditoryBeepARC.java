package sua.autonomouscar.infraestructure.interaction.ARC;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.impl.AdaptiveReadyComponent;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;
import es.upv.pros.tatami.osgi.utils.logger.SmartLogger;
import sua.autonomouscar.infraestructure.interaction.AuditoryBeep;
import sua.autonomouscar.interaction.interfaces.IInteractionMechanism;

public class AuditoryBeepARC extends AdaptiveReadyComponent implements IAdaptiveReadyComponent {
	
	public static String PROVIDED_MECHANISM = "provided_mechanism";
	protected IInteractionMechanism mechanism = null;
	
	public AuditoryBeepARC(BundleContext context, String id) {
		super(context, context.getBundle().getSymbolicName());
		logger = SmartLogger.getLogger(context.getBundle().getSymbolicName());
		this.mechanism = new AuditoryBeep(this.context, id);
	}
	
	@Override
	public IAdaptiveReadyComponent deploy() {
		((AuditoryBeep)this.mechanism).registerThing();
		return super.deploy();
	}
	
	@Override
	public IAdaptiveReadyComponent undeploy() {
		((AuditoryBeep)this.mechanism).unregisterThing();
		this.mechanism = null;
		return super.undeploy();
	}
	
	@Override
	public Object getServiceSupply(String serviceSupply) {
		
		if (serviceSupply.equals(PROVIDED_MECHANISM)) {
			super.getServiceSupply(serviceSupply);
			return this.mechanism;
		}
		
		return super.getServiceSupply(serviceSupply);
	}

}
