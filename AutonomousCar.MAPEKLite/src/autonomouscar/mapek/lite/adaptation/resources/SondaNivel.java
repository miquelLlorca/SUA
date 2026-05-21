package autonomouscar.mapek.lite.adaptation.resources;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import sua.autonomouscar.devices.interfaces.IRoadSensor;
import sua.autonomouscar.driving.interfaces.IDrivingService;
import sua.autonomouscar.interfaces.ERoadType;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;
import autonomouscar.mapek.lite.adaptation.resources.MonitorTipo;



// ROAD SENSOR

public class SondaNivel extends Probe implements ServiceListener{
	
	public static String ID = "Sonda Nivel conduccion";

	public SondaNivel(BundleContext context) {
		super(context, ID);
		MonitorNivel m = new MonitorNivel(context);
		super.addTheMonitor(m);
		// añadir filtro que escuche a road sensor
		String filter = "(ObjectClass="+ IDrivingService.class.getName()+")";
		
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
		// TODO Auto-generated method stub
		IDrivingService r = (IDrivingService) context.getService(event.getServiceReference());
		this.reportarMedicion(r.getId());
	}

}
