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
import sua.autonomouscar.infraestructure.devices.ARC.LineSensorARC;
import sua.autonomouscar.infraestructure.driving.ARC.L1_DrivingServiceARC;
import sua.autonomouscar.infraestructure.interaction.ARC.NotificationServiceARC;


// SI esta en L3 Y detecta que Road Type es no estandar o off road
// Entonces degrada a L1

public class OffRoadAdaptationRule extends AdaptationRule {
	
	protected static SmartLogger logger = SmartLogger.getLogger(OffRoadAdaptationRule.class);
	public static String ID = "Regla ADS-L3-1";
	
	IKnowledgeProperty kp_RoadType = null;
	IKnowledgeProperty kp_nivelConduccion = null;
	// puede que haga falta añadir las lineas 
	
	public OffRoadAdaptationRule(BundleContext context) {
		super(context, ID);
		this.setListenToKnowledgePropertyChanges("Tipo_Carretera");
		//this.setListenToKnowledgePropertyChanges("Nivel_Conducción"); // no haria falta escuchar

		kp_RoadType = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Tipo_Carretera");
		kp_nivelConduccion = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Nivel_Conduccion");

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
		
	


		if ( RoadType.equals("OFF_ROAD")) {
			
			return this.configuracionSistemaActivarL1();
			
		} else {
			// Aqui habria que lanzar una excepcion ????
			
			logger.trace("Cannot understand knowledge property value. Not executing the rule ...");
			throw new RuleException("Unknown property value",
					"Cannot understand knowledge property value. Not executing the rule ...");
		}
		
		
	}
	
	
	
	protected IRuleComponentsSystemConfiguration configuracionSistemaActivarL1() {


		IRuleComponentsSystemConfiguration theNextSystemConfiguration =
				SystemConfigurationHelper.createPartialSystemConfiguration(
						this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());

		
		// Desactivar L3
		String id = String.format("driving.L3.%s", kp_nivelConduccion);
		
		SystemConfigurationHelper.componentToRemove(theNextSystemConfiguration,  id, "1.0.0");
		// hace falta hacer binding to remove??
	
		
		
		// Activar L1
		SystemConfigurationHelper.componentToAdd(theNextSystemConfiguration, "driving.L1.AssistedDriving", "1.0.0");
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L1AssistedDriving", "1.0.0", L1_DrivingServiceARC.REQUIRED_FRONTDISTANCESENSOR,
				"device.FrontDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L1AssistedDriving", "1.0.0", L1_DrivingServiceARC.REQUIRED_RIGHTLINESENSOR,
				"device.RightLineSensor", "1.0.0", LineSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L1AssistedDriving", "1.0.0", L1_DrivingServiceARC.REQUIRED_LEFTLINESENSOR,
				"device.LeftLineSensor", "1.0.0", LineSensorARC.PROVIDED_SENSOR);
		
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"driving.L1AssistedDriving", "1.0.0", L1_DrivingServiceARC.REQUIRED_NOTIFICATIONSERVICE,
				"interaction.NotificationService", "1.0.0", NotificationServiceARC.PROVIDED_SERVICE);

		SystemConfigurationHelper.setParameter(
				theNextSystemConfiguration, "driving.L1.AssistedDriving", "1.0.0", L1_DrivingServiceARC.PARAMETER_LONGITUDINALSECURITYDISTANCE,100);
		
		
		return theNextSystemConfiguration;		
		
	}

	
}
