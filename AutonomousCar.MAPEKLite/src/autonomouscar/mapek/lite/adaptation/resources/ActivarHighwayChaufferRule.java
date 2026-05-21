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
import sua.autonomouscar.infraestructure.devices.ARC.HumanSensorsARC;
import sua.autonomouscar.infraestructure.devices.ARC.LineSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.RoadSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.EngineARC;
import sua.autonomouscar.infraestructure.devices.ARC.SteeringARC;
import sua.autonomouscar.infraestructure.driving.ARC.DrivingServiceARC;
import sua.autonomouscar.infraestructure.driving.ARC.FallbackPlanARC;
import sua.autonomouscar.infraestructure.driving.ARC.L1_DrivingServiceARC;
import sua.autonomouscar.infraestructure.driving.ARC.L2_DrivingServiceARC;
import sua.autonomouscar.infraestructure.driving.ARC.L3_DrivingServiceARC;
import sua.autonomouscar.infraestructure.interaction.ARC.NotificationServiceARC;

// REGLA DE INICIALIZACION: Activar L3_HighwayChauffer
//
// Condicion: tipo_via == HIGHWAY y circulacion_fluida == TRUE
//            y Nivel_Conduccion no es ya HighwayChauffer
// Accion: activar L3_HighwayChauffer con todos sus bindings
//         ACTUALIZA-KNOWLEDGE: Nivel_Conduccion = HighwayChauffer
//                              Distancia_Seguridad = DISTANCIA_VIA_RAPIDA

public class ActivarHighwayChaufferRule extends AdaptationRule {

    protected static SmartLogger logger = SmartLogger.getLogger(ActivarHighwayChaufferRule.class);
    public static String ID = "Regla Activar HighwayChauffer";

    IKnowledgeProperty kp_TipoCarretera     = null;
    IKnowledgeProperty kp_NivelConduccion   = null;
    IKnowledgeProperty kp_CirculacionFluida = null;
    IKnowledgeProperty kp_DistanciaSeguridad = null;

    public ActivarHighwayChaufferRule(BundleContext context) {
        super(context, ID);

        this.setListenToKnowledgePropertyChanges("Tipo_Carretera");
        this.setListenToKnowledgePropertyChanges("Circulacion_Fluida");

        kp_TipoCarretera     = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Tipo_Carretera");
        kp_NivelConduccion   = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Nivel_Conduccion");
        kp_CirculacionFluida = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Circulacion_Fluida");
        kp_DistanciaSeguridad = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("Distancia_Seguridad");

    }

    @Override
    public boolean checkAffectedByChange(IKnowledgeProperty property) {
        if (kp_TipoCarretera == null || kp_TipoCarretera.getValue() == null) {
            return false;
        }
        return true;
    }

    @Override
    public IRuleSystemConfiguration onExecute(IKnowledgeProperty property) throws RuleException {

        String tipoVia = (String) kp_TipoCarretera.getValue();

        // Solo aplica si estamos en HIGHWAY
        if (!tipoVia.equals("HIGHWAY")) {
            throw new RuleException("Not HIGHWAY", "Not executing the rule ...");
        }

        // Solo aplica si HighwayChauffer no esta ya activo
        if (kp_NivelConduccion.getValue() != null &&
            kp_NivelConduccion.getValue().equals("HighwayChauffer")) {
            throw new RuleException("HighwayChauffer already active", "Not executing the rule ...");
        }

        // Comprobamos circulacion fluida si ya hay valor; si no lo hay, activamos igualmente
        if (kp_CirculacionFluida.getValue() != null &&
            !Boolean.TRUE.equals(kp_CirculacionFluida.getValue())) {
            throw new RuleException("Traffic not fluid", "Not executing the rule ...");
        }

        return this.activarHighwayChauffer();
    }

    protected IRuleComponentsSystemConfiguration activarHighwayChauffer() {

        IRuleComponentsSystemConfiguration config =
                SystemConfigurationHelper.createPartialSystemConfiguration(
                        this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());

        // Desactivar modos inferiores que pudieran estar activos
        SystemConfigurationHelper.componentToRemove(config, "driving.L0.ManualDriving", "1.0.0");
        SystemConfigurationHelper.componentToRemove(config, "driving.L1.AssistedDriving", "1.0.0");
        SystemConfigurationHelper.componentToRemove(config, "driving.L2.AdaptativeCruiseControl", "1.0.0");
        SystemConfigurationHelper.componentToRemove(config, "driving.L2.LaneKeepingAssist", "1.0.0");
        SystemConfigurationHelper.componentToRemove(config, "driving.L3.CityChauffer", "1.0.0");
        SystemConfigurationHelper.componentToRemove(config, "driving.L3.TrafficJamChauffer", "1.0.0");

        // Activar L3.HighwayChauffer
        SystemConfigurationHelper.componentToAdd(config, "driving.L3.HighwayChauffer", "1.0.0");

        // Bindings
        SystemConfigurationHelper.bindingToAdd(config,
                "driving.L3.HighwayChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_ROADSENSOR,
                "device.RoadSensor", "1.0.0", RoadSensorARC.PROVIDED_SENSOR);

        SystemConfigurationHelper.bindingToAdd(config,
                "driving.L3.HighwayChauffer", "1.0.0", L2_DrivingServiceARC.REQUIRED_ENGINE,
                "device.Engine", "1.0.0", EngineARC.PROVIDED_DEVICE);

        SystemConfigurationHelper.bindingToAdd(config,
                "driving.L3.HighwayChauffer", "1.0.0", L2_DrivingServiceARC.REQUIRED_STEERING,
                "device.Steering", "1.0.0", SteeringARC.PROVIDED_DEVICE);

        SystemConfigurationHelper.bindingToAdd(config,
                "driving.L3.HighwayChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_HUMANSENSORS,
                "device.HumanSensors", "1.0.0", HumanSensorsARC.PROVIDED_SENSOR);

        SystemConfigurationHelper.bindingToAdd(config,
                "driving.L3.HighwayChauffer", "1.0.0", L3_DrivingServiceARC.REQUIRED_FALLBACKPLAN,
                "driving.FallbackPlan.ParkInTheRoadShoulder", "1.0.0", DrivingServiceARC.PROVIDED_DRIVINGSERVICE);

        SystemConfigurationHelper.bindingToAdd(config,
                "driving.L3.HighwayChauffer", "1.0.0", L1_DrivingServiceARC.REQUIRED_FRONTDISTANCESENSOR,
                "device.FrontDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);

        SystemConfigurationHelper.bindingToAdd(config,
                "driving.L3.HighwayChauffer", "1.0.0", L2_DrivingServiceARC.REQUIRED_REARDISTANCESENSOR,
                "device.RearDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);

        SystemConfigurationHelper.bindingToAdd(config,
                "driving.L3.HighwayChauffer", "1.0.0", L2_DrivingServiceARC.REQUIRED_RIGHTDISTANCESENSOR,
                "device.RightDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);

        SystemConfigurationHelper.bindingToAdd(config,
                "driving.L3.HighwayChauffer", "1.0.0", L2_DrivingServiceARC.REQUIRED_LEFTDISTANCESENSOR,
                "device.LeftDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);

        SystemConfigurationHelper.bindingToAdd(config,
                "driving.L3.HighwayChauffer", "1.0.0", L1_DrivingServiceARC.REQUIRED_RIGHTLINESENSOR,
                "device.RightLineSensor", "1.0.0", LineSensorARC.PROVIDED_SENSOR);

        SystemConfigurationHelper.bindingToAdd(config,
                "driving.L3.HighwayChauffer", "1.0.0", L1_DrivingServiceARC.REQUIRED_LEFTLINESENSOR,
                "device.LeftLineSensor", "1.0.0", LineSensorARC.PROVIDED_SENSOR);

        SystemConfigurationHelper.bindingToAdd(config,
                "driving.L3.HighwayChauffer", "1.0.0", L1_DrivingServiceARC.REQUIRED_NOTIFICATIONSERVICE,
                "interaction.NotificationService", "1.0.0", NotificationServiceARC.PROVIDED_SERVICE);

        // Parametros de distancia de seguridad para via rapida
        SystemConfigurationHelper.setParameter(config,
                "driving.L3.HighwayChauffer", "1.0.0",
                L1_DrivingServiceARC.PARAMETER_LONGITUDINALSECURITYDISTANCE,
                MonitorDistancia.DISTANCIA_VIA_RAPIDA);

        SystemConfigurationHelper.setParameter(config,
                "driving.L3.HighwayChauffer", "1.0.0",
                L2_DrivingServiceARC.PARAMETER_LATERALSECURITYDISTANCE,
                MonitorDistancia.DISTANCIA_VIA_RAPIDA);

        // ACTUALIZA-KNOWLEDGE
        kp_NivelConduccion.setValue("HighwayChauffer");
        if (kp_DistanciaSeguridad != null)
            kp_DistanciaSeguridad.setValue(MonitorDistancia.DISTANCIA_VIA_RAPIDA);

        logger.debug("HighwayChauffer activated. Distancia_Seguridad = " + MonitorDistancia.DISTANCIA_VIA_RAPIDA);

        return config;
    }
}