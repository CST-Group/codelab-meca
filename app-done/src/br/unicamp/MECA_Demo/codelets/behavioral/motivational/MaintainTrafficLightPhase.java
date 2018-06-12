package br.unicamp.MECA_Demo.codelets.behavioral.motivational;

import java.util.ArrayList;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.meca.memory.WorkingMemory;
import br.unicamp.meca.system1.codelets.MotivationalBehavioralCodelet;

/**
 * Created by du on 06/04/17.
 */
public class MaintainTrafficLightPhase extends MotivationalBehavioralCodelet {

	private ArrayList<Memory> drivesMO;

	private Memory shouldChangePhaseMemory;

	private Memory nextActionBroadcastedMemory;

	public MaintainTrafficLightPhase(String id, String motorCodeletId, ArrayList<String> motivationalCodeletsIds, String soarCodeletId) {
		super(id, motorCodeletId, motivationalCodeletsIds,soarCodeletId);
	}

	@Override
	public void accessMemoryObjects() {
		int index = 0;

		if(shouldChangePhaseMemory==null && motorCodeletId!=null)
			shouldChangePhaseMemory = this.getOutput(motorCodeletId, index);

		if(drivesMO==null||drivesMO.size()==0)
		{
			drivesMO = new ArrayList<>();

			if(getMotivationalCodeletsIds()!=null){

				for(String motivationalCodeletsId : getMotivationalCodeletsIds())
				{
					Memory inputDrive = this.getInput(motivationalCodeletsId + "_DRIVE_MO");
					drivesMO.add(inputDrive);
				}
			}
		}

		if(nextActionBroadcastedMemory == null && soarCodeletId!=null)
			nextActionBroadcastedMemory = this.getBroadcast(soarCodeletId, index);	
	}

	@Override
	public void calculateActivation() {

		double active = 0;

		try{

			if(nextActionBroadcastedMemory!=null && nextActionBroadcastedMemory.getI()!=null && ((WorkingMemory) nextActionBroadcastedMemory.getI()).getNextActionMemory()!=null && ((WorkingMemory) nextActionBroadcastedMemory.getI()).getNextActionMemory().getI()!=null && !((Boolean)((WorkingMemory) nextActionBroadcastedMemory.getI()).getNextActionMemory().getI())){

				activation = 1.0d;

			}else if (drivesMO!=null && drivesMO.size() > 0){

				for (Memory driveMO: drivesMO) {
					active += driveMO.getEvaluation();
				}

				active /= drivesMO.size();

			}

		}catch(Exception e){


		}

		try {

			if(active<0.0d)
				active=0.0d;

			if(active>1.0d)
				active=1.0d;

			setActivation(active);

		} catch (CodeletActivationBoundsException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void proc() {
		((MemoryContainer)shouldChangePhaseMemory).setI(Boolean.FALSE,getActivation(),id);
	}
}
