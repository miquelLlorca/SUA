package autonomouscar.mapek.lite.adaptation.resources;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

import sua.autonomouscar.devices.interfaces.IFaceMonitor;
import sua.autonomouscar.devices.interfaces.IHumanSensors;
import sua.autonomouscar.infraestructure.devices.ARC.SteeringARC;
import sua.autonomouscar.interfaces.EFaceStatus;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;



// ROAD SENSOR

public class SondaSteering extends Probe implements ServiceListener{
	
	public static String ID = "Sonda_Steering";

	public SondaSteering(BundleContext context) {
		super(context, ID);
		MonitorSteering m = new MonitorSteering(context);
		super.addTheMonitor(m);
		// añadir filtro que escuche a driver sensor
		String filter = "(ObjectClass="+ SteeringARC.class.getName()+")";
		
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
		IFaceMonitor sensor = (IFaceMonitor) context.getService(event.getServiceReference());
		EFaceStatus cara = sensor.getFaceStatus();
        System.out.println("Cara detectada: " + cara);
        this.reportMeasure(String.valueOf(cara).toUpperCase());
        
        
        
    }

}
