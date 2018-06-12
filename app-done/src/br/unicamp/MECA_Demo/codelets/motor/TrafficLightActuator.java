/**
 *     Copyright 2016 CST-Group

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
package br.unicamp.MECA_Demo.codelets.motor;

import java.util.ArrayList;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.jtraci.entities.Phase;
import br.unicamp.jtraci.entities.TrafficLight;
import br.unicamp.meca.system1.codelets.MotorCodelet;

/**
 * @author andre
 *
 */
public class TrafficLightActuator extends MotorCodelet {

	private TrafficLight trafficLight;	

	private ArrayList<Phase> phases;

	private Memory shouldChangePhaseMemory;

	public TrafficLightActuator(String id, TrafficLight trafficLight, ArrayList<Phase> phases) {

		super(id);
		this.trafficLight = trafficLight;
		this.phases = phases;

	}

	@Override
	public void accessMemoryObjects() {

		int index=0;

		if(shouldChangePhaseMemory==null)
			shouldChangePhaseMemory = this.getInput(id, index);

	}


	@Override
	public void calculateActivation() {

		try {

			setActivation(0.0d);

		} catch (CodeletActivationBoundsException e){		

			e.printStackTrace();
		}

	}


	@Override
	public void proc() {

		int phaseIndex = -1;	

		if(trafficLight!=null && phases!=null && shouldChangePhaseMemory!=null && shouldChangePhaseMemory.getI()!=null){

			try{

				Boolean shouldChangePhase = (Boolean) shouldChangePhaseMemory.getI();

				if(shouldChangePhase!=null){

					if(shouldChangePhase){												

						phaseIndex = trafficLight.getCurrentPhase();

						if(phaseIndex == phases.size()-1){												
							phaseIndex = 0;
						}else{
							phaseIndex++;
						}

						String nextPhase = phases.get(phaseIndex).getDefinition();

						while( ( nextPhase.contains("y") || nextPhase.contains("Y") ) || ( !nextPhase.contains("g") && !nextPhase.contains("G") ) ) {

							if(phaseIndex == phases.size()-1){												
								phaseIndex = 0;
							}else{
								phaseIndex++;
							}

							nextPhase = phases.get(phaseIndex).getDefinition();

						}							

						if(phaseIndex >= 0 && phases.size() > phaseIndex){											

							trafficLight.setCurrentPhase(phaseIndex);	

							//								System.out.println("Set phase "+phaseIndex);
						}

					}								
				}	

			}catch(Exception e){

				e.printStackTrace();
			}
		}

	}

}
