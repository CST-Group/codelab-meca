package br.unicamp.MECA_Demo.codelets.communication;

import br.unicamp.MECA_Demo.util.communication.Message;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.jtraci.entities.TrafficLight;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Created by du on 05/12/16.
 */
public class SendMessageCodelet extends Codelet {

    public static final String OUTPUT_MESSAGE = "OUTPUT_MESSAGE";
    public static final String INPUT_MESSAGE_BUFFER = "INPUT_MESSAGE_BUFFER";



    public Queue<Message> outputMessages;
    public Map<TrafficLight, Memory> inputMessageBuffer;

    public Memory outputMessageMO;
    public Memory inputMessageBufferMO;



    public SendMessageCodelet(){
        outputMessages = new LinkedList<Message>();
        inputMessageBuffer = new HashMap<TrafficLight, Memory>();
    }

    @Override
    public void accessMemoryObjects() {
        if(outputMessageMO == null)
        {
            outputMessageMO = this.getInput(OUTPUT_MESSAGE, 0);
        }

        if(inputMessageBufferMO == null){
            inputMessageBufferMO = this.getInput(INPUT_MESSAGE_BUFFER, 0);
            inputMessageBuffer = (Map<TrafficLight, Memory>) inputMessageBufferMO.getI();
        }
    }

    @Override
    public void calculateActivation() {

        try {
            setActivation(0);
        } catch (CodeletActivationBoundsException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void proc() {

        if(outputMessageMO.getI() != null){
            outputMessages.add((Message) outputMessageMO.getI());
        }

        if(outputMessages.size() != 0){

            Message message = outputMessages.poll();

            for (Map.Entry<TrafficLight, Memory> inputMB: inputMessageBuffer.entrySet()) {
                if(message.getTo().equals(inputMB.getKey().getID())){
                    Memory inputMessageMO = inputMB.getValue();
                    inputMessageMO.setI(message);
                }
            }
        }

    }
}
