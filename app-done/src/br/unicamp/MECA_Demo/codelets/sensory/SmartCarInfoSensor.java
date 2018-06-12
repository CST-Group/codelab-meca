/**
 * 
 */
package br.unicamp.MECA_Demo.codelets.sensory;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.jtraci.entities.Lane;
import br.unicamp.jtraci.entities.TrafficLight;
import br.unicamp.jtraci.entities.Vehicle;
import br.unicamp.meca.system1.codelets.SensoryCodelet;

/**
 * @author andre
 *
 */
public class SmartCarInfoSensor extends SensoryCodelet {

	private Memory smartCarInfoMO;

	private ArrayList<Vehicle> smartCars;

	private ArrayList<String> incomingLaneIds;

	private TrafficLight trafficLight;

	/**
	 * 
	 * @param id
	 * @param smartCarsIDs
	 * @param incomingLaneIds 
	 * @param trafficLight 
	 */
	public SmartCarInfoSensor(String id, ArrayList<String> smartCarsIDs, ArrayList<String> incomingLaneIds, TrafficLight trafficLight) {

		super(id);	

		smartCars = new ArrayList<>();

		for(String smartCarId : smartCarsIDs){

			Vehicle smartCar = new Vehicle();
			smartCar.setID(smartCarId);

			smartCars.add(smartCar);

		}

		this.incomingLaneIds = incomingLaneIds;
		this.trafficLight = trafficLight;


	}

	/* (non-Javadoc)
	 * @see br.unicamp.cst.core.entities.Codelet#accessMemoryObjects()
	 */
	@Override
	public void accessMemoryObjects() {

		int index=0;

		if(smartCarInfoMO == null)
			smartCarInfoMO = this.getOutput(id, index);


	}

	/* (non-Javadoc)
	 * @see br.unicamp.cst.core.entities.Codelet#calculateActivation()
	 */
	@Override
	public void calculateActivation() {

		try{

			setActivation(0.0d);

		} catch (CodeletActivationBoundsException e) {

			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see br.unicamp.cst.core.entities.Codelet#proc()
	 */
	@Override
	public void proc() {

		try{

			ArrayList<String> allVehiclesRunning = (ArrayList<String>) smartCars.get(0).getAllVehicles();

			if(allVehiclesRunning!=null && allVehiclesRunning.size()>0){

				String smartCarsInfoString = "";

				for(Vehicle smartCar : smartCars){

					if(allVehiclesRunning.contains(smartCar.getID())){

						String laneId = smartCar.getLaneID();

						if(laneId!=null && incomingLaneIds.contains(laneId)){

							double carSpeed = smartCar.getSpeed();

							double carX = 0.0d;

							double carY = 0.0d;

							Point2D carPosition = smartCar.getPosition();
							if(carPosition!=null){
								carX = carPosition.getX();
								carY = carPosition.getY();
							}

							Lane lane = new Lane(laneId);

							Path2D laneShape = lane.getShape();

							Point2D lightPosition = laneShape.getCurrentPoint();

							double distanceBetweenCarAndLight = carPosition.distance(lightPosition);

							int indexOfIncomingLane = incomingLaneIds.indexOf(laneId);

							String tfState = trafficLight.getState();

							char stateOfSmartCarLane = tfState.charAt(indexOfIncomingLane);

							String smartCarInfoString = smartCar.getID()+";"+carX+";"+carY+";"+carSpeed+";"+laneId+";"+distanceBetweenCarAndLight+";"+stateOfSmartCarLane;

							//							System.out.println(smartCarInfoString);		

							if(smartCarsInfoString.length()==0)
								smartCarsInfoString+=smartCarInfoString;
							else
								smartCarsInfoString+="/"+smartCarInfoString;

						}
					}			
				}

				if(smartCarsInfoString.length()>0)
					smartCarInfoMO.setI(smartCarsInfoString);
				else
					smartCarInfoMO.setI(null);

			}else {

				smartCarInfoMO.setI(null);
			}		

		}catch(Exception e){

			smartCarInfoMO.setI(null);

		}

	}

}
