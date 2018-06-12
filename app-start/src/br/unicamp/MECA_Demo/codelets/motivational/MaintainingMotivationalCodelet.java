package br.unicamp.MECA_Demo.codelets.motivational;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.cst.motivational.Drive;
import br.unicamp.meca.system1.codelets.MotivationalCodelet;

/**
 * Created by du on 06/04/17.
 */
public class MaintainingMotivationalCodelet extends MotivationalCodelet {

    public static final int MIN_PHASE_TIME = 4;

    private static final int MAX_PHASE_TIME = 120;

    public MaintainingMotivationalCodelet(String id, double level, double priority, double urgencyThreshold, ArrayList<String> sensoryCodeletsIds, HashMap<String, Double> motivationalCodeletsIds) throws CodeletActivationBoundsException {
        super(id, level, priority, urgencyThreshold, sensoryCodeletsIds, motivationalCodeletsIds);

    }

    @Override
    public double calculateSimpleActivation(List<Memory> sensors) {

        double activation = 0;

        Integer timeWhenCurrentPhaseWasSet = null;

        for (Memory sensoryMemory : sensors) {

            if (sensoryMemory.getName().contains("PhaseTimeSensor") && sensoryMemory.getI() instanceof Integer) {
                 timeWhenCurrentPhaseWasSet = (Integer) sensoryMemory.getI();
            }

        }
        
        if(timeWhenCurrentPhaseWasSet!=null){        
        	
        	if(timeWhenCurrentPhaseWasSet <= MIN_PHASE_TIME){
        		
                activation = 1.0d;

            } else if(timeWhenCurrentPhaseWasSet > MAX_PHASE_TIME){
            	
                activation = 0.0d;
                
            } else {            

            	activation = 1 - (double) timeWhenCurrentPhaseWasSet / (double) MAX_PHASE_TIME;
               
            }

        }
        else
        {
        	
            activation = 1.0d;
        }
       
        return activation;
    }

    @Override
    public double calculateSecundaryDriveActivation(List<Memory> sensors, List<Drive> drives) {
        return 0;
    }
}
