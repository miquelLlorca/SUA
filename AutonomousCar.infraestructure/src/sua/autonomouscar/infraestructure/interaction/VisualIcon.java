package sua.autonomouscar.infraestructure.interaction;

import org.osgi.framework.BundleContext;

import sua.autonomouscar.interaction.interfaces.IInteractionMechanism;

public class VisualIcon extends InteractionMechanism {
	
	public VisualIcon(BundleContext context, String device) {
		super(context, String.format("%s", device));
	}

	@Override
	public IInteractionMechanism performTheInteraction(String message) {
		// 🗣
		this.showMessage("⚠️");
		return this;
	}





}
