package br.unicamp.MECA_Demo.codelets.motivational;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.unicamp.MECA_Demo.codelets.perceptual.SituationPerceptualCodelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.cst.motivational.Drive;
import br.unicamp.jtraci.entities.Logic;
import br.unicamp.jtraci.entities.Phase;
import br.unicamp.meca.system1.codelets.MotivationalCodelet;

/**
 * Created by du on 16/03/17.
 */
public class ChangingMotivationalCodelet extends MotivationalCodelet {

	public ChangingMotivationalCodelet(String id, double level, double priority, double urgencyThreshold, ArrayList<String> sensoryCodeletsIds, HashMap<String, Double> motivaitonalCodeletsIds) throws CodeletActivationBoundsException {
		super(id, level, priority, urgencyThreshold, sensoryCodeletsIds, motivaitonalCodeletsIds);
		
	}

	@Override
	public double calculateSimpleActivation(List<Memory> sensors) {


		double activation = 0;

		ArrayList<Integer> laneVehicleNumbers = null;
		ArrayList<Double> laneMeanSpeeds = null;
		List<Double> laneOccupancies = null;
		Logic currentLogic = null;

		for (Memory sensoryMemory : sensors) {

			if (sensoryMemory.getName().contains("OccupancySensor") && sensoryMemory.getI() instanceof ArrayList) {
				laneOccupancies = (ArrayList<Double>) sensoryMemory.getI();
			}

			if (sensoryMemory.getName() != null && sensoryMemory.getName().contains("MeanSpeedSensor") && sensoryMemory.getI() instanceof ArrayList) {

				laneMeanSpeeds = (ArrayList<Double>) sensoryMemory.getI();
			}

			if (sensoryMemory.getName() != null && sensoryMemory.getName().contains("VehicleNumberSensor") && sensoryMemory.getI() instanceof ArrayList) {

				laneVehicleNumbers = (ArrayList<Integer>) sensoryMemory.getI();
			}

			if (sensoryMemory.getName() != null && sensoryMemory.getName().contains("CurrentPhaseSensor") && sensoryMemory.getI() instanceof Logic) {

				currentLogic = (Logic) sensoryMemory.getI();
			}
		}

		if(currentLogic!=null && currentLogic.getPhases()!=null && currentLogic.getPhases().size() > 0 && laneOccupancies!=null && laneOccupancies.size() > 0 && laneMeanSpeeds!=null && laneMeanSpeeds.size() > 0 && laneVehicleNumbers!=null && laneVehicleNumbers.size() > 0){

			List<Phase> possiblePhases = currentLogic.getPhases();

			int bestPhaseIndex = -1;

			double bestPhaseValue = Double.NEGATIVE_INFINITY;

			for(int i=0; i < possiblePhases.size(); i++)
			{
				Phase phase = possiblePhases.get(i); 			
				double phaseValue = 0.0d;

				for(int j=0; j < phase.getDefinition().length();j++)
				{
					if(phase.getDefinition().charAt(j) == 'g' || phase.getDefinition().charAt(j) == 'G'){

						Integer vehicleNumber = laneVehicleNumbers.get(j);
						Double laneMeanSpeed = laneMeanSpeeds.get(j);
						Double laneOccupancy = laneOccupancies.get(j);

						if(vehicleNumber > 0){

							phaseValue += laneOccupancy + (1.0d - laneMeanSpeed / SituationPerceptualCodelet.AVERAGE_MEAN_SPEED);

						}				    					
					}
				}

				if(phaseValue>bestPhaseValue)
				{
					bestPhaseValue = phaseValue;
					bestPhaseIndex = i;
				}
			}

			if(bestPhaseIndex == currentLogic.getCurrentPhase()){

				activation = 0.0d;

			} else {

				activation = 1.0d;
			}

		}

		return activation;
	}

	@Override
	public double calculateSecundaryDriveActivation(List<Memory> sensors, List<Drive> drives) {
		return 0;
	}
}
