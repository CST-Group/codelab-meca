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
package br.unicamp.MECA_Demo.codelets.behavioral.random;

import java.util.Random;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.meca.memory.WorkingMemory;
import br.unicamp.meca.system1.codelets.RandomBehavioralCodelet;

/**
 * @author andre
 *
 */
public class RandomChangePhase extends RandomBehavioralCodelet {	

	private Memory shouldChangePhaseMemory;

	private Memory nextActionBroadcastedMemory;

	private double factor = 0.5d;

	public RandomChangePhase(String id, String motorCodeletId, String soarCodeletId) {

		super(id, motorCodeletId,soarCodeletId);
	}


	@Override
	public void accessMemoryObjects() {

		int index=0;

		if(shouldChangePhaseMemory==null && motorCodeletId!=null)
			shouldChangePhaseMemory = this.getOutput(motorCodeletId, index);

		if(nextActionBroadcastedMemory == null && soarCodeletId!=null)
			nextActionBroadcastedMemory = this.getBroadcast(soarCodeletId, index);	

	}


	@Override
	public void calculateActivation() {

		double activation=0.0d;	

		try{

			if(nextActionBroadcastedMemory!=null && nextActionBroadcastedMemory.getI()!=null && ((WorkingMemory) nextActionBroadcastedMemory.getI()).getNextActionMemory()!=null && ((WorkingMemory) nextActionBroadcastedMemory.getI()).getNextActionMemory().getI()!=null){

				activation = 1.0d;
				
			}else {

				double rangeMin = 0.0d;
				double rangeMax = 1.0d;

				Random random = new Random(System.currentTimeMillis());

				activation = rangeMin + (rangeMax - rangeMin) * random.nextDouble();

				if(activation >= 0.999d) // Very hard for the random phase codelet to prevail
					activation = 1.0d;
				else
					activation = 0.0d;

			}

		}catch(Exception e){

			e.printStackTrace();
			
		}

		try 
		{			
			this.setActivation(activation*factor);

		} catch (CodeletActivationBoundsException e) 
		{			
			e.printStackTrace();
		}

	}


	@Override
	public void proc() {

		try{

			if(nextActionBroadcastedMemory!=null && nextActionBroadcastedMemory.getI()!=null && ((WorkingMemory) nextActionBroadcastedMemory.getI()).getNextActionMemory()!=null && ((WorkingMemory) nextActionBroadcastedMemory.getI()).getNextActionMemory().getI()!=null){

				if(((Boolean)((WorkingMemory) nextActionBroadcastedMemory.getI()).getNextActionMemory().getI()))
					((MemoryContainer)shouldChangePhaseMemory).setI(Boolean.TRUE,getActivation(),id);
				else
					((MemoryContainer)shouldChangePhaseMemory).setI(Boolean.FALSE,getActivation(),id);

			}else {

				if(activation == factor)
					((MemoryContainer)shouldChangePhaseMemory).setI(Boolean.TRUE,getActivation(),id);
				else
					((MemoryContainer)shouldChangePhaseMemory).setI(Boolean.FALSE,getActivation(),id);
			}

		}catch(Exception e){

			e.printStackTrace();

		}
	}

}
