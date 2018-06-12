/**
 * 
 */
package br.unicamp.MECA_Demo.codelets.sensory;

import java.util.List;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.jtraci.entities.Logic;
import br.unicamp.jtraci.entities.TrafficLight;
import br.unicamp.meca.system1.codelets.SensoryCodelet;

/**
 * @author andre
 *
 */
public class CurrentPhaseSensor extends SensoryCodelet {
	
	private TrafficLight trafficLight;
	
	private Memory currentPhaseMO;
	
	/**
	 * @param id
	 * @param trafficLight
	 */
	public CurrentPhaseSensor(String id, TrafficLight trafficLight) {
		super(id);
		this.trafficLight = trafficLight;
	}
	
	@Override
	public void accessMemoryObjects() {
		
		int index=0;

		if(currentPhaseMO == null)
			currentPhaseMO = this.getOutput(id, index);
		
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
		
		currentPhaseMO.setI(null);
		
		try{

			if(trafficLight!=null){
				
				String currentProgram = trafficLight.getCurrentProgram();
				
				if(currentProgram!=null){
					
					List<Logic> completeDefinition = trafficLight.getCompleteDefinition();
					
					if(completeDefinition!=null){
						
						Logic currentLogic = completeDefinition.get(Integer.valueOf(currentProgram));
						
						currentPhaseMO.setI(currentLogic);
						
					}
									
				}						
				
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
		
	}
	

}
