/**
 * 
 */
package br.unicamp.MECA_Demo.codelets.sensory;

import java.util.ArrayList;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.jtraci.entities.InductionLoop;
import br.unicamp.jtraci.entities.Lane;
import br.unicamp.meca.system1.codelets.SensoryCodelet;

/**
 * @author andre
 * 
 * It is important for this SensoryCodelet to work properly that the Induction Loops
 * are positioned very close to the Traffic Lights, for instance 5m away
 *
 */
public class VehicleNumberSensor extends SensoryCodelet {

	private ArrayList<InductionLoop> inductionLoops;
	
	private ArrayList<Lane> incomingLanes;

	private Memory vehicleNumberMO;

	/**
	 * @param id
	 * @param inductionLoops
	 * @param incomingLanes 
	 */
	public VehicleNumberSensor(String id, ArrayList<InductionLoop> inductionLoops, ArrayList<Lane> incomingLanes) {
		super(id);
		this.inductionLoops = inductionLoops;
		this.incomingLanes = incomingLanes;
	}

	@Override
	public void accessMemoryObjects() {

		int index=0;

		if(vehicleNumberMO == null)
			vehicleNumberMO = this.getOutput(id, index);

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

			ArrayList<Integer> laneVehicleNumbers = new ArrayList<>();

			for(Lane lane : incomingLanes){

				if(lane!=null)
					laneVehicleNumbers.add(lane.getLastStepVehicleNumber());

			}

			vehicleNumberMO.setI(laneVehicleNumbers);


		} else {

			vehicleNumberMO.setI(null);

		}

	}

}
