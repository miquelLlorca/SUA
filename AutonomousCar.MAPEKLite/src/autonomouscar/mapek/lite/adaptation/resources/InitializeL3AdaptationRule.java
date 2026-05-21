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
import sua.autonomouscar.infraestructure.devices.ARC.*;
import sua.autonomouscar.infraestructure.devices.ARC.LineSensorARC;
import sua.autonomouscar.infraestructure.driving.ARC.FallbackPlanARC;
import sua.autonomouscar.infraestructure.driving.ARC.L1_DrivingServiceARC;
import sua.autonomouscar.infraestructure.driving.ARC.L3_DrivingServiceARC;
import sua.autonomouscar.infraestructure.interaction.ARC.NotificationServiceARC;


// SI esta en L3 Y detecta que Road Type es highway o standard
// Entonces bindea el fallback plan de shoulder, 
// si no, el default

// Requiere acceso al control del motor y la dirección, 
// 	sensor de distancia lateral derecho, y sensor de carril derecho

public class InitializeL3AdaptationRule extends AdaptationRule {
	
	protected static SmartLogger logger = SmartLogger.getLogger(InitializeL3AdaptationRule.class);
	public static String ID = "Regla INIT";

	IKnowledgeProperty kp_RoadType = null;
	IKnowledgeProperty kp_RoadStatus = null;
	IKnowledgeProperty kp_nivelConduccion = null;
	// puede que haga falta añadir las lineas 
	
	public InitializeL3AdaptationRule(BundleContext context) {
		super(context, ID);
		this.setListenToKnowledgePropertyChanges("Tipo_Carretera");
		this.setListenToKnowledgePropertyChanges("Estado_Carretera");
		this.setListenToKnowledgePropertyChanges("Nivel_Conducción"); 
		
		kp_RoadType = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Tipo_Carretera");
		kp_RoadStatus = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Estado_Carretera");
		kp_nivelConduccion = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Nivel_Conduccion");

	}

	@Override
	public boolean checkAffectedByChange(IKnowledgeProperty property) {
		
		if (kp_RoadType == null || kp_nivelConduccion == null) {
			logger.trace("Required Knowledge property not set. Not executing the rule ...");
			return false;
		}
		return true;
	}
	
	@Override
	public IRuleSystemConfiguration onExecute(IKnowledgeProperty property) throws RuleException {
	

		String RoadType = null;
		String RoadStatus = null;
		String Nivel = null;
		if ( kp_RoadType.getValue() != null ) {
			RoadType = (String) kp_RoadType.getValue();
		} else {
			logger.trace("RoadType NULL! Not executing the rule ...");
			throw new RuleException("RoadType null value!", "Not executing the rule ...");		
		}
		
		if ( kp_RoadStatus.getValue() != null ) {
			RoadStatus = (String) kp_RoadStatus.getValue();
		} else {
			logger.trace("RoadStatus NULL! Not executing the rule ...");
			throw new RuleException("kp_RoadStatus null value!", "Not executing the rule ...");		
		}
		
		/*if ( kp_nivelConduccion.getValue() != null ) {
			Nivel = (String) kp_nivelConduccion.getValue();
		} else {
			logger.trace("NivelConduccion NULL! Not executing the rule ...");
			throw new RuleException("NivelConduccion null value!", "Not executing the rule ...");		
		}*/
		
	


		if (kp_nivelConduccion.getValue() == null) {
			if(RoadType.equals("STD") && RoadStatus.equals("FLUID") ) {
				return this.configuracionSistemaActivarL3STD();
			} else if(RoadType.equals("HIGHWAY") && RoadStatus.equals("FLUID")){
				return this.configuracionSistemaActivarL3Highway();
			} else {
				logger.trace("Cannot understand knowledge property value. Not executing the rule ...");
				throw new RuleException("Unknown property value",
						"Cannot understand knowledge property value. Not executing the rule ...");
				
			}
		}else {
			
			logger.trace("Cannot understand knowledge property value. Not executing the rule ...");
			throw new RuleException("Unknown property value",
					"Cannot understand knowledge property value. Not executing the rule ...");
		}
		
	}
	
	
	
	protected IRuleComponentsSystemConfiguration configuracionSistemaActivarL3STD() {


		IRuleComponentsSystemConfiguration theNextSystemConfiguration =
				SystemConfigurationHelper.createPartialSystemConfiguration(
						this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());


		
		return theNextSystemConfiguration;		
		
	}
	
	
	protected IRuleComponentsSystemConfiguration configuracionSistemaActivarL3Highway() {


		IRuleComponentsSystemConfiguration theNextSystemConfiguration =
				SystemConfigurationHelper.createPartialSystemConfiguration(
						this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());

		
		
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_FRONTDISTANCESENSOR,
				"device.FrontDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_LEFTDISTANCESENSOR,
				"device.LeftDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_RIGHTDISTANCESENSOR,
				"device.RightDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_REARDISTANCESENSOR,
				"device.RearDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_RIGHTLINESENSOR,
				"device.RightLineSensor", "1.0.0", LineSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_LEFTLINESENSOR,
				"device.LeftLineSensor", "1.0.0", LineSensorARC.PROVIDED_SENSOR);
		
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_STEERING,
				"device.Steering", "1.0.0", SteeringARC.PROVIDED_DEVICE);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_ENGINE,
				"device.Engine", "1.0.0", EngineARC.PROVIDED_DEVICE);

		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_ROADSENSOR,
				"device.RoadSensor", "1.0.0", RoadSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_HUMANSENSORS,
				"device.HumanSensors", "1.0.0", HumanSensorsARC.PROVIDED_SENSOR);

		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_NOTIFICATIONSERVICE,
				"interaction.NotificationService", "1.0.0", NotificationServiceARC.PROVIDED_SERVICE);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_FALLBACKPLAN,
				"driving.FallbackPlan.Emergency", "1.0.0", FallbackPlanARC.PROVIDED_DRIVINGSERVICE);
		
		
		SystemConfigurationHelper.setParameter(theNextSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_DrivingServiceARC.PARAMETER_REFERENCESPEED, "100");
		SystemConfigurationHelper.setParameter(theNextSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_DrivingServiceARC.PARAMETER_LATERALSECURITYDISTANCE, "50");
		SystemConfigurationHelper.setParameter(theNextSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_DrivingServiceARC.PARAMETER_LONGITUDINALSECURITYDISTANCE, "300");
		
		
		return theNextSystemConfiguration;		
		
	}

	
}
