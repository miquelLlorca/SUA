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

public class IluminacionConfortAdaptationRule extends AdaptationRule {
	
	protected static SmartLogger logger = SmartLogger.getLogger(IluminacionConfortAdaptationRule.class);
	public static String ID = "Regla Iluminacion Confort";
	
	IKnowledgeProperty kp_FranjaDia = null;
	IKnowledgeProperty kp_Modo = null;
	IKnowledgeProperty kp_EnCasa = null;

	
	public IluminacionConfortAdaptationRule(BundleContext context) {
		super(context, ID);
		this.setListenToKnowledgePropertyChanges("FranjaDia");
		this.setListenToKnowledgePropertyChanges("Modo");
		this.setListenToKnowledgePropertyChanges("EnCasa");

		kp_FranjaDia = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("FranjaDia");
		kp_Modo = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Modo");
		kp_EnCasa = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("EnCasa");

	}

	@Override
	public boolean checkAffectedByChange(IKnowledgeProperty property) {
		
		if (kp_FranjaDia == null || kp_Modo == null || kp_EnCasa == null) {
			logger.trace("Required Knowledge property not set. Not executing the rule ...");
			return false;
		}
		return true;
	}
	
	@Override
	public IRuleSystemConfiguration onExecute(IKnowledgeProperty property) throws RuleException {
	
		
		String franjaDia = null;
		if ( kp_FranjaDia.getValue() != null ) {
			franjaDia = (String) kp_FranjaDia.getValue();
		} else {
			logger.trace("FranjaDia NULL! Not executing the rule ...");
			throw new RuleException("FranjaDia null value!", "Not executing the rule ...");		
		}
		
		String modo = null;
		if ( kp_Modo.getValue() != null ) {
			modo = (String) kp_Modo.getValue();
		} else {
			logger.trace("ModoAuto NULL! Not executing the rule ...");
			throw new RuleException("ModoAuto NULL!", "Not executing the rule ...");
		}

		Boolean enCasa;
		if ( kp_EnCasa.getValue() != null ) {
			enCasa = (Boolean) kp_EnCasa.getValue();
		} else {
			logger.trace("EnCasa NULL! Not executing the rule ...");
			throw new RuleException("EnCasa NULL!", "Not executing the rule ...");
		}


		if ( franjaDia.equals("DIA") || modo.equals("MANUAL") || !enCasa ) {
			
			return this.configuracionSistemaDesactivarControlIluminacion();
			
		} else if (franjaDia.equals("NOCHE") && modo.equals("AUTO") && enCasa ) {
			
			return this.configuracionSistemaActivarControlIluminacion();
			
		} else {
			logger.trace("Cannot understand knowledge property value. Not executing the rule ...");
			throw new RuleException("Unknown property value",
					"Cannot understand knowledge property value. Not executing the rule ...");
		}
		
		
	}
	
	
	
	protected IRuleComponentsSystemConfiguration configuracionSistemaDesactivarControlIluminacion() {
		// Desactivamos el controlador de iluminacion
		IRuleComponentsSystemConfiguration theNextSystemConfiguration = SystemConfigurationHelper.createPartialSystemConfiguration(this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());

		// Eliminamos el controlador de iluminacion
		SystemConfigurationHelper.componentToRemove(theNextSystemConfiguration, "SmartHome.ControladorIluminacion", "1.0.0");

		return theNextSystemConfiguration;		
		
	}

	protected IRuleComponentsSystemConfiguration configuracionSistemaActivarControlIluminacion() {
		// Activamos el controlador de iluminacion
		
		IRuleComponentsSystemConfiguration theNextSystemConfiguration = SystemConfigurationHelper.createPartialSystemConfiguration(this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());
				
		// Añadimos la luz, el sensor de movimiento y el controlador de iluminacion
		SystemConfigurationHelper.componentToAdd(theNextSystemConfiguration, "SmartHome.Luz", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theNextSystemConfiguration, "SmartHome.SensorMovimiento", "1.0.0");
		SystemConfigurationHelper.componentToRemove(theNextSystemConfiguration, "SmartHome.ControladorIluminacion", "1.0.0");

		// Conectamos el controlador de iluminacion con la luz y con el sensor de movimiento (cada uno con su interfaz requerida corresponiente)
		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"SmartHome.ControladorIluminacion", "1.0.0", "req_Luz",
				"SmartHome.Luz", "1.0.0", "prov_Luz");

		SystemConfigurationHelper.bindingToAdd(theNextSystemConfiguration, 
				"SmartHome.ControladorIluminacion", "1.0.0", "req_SensorMovimiento",
				"SmartHome.SensorMovimiento", "1.0.0", "prov_SensorMovimiento");

		
		return theNextSystemConfiguration;
		
		
	}

	
}
