package sua.autonomouscar.infraestructure.devices;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import sua.autonomouscar.devices.interfaces.IEngine;
import sua.autonomouscar.devices.interfaces.ISpeedometer;
import sua.autonomouscar.devices.interfaces.IThing;
import sua.autonomouscar.infraestructure.Thing;

public class Speedometer extends Thing implements ISpeedometer {
	
	public static final String SPEED = "speed";
	public static final int VEHICLE_MAX_SPEED = 240;
	protected SpeedChangeListener listener = null;

	public Speedometer(BundleContext context, String id) {
		super(context, id);
		this.addImplementedInterface(ISpeedometer.class.getName());
		this.setCurrentSpeed(0);
		
		this.listener = new SpeedChangeListener(context, this);
	}
	
	
	@Override
	public int getCurrentSpeed() {
		return (int) this.getProperty(Speedometer.SPEED);
	}
	
	protected ISpeedometer setCurrentSpeed(int speed) {
		this.setProperty(Speedometer.SPEED, speed);
		return this;
	}

	
	
	protected IEngine getEngine() {
		ServiceReference<?> engine_ref = this.getBundleContext().getServiceReference(IEngine.class.getName());
		if ( engine_ref == null )
			return null;
		return (IEngine) this.getBundleContext().getService(engine_ref);
	}
	
	
	public int calculateSpeedFromRPM(int rpm) {
		if ( rpm < Engine.MINIMUM_RPM )
			return 0;
		else if ( rpm > Engine.MAXIMUM_RPM )
			return Speedometer.VEHICLE_MAX_SPEED;
		
		return Speedometer.VEHICLE_MAX_SPEED * ( rpm - Engine.MINIMUM_RPM ) / (Engine.MAXIMUM_RPM - Engine.MINIMUM_RPM);
	}
	
	public int calculateRPMFromSpeed(int speed) {
		if ( speed <= 0 )
			return Engine.MINIMUM_RPM;
		else if ( speed > Speedometer.VEHICLE_MAX_SPEED )
			return Engine.MAXIMUM_RPM;
		
		return Engine.MINIMUM_RPM + ( (Engine.MAXIMUM_RPM - Engine.MINIMUM_RPM) * speed / Speedometer.VEHICLE_MAX_SPEED );
	}
	
	@Override
	public ISpeedometer updateSpeed(int rpm) {
		this.setCurrentSpeed(this.calculateSpeedFromRPM(rpm));
		return this;
	}
	
	
	@Override
	public IThing registerThing() {
		super.registerThing();
		this.listener.start();
		return this;
	}
	
	@Override
	public IThing unregisterThing() {
		this.listener.stop();	this.listener = null;
		super.unregisterThing();
		return this;
	}
	
	
	
	
	
	
	
	
	class SpeedChangeListener implements ServiceListener {
		
		protected BundleContext context = null;
		protected Speedometer speedodometer = null;
		
		public SpeedChangeListener(BundleContext context, Speedometer speedometer) {
			this.context = context;
			this.speedodometer = speedometer;
		}
		
		public void start() {
			String filter = "(" + Constants.OBJECTCLASS + "=" + IEngine.class.getName() + ")";
			try {
				this.context.addServiceListener(this, filter);
			} catch (InvalidSyntaxException e) {
			}
		}
		
		public void stop() {
			this.context.removeServiceListener(this);
		}

		@Override
		public void serviceChanged(ServiceEvent event) {
		
			IEngine engine = (IEngine)context.getService(event.getServiceReference());
			switch (event.getType()) {
			case ServiceEvent.REGISTERED:
			case ServiceEvent.MODIFIED:
				this.speedodometer.updateSpeed(engine.getCurrentRPM());
				break;

			case ServiceEvent.UNREGISTERING:
			case ServiceEvent.MODIFIED_ENDMATCH:
				this.speedodometer.updateSpeed(0);
				break;
			default:
				break;
			}
		}
		
		

	}

}
