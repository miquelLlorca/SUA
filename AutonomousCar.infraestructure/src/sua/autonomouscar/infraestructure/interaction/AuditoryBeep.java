package sua.autonomouscar.infraestructure.interaction;

import org.osgi.framework.BundleContext;

import sua.autonomouscar.interaction.interfaces.IInteractionMechanism;

public class AuditoryBeep extends InteractionMechanism {

	public AuditoryBeep(BundleContext context, String device) {
		super(context, String.format("%s", device));
	}

	@Override
	public IInteractionMechanism performTheInteraction(String message) {
		this.showMessage("🔔");
		return this;
	}





}
