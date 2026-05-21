package autonomouscar.mapek.lite.adaptation.resources;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import sua.autonomouscar.devices.interfaces.IHumanSensors;
import sua.autonomouscar.interfaces.ERoadType;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;
import autonomouscar.mapek.lite.adaptation.resources.MonitorTipo;



// ROAD SENSOR

public class SondaDriver extends Probe implements ServiceListener{
	
	public static String ID = "Sonda_Conductor";

	public SondaDriver(BundleContext context) {
		super(context, ID);
		MonitorDriver m = new MonitorDriver(context);
		super.addTheMonitor(m);
		// añadir filtro que escuche a driver sensor
		String filter = "(ObjectClass="+ IHumanSensors.class.getName()+")";
		
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
		IHumanSensors sensor = (IHumanSensors) context.getService(event.getServiceReference());
        System.out.println("Cambio: " + sensor.getClass());
        this.reportMeasure(String.valueOf(sensor.isDriverSeatOccupied()));
        
        
        
    }

}
