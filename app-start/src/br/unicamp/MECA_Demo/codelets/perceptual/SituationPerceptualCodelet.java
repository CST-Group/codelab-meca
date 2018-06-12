/**
 *
 */
package br.unicamp.MECA_Demo.codelets.perceptual;

import java.util.ArrayList;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.meca.system1.codelets.PerceptualCodelet;

/**
 * @author andre
 */
public class SituationPerceptualCodelet extends PerceptualCodelet {

    public static final Double AVERAGE_MEAN_SPEED = 16.6666d;

    private ArrayList<Memory> sensoryMemories = new ArrayList<Memory>();

    private Memory trafficSituation;
    
    private Integer timeWhenCurrentPhaseWasSet;
    
    private ArrayList<Double> laneOccupancies;
    
    private ArrayList<Integer> laneVehicleNumbers;
    
    private ArrayList<Double> laneMeanSpeeds;

    public SituationPerceptualCodelet(String id, ArrayList<String> sensoryCodeletsIds) {
        super(id, sensoryCodeletsIds);
    }

    @Override
    public void accessMemoryObjects() {

        int index = 0;

        if (trafficSituation == null)
            trafficSituation = this.getOutput(id, index);

        if (sensoryMemories == null || sensoryMemories.size() == 0) {
            if (sensoryCodeletsIds != null) {

                for (String sensoryCodeletsId : sensoryCodeletsIds) {
                    sensoryMemories.add(this.getInput(sensoryCodeletsId, index));
                }

            }

        }


    }

    @Override
    public void calculateActivation() {

        double activation = 0.0d;

        int counter = 0;              

        if (sensoryMemories != null && sensoryMemories.size() > 0) {

            for (Memory sensoryMemory : sensoryMemories) {

                if (sensoryMemory != null && sensoryMemory.getName() != null && sensoryMemory.getName().contains("PhaseTimeSensor") && sensoryMemory.getI() instanceof Integer) {

                    timeWhenCurrentPhaseWasSet = (Integer) sensoryMemory.getI();
                }

                if (sensoryMemory != null && sensoryMemory.getName() != null && sensoryMemory.getName().contains("OccupancySensor") && sensoryMemory.getI() instanceof ArrayList) {

                    laneOccupancies = (ArrayList<Double>) sensoryMemory.getI();
                }

                if (sensoryMemory != null && sensoryMemory.getName() != null && sensoryMemory.getName().contains("VehicleNumberSensor") && sensoryMemory.getI() instanceof ArrayList) {

                    laneVehicleNumbers = (ArrayList<Integer>) sensoryMemory.getI();
                }

                if (sensoryMemory != null && sensoryMemory.getName() != null && sensoryMemory.getName().contains("MeanSpeedSensor") && sensoryMemory.getI() instanceof ArrayList) {

                    laneMeanSpeeds = (ArrayList<Double>) sensoryMemory.getI();
                }
            }


            if (laneMeanSpeeds != null && laneVehicleNumbers != null && laneVehicleNumbers.size() > 0 && laneMeanSpeeds.size() > 0) {

                Double meanSpeed = 0.0d;

                for (int i = 0; i < laneMeanSpeeds.size(); i++) {

                    Double laneMeanSpeed = laneMeanSpeeds.get(i);
                    Integer laneVehicleNumber = laneVehicleNumbers.get(i);
                    if (laneVehicleNumber > 0) {
                        meanSpeed += laneMeanSpeed;
                        counter++;
                    }

                }

                if(counter > 0){
                	
                	meanSpeed /= counter;

                    activation = 1.0d - meanSpeed / AVERAGE_MEAN_SPEED;
                    
                } else {
                	
                	activation = 0.0d;
                	
                }
                
                
            }


        }

        try {
            if (activation < 0.0d)
                activation = 0.0d;

            if (activation > 1.0d)
                activation = 1.0d;

            this.setActivation(activation);

        } catch (CodeletActivationBoundsException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void proc() {
    	
        Double laneOccupanciesAverage = -1.0d;
        
        if(laneOccupancies!=null && laneOccupancies.size() > 0){
        	
        	laneOccupanciesAverage = 0.0d;
        	
        	for(Double laneOccupancy : laneOccupancies){
        		
        		laneOccupanciesAverage += laneOccupancy;
        		
        	}
        	
        	laneOccupanciesAverage /= laneOccupancies.size();
        	
        }
        
        Integer laneVehicleNumbersAverage = -1;

        if(laneVehicleNumbers!=null && laneVehicleNumbers.size() > 0){

        	laneVehicleNumbersAverage = 0;

        	for(Integer laneVehicleNumber : laneVehicleNumbers){

        		laneVehicleNumbersAverage += laneVehicleNumber;

        	}

        	laneVehicleNumbersAverage /= laneVehicleNumbers.size();

        }
        
        Double laneMeanSpeedsAverage = -1.0d;
        
        if(laneMeanSpeeds!=null && laneMeanSpeeds.size() > 0){

        	laneMeanSpeedsAverage = 0.0d;

        	for(Double laneMeanSpeed : laneMeanSpeeds){

        		laneMeanSpeedsAverage += laneMeanSpeed;

        	}

        	laneMeanSpeedsAverage /= laneMeanSpeeds.size();

        }

        trafficSituation.setI(activation+";"+timeWhenCurrentPhaseWasSet+";"+laneOccupanciesAverage+";"+laneVehicleNumbersAverage+";"+laneMeanSpeedsAverage);

    }

}
