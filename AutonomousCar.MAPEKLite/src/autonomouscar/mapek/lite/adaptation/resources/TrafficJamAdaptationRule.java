package autonomouscar.mapek.lite.adaptation.resources;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.structures.systemconfiguration.interfaces.IRuleComponentsSystemConfiguration;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.AdaptationRule;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.exceptions.analyzing.RuleException;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.SystemConfigurationHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.structures.systemconfiguration.interfaces.IRuleSystemConfiguration;
import es.upv.pros.tatami.osgi.utils.interfaces.ITimeStamped;
import es.upv.pros.tatami.osgi.utils.logger.SmartLogger;
import sua.autonomouscar.infraestructure.devices.ARC.DistanceSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.EngineARC;
import sua.autonomouscar.infraestructure.devices.ARC.HumanSensorsARC;
import sua.autonomouscar.infraestructure.devices.ARC.LineSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.RoadSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.SteeringARC;
import sua.autonomouscar.infraestructure.driving.ARC.FallbackPlanARC;
import sua.autonomouscar.infraestructure.driving.ARC.L1_DrivingServiceARC;
import sua.autonomouscar.infraestructure.driving.ARC.L3_DrivingServiceARC;
import sua.autonomouscar.infraestructure.interaction.ARC.NotificationServiceARC;


// Requisito 2 (Ignacio). Está en L3_HighwayChauffer y pasa a L3_TrafficJam

public class TrafficJamAdaptationRule extends AdaptationRule {
	
	protected static SmartLogger logger = SmartLogger.getLogger(TrafficJamAdaptationRule.class);
	public static String ID = "Regla ADS-L3-2-HighwayChauffer-to-TrafficJam";
	
	IKnowledgeProperty kp_RoadStatus = null;
	IKnowledgeProperty kp_nivelConduccion = null;
	// puede que haga falta añadir las líneas 
	
	public TrafficJamAdaptationRule(BundleContext context) {
		super(context, ID);
		this.setListenToKnowledgePropertyChanges("Estado_Carretera");
		//this.setListenToKnowledgePropertyChanges("Nivel_Conducción"); // no haría falta escuchar

		kp_RoadStatus = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Estado_Carretera");
		kp_nivelConduccion = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Nivel_Conduccion");

	}

	@Override
	public boolean checkAffectedByChange(IKnowledgeProperty property) {
		
		if (kp_RoadStatus == null) {
			logger.trace("Required Knowledge property not set. Not executing the rule ...");
			return false;
		}
		return true;
	}
	
	@Override
	public IRuleSystemConfiguration onExecute(IKnowledgeProperty property) throws RuleException {
		
		// Debo cambiar si hay jam al modo de conducción: L3_TrafficJam
		String RoadType = null;
		if ( kp_RoadStatus.getValue() != null ) {
			RoadType = (String) kp_RoadStatus.getValue();
		} else {
			logger.trace("RoadType NULL! Not executing the rule ...");
			throw new RuleException("RoadType null value!", "Not executing the rule...");		
		}

		if ( RoadType.equals("JAM")) {			
			return this.configuracionSistemaActivarL1();			
		} else {
			// Aqui habria que lanzar una excepcion ????
			
			logger.trace("Cannot understand knowledge property value. Not executing the rule...");
			throw new RuleException("Unknown property value",
					"Cannot understand knowledge property value. Not executing the rule...");
		}
	}
	
	
	protected IRuleComponentsSystemConfiguration configuracionSistemaActivarL1() {


		IRuleComponentsSystemConfiguration theNextSystemConfiguration =
				SystemConfigurationHelper.createPartialSystemConfiguration(
						this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());

		
		// Desactivar L3
		System.out.println(kp_nivelConduccion.getValue());
		String id = String.format("driving.L3.%s", kp_nivelConduccion.getValue());

		SystemConfigurationHelper.componentToRemove(theNextSystemConfiguration,  "driving.L3.HighwayChauffer", "1.0.0");
		SystemConfigurationHelper.componentToRemove(theNextSystemConfiguration,  "driving.L3.CityChauffer", "1.0.0");
		
		// activar L3 jam
		SystemConfigurationHelper.componentToAdd(theNextSystemConfiguration, "driving.L3.TrafficJamChauffer", "1.0.0");
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_FRONTDISTANCESENSOR,
				"device.FrontDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_LEFTDISTANCESENSOR,
				"device.LeftDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_RIGHTDISTANCESENSOR,
				"device.RightDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_REARDISTANCESENSOR,
				"device.RearDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_RIGHTLINESENSOR,
				"device.RightLineSensor", "1.0.0", LineSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_LEFTLINESENSOR,
				"device.LeftLineSensor", "1.0.0", LineSensorARC.PROVIDED_SENSOR);
		
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_STEERING,
				"device.Steering", "1.0.0", SteeringARC.PROVIDED_DEVICE);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_ENGINE,
				"device.Engine", "1.0.0", EngineARC.PROVIDED_DEVICE);

		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_ROADSENSOR,
				"device.RoadSensor", "1.0.0", RoadSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_HUMANSENSORS,
				"device.HumanSensors", "1.0.0", HumanSensorsARC.PROVIDED_SENSOR);

		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_NOTIFICATIONSERVICE,
				"interaction.NotificationService", "1.0.0", NotificationServiceARC.PROVIDED_SERVICE);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_FALLBACKPLAN,
				"driving.FallbackPlan.Emergency", "1.0.0", FallbackPlanARC.PROVIDED_DRIVINGSERVICE);
		
		
		SystemConfigurationHelper.setParameter(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", 
				L3_DrivingServiceARC.PARAMETER_REFERENCESPEED, "100");
		SystemConfigurationHelper.setParameter(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", 
				L3_DrivingServiceARC.PARAMETER_LATERALSECURITYDISTANCE, "50");
		SystemConfigurationHelper.setParameter(theNextSystemConfiguration, 
				"driving.L3.TrafficJamChauffer", "1.0.0", 
				L3_DrivingServiceARC.PARAMETER_LONGITUDINALSECURITYDISTANCE, "300");
		
		
	
		return theNextSystemConfiguration;		
		
	}

	
}
