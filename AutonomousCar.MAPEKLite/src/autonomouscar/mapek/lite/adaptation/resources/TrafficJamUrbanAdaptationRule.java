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


// SI esta en L3 Y detecta que Road Type es no estandar o off road
// Entonces degrada a L1

public class TrafficJamUrbanAdaptationRule extends AdaptationRule {
	
	protected static SmartLogger logger = SmartLogger.getLogger(TrafficJamUrbanAdaptationRule.class);
	public static String ID = "Regla ADS-L3-5";
	
	IKnowledgeProperty kp_RoadType = null;
	IKnowledgeProperty kp_nivelConduccion = null;
	IKnowledgeProperty kp_distancia = null;
	IKnowledgeProperty kp_humano = null;
	IKnowledgeProperty kp_line = null;
	
	public TrafficJamUrbanAdaptationRule(BundleContext context) {
		super(context, ID);
		this.setListenToKnowledgePropertyChanges("Tipo_Carretera");
	    //this.setListenToKnowledgePropertyChanges("Nivel_Conduccion");
	    //this.setListenToKnowledgePropertyChanges("Distancia");
	    //this.setListenToKnowledgePropertyChanges("Humano");
	    //this.setListenToKnowledgePropertyChanges("Linea");

	    kp_RoadType = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Tipo_Carretera");
	    kp_nivelConduccion = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Nivel_Conduccion");
	    kp_distancia = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Distancia");
	    kp_humano = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Humano");
	    kp_line = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Linea");

	}

	@Override
	public boolean checkAffectedByChange(IKnowledgeProperty property) {
		
		if (kp_RoadType == null) {
			logger.trace("Required Knowledge property not set. Not executing the rule ...");
			return false;
		}
		return true;
	}
	
	@Override
	public IRuleSystemConfiguration onExecute(IKnowledgeProperty property) throws RuleException {
	
		
		String RoadType = null;
		if ( kp_RoadType.getValue() != null ) {
			RoadType = (String) kp_RoadType.getValue();
		} else {
			logger.trace("RoadType NULL! Not executing the rule ...");
			throw new RuleException("RoadType null value!", "Not executing the rule ...");		
		}
		String nivelConduccion;
		if (kp_nivelConduccion.getValue() != null) {
			nivelConduccion = (String) kp_nivelConduccion.getValue();
		} else {
			logger.trace("Nivel_Conduccion NULL! Not executing the rule ...");
			throw new RuleException("Nivel_Conduccion null value!", "Not executing the rule ...");
		}

		System.out.println(nivelConduccion);
		if (nivelConduccion.equals("L3_TRAFFICJAMCHAUFFER") && RoadType.equals("CITY")) {
			
			return this.configuracionSistemaActivarL3_CityChauffer();
			
		} else {
			
			logger.trace("Cannot understand knowledge property value. Not executing the rule ...");
			throw new RuleException("Unknown property value",
					"Cannot understand knowledge property value. Not executing the rule ...");
		}
		
		
	}
	
	
	
	protected IRuleComponentsSystemConfiguration configuracionSistemaActivarL3_CityChauffer() {


		IRuleComponentsSystemConfiguration theNextSystemConfiguration =
				SystemConfigurationHelper.createPartialSystemConfiguration(
						this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());

		
		// Desactivar L3
		SystemConfigurationHelper.componentToRemove(theNextSystemConfiguration,  "driving.L3.TrafficJamChauffer", "1.0.0");
		SystemConfigurationHelper.componentToRemove(theNextSystemConfiguration,  "driving.L3.HighwayChauffer", "1.0.0");
		
		// Activar L3_City_Chauffer
		SystemConfigurationHelper.componentToAdd(theNextSystemConfiguration, "driving.L3.CityChauffer", "1.0.0");

		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_FRONTDISTANCESENSOR,
				"device.FrontDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_LEFTDISTANCESENSOR,
				"device.LeftDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_RIGHTDISTANCESENSOR,
				"device.RightDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_REARDISTANCESENSOR,
				"device.RearDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_RIGHTLINESENSOR,
				"device.RightLineSensor", "1.0.0", LineSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_LEFTLINESENSOR,
				"device.LeftLineSensor", "1.0.0", LineSensorARC.PROVIDED_SENSOR);
		
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_STEERING,
				"device.Steering", "1.0.0", SteeringARC.PROVIDED_DEVICE);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_ENGINE,
				"device.Engine", "1.0.0", EngineARC.PROVIDED_DEVICE);

		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_ROADSENSOR,
				"device.RoadSensor", "1.0.0", RoadSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_HUMANSENSORS,
				"device.HumanSensors", "1.0.0", HumanSensorsARC.PROVIDED_SENSOR);

		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_NOTIFICATIONSERVICE,
				"interaction.NotificationService", "1.0.0", NotificationServiceARC.PROVIDED_SERVICE);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_FALLBACKPLAN,
				"driving.FallbackPlan.Emergency", "1.0.0", FallbackPlanARC.PROVIDED_DRIVINGSERVICE);
		
		
		SystemConfigurationHelper.setParameter(theNextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_DrivingServiceARC.PARAMETER_REFERENCESPEED, "100");
		SystemConfigurationHelper.setParameter(theNextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_DrivingServiceARC.PARAMETER_LATERALSECURITYDISTANCE, "50");
		SystemConfigurationHelper.setParameter(theNextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_DrivingServiceARC.PARAMETER_LONGITUDINALSECURITYDISTANCE, "300");

		
		return theNextSystemConfiguration;		
		
	}

	
}
