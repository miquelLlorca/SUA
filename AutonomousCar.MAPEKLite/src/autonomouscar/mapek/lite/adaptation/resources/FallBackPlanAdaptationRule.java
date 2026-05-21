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
import sua.autonomouscar.infraestructure.driving.ARC.FallbackPlanARC;
import sua.autonomouscar.infraestructure.driving.ARC.L1_DrivingServiceARC;
import sua.autonomouscar.infraestructure.driving.ARC.L3_DrivingServiceARC;
import sua.autonomouscar.infraestructure.interaction.ARC.NotificationServiceARC;


// SI esta en L3 Y detecta que Road Type es highway o standard
// Entonces bindea el fallback plan de shoulder, 
// si no, el default

// Requiere acceso al control del motor y la dirección, 
// 	sensor de distancia lateral derecho, y sensor de carril derecho

public class FallBackPlanAdaptationRule extends AdaptationRule {
	
	protected static SmartLogger logger = SmartLogger.getLogger(FallBackPlanAdaptationRule.class);
	public static String ID = "Regla ADS-L3-8";
	
	IKnowledgeProperty kp_RoadType = null;
	IKnowledgeProperty kp_nivelConduccion = null;
	// puede que haga falta añadir las lineas 
	
	public FallBackPlanAdaptationRule(BundleContext context) {
		super(context, ID);
		this.setListenToKnowledgePropertyChanges("Tipo_Carretera");
		this.setListenToKnowledgePropertyChanges("Nivel_Conducción"); 
		
		kp_RoadType = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Tipo_Carretera");
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
		String Nivel = null;
		if ( kp_RoadType.getValue() != null ) {
			RoadType = (String) kp_RoadType.getValue();
		} else {
			logger.trace("RoadType NULL! Not executing the rule ...");
			throw new RuleException("RoadType null value!", "Not executing the rule ...");		
		}
		if ( kp_nivelConduccion.getValue() != null ) {
			Nivel = (String) kp_nivelConduccion.getValue();
		} else {
			logger.trace("NivelConduccion NULL! Not executing the rule ...");
			throw new RuleException("NivelConduccion null value!", "Not executing the rule ...");		
		}
		
	


		if (Nivel.equals("L3")) {
			if(RoadType.equals("HIGHWAY") || RoadType.equals("STD")) {
				return this.configuracionSistemaActivarShoulder();
			} else {
				return this.configuracionSistemaActivarDefault();
				
			}
		}else {
			
			logger.trace("Cannot understand knowledge property value. Not executing the rule ...");
			throw new RuleException("Unknown property value",
					"Cannot understand knowledge property value. Not executing the rule ...");
		}
		
		
	}
	
	
	
	protected IRuleComponentsSystemConfiguration configuracionSistemaActivarShoulder() {


		IRuleComponentsSystemConfiguration theNextSystemConfiguration =
				SystemConfigurationHelper.createPartialSystemConfiguration(
						this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());

		// Desactivar L3
		String id = String.format("driving.L3.%s", kp_nivelConduccion);
		
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				id, "1.0.0", L3_DrivingServiceARC.REQUIRED_FALLBACKPLAN,
				"driving.FallbackPlan.ParkInTheRoadShoulder", "1.0.0", FallbackPlanARC.PROVIDED_DRIVINGSERVICE);
		
		return theNextSystemConfiguration;		
		
	}
	
	
	protected IRuleComponentsSystemConfiguration configuracionSistemaActivarDefault() {


		IRuleComponentsSystemConfiguration theNextSystemConfiguration =
				SystemConfigurationHelper.createPartialSystemConfiguration(
						this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());

		// Desactivar L3
		String id = String.format("driving.L3.%s", kp_nivelConduccion);
		
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				id, "1.0.0", L3_DrivingServiceARC.REQUIRED_FALLBACKPLAN,
				"driving.FallbackPlan.Emergency", "1.0.0", FallbackPlanARC.PROVIDED_DRIVINGSERVICE);

		return theNextSystemConfiguration;		
		
	}

	
}
