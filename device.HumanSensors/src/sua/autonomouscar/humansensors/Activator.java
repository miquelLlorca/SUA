package sua.autonomouscar.humansensors;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import sua.autonomouscar.infraestructure.devices.ARC.DriverFaceMonitorARC;
import sua.autonomouscar.infraestructure.devices.ARC.HandsOnWheelSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.HumanSensorsARC;
import sua.autonomouscar.infraestructure.devices.ARC.SeatSensorARC;

public class Activator implements BundleActivator {

	private static BundleContext context;
	protected HumanSensorsARC humanSensorsARC = null;
	protected DriverFaceMonitorARC driverFaceMonitorARC = null;
	protected SeatSensorARC driverSeatSensorARC = null;
	protected SeatSensorARC copilotSeatSensorARC = null;
	protected HandsOnWheelSensorARC howSensorARC = null;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		
		// Driver Face Monitor
		this.driverFaceMonitorARC = new DriverFaceMonitorARC(bundleContext, "device.DriverFaceMonitor", "DriverFaceMonitor");
		this.driverFaceMonitorARC.start();
	
		
		// Driver Seat Sensor
		this.driverSeatSensorARC = new SeatSensorARC(bundleContext, "device.DriverSeatSensor", "DriverSeatSensor");
		this.driverSeatSensorARC.start();
		
		// Copilot Seat Sensor
		this.copilotSeatSensorARC = new SeatSensorARC(bundleContext, "device.CopilotSeatSensor", "CopilotSeatSensor");
		this.copilotSeatSensorARC.start();
		
		// Driver Hands On Wheel Sensor
		this.howSensorARC = new HandsOnWheelSensorARC(bundleContext, "device.HandsOnWheelSensor", "HandsOnWheelSensor");
		this.howSensorARC.start();

		// Human Sensors (aggregate)
		this.humanSensorsARC = new HumanSensorsARC(bundleContext, "device.HumanSensors", "HumanSensors");
		this.humanSensorsARC.start();

		/*
		this.humanSensorsARC.bindService(HumanSensorsARC.REQUIRED_FACEMONITOR, 
									  driverFaceMonitorARC.getServiceSupply(DriverFaceMonitorARC.PROVIDED_SENSOR));

		this.humanSensorsARC.bindService(HumanSensorsARC.REQUIRED_DRIVERSEATSENSOR, 
				  driverSeatSensorARC.getServiceSupply(SeatSensorARC.PROVIDED_SENSOR));

		this.humanSensorsARC.bindService(HumanSensorsARC.REQUIRED_COPILOTSEATSENSOR,
				  copilotSeatSensorARC.getServiceSupply(SeatSensorARC.PROVIDED_SENSOR));

		this.humanSensorsARC.bindService(HumanSensorsARC.REQUIRED_HANDSONWHEELSENSOR, 
				howSensorARC.getServiceSupply(HandsOnWheelSensorARC.PROVIDED_SENSOR));
	    */
	}

	public void stop(BundleContext bundleContext) throws Exception {

		this.howSensorARC.stop();			this.howSensorARC = null;
		this.driverSeatSensorARC.stop();	this.driverSeatSensorARC = null;
		this.copilotSeatSensorARC.stop();	this.copilotSeatSensorARC = null;
		this.driverFaceMonitorARC.stop();	this.driverFaceMonitorARC = null;
		this.humanSensorsARC.stop();		this.humanSensorsARC = null;
		Activator.context = null;
	}

}
