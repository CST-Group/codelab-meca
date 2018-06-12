/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.MECA_Demo.codelets.system2;

import java.io.File;
import java.util.List;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.meca.system2.codelets.SoarCodelet;

/**
 * @author Wandemberg Gibaut
 */
public class TrafficSoarCodelet extends SoarCodelet {

    private double factor = 0.5;

    public TrafficSoarCodelet(String id, String pathToCommands, String _agentName, File _productionPath, Boolean startSOARDebugger) {

        super(id, pathToCommands, _agentName, _productionPath, startSOARDebugger);
    }

    //@Override
    //public void proc(){
    //A estrutura passada pro SOAR tera a seguinte logica/configuracao:

    //input-link --> CURRENT_PERCEPTION --> CONFIGURATION --> SMARTCAR_INFO --> ...
    //                                                    --> TRAFFIC_LIGHT --> ...

    //}


    @Override
    public void fromPlanToAction() {

        //pega um plano gerado pelo soar e poe no nextActionMemory
        List<List<Object>> list = (List<List<Object>>) getWorkingMemory().getPlansMemory().getI();
        String command = "";
        Memory tempNextAction = getWorkingMemory().getNextActionMemory();
        Memory tempPlansMemory = getWorkingMemory().getPlansMemory();

        List<Object> plan = list.get(0);

        if (!plan.isEmpty()) {

            command = plan.get(0).getClass().toString();
        }

        boolean signal;


        if (command.equals("class br.unicamp.MECA_Demo.util.SOAR_commands.SoarCommandKeep")) {

            signal = false;
            tempNextAction.setI(signal);

            try {

                setActivation(0.5d*factor);

            } catch (CodeletActivationBoundsException e) {

                e.printStackTrace();
            }
        } else if (command.equals("class br.unicamp.MECA_Demo.util.SOAR_commands.SoarCommandChange")) {

            signal = true;
            tempNextAction.setI(signal);

            try {

                setActivation(0.7d*factor);

            } catch (CodeletActivationBoundsException e) {

                e.printStackTrace();
            }
        } else if (command.equals("class br.unicamp.MECA_Demo.util.SOAR_commands.SoarCommandDoNothing")) {

            tempNextAction.setI(null);

            try {

                setActivation(0.2d*factor);

            } catch (CodeletActivationBoundsException e) {

                e.printStackTrace();
            }
        }

        getWorkingMemory().setNextActionMemory(tempNextAction);

        list.remove(0);
        tempPlansMemory.setI(list);
        getWorkingMemory().setPlansMemory(tempPlansMemory);
    }

    @Override
    public void calculateActivation() {

    }

}
