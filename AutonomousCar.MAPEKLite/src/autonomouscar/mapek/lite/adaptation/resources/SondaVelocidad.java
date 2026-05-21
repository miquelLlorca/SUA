package autonomouscar.mapek.lite.adaptation.resources;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;
import sua.autonomouscar.devices.interfaces.ISpeedometer;

// PROBE 4.1.8 – Speedometer (ADS_L3-3)
// Escucha el servicio OSGi ISpeedometer y reporta la velocidad actual al MonitorVelocidad.

public class SondaVelocidad extends Probe implements ServiceListener {

	public static String ID = "Sonda Velocidad";

	public SondaVelocidad(BundleContext context) {
		super(context, ID);

		String filter = "(ObjectClass=" + ISpeedometer.class.getName() + ")";
		try {
			context.addServiceListener(this, filter);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
	}

	public void reportarMedicion(int velocidad) {
		this.reportMeasure(velocidad);
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		ISpeedometer s = (ISpeedometer) context.getService(event.getServiceReference());
		if (s != null) {
			this.reportarMedicion(s.getCurrentSpeed());
		}
	}
}