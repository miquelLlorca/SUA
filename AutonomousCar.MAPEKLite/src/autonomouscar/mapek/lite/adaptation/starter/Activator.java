package autonomouscar.mapek.lite.adaptation.starter;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import autonomouscar.mapek.lite.adaptation.resources.IluminacionConfortAdaptationRule;
import autonomouscar.mapek.lite.adaptation.resources.MonitorModo;
import autonomouscar.mapek.lite.adaptation.resources.MonitorTipo;
import autonomouscar.mapek.lite.adaptation.resources.OffRoadAdaptationRule;
import autonomouscar.mapek.lite.adaptation.resources.SondaModo;
import autonomouscar.mapek.lite.adaptation.resources.SondaTipo;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.structures.systemconfiguration.interfaces.IComponentsSystemConfiguration;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.structures.systemconfiguration.interfaces.IRuleComponentsSystemConfiguration;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.SystemConfigurationHelper;
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


import sua.autonomouscar.infraestructure.interaction.ARC.NotificationServiceARC;


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
		IKnowledgeProperty kp_nivelConduccion = BasicMAPEKLiteLoopHelper.createKnowledgeProperty("Nivel_Conduccion");


		// ADAPTATION RULES
 		IAdaptiveReadyComponent OffRoadAdaptationRuleARC_ADS_L3_1 = 
 				BasicMAPEKLiteLoopHelper.deployAdaptationRule(new OffRoadAdaptationRule(bundleContext));	
		

 		
		// MONITORS
		IAdaptiveReadyComponent roadTypeMonitorARC = BasicMAPEKLiteLoopHelper.deployMonitor(new MonitorTipo(bundleContext));		

		// PROBES
		IAdaptiveReadyComponent roadTypeProbeARC = BasicMAPEKLiteLoopHelper.deployProbe(new SondaTipo(bundleContext), roadTypeMonitorARC);


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
		
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "driving.FallbackPlan.ParkInTheRoadShoulder", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "driving.FallbackPlan.Emergency", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "driving.L0.ManualDriving", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "driving.L1.AssistedDriving", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "driving.L2.AdaptativeCruiseControl", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "driving.L2.LaneKeepingAssist", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "driving.L3.CityChauffer", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "driving.L3.HighwayChauffer", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "driving.L3.TrafficJamChauffer", "1.0.0");
		
		
		
		
		return theInitialSystemConfiguration;
		
	}
}
