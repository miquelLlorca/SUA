package autonomouscar.mapek.lite.adaptation.resources;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Monitor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IMonitor;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;

public class MonitorModo extends Monitor {
	
	public static String ID = "Monitor Modo";

	public MonitorModo(BundleContext context) {
		super(context, ID);
	}

	@Override
	public IMonitor report(Object measure) {
		
		this.logger.debug(String.format("Received measure: %s", measure.toString()));
		
		try {
			String value = (String) measure;
			
			IKnowledgeProperty kp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Modo");
			if ( kp.getValue() == null || kp.getValue() != value ) { // sólo actualizamos si el valor es diferente
				this.logger.debug(String.format("Updating Knowledge Property %s TO %s", kp.getId(), value));
				kp.setValue(value);
			}
			
		} catch (Exception e) {
			return this;
		}
		
		return this;
	}

}
