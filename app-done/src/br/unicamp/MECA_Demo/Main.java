/**
 * Copyright 2016 CST-Group
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.unicamp.MECA_Demo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.unicamp.MECA_Demo.codelets.behavioral.motivational.ChangeTrafficLightPhase;
import br.unicamp.MECA_Demo.codelets.behavioral.motivational.MaintainTrafficLightPhase;
import br.unicamp.MECA_Demo.codelets.behavioral.random.RandomChangePhase;
import br.unicamp.MECA_Demo.codelets.behavioral.reactive.ReactToLanesSituation;
import br.unicamp.MECA_Demo.codelets.motivational.ChangingMotivationalCodelet;
import br.unicamp.MECA_Demo.codelets.motivational.MaintainingMotivationalCodelet;
import br.unicamp.MECA_Demo.codelets.motor.TrafficLightActuator;
import br.unicamp.MECA_Demo.codelets.perceptual.SituationPerceptualCodelet;
import br.unicamp.MECA_Demo.codelets.perceptual.SmartCarPerceptualCodelet;
import br.unicamp.MECA_Demo.codelets.sensory.CurrentPhaseSensor;
import br.unicamp.MECA_Demo.codelets.sensory.MeanSpeedSensor;
import br.unicamp.MECA_Demo.codelets.sensory.OccupancySensor;
import br.unicamp.MECA_Demo.codelets.sensory.PhaseTimeSensor;
import br.unicamp.MECA_Demo.codelets.sensory.SmartCarInfoSensor;
import br.unicamp.MECA_Demo.codelets.sensory.VehicleNumberSensor;
import br.unicamp.MECA_Demo.codelets.system2.SmartCarAttentionCodelet;
import br.unicamp.MECA_Demo.codelets.system2.TrafficSoarCodelet;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.cst.util.MindViewer;
import br.unicamp.jtraci.entities.ControlledLinks;
import br.unicamp.jtraci.entities.InductionLoop;
import br.unicamp.jtraci.entities.Lane;
import br.unicamp.jtraci.entities.Phase;
import br.unicamp.jtraci.entities.TrafficLight;
import br.unicamp.meca.mind.MecaMind;
import br.unicamp.meca.system1.codelets.MotivationalBehavioralCodelet;
import br.unicamp.meca.system1.codelets.MotivationalCodelet;
import br.unicamp.meca.system1.codelets.MotorCodelet;
import br.unicamp.meca.system1.codelets.PerceptualCodelet;
import br.unicamp.meca.system1.codelets.RandomBehavioralCodelet;
import br.unicamp.meca.system1.codelets.ReactiveBehavioralCodelet;
import br.unicamp.meca.system1.codelets.SensoryCodelet;

/**
 * @author andre
 */
public class Main {

	public Main(String ipServidor, int port, boolean sumoGui, boolean sumoConnect, ArrayList<String> smartCarsIDs) {
		try{
			//NativeUtils.loadFileFromJar("soar_rules/soarRulesVera.soar");
                    NativeUtils.loadFileFromJar("soar_rules/soarRulesVera.soar");    
		}catch(Exception e){
		}

		String soarRulesPath = "soar_rules/soarRulesVera.soar";

		List<MecaMind> mecaMinds = new ArrayList<>();

		SimulationRunnable simulationRunnable = new SimulationRunnable(ipServidor, port, sumoGui, sumoConnect);

		for (TrafficLight trafficLight : simulationRunnable.getTrafficLights()) {

			/*
			 * Declaring System 1 elements
			 */
			List<SensoryCodelet> sensoryCodelets = new ArrayList<>();
			List<PerceptualCodelet> perceptualCodelets = new ArrayList<>();
			List<MotivationalCodelet> motivationalCodelets = new ArrayList<>();
			List<RandomBehavioralCodelet> randomBehavioralCodelets = new ArrayList<>();
			List<ReactiveBehavioralCodelet> reactiveBehavioralCodelets = new ArrayList<>();
			List<MotivationalBehavioralCodelet> motivationalBehavioralCodelets = new ArrayList<>();
			List<MotorCodelet> motorCodelets = new ArrayList<>();

			ArrayList<Phase> phases = simulationRunnable.getPhases(trafficLight);
			TrafficLightActuator trafficLightActuator = new TrafficLightActuator("TrafficLightActuator" + trafficLight.getID(), trafficLight, phases);
			motorCodelets.add(trafficLightActuator);

			ArrayList<Lane> incomingLanes = new ArrayList<>();
			ArrayList<InductionLoop> inductionLoops = new ArrayList<>();
			ArrayList<String> sensoryCodeletsIds = new ArrayList<>();
			ArrayList<String> perceptualCodeletsIds = new ArrayList<>();
			ArrayList<String> changingMotivationalCodeletIds = new ArrayList<>();
			ArrayList<String> maintainMotivationalCodeletsIds = new ArrayList<>();

			ArrayList<String> incomingLaneIds = new ArrayList<>();
			ArrayList<String> smartCarSensoryCodeletsIds = new ArrayList<>();
			ArrayList<String> smartCarPerceptualCodeletsIds = new ArrayList<>();

			ControlledLinks controlledLinks = trafficLight.getControlledLinks();
			br.unicamp.jtraci.entities.Link[][] links = controlledLinks.getLinks();
			for (int i = 0; i < links.length; i++) {
				for (int j = 0; j < links[i].length; j++) {

					br.unicamp.jtraci.entities.Link link = links[i][j];
					Lane incomingLane = link.getIncomingLane();
					incomingLanes.add(incomingLane);
					incomingLaneIds.add(incomingLane.getID());
					inductionLoops.add(simulationRunnable.getInductionLoop(incomingLane));
				}
			}

			OccupancySensor occupancySensor = new OccupancySensor("OccupancySensor - " + trafficLight.getID(), inductionLoops, incomingLanes, trafficLight);
			sensoryCodeletsIds.add(occupancySensor.getId());
			sensoryCodelets.add(occupancySensor);

			PhaseTimeSensor phaseTimeSensor = new PhaseTimeSensor("PhaseTimeSensor - " + trafficLight.getID(), trafficLight);
			sensoryCodeletsIds.add(phaseTimeSensor.getId());
			sensoryCodelets.add(phaseTimeSensor);	

			VehicleNumberSensor vehicleNumberSensor = new VehicleNumberSensor("VehicleNumberSensor - " + trafficLight.getID(), inductionLoops, incomingLanes);
			sensoryCodeletsIds.add(vehicleNumberSensor.getId());
			sensoryCodelets.add(vehicleNumberSensor);

			MeanSpeedSensor meanSpeedSensor = new MeanSpeedSensor("MeanSpeedSensor - " + trafficLight.getID(), inductionLoops, incomingLanes);
			sensoryCodeletsIds.add(meanSpeedSensor.getId());
			sensoryCodelets.add(meanSpeedSensor);
			
			CurrentPhaseSensor currentPhaseSensor = new CurrentPhaseSensor("CurrentPhaseSensor - " + trafficLight.getID(), trafficLight);
			sensoryCodeletsIds.add(currentPhaseSensor.getId());
			sensoryCodelets.add(currentPhaseSensor);	

			if(smartCarsIDs!=null && smartCarsIDs.size() > 0){												

				SmartCarInfoSensor smartCarInfoSensor = new SmartCarInfoSensor("SmartCarInfoSensor - " + trafficLight.getID(), smartCarsIDs ,incomingLaneIds,trafficLight);           
				smartCarSensoryCodeletsIds.add(smartCarInfoSensor.getId());
				sensoryCodelets.add(smartCarInfoSensor);

			}

			//Motivational Codelets
			ChangingMotivationalCodelet changingMotivationalCodelet;
			MaintainingMotivationalCodelet maintainingMotivationalCodelet;

			try {
				changingMotivationalCodelet = new ChangingMotivationalCodelet("ChangingMotivationalCodelet - " + trafficLight.getID(), 0, 0.5, 0.3, sensoryCodeletsIds, new HashMap<String, Double>());
				maintainingMotivationalCodelet = new MaintainingMotivationalCodelet("MaintainingMotivationalCodelet - " + trafficLight.getID(), 0, 0.49, 0.9677, sensoryCodeletsIds, new HashMap<String, Double>());

				changingMotivationalCodeletIds.add(changingMotivationalCodelet.getId());
				maintainMotivationalCodeletsIds.add(maintainingMotivationalCodelet.getId());

				motivationalCodelets.add(changingMotivationalCodelet);
				motivationalCodelets.add(maintainingMotivationalCodelet);

			} catch (CodeletActivationBoundsException e) {
				e.printStackTrace();
			}

			SituationPerceptualCodelet situationPerceptualCodelet = new SituationPerceptualCodelet("SituationPerceptualCodelet - " + trafficLight.getID(), sensoryCodeletsIds);
			perceptualCodeletsIds.add(situationPerceptualCodelet.getId());
			perceptualCodelets.add(situationPerceptualCodelet);

			SmartCarPerceptualCodelet smartCarPerceptualCodelet = new SmartCarPerceptualCodelet("SmartCarPerceptualCodelet - " + trafficLight.getID(), smartCarSensoryCodeletsIds);
			smartCarPerceptualCodeletsIds.add(smartCarPerceptualCodelet.getId());
			perceptualCodelets.add(smartCarPerceptualCodelet);

			File test = new File (soarRulesPath);
			TrafficSoarCodelet trafficSoar = new TrafficSoarCodelet("TrafficSoarCodelet - " + trafficLight.getID(),"br.unicamp.MECA_Demo.util.SOAR_commands","TrafficSoarCodelet - " + trafficLight.getID(), new File (soarRulesPath), false);

			RandomChangePhase openRandomPhase = new RandomChangePhase("RandomChangePhase-TL" + trafficLight.getID(), trafficLightActuator.getId(),trafficSoar.getId());
			randomBehavioralCodelets.add(openRandomPhase);

			ReactToLanesSituation reactToLanesSituation = new ReactToLanesSituation("ReactToLanesSituation-TL" + trafficLight.getID(), perceptualCodeletsIds, trafficLightActuator.getId(),trafficSoar.getId());
			reactiveBehavioralCodelets.add(reactToLanesSituation);

			ChangeTrafficLightPhase changeTrafficLightPhase = new ChangeTrafficLightPhase("ChangeTrafficLightPhase-TL" + trafficLight.getID(), trafficLightActuator.getId(), changingMotivationalCodeletIds,trafficSoar.getId());
			motivationalBehavioralCodelets.add(changeTrafficLightPhase);

			MaintainTrafficLightPhase maintainTrafficLightPhase = new MaintainTrafficLightPhase("MaintainTrafficLightPhase-TL" + trafficLight.getID(), trafficLightActuator.getId(), maintainMotivationalCodeletsIds,trafficSoar.getId());
			motivationalBehavioralCodelets.add(maintainTrafficLightPhase);


			smartCarPerceptualCodeletsIds.add(situationPerceptualCodelet.getId());
			SmartCarAttentionCodelet smartAttention = new SmartCarAttentionCodelet("SmartCarAttentionCodelet - " + trafficLight.getID(), smartCarPerceptualCodeletsIds,trafficLight, phases);                                    

			MecaMind mecaMind = new MecaMind("Mind of the TL "+trafficLight.getID());
			mecaMind.setSensoryCodelets(sensoryCodelets);
			mecaMind.setPerceptualCodelets(perceptualCodelets);
			mecaMind.setMotivationalCodelets(motivationalCodelets);
			mecaMind.setRandomBehavioralCodelets(randomBehavioralCodelets);
			mecaMind.setReactiveBehavioralCodelets(reactiveBehavioralCodelets);
			mecaMind.setMotivationalBehavioralCodelets(motivationalBehavioralCodelets);
			mecaMind.setMotorCodelets(motorCodelets);

			mecaMind.setAttentionCodeletSystem1(smartAttention);
			mecaMind.setSoarCodelet(trafficSoar);

			mecaMind.mountMecaMind();

			mecaMinds.add(mecaMind);

		}

		for (MecaMind mecaMind : mecaMinds) {

			mecaMind.start();
			System.out.println();
		}

		Thread simulationThread = new Thread(simulationRunnable);
		simulationThread.start();
		
		if(sumoGui){

			for (MecaMind mecaMind : mecaMinds) {

				List<Codelet> listOfCodelets = new ArrayList<>();
				listOfCodelets.addAll(mecaMind.getRandomBehavioralCodelets());
				listOfCodelets.addAll(mecaMind.getReactiveBehavioralCodelets());
				listOfCodelets.addAll(mecaMind.getMotivationalBehavioralCodelets());

				listOfCodelets.add(mecaMind.getSoarCodelet());

				MindViewer mv = new MindViewer(mecaMind, "MECA Mind Inspection - "+mecaMind.getId(), listOfCodelets);
				//mv.setVisible(true);
				//mv.StartTimer();
				//mv.updateTree(mecaMind);

				List<? extends Codelet> codelets = mecaMind.getMotivationalCodelets();

				mv.initMotivationalSubsystemViewer((List<Codelet>) codelets,
						new ArrayList<Codelet>(),
						new ArrayList<Codelet>(),
						new ArrayList<Codelet>(),
						new ArrayList<Codelet>());

				mv.setVisible(true);
				//mv.StartTimer();
				//mv.updateTree(mecaMind);


				/*
				 * Code Rack monitor
				 */


				//CodeletsMonitor codeletsMonitor = new CodeletsMonitor(listOfCodelets, 100l, "CodeRack Inspection - "+mecaMind.getId(), true, 10000);
				//codeletsMonitor.start();

			}			
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println("Usage: AppSumoECA <P1> <P2> <P3> <P4> <P5>* <P6>* ... <Pn>*");
		System.out.println("<P1> = SUMO Server IP");
		System.out.println("<P2> = SUMO Server port");
		System.out.println("<P3> = Start SUMO (-R)/ SUMO already running (-L)");
		System.out.println("<P4> = SUMO Gui / Meca Viewer (-S) / None (-N)");
		System.out.println("<P5>* <P6>* ... <Pn>* = List of Smart Cars IDs (optional)");

		if (args.length < 4) {
			return;
		}

		String ipServidor = args[0];
		int port = Integer.valueOf(args[1]);
		ArrayList<String> smartCarsIDs = null;

		boolean sumoGui = false;
		boolean sumoConnect = false;

		if(args[2].equals("-R")){
		    sumoConnect = true;
        }

        if(args[3].equals("-S")){
		    sumoGui = true;
        }

		if(args.length > 4){

			smartCarsIDs = new ArrayList<>();

			for(int i = 4; i < args.length; i++){
				smartCarsIDs.add(args[i]);
			}

		}

		Main app = new Main(ipServidor, port, sumoGui, sumoConnect,smartCarsIDs);

	}

}
