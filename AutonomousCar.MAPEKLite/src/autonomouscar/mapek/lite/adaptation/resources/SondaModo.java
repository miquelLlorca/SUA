package autonomouscar.mapek.lite.adaptation.resources;

import org.osgi.framework.BundleContext;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;

public class SondaModo extends Probe {
	
	public static String ID = "Sonda Modo";

	public SondaModo(BundleContext context) {
		super(context, ID);
	}
	
	
	public void reportarMedicion(String modo) {
		this.reportMeasure(modo.toUpperCase());
	}

}
