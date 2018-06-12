/**
 * 
 */
package br.unicamp.MECA_Demo.codelets.sensory;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.jtraci.entities.TrafficLight;
import br.unicamp.jtraci.simulation.SumoSimulation;
import br.unicamp.meca.system1.codelets.SensoryCodelet;

/**
 * @author andre
 *
 */
public class PhaseTimeSensor extends SensoryCodelet {
	
	private TrafficLight trafficLight;
	
	private Memory currentPhaseTimeMO;
	
	private Integer lastCurrentPhase;
	
	private Integer timeWhenLastCurrentPhaseWasSet;
	
	private SumoSimulation sumoSimulation;

	/**
	 * @param id
	 * @param trafficLight
	 */
	public PhaseTimeSensor(String id, TrafficLight trafficLight) {
		super(id);
		this.trafficLight = trafficLight;
		sumoSimulation = SumoSimulation.getInstance();
	}

	@Override
	public void accessMemoryObjects() {
		
		int index=0;

		if(currentPhaseTimeMO == null)
			currentPhaseTimeMO = this.getOutput(id, index);
		
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
		
		if(trafficLight!=null){
			
			Integer currentPhase = trafficLight.getCurrentPhase();		
			
			if(lastCurrentPhase == null || !lastCurrentPhase.equals(currentPhase)){
				
				lastCurrentPhase = currentPhase;
				timeWhenLastCurrentPhaseWasSet = sumoSimulation.getCurrentStep();
				
			}
			
			int now = sumoSimulation.getCurrentStep();
			
			int timeWhenCurrentPhaseWasSet = now - timeWhenLastCurrentPhaseWasSet;
			
//			System.out.println("timeWhenCurrentPhaseWasSet - "+ timeWhenCurrentPhaseWasSet);
			
			currentPhaseTimeMO.setI(timeWhenCurrentPhaseWasSet);
			
		}else {
			
			currentPhaseTimeMO.setI(null);
			
		}
		
	}

}
