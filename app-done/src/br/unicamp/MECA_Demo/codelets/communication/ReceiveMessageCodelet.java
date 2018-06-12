package br.unicamp.MECA_Demo.codelets.communication;

import br.unicamp.MECA_Demo.util.communication.Message;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by du on 05/12/16.
 */
public class ReceiveMessageCodelet extends Codelet {


    public static final String INPUT_MESSAGE = "INPUT_MESSAGE";
    public static final String MESSAGE_QUEUE = "MESSAGE_QUEUE";

    private Memory inputMessageMO;
    private Memory messagesInQueueMO;

    private Queue<Message> messagesInQueue;


    public ReceiveMessageCodelet(){
        messagesInQueue = new LinkedList<Message>();
    }

    @Override
    public void accessMemoryObjects() {

        if(inputMessageMO == null)
        {
            inputMessageMO = this.getInput(INPUT_MESSAGE, 0);
        }

        if(messagesInQueueMO == null){

            messagesInQueueMO = this.getOutput(MESSAGE_QUEUE, 0);

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
        if(inputMessageMO.getI() != null){
            if(!messagesInQueue.contains(inputMessageMO.getI())) {
                messagesInQueue.add((Message) inputMessageMO.getI());

                messagesInQueueMO.setI(messagesInQueue);
            }
        }
    }
}
