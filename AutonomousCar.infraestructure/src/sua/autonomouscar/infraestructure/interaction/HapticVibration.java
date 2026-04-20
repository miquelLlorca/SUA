package sua.autonomouscar.infraestructure.interaction;

import org.osgi.framework.BundleContext;

import sua.autonomouscar.interaction.interfaces.IInteractionMechanism;

public class HapticVibration extends InteractionMechanism {
	
	public HapticVibration(BundleContext context, String device) {
		super(context, String.format("%s", device));
	}

	@Override
	public IInteractionMechanism performTheInteraction(String message) {
		this.showMessage("〰️");
		return this;
	}





}
