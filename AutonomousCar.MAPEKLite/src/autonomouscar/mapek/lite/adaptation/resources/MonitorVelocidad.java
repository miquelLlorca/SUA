package autonomouscar.mapek.lite.adaptation.resources;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Monitor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IMonitor;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;

// MONITOR 2 – Speed Monitor (ADS_L3-3)
// Recibe la velocidad actual del vehiculo (Integer, en km/h).
// Determina si la circulacion es fluida comparando con UMBRAL_CIRCULACION_FLUIDA.
// Actualiza la KnowledgeProperty "Circulacion_Fluida" (Boolean) solo si el valor cambia.

public class MonitorVelocidad extends Monitor {

	public static String ID = "Monitor Velocidad";

	// Velocidad minima (km/h) para considerar la circulacion fluida en via rapida
	public static final int UMBRAL_CIRCULACION_FLUIDA = 50;

	public MonitorVelocidad(BundleContext context) {
		super(context, ID);
	}

	@Override
	public IMonitor report(Object measure) {

		this.logger.debug(String.format("Received measure: %s", measure.toString()));

		try {
			Integer velocidad = (Integer) measure;
			boolean circulacionFluida = velocidad > UMBRAL_CIRCULACION_FLUIDA;

			IKnowledgeProperty kp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Circulacion_Fluida");
			if (kp.getValue() == null || !kp.getValue().equals(circulacionFluida)) {
				this.logger.debug(String.format("Updating Knowledge Property %s TO %s", kp.getId(), circulacionFluida));
				kp.setValue(circulacionFluida);
			}

		} catch (Exception e) {
			return this;
		}

		return this;
	}
}