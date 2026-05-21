package autonomouscar.mapek.lite.adaptation.resources;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;
import sua.autonomouscar.devices.interfaces.IDistanceSensor;

// PROBE 4.1.5 – DistanceSensor (ADS_L3-3)
// Escucha cualquier servicio OSGi IDistanceSensor y reporta la distancia al MonitorDistancia.

public class SondaDistanciaSeguridad extends Probe implements ServiceListener {

	public static String ID = "Sonda Distancia";

	public SondaDistanciaSeguridad(BundleContext context) {
		super(context, ID);

		String filter = "(ObjectClass=" + IDistanceSensor.class.getName() + ")";
		try {
			context.addServiceListener(this, filter);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
	}

	public void reportarMedicion(int distancia) {
		this.reportMeasure(distancia);
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		IDistanceSensor sensor = (IDistanceSensor) context.getService(event.getServiceReference());
		if (sensor != null) {
			this.reportarMedicion(sensor.getDistance());
		}
	}
}