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
import sua.autonomouscar.infraestructure.devices.ARC.SteeringARC;
import sua.autonomouscar.infraestructure.devices.ARC.RoadSensorARC;
import sua.autonomouscar.infraestructure.driving.ARC.*;
import sua.autonomouscar.infraestructure.interaction.ARC.NotificationServiceARC;

// REGLA ADS_L3-3: Cambio de modo Highway a City
//
// Condicion 1: L3_HighwayChauffer activo + tipo_via == CITY
//   -> Desactivar HighwayChauffer, activar CityChauffer
//   -> ACTUALIZA-KNOWLEDGE: Distancia_Seguridad = DISTANCIA_CIUDAD
//
// Condicion 2: L3_HighwayChauffer activo + tipo_via == HIGHWAY + Circulacion_Fluida == TRUE
//   -> Mantener HighwayChauffer (actualizar solo parametro distancia)
//   -> ACTUALIZA-KNOWLEDGE: Distancia_Seguridad = DISTANCIA_VIA_RAPIDA

public class HighwayCityAdaptationRule extends AdaptationRule {

	protected static SmartLogger logger = SmartLogger.getLogger(HighwayCityAdaptationRule.class);
	public static String ID = "Regla ADS-L3-3";

	// Reusamos las constantes definidas en MonitorDistancia para coherencia
	public static final int DISTANCIA_VIA_RAPIDA = MonitorDistanciaSeguridad.DISTANCIA_VIA_RAPIDA;
	public static final int DISTANCIA_CIUDAD     = MonitorDistanciaSeguridad.DISTANCIA_CIUDAD;

	IKnowledgeProperty kp_TipoCarretera     = null;
	IKnowledgeProperty kp_NivelConduccion   = null;
	IKnowledgeProperty kp_CirculacionFluida = null;
	IKnowledgeProperty kp_DistanciaSeguridad = null;

	public HighwayCityAdaptationRule(BundleContext context) {
		super(context, ID);

		// La regla se dispara cuando cambia el tipo de via o la fluidez del trafico
		this.setListenToKnowledgePropertyChanges("Tipo_Carretera");
		this.setListenToKnowledgePropertyChanges("Estado_Carretera");

		kp_TipoCarretera     = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Tipo_Carretera");
		kp_NivelConduccion   = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Nivel_Conduccion");
		kp_CirculacionFluida = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Estado_Carretera");
		kp_DistanciaSeguridad = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Distancia_Seguridad");
	}

	@Override
	public boolean checkAffectedByChange(IKnowledgeProperty property) {
		if (kp_TipoCarretera == null || kp_NivelConduccion == null) {
			logger.trace("Required Knowledge properties not set. Not executing the rule ...");
			return false;
		}
		return true;
	}

	@Override
	public IRuleSystemConfiguration onExecute(IKnowledgeProperty property) throws RuleException {

		// Leer tipo de via
		String tipoVia;
		if (kp_TipoCarretera.getValue() != null) {
			tipoVia = (String) kp_TipoCarretera.getValue();
		} else {
			logger.trace("Tipo_Carretera NULL! Not executing the rule ...");
			throw new RuleException("Tipo_Carretera null value!", "Not executing the rule ...");
		}

		// Leer nivel de conduccion activo
		String nivelConduccion;
		if (kp_NivelConduccion.getValue() != null) {
			nivelConduccion = (String) kp_NivelConduccion.getValue();
		} else {
			logger.trace("Nivel_Conduccion NULL! Not executing the rule ...");
			throw new RuleException("Nivel_Conduccion null value!", "Not executing the rule ...");
		}
		// Solo aplica cuando HighwayChauffer esta activo
		if (nivelConduccion.equals("L3_HIGHWAYCHAUFFER")) { 
			// Condicion 1: entramos en ciudad -> cambiar a CityChauffer
			if (tipoVia.equals("CITY")) {
				return this.activarCityChauffer();
			}
			
			// Condicion 2: seguimos en via rapida con circulacion fluida -> mantener HighwayChauffer
			if (tipoVia.equals("HIGHWAY")) {
				String circulacionFluida = null;
				if (kp_CirculacionFluida != null && kp_CirculacionFluida.getValue() != null) {
					circulacionFluida = (String) kp_CirculacionFluida.getValue();
				}
				
				if (!circulacionFluida.equals("FLUID")) {
					return this.mantenerHighwayChauffer();
				}
			}
		}else {
			logger.trace("L3_HighwayChauffer not active. Not executing the rule ...");
			throw new RuleException("L3_HighwayChauffer not active", "Not executing the rule ...");
			
		}


		logger.trace("Conditions not met for rule execution.");
		throw new RuleException("Conditions not met",
				"Cannot determine a valid transition for the current state.");
	}

	// -----------------------------------------------------------------------
	// Condicion 1: CITY -> desactivar HighwayChauffer, activar CityChauffer
	// -----------------------------------------------------------------------
	protected IRuleComponentsSystemConfiguration activarCityChauffer() {

		IRuleComponentsSystemConfiguration config =
				SystemConfigurationHelper.createPartialSystemConfiguration(
						this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());

		// Desactivar L3.HighwayChauffer
		SystemConfigurationHelper.componentToRemove(config, "driving.L3.HighwayChauffer", "1.0.0");

		// Activar L3.CityChauffer con todos sus bindings necesarios
		SystemConfigurationHelper.componentToAdd(config, "driving.L3.CityChauffer", "1.0.0");

		// Binding: RoadSensor (requerido por L3_DrivingServiceARC)
		SystemConfigurationHelper.bindingToAdd(config,
				"driving.L3.CityChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_ROADSENSOR,
				"device.RoadSensor", "1.0.0", RoadSensorARC.PROVIDED_SENSOR);

		// Binding: FrontDistanceSensor (heredado de L1_DrivingServiceARC)
		SystemConfigurationHelper.bindingToAdd(config,
				"driving.L3.CityChauffer", "1.0.0", L1_DrivingServiceARC.REQUIRED_FRONTDISTANCESENSOR,
				"device.FrontDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);

		// Binding: RearDistanceSensor (heredado de L2_DrivingServiceARC)
		SystemConfigurationHelper.bindingToAdd(config,
				"driving.L3.CityChauffer", "1.0.0", L2_DrivingServiceARC.REQUIRED_REARDISTANCESENSOR,
				"device.RearDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);

		// Binding: RightDistanceSensor (heredado de L2_DrivingServiceARC)
		SystemConfigurationHelper.bindingToAdd(config,
				"driving.L3.CityChauffer", "1.0.0", L2_DrivingServiceARC.REQUIRED_RIGHTDISTANCESENSOR,
				"device.RightDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);

		// Binding: LeftDistanceSensor (heredado de L2_DrivingServiceARC)
		SystemConfigurationHelper.bindingToAdd(config,
				"driving.L3.CityChauffer", "1.0.0", L2_DrivingServiceARC.REQUIRED_LEFTDISTANCESENSOR,
				"device.LeftDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);

		// Binding: NotificationService (heredado de L1_DrivingServiceARC)
		SystemConfigurationHelper.bindingToAdd(config,
				"driving.L3.CityChauffer", "1.0.0", L1_DrivingServiceARC.REQUIRED_NOTIFICATIONSERVICE,
				"interaction.NotificationService", "1.0.0", NotificationServiceARC.PROVIDED_SERVICE);

		// Parametros de distancia de seguridad para ciudad
		SystemConfigurationHelper.setParameter(config,
				"driving.L3.CityChauffer", "1.0.0",
				L1_DrivingServiceARC.PARAMETER_LONGITUDINALSECURITYDISTANCE, DISTANCIA_CIUDAD);

		SystemConfigurationHelper.setParameter(config,
				"driving.L3.CityChauffer", "1.0.0",
				L2_DrivingServiceARC.PARAMETER_LATERALSECURITYDISTANCE, DISTANCIA_CIUDAD);

		// ACTUALIZA-KNOWLEDGE: distancia_seguridad = DISTANCIA_CIUDAD
		if (kp_DistanciaSeguridad != null) {
			kp_DistanciaSeguridad.setValue(DISTANCIA_CIUDAD);
		}

		// Actualizar nivel de conduccion en el Knowledge
		//kp_NivelConduccion.setValue("CityChauffer");

		logger.debug("Transition: HighwayChauffer -> CityChauffer. Distancia_Seguridad = " + DISTANCIA_CIUDAD);

		return config;
	}

	// -----------------------------------------------------------------------
	// Condicion 2: HIGHWAY + fluida -> mantener HighwayChauffer, actualizar KPs
	// -----------------------------------------------------------------------
	protected IRuleComponentsSystemConfiguration mantenerHighwayChauffer() {

		IRuleComponentsSystemConfiguration config =
				SystemConfigurationHelper.createPartialSystemConfiguration(
						this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());

		
		
		// No se cambia el componente activo; solo se actualizan los parametros de distancia
		SystemConfigurationHelper.setParameter(config,
				"driving.L3.HighwayChauffer", "1.0.0",
				L1_DrivingServiceARC.PARAMETER_LONGITUDINALSECURITYDISTANCE, DISTANCIA_VIA_RAPIDA);

		SystemConfigurationHelper.setParameter(config,
				"driving.L3.HighwayChauffer", "1.0.0",
				L2_DrivingServiceARC.PARAMETER_LATERALSECURITYDISTANCE, DISTANCIA_VIA_RAPIDA);
	
		
		
		
		// ACTUALIZA-KNOWLEDGE: distancia_seguridad = DISTANCIA_VIA_RAPIDA
		if (kp_DistanciaSeguridad != null) {
			kp_DistanciaSeguridad.setValue(DISTANCIA_VIA_RAPIDA);
		}

		logger.debug("Maintaining HighwayChauffer. Distancia_Seguridad = " + DISTANCIA_VIA_RAPIDA);

		return config;
	}
}