/**
 * 
 */
package br.unicamp.MECA_Demo.codelets.sensory;

import java.util.ArrayList;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.jtraci.entities.InductionLoop;
import br.unicamp.jtraci.entities.Lane;
import br.unicamp.jtraci.entities.TrafficLight;
import br.unicamp.meca.system1.codelets.SensoryCodelet;

/**
 * @author andre
 * 
 * It is important for this SensoryCodelet to work properly that the Induction Loops
 * are positioned very close to the Traffic Lights, for instance 5m away
 *
 */
public class OccupancySensor extends SensoryCodelet {

	private ArrayList<InductionLoop> inductionLoops;
	
	private ArrayList<Lane> incomingLanes;

	private TrafficLight trafficLight;

	private Memory occupancyMO;

	/**
	 * @param id
	 * @param inductionLoops
	 * @param incomingLanes 
	 */
	public OccupancySensor(String id, ArrayList<InductionLoop> inductionLoops, ArrayList<Lane> incomingLanes, TrafficLight trafficLight) {
		super(id);
		this.inductionLoops = inductionLoops;
		this.incomingLanes = incomingLanes;
		this.trafficLight = trafficLight;
	}

	@Override
	public void accessMemoryObjects() {

		int index=0;

		if(occupancyMO == null)
			occupancyMO = this.getOutput(id, index);

	}

	@Override
	public void calculateActivation() {

		try{

			setActivation(0.0d);

		} catch (CodeletActivationBoundsException e) {

			e.printStackTrace();
		}

	}

	@Override
	public void proc() {

		if(incomingLanes!=null && incomingLanes.size()>0){

			ArrayList<Double> lanesOccupancies = new ArrayList<>();

            // Get All Lane Occupancies
            for(Lane lane : incomingLanes){

                if(lane!=null)
                    lanesOccupancies.add(lane.getLastStepOccupancy());

            }

			occupancyMO.setI(lanesOccupancies);


		} else {

			occupancyMO.setI(null);

		}


	}

}
