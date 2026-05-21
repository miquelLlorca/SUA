package autonomouscar.mapek.lite.adaptation.starter;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import autonomouscar.mapek.lite.adaptation.resources.ActivarHighwayChaufferRule;
import autonomouscar.mapek.lite.adaptation.resources.FallBackPlanAdaptationRule;
import autonomouscar.mapek.lite.adaptation.resources.HighwayCityAdaptationRule;
import autonomouscar.mapek.lite.adaptation.resources.IluminacionConfortAdaptationRule;
import autonomouscar.mapek.lite.adaptation.resources.InitializeL3AdaptationRule;
import autonomouscar.mapek.lite.adaptation.resources.MonitorDistancia;
import autonomouscar.mapek.lite.adaptation.resources.MonitorDistanciaSeguridad;
import autonomouscar.mapek.lite.adaptation.resources.MonitorDriver;
import autonomouscar.mapek.lite.adaptation.resources.MonitorEngine;
import autonomouscar.mapek.lite.adaptation.resources.MonitorEstado;
import autonomouscar.mapek.lite.adaptation.resources.MonitorFace;
import autonomouscar.mapek.lite.adaptation.resources.MonitorHandsOnWheel;
import autonomouscar.mapek.lite.adaptation.resources.MonitorLine;
import autonomouscar.mapek.lite.adaptation.resources.MonitorModo;
import autonomouscar.mapek.lite.adaptation.resources.MonitorNivel;
import autonomouscar.mapek.lite.adaptation.resources.MonitorSpeedometer;
import autonomouscar.mapek.lite.adaptation.resources.MonitorSteering;
import autonomouscar.mapek.lite.adaptation.resources.MonitorTipo;
import autonomouscar.mapek.lite.adaptation.resources.MonitorVelocidad;
import autonomouscar.mapek.lite.adaptation.resources.OffRoadAdaptationRule;
import autonomouscar.mapek.lite.adaptation.resources.SensorAdaptationRule;
import autonomouscar.mapek.lite.adaptation.resources.SondaDistancia;
import autonomouscar.mapek.lite.adaptation.resources.SondaDistanciaSeguridad;
import autonomouscar.mapek.lite.adaptation.resources.SondaDriver;
import autonomouscar.mapek.lite.adaptation.resources.SondaEngine;
import autonomouscar.mapek.lite.adaptation.resources.SondaEstado;
import autonomouscar.mapek.lite.adaptation.resources.SondaFace;
import autonomouscar.mapek.lite.adaptation.resources.SondaHandsOnWheel;
import autonomouscar.mapek.lite.adaptation.resources.SondaLine;
import autonomouscar.mapek.lite.adaptation.resources.SondaModo;
import autonomouscar.mapek.lite.adaptation.resources.SondaNivel;
import autonomouscar.mapek.lite.adaptation.resources.SondaSpeedometer;
import autonomouscar.mapek.lite.adaptation.resources.SondaSteering;
import autonomouscar.mapek.lite.adaptation.resources.SondaTipo;
import autonomouscar.mapek.lite.adaptation.resources.SondaVelocidad;
import autonomouscar.mapek.lite.adaptation.resources.TrafficJamAdaptationRule;
import autonomouscar.mapek.lite.adaptation.resources.TrafficJamUrbanAdaptationRule;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.structures.systemconfiguration.interfaces.IComponentsSystemConfiguration;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.structures.systemconfiguration.interfaces.IRuleComponentsSystemConfiguration;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.SystemConfigurationHelper;
import es.upv.pros.tatami.osgi.utils.interfaces.ITimeStamped;
import es.upv.pros.tatami.osgi.utils.components.SearchTools;
import sua.autonomouscar.interfaces.IIdentifiable;
import es.upv.pros.tatami.osgi.utils.interfaces.ITimeStamped;

import sua.autonomouscar.infraestructure.devices.ARC.EngineARC;
import sua.autonomouscar.infraestructure.devices.ARC.RoadSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.HumanSensorsARC;
import sua.autonomouscar.infraestructure.devices.DistanceSensor;
import sua.autonomouscar.infraestructure.devices.ARC.DistanceSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.LineSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.SteeringARC;

import sua.autonomouscar.infraestructure.driving.ARC.FallbackPlanARC;
import sua.autonomouscar.infraestructure.driving.ARC.DrivingServiceARC;
import sua.autonomouscar.infraestructure.driving.ARC.L0_DrivingServiceARC;
import sua.autonomouscar.infraestructure.driving.ARC.L1_DrivingServiceARC;
import sua.autonomouscar.infraestructure.driving.ARC.L2_DrivingServiceARC;
import sua.autonomouscar.infraestructure.driving.ARC.L3_DrivingServiceARC;

import es.upv.pros.tatami.adaptation.mapek.lite.helpers.resources.adaptation.SelfConfigureProbe;
import es.upv.pros.tatami.adaptation.mapek.lite.resources.ARC.artifacts.components.arc.ProbeARC;
import sua.autonomouscar.infraestructure.interaction.ARC.NotificationServiceARC;
import sua.autonomouscar.simulation.*;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		BasicMAPEKLiteLoopHelper.BUNDLECONTEXT = bundleContext;
		BasicMAPEKLiteLoopHelper.REFERENCE_MODEL = "AutonomousCar"; //System.getProperty("model", "default-model");

		// ... adding the initial system configuration
		IComponentsSystemConfiguration theInitialSystemConfiguration = 
				SystemConfigurationHelper.createSystemConfiguration("InititalConfiguration");
		SystemConfigurationHelper.addComponent(theInitialSystemConfiguration, "device.RoadSensor", "1.0.0");
		BasicMAPEKLiteLoopHelper.INITIAL_SYSTEMCONFIGURATION = theInitialSystemConfiguration;

		BasicMAPEKLiteLoopHelper.MODELSREPOSITORY_FOLDER = System.getProperty("modelsrepository.folder");
		BasicMAPEKLiteLoopHelper.ADAPTATIONREPORTS_FOLDER = System.getProperty("adaptationreports.folder");
		// STARTING THE MAPE-K LOOP
		
		BasicMAPEKLiteLoopHelper.startLoopModules();

		
		
		BasicMAPEKLiteLoopHelper.addInitialSelfConfigurationCapabilities(createInitialSystemConfiguration());
		
		
		
		
		// ADAPTATION PROPERTIES
		IKnowledgeProperty kp_roadType = BasicMAPEKLiteLoopHelper.createKnowledgeProperty("Tipo_Carretera");
		IKnowledgeProperty kp_roadStatus = BasicMAPEKLiteLoopHelper.createKnowledgeProperty("Estado_Carretera");
		IKnowledgeProperty kp_nivelConduccion = BasicMAPEKLiteLoopHelper.createKnowledgeProperty("Nivel_Conduccion");

		IKnowledgeProperty kp_distanciaSeguridad = BasicMAPEKLiteLoopHelper.createKnowledgeProperty("Distancia_Seguridad");
		IKnowledgeProperty kp_humano = BasicMAPEKLiteLoopHelper.createKnowledgeProperty("Humano");
		IKnowledgeProperty kp_cara = BasicMAPEKLiteLoopHelper.createKnowledgeProperty("Cara");
		
		IKnowledgeProperty kp_manos = BasicMAPEKLiteLoopHelper.createKnowledgeProperty("Manos");
		IKnowledgeProperty kp_motor = BasicMAPEKLiteLoopHelper.createKnowledgeProperty("Motor");
		
		IKnowledgeProperty kp_steer = BasicMAPEKLiteLoopHelper.createKnowledgeProperty("Steering");
		IKnowledgeProperty kp_dist = BasicMAPEKLiteLoopHelper.createKnowledgeProperty("Distancia");
		IKnowledgeProperty kp_line = BasicMAPEKLiteLoopHelper.createKnowledgeProperty("Linea");
		
		
		
		
		// ADAPTATION RULES
	
 		IAdaptiveReadyComponent OffRoadAdaptationRuleARC_ADS_L3_1 = 
 				BasicMAPEKLiteLoopHelper.deployAdaptationRule(new OffRoadAdaptationRule(bundleContext));
 		
 		IAdaptiveReadyComponent TrafficJamAdaptationRuleARC_ADS_L3_2 = 
 				BasicMAPEKLiteLoopHelper.deployAdaptationRule(new TrafficJamAdaptationRule(bundleContext));
 		
 		IAdaptiveReadyComponent HighwayCityAdaptationRuleARC_ADS_L3_3 =
 				BasicMAPEKLiteLoopHelper.deployAdaptationRule(new HighwayCityAdaptationRule(bundleContext));
 		
 		IAdaptiveReadyComponent TrafficJam2UrbanAdaptationRuleARC_ADS_L3_5 = 
 				BasicMAPEKLiteLoopHelper.deployAdaptationRule(new TrafficJamUrbanAdaptationRule(bundleContext));
 		
 		IAdaptiveReadyComponent FallBackPlanAdaptationRuleARC_ADS_L3_8 = 
 				BasicMAPEKLiteLoopHelper.deployAdaptationRule(new FallBackPlanAdaptationRule(bundleContext));	
 		
 		IAdaptiveReadyComponent SensorAdaptationRuleARC_ADS_1 = 
 				BasicMAPEKLiteLoopHelper.deployAdaptationRule(new SensorAdaptationRule(bundleContext));

 		
		// MONITORS
		IAdaptiveReadyComponent roadTypeMonitorARC = BasicMAPEKLiteLoopHelper.deployMonitor(new MonitorTipo(bundleContext));		
		IAdaptiveReadyComponent roadStatusMonitorARC = BasicMAPEKLiteLoopHelper.deployMonitor(new MonitorEstado(bundleContext));		
		IAdaptiveReadyComponent speedMonitorARC = BasicMAPEKLiteLoopHelper.deployMonitor(new MonitorVelocidad(bundleContext));
		IAdaptiveReadyComponent securityDistanceMonitorARC = BasicMAPEKLiteLoopHelper.deployMonitor(new MonitorDistanciaSeguridad(bundleContext));
		IAdaptiveReadyComponent levelMonitorARC = BasicMAPEKLiteLoopHelper.deployMonitor(new MonitorNivel(bundleContext));
		IAdaptiveReadyComponent faceMonitorARC = BasicMAPEKLiteLoopHelper.deployMonitor(new MonitorFace(bundleContext));
		IAdaptiveReadyComponent driverMonitorARC = BasicMAPEKLiteLoopHelper.deployMonitor(new MonitorDriver(bundleContext));
		IAdaptiveReadyComponent handsOnWheelMonitorARC = BasicMAPEKLiteLoopHelper.deployMonitor(new MonitorHandsOnWheel(bundleContext));	
		IAdaptiveReadyComponent distanceMonitorARC = BasicMAPEKLiteLoopHelper.deployMonitor(new MonitorDistancia(bundleContext));
		IAdaptiveReadyComponent steeringMonitorARC = BasicMAPEKLiteLoopHelper.deployMonitor(new MonitorSteering(bundleContext));
		IAdaptiveReadyComponent engineMonitorARC = BasicMAPEKLiteLoopHelper.deployMonitor(new MonitorEngine(bundleContext));
		IAdaptiveReadyComponent lineMonitorARC = BasicMAPEKLiteLoopHelper.deployMonitor(new MonitorLine(bundleContext));
		
		
		// PROBES
		IAdaptiveReadyComponent roadTypeProbeARC = BasicMAPEKLiteLoopHelper.deployProbe(new SondaTipo(bundleContext), roadTypeMonitorARC);
		IAdaptiveReadyComponent roadStatusProbeARC = BasicMAPEKLiteLoopHelper.deployProbe(new SondaEstado(bundleContext), roadStatusMonitorARC);
		IAdaptiveReadyComponent speedProbeARC = BasicMAPEKLiteLoopHelper.deployProbe(new SondaVelocidad(bundleContext), speedMonitorARC);
		IAdaptiveReadyComponent securityDistanceProbeARC = BasicMAPEKLiteLoopHelper.deployProbe(new SondaDistanciaSeguridad(bundleContext), securityDistanceMonitorARC);
		IAdaptiveReadyComponent levelProbeARC = BasicMAPEKLiteLoopHelper.deployProbe(new SondaNivel(bundleContext), levelMonitorARC);
		IAdaptiveReadyComponent faceProbeARC = BasicMAPEKLiteLoopHelper.deployProbe(new SondaFace(bundleContext), faceMonitorARC);
		IAdaptiveReadyComponent driverProbeARC = BasicMAPEKLiteLoopHelper.deployProbe(new SondaDriver(bundleContext), driverMonitorARC);
		IAdaptiveReadyComponent handsOnWheelProbeARC = BasicMAPEKLiteLoopHelper.deployProbe(new SondaHandsOnWheel(bundleContext), handsOnWheelMonitorARC);
		IAdaptiveReadyComponent distanceProbeARC = BasicMAPEKLiteLoopHelper.deployProbe(new SondaDistancia(bundleContext), distanceMonitorARC);
		IAdaptiveReadyComponent steeringProbeARC = BasicMAPEKLiteLoopHelper.deployProbe(new SondaSteering(bundleContext), steeringMonitorARC);
		IAdaptiveReadyComponent engineProbeARC = BasicMAPEKLiteLoopHelper.deployProbe(new SondaEngine(bundleContext), engineMonitorARC);
		IAdaptiveReadyComponent lineProbeARC = BasicMAPEKLiteLoopHelper.deployProbe(new SondaLine(bundleContext), lineMonitorARC);
		
		
		//console.commands.initialize();
		String sondaFilter = String.format("(%s=%s)", IIdentifiable.ID, SelfConfigureProbe.ID);
		IAdaptiveReadyComponent selfConfigureProbeARC = SearchTools.doSearch(bundleContext, IAdaptiveReadyComponent.class, sondaFilter);
		SelfConfigureProbe selfConfigureProbe = (SelfConfigureProbe) selfConfigureProbeARC.getServiceSupply(ProbeARC.SUPPLY_PROBESERVICE);
		selfConfigureProbe.sendSelfConfigureRequest();
	}
	

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}


	protected IRuleComponentsSystemConfiguration createInitialSystemConfiguration() {
		
		IRuleComponentsSystemConfiguration theInitialSystemConfiguration = SystemConfigurationHelper.createPartialSystemConfiguration("InitialConfiguration_" + ITimeStamped.getCurrentTimeStamp());
			
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "device.RoadSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "device.Engine", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "device.Steering", "1.0.0");	
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "device.Speedometer", "1.0.0");

		
		
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "device.HumanSensors", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "device.HumanSensors.HandsOnWheelSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "device.HumanSensors.CopilotSeatSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "device.HumanSensors.DriverSeatSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "device.HumanSensors.DriverFaceSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "device.HumanSensors.DriverFaceMonitor", "1.0.0");

		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "device.LeftDistanceSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "device.FrontDistanceSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "device.RightDistanceSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "device.RearDistanceSensor", "1.0.0");

		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "device.LIDAR.LeftDistanceSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "device.LIDAR.FrontDistanceSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "device.LIDAR.RightDistanceSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "device.LIDAR.RearDistanceSensor", "1.0.0");
		
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "device.LeftLineSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "device.RightLineSensor", "1.0.0");
		
		
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "driving.L3.HighwayChauffer", "1.0.0");
		
		/*
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "driving.L3.CityChauffer", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "driving.L0.ManualDriving", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "driving.FallbackPlan.ParkInTheRoadShoulder", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "driving.FallbackPlan.Emergency", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "driving.L1.AssistedDriving", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "driving.L2.AdaptativeCruiseControl", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "driving.L2.LaneKeepingAssist", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "driving.L3.TrafficJamChauffer", "1.0.0");
		*/
		
		
		
		return theInitialSystemConfiguration;
		
	}
}
