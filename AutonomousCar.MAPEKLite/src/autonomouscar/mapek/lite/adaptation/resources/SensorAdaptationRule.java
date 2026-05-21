package autonomouscar.mapek.lite.adaptation.resources;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.structures.systemconfiguration.interfaces.IRuleComponentsSystemConfiguration;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.AdaptationRule;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.exceptions.analyzing.RuleException;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.SystemConfigurationHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.structures.systemconfiguration.interfaces.IRuleSystemConfiguration;
import es.upv.pros.tatami.osgi.utils.components.OSGiUtils;
import es.upv.pros.tatami.osgi.utils.interfaces.ITimeStamped;
import es.upv.pros.tatami.osgi.utils.logger.SmartLogger;
import sua.autonomouscar.devices.interfaces.IDistanceSensor;
import sua.autonomouscar.infraestructure.devices.ARC.DistanceSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.EngineARC;
import sua.autonomouscar.infraestructure.devices.ARC.HumanSensorsARC;
import sua.autonomouscar.infraestructure.devices.ARC.LineSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.SteeringARC;
import sua.autonomouscar.infraestructure.devices.ARC.RoadSensorARC;
import sua.autonomouscar.infraestructure.driving.ARC.*;
import sua.autonomouscar.infraestructure.interaction.ARC.NotificationServiceARC;

// REGLA ADS_1: Eleccion de sensores mas precisos
// 
//

public class SensorAdaptationRule extends AdaptationRule {

	protected static SmartLogger logger = SmartLogger.getLogger(SensorAdaptationRule.class);
	public static String ID = "Regla ADS-1";

	IKnowledgeProperty kp_NivelConduccion   = null;

	public SensorAdaptationRule(BundleContext context) {
		super(context, ID);

		// La regla se dispara cuando cambia el tipo de via o la fluidez del trafico
		this.setListenToKnowledgePropertyChanges("Nivel_Conduccion");

		kp_NivelConduccion   = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Nivel_Conduccion");
	}

	@Override
	public boolean checkAffectedByChange(IKnowledgeProperty property) {
		if (kp_NivelConduccion == null) {
			logger.trace("Required Knowledge properties not set. Not executing the rule ...");
			return false;
		}
		return true;
	}

	@Override
	public IRuleSystemConfiguration onExecute(IKnowledgeProperty property) throws RuleException {


		// Leer nivel de conduccion activo
		String nivelConduccion;
		if (kp_NivelConduccion.getValue() != null) {
			nivelConduccion = (String) kp_NivelConduccion.getValue();
		} else {
			logger.trace("Nivel_Conduccion NULL! Not executing the rule ...");
			throw new RuleException("Nivel_Conduccion null value!", "Not executing the rule ...");
		}
		
		if(OSGiUtils.getService(context, IDistanceSensor.class, "(id=LIDAR-FrontDistanceSensor)") != null) {
			// Solo aplica cuando HighwayChauffer esta activo
			if (nivelConduccion.startsWith("L3")) {
				return this.activateLIDARL3(nivelConduccion);
				
			}else if(nivelConduccion.startsWith("L2_ADAPTATIVECRUISECONTROL")) {
				return this.activateLIDARL2();
				
			}else if(nivelConduccion.startsWith("L1_ASSISTEDDRIVING")) {
				return this.activateLIDARL1();
				
			}else {
				logger.trace("Driving service not active. Not executing the rule ...");
				throw new RuleException("Driving service  not active", "Not executing the rule ...");
				
			}
			
		}else {
			logger.trace("LIDAR not active. Not executing the rule ...");
			throw new RuleException("LIDAR not active", "Not executing the rule ...");
			
		}
	}


	protected IRuleComponentsSystemConfiguration activateLIDARL3(String nivelConduccionRAW) {

		IRuleComponentsSystemConfiguration config =
				SystemConfigurationHelper.createPartialSystemConfiguration(
						this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());

		
		String nivelConduccion = "driving.L3.";
		
		if(nivelConduccionRAW.equals("L3_TRAFFICJAMCHAUFFER")) {
			nivelConduccion += "TrafficJamChauffer";
		}else if(nivelConduccionRAW.equals("L3_HIGHWAYCHAUFFER")) {
			nivelConduccion += "HighwayChauffer";
		}else if(nivelConduccionRAW.equals("L3_CITYCHAUFFER")) {
			nivelConduccion += "CityChauffer";
		}
		
		
		// Desactivar sensores distancia

		SystemConfigurationHelper.bindingToRemove(config, 
				nivelConduccion, "1.0.0", L3_DrivingServiceARC.REQUIRED_FRONTDISTANCESENSOR,
				"device.FrontDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToRemove(config, 
				nivelConduccion, "1.0.0", L3_DrivingServiceARC.REQUIRED_LEFTDISTANCESENSOR,
				"device.LeftDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToRemove(config, 
				nivelConduccion, "1.0.0", L3_DrivingServiceARC.REQUIRED_RIGHTDISTANCESENSOR,
				"device.RightDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToRemove(config, 
				nivelConduccion, "1.0.0", L3_DrivingServiceARC.REQUIRED_REARDISTANCESENSOR,
				"device.RearDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);

		SystemConfigurationHelper.bindingToAdd(config, 
				nivelConduccion, "1.0.0", L3_DrivingServiceARC.REQUIRED_FRONTDISTANCESENSOR,
				"device.LIDAR.FrontDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(config, 
				nivelConduccion, "1.0.0", L3_DrivingServiceARC.REQUIRED_LEFTDISTANCESENSOR,
				"device.LIDAR.LeftDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(config, 
				nivelConduccion, "1.0.0", L3_DrivingServiceARC.REQUIRED_RIGHTDISTANCESENSOR,
				"device.LIDAR.RightDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(config, 
				nivelConduccion, "1.0.0", L3_DrivingServiceARC.REQUIRED_REARDISTANCESENSOR,
				"device.LIDAR.RearDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);


		return config;
	}
	
	
	
	protected IRuleComponentsSystemConfiguration activateLIDARL2() {

		IRuleComponentsSystemConfiguration config =
				SystemConfigurationHelper.createPartialSystemConfiguration(
						this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());

		
		String nivelConduccion = "driving.L2.AdaptativeCruiseControl";
		// Desactivar sensores distancia

		SystemConfigurationHelper.bindingToRemove(config, 
				nivelConduccion, "1.0.0", L2_DrivingServiceARC.REQUIRED_FRONTDISTANCESENSOR,
				"device.FrontDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);

		SystemConfigurationHelper.bindingToAdd(config, 
				nivelConduccion, "1.0.0", L2_DrivingServiceARC.REQUIRED_FRONTDISTANCESENSOR,
				"device.LIDAR.FrontDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);

		return config;
	}
	
	
	
	protected IRuleComponentsSystemConfiguration activateLIDARL1() {

		IRuleComponentsSystemConfiguration config =
				SystemConfigurationHelper.createPartialSystemConfiguration(
						this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());

		
		String nivelConduccion = "driving.L1.AssistedDriving";
		// Desactivar sensores distancia

		SystemConfigurationHelper.bindingToRemove(config, 
				nivelConduccion, "1.0.0", L1_DrivingServiceARC.REQUIRED_FRONTDISTANCESENSOR,
				"device.FrontDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);

		SystemConfigurationHelper.bindingToAdd(config, 
				nivelConduccion, "1.0.0", L1_DrivingServiceARC.REQUIRED_FRONTDISTANCESENSOR,
				"device.LIDAR.FrontDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);

		return config;
	}

	
}