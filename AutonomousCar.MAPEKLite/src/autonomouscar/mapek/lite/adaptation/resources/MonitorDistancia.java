package autonomouscar.mapek.lite.adaptation.resources;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Monitor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IMonitor;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;

// MONITOR 3 – Distance Monitor (ADS_L3-3)
// Recibe una distancia medida (Integer) y determina la distancia de seguridad correcta
// consultando el tipo de via actual en el Knowledge ("Tipo_Carretera").
// Actualiza la KnowledgeProperty "Distancia_Seguridad" (Integer) solo si el valor cambia.

public class MonitorDistancia extends Monitor {

	public static String ID = "Monitor Distancia";

	// Distancias de seguridad en metros segun tipo de via
	public static final int DISTANCIA_VIA_RAPIDA = 150;
	public static final int DISTANCIA_CIUDAD     = 50;

	public MonitorDistancia(BundleContext context) {
		super(context, ID);
	}

	@Override
	public IMonitor report(Object measure) {

		this.logger.debug(String.format("Received measure: %s", measure.toString()));

		try {
			IKnowledgeProperty kpTipoCarretera  = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Tipo_Carretera");
			IKnowledgeProperty kpDistancia      = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Distancia_Seguridad");

			if (kpTipoCarretera == null || kpTipoCarretera.getValue() == null) {
				this.logger.debug("Tipo_Carretera not available yet. Skipping distance update.");
				return this;
			}

			String tipoVia = (String) kpTipoCarretera.getValue();
			int nuevaDistancia = tipoVia.equals("CITY") ? DISTANCIA_CIUDAD : DISTANCIA_VIA_RAPIDA;

			if (kpDistancia.getValue() == null || !kpDistancia.getValue().equals(nuevaDistancia)) {
				this.logger.debug(String.format("Updating Knowledge Property %s TO %d",
						kpDistancia.getId(), nuevaDistancia));
				kpDistancia.setValue(nuevaDistancia);
			}

		} catch (Exception e) {
			return this;
		}

		return this;
	}
}