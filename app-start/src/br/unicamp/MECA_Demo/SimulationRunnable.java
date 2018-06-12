/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.MECA_Demo;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.unicamp.MECA_Demo.codelets.communication.ReceiveMessageCodelet;
import br.unicamp.MECA_Demo.util.graph.Graph;
import br.unicamp.MECA_Demo.util.graph.Link;
import br.unicamp.MECA_Demo.util.graph.Node;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.Mind;
import br.unicamp.jtraci.entities.Edge;
import br.unicamp.jtraci.entities.InductionLoop;
import br.unicamp.jtraci.entities.Junction;
import br.unicamp.jtraci.entities.Lane;
import br.unicamp.jtraci.entities.Logic;
import br.unicamp.jtraci.entities.Phase;
import br.unicamp.jtraci.entities.TrafficLight;
import br.unicamp.jtraci.simulation.SumoSimulation;

/**
 * @author gudwin, andre
 */
public class SimulationRunnable implements Runnable {

    private SumoSimulation sumoSimulation;

    private List<TrafficLight> trafficLights;

    private List<InductionLoop> inductionLoops;

    public SimulationRunnable(String ipServidor, int port, boolean sumoGui, boolean sumoToConnect) {

        sumoSimulation = SumoSimulation.getInstance();
        //executeCommand("sumo-gui -c sumo/twinT.sumocfg --remote-port 8000 -S &");

        if (sumoToConnect) {
            try {
                if (sumoGui) {
                    Runtime.getRuntime().exec("sumo-gui -n sumo/twinT/twinT.net.xml -r sumo/twinT/twinT.p1.0.1.rou.xml -a sumo/twinT/twinT.add.xml --remote-port "+port+" -S");
                } else {
                    Runtime.getRuntime().exec("sumo -n sumo/twinT/twinT.net.xml -r sumo/twinT/twinT.p1.0.1.rou.xml -a sumo/twinT/twinT.add.xml --remote-port "+port+" -S");
                }
                //
                //Runtime.getRuntime().exec("sumo-gui -c sumo/twinT.sumocfg --remote-port 8000 -S");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //sumoSimulation.runSumoGui("sumo/twinT.sumocfg", port);
        //try {Thread.sleep(20000);} catch (Exception e) {e.printStackTrace();}
        System.out.println("Trying to connect... ");
        try {
            sumoSimulation.connect(InetAddress.getByName(ipServidor), port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Connected !");
        trafficLights = sumoSimulation.getAllTrafficLights();
        System.out.println("Simulation: " + trafficLights.size() + " traffic lights loaded !");
        inductionLoops = sumoSimulation.getAllInductionLoops();
        System.out.println("Simulation: " + inductionLoops.size() + " induction loops loaded !");
    }


    @Override
    public void run() {

        try {

            int stepTime = sumoSimulation.getCurrentStep();

            List<br.unicamp.jtraci.entities.Vehicle> vehicleList = sumoSimulation.getAllVehicles();
            int vehicleNumber = vehicleList.size();

            while (stepTime < 5 || vehicleNumber > 0) {
                /*
				 * Next step
				 */
                sumoSimulation.nextStep();
                stepTime++;
                vehicleList = sumoSimulation.getAllVehicles();
                vehicleNumber = vehicleList.size();
                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            sumoSimulation.close();

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } finally {
            System.exit(-1);
        }

    }


    public InductionLoop getInductionLoop(Lane l) {

        String incomingLaneID = l.getID();
        InductionLoop incomingLaneInductionLoop = null;
        if (inductionLoops != null) {
            for (InductionLoop inductionLoop : inductionLoops) {
                if (inductionLoop.getLaneID().equalsIgnoreCase(incomingLaneID)) {
                    incomingLaneInductionLoop = inductionLoop;
                    break;
                }
            }
        }
        return incomingLaneInductionLoop;
    }


    public List<Edge> getOutgoingEdgesFromJunction(String junctionID) {

        List<Edge> allEdges = sumoSimulation.getAllEdges();

        List<Edge> outgoingLanes = new ArrayList<Edge>();

        for (Edge edge : allEdges) {

            String[] edgeSplited = edge.getID().split(":");
            if (edgeSplited[0].equals(junctionID)) {
                outgoingLanes.add(edge);
            }
        }
        return outgoingLanes;
    }

    public Map<TrafficLight, Memory> getReceiveInputMessageMemories(List<TrafficLight> trafficLights, Mind mind) {

        Map<TrafficLight, Memory> inputMessageBuffer = new HashMap<>();

        for (TrafficLight trafficLight : trafficLights) {

            Memory inputMessagesMO = mind.createMemoryObject(ReceiveMessageCodelet.INPUT_MESSAGE, "-1");
            inputMessageBuffer.put(trafficLight, inputMessagesMO);
        }
        return inputMessageBuffer;
    }

    public Junction findJuction(String junctionID, List<Junction> junctions) {

        Junction junction = null;
        for (Junction it : junctions) {
            if (it.getID().equals(junctionID)) {
                junction = it;
                break;
            }
        }
        return junction;
    }

    public TrafficLight findTrafficLight(String trafficLightID, List<TrafficLight> trafficLights) {

        TrafficLight trafficLight = null;
        for (TrafficLight it : trafficLights) {
            if (it.getID().equals(trafficLightID)) {
                trafficLight = it;
                break;
            }
        }
        return trafficLight;
    }

    public Graph findNeighbors(Map<String, List<Edge>> agentsOutogingEdges, List<Junction> junctions, List<TrafficLight> trafficLights) {

        Graph newGraph = new Graph("Traffic");
        List<String> agentsToDo = new ArrayList<>();
        agentsToDo.add(junctions.get(0).getID());
        List<String> agentsDone = new ArrayList<>();
        while (agentsToDo.size() > 0) {
            String junctionID = agentsToDo.get(0);
            if (!agentsDone.contains(junctionID)) {
                List<Edge> outgoingEdges = agentsOutogingEdges.get(junctionID);
                for (Edge outgoingEdge : outgoingEdges) {
                    TrafficLight trafficLight = findTrafficLight(junctionID, trafficLights);
                    Junction junction = findJuction(junctionID, junctions);
                    Node refereceNode = new Node(junctionID, trafficLight != null ? trafficLight : junction);
                    Link newLink = new Link(outgoingEdge.getID(), outgoingEdge.getCurrentTravelTime() * outgoingEdge.getLastStepMeanSpeed());
                    String[] junctionPrevious = outgoingEdge.getID().split(":");
                    TrafficLight trafficLightNewNode = findTrafficLight(junctionPrevious[1], trafficLights);
                    Junction junctionNewNode = findJuction(junctionPrevious[1], junctions);
                    Node newNode = new Node(junctionPrevious[1], trafficLightNewNode != null ? trafficLightNewNode : junctionNewNode);
                    newGraph.addNode(newNode, newLink, refereceNode);
                    agentsToDo.add(junctionPrevious[1]);
                }
                agentsToDo.remove(0);
                agentsDone.add(junctionID);
            } else {
                agentsToDo.remove(0);
            }
        }


        return newGraph;
    }

    public ArrayList<Phase> getPhases(TrafficLight trafficLight) {

        List<Logic> logics = trafficLight.getCompleteDefinition();

        ArrayList<Phase> phases = new ArrayList<>();

        for (Logic logic : logics) {

            phases.addAll(logic.getPhases());
        }
        return phases;
    }

    /**
     * @return the trafficLights
     */
    public List<TrafficLight> getTrafficLights() {
        return trafficLights;
    }


}
