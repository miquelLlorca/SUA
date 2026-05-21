package autonomouscar.mapek.lite.adaptation.resources;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

import sua.autonomouscar.devices.interfaces.IEngine;
import sua.autonomouscar.devices.interfaces.IRoadSensor;
import sua.autonomouscar.interfaces.ERoadType;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;
import autonomouscar.mapek.lite.adaptation.resources.MonitorTipo;

// Sonda de motor

public class SondaEngine extends Probe implements ServiceListener{
	
	public static String ID = "Sonda Motor";

	public SondaEngine(BundleContext context) {
		super(context, ID);
		MonitorTipo m = new MonitorTipo(context);
		super.addTheMonitor(m);
		// añadir filtro que escuche a motor
		String filter = "(ObjectClass="+ IEngine.class.getName()+")";
		
		try {
			context.addServiceListener(this, filter);
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void reportarMedicion(int revoluciones) {
		this.reportMeasure(revoluciones);
	}


	@Override
	public void serviceChanged(ServiceEvent event) {
		// TODO Auto-generated method stub
		IEngine r = (IEngine) context.getService(event.getServiceReference());
		System.out.println(r.getCurrentRPM() + " RPM");
		this.reportarMedicion(r.getCurrentRPM());
	}

}
