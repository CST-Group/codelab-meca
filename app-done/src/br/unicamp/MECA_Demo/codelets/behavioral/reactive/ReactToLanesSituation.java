/**
 * 
 */
package br.unicamp.MECA_Demo.codelets.behavioral.reactive;

import java.util.ArrayList;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.meca.memory.WorkingMemory;
import br.unicamp.meca.system1.codelets.ReactiveBehavioralCodelet;

/**
 * @author andre
 *
 */
public class ReactToLanesSituation extends ReactiveBehavioralCodelet {	

	private Memory trafficSituation;

	private Memory shouldChangePhaseMemory;	

	private Memory nextActionBroadcastedMemory;

	private double factor = 0.45;

	public ReactToLanesSituation(String id, ArrayList<String> perceptualCodeletsIds, String motorCodeletId, String soarCodeletId) {

		super(id, perceptualCodeletsIds, motorCodeletId,soarCodeletId);
	}

	@Override
	public void accessMemoryObjects() {

		int index=0;

		if(shouldChangePhaseMemory==null && motorCodeletId!=null)
			shouldChangePhaseMemory = this.getOutput(motorCodeletId, index);

		if(trafficSituation==null && perceptualCodeletsIds!=null && perceptualCodeletsIds.size()>0 && perceptualCodeletsIds.get(0)!=null)
			trafficSituation = this.getInput(perceptualCodeletsIds.get(0), index);

		if(nextActionBroadcastedMemory == null && soarCodeletId!=null)
			nextActionBroadcastedMemory = this.getBroadcast(soarCodeletId, index);	

	}

	@Override
	public void calculateActivation() {

		double activation = 0.0d;	

		try{

			if(nextActionBroadcastedMemory!=null && nextActionBroadcastedMemory.getI()!=null && ((WorkingMemory) nextActionBroadcastedMemory.getI()).getNextActionMemory()!=null && ((WorkingMemory) nextActionBroadcastedMemory.getI()).getNextActionMemory().getI()!=null){

				activation = 1.00d;

			}else {

				if(trafficSituation!=null && trafficSituation.getI()!=null){
					
					String[] trafficSituationString = ((String) trafficSituation.getI()).split("[;]");
					
					activation = Double.valueOf(trafficSituationString[0]);
				}
					

			}

		}catch(Exception e){
			
			e.printStackTrace();
		}

		try 
		{
			if(activation<0.0d)
				activation=0.0d;

			if(activation>1.0d)
				activation=1.0d;

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

				if(activation > 0.5d * factor)
					((MemoryContainer)shouldChangePhaseMemory).setI(Boolean.TRUE,getActivation(),id);
				else
					((MemoryContainer)shouldChangePhaseMemory).setI(Boolean.FALSE,getActivation(),id);
			}

		}catch(Exception e){

			e.printStackTrace();
			
		}
		
	}
}
