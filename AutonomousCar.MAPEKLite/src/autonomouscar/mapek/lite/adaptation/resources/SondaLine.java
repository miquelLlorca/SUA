package autonomouscar.mapek.lite.adaptation.resources;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import sua.autonomouscar.devices.interfaces.ILineSensor;
import sua.autonomouscar.interfaces.ERoadType;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;
import autonomouscar.mapek.lite.adaptation.resources.MonitorTipo;



// ROAD SENSOR

public class SondaLine extends Probe implements ServiceListener{
	
	public static String ID = "Sonda_Linea";

	public SondaLine(BundleContext context) {
		super(context, ID);
		MonitorLine m = new MonitorLine(context);
		super.addTheMonitor(m);
		// añadir filtro que escuche a line sensor
		String filter = "(ObjectClass="+ ILineSensor.class.getName()+")";
		
		try {
			context.addServiceListener(this, filter);
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void reportarMedicion(String tipo) {
		this.reportMeasure(tipo.toUpperCase());
	}


	@Override
	public void serviceChanged(ServiceEvent event) {
        ILineSensor sensor = (ILineSensor) context.getService(event.getServiceReference());
        boolean lineDetected = sensor.isLineDetected();
        System.out.println("Línea detectada: " + lineDetected);
        this.reportMeasure(String.valueOf(lineDetected).toUpperCase());
    }

}
