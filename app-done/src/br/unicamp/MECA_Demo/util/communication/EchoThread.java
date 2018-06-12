package br.unicamp.MECA_Demo.util.communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;

/**
 * Created by du on 08/06/17.
 */
public class EchoThread extends Thread {

    private ServerSocket serverSocket;
    private Socket socket;
    private Map<Agent, Socket> agentsSocket;
    private PrintStream out;
    private BufferedReader in;


    public EchoThread(ServerSocket serverSocket, Socket clientSocket, Map<Agent, Socket> agentsSocket) {
        setServerSocket(serverSocket);
        setSocket(clientSocket);
        setAgentsSocket(agentsSocket);
        try {
            setOut(new PrintStream(getSocket().getOutputStream()));
            setIn(new BufferedReader(new InputStreamReader(
                    getSocket().getInputStream())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        while (true) {
            Message inputMessage = readMessage();

            if (!inputMessage.getType().equals(Message.MESSAGE_TO_SERVER)) {

                Message statusMessage = null;

                if (inputMessage.getType().equals(Message.CONNECTED_TO_SERVER)) {
                    System.out.println("Server -> Agent: " + inputMessage.getContent() + " - Connected");

                    statusMessage = new Message(Message.SERVER, inputMessage.getFrom(), Message.MESSAGE_OK, Message.CLIENT_ACCEPT, inputMessage.getMessageId());

                    this.getAgentsSocket().put(new Agent(inputMessage.getFrom(), Agent.FREE), this.getSocket());
                } else if (inputMessage.getType().equals(Message.INFO_AGENT)) {
                    System.out.println("Server: Message's Content: " + inputMessage.getContent() + " -> From:" + inputMessage.getFrom() + " To:" + inputMessage.getTo());

                    Optional<Map.Entry<Agent, Socket>> first = getAgentsSocket().entrySet().stream().filter(x -> x.getKey().getAgentId().equals(inputMessage.getTo())).findFirst();

                    if (first.isPresent()) {
                        Map.Entry<Agent, Socket> clientReceiver = first.get();

                        if (clientReceiver.getKey().getStatus().equals(Agent.BUSY) && clientReceiver.getKey().getAgentConnectedId().equals(inputMessage.getFrom())) {
                            sendMessage(clientReceiver.getValue(), inputMessage);

                            statusMessage = new Message(Message.SERVER, inputMessage.getFrom(), Message.MESSAGE_OK, Message.SENT_TO_AGENT, inputMessage.getMessageId());
                        } else {
                            statusMessage = new Message(Message.SERVER, inputMessage.getFrom(), Message.MESSAGE_AGENT_BUSY.replace("@ID", inputMessage.getTo()), Message.ERROR, inputMessage.getMessageId());
                        }
                    } else {
                        statusMessage = new Message(Message.SERVER, inputMessage.getFrom(), Message.MESSAGE_NOT_FOUND_AGENT, Message.ERROR, inputMessage.getMessageId());
                    }
                } else if (inputMessage.getType().equals(Message.START_CONVERSATION)){

                    Optional<Map.Entry<Agent, Socket>> agentTo = getAgentsSocket().entrySet().stream().filter(x -> x.getKey().getAgentId().equals(inputMessage.getTo())).findFirst();

                    if (agentTo.isPresent()) {
                        Map.Entry<Agent, Socket> clientReceiver = agentTo.get();

                        if(clientReceiver.getKey().getStatus().equals(Agent.FREE)){
                            Map.Entry<Agent, Socket> agentFrom = getAgentsSocket().entrySet().stream().filter(x -> x.getKey().getAgentId().equals(inputMessage.getFrom())).findFirst().get();

                            agentFrom.getKey().setStatus(Agent.BUSY);
                            agentFrom.getKey().setAgentConnectedId(clientReceiver.getKey().getAgentId());

                            clientReceiver.getKey().setStatus(Agent.BUSY);
                            clientReceiver.getKey().setAgentConnectedId(agentFrom.getKey().getAgentId());

                            statusMessage = new Message(Message.SERVER, inputMessage.getFrom(), Message.MESSAGE_CONVERSATION_STARTED.replace("@ID", agentFrom.getKey().getAgentId()), Message.MESSAGE_FROM_SERVER, inputMessage.getMessageId());
                        }
                        else{
                            statusMessage = new Message(Message.SERVER, inputMessage.getFrom(), Message.MESSAGE_AGENT_BUSY.replace("@ID", clientReceiver.getKey().getAgentId()), Message.MESSAGE_FROM_SERVER, inputMessage.getMessageId());
                        }

                    } else {
                        statusMessage = new Message(Message.SERVER, inputMessage.getFrom(), Message.MESSAGE_NOT_FOUND_AGENT, Message.ERROR, inputMessage.getMessageId());
                    }
                } else if(inputMessage.getType().equals(Message.END_CONVERSATION)){

                    Optional<Map.Entry<Agent, Socket>> agentTo = getAgentsSocket().entrySet().stream().filter(x -> x.getKey().getAgentId().equals(inputMessage.getTo())).findFirst();

                    if (agentTo.isPresent()) {
                        Map.Entry<Agent, Socket> clientReceiver = agentTo.get();

                        if(clientReceiver.getKey().getStatus().equals(Agent.BUSY)){
                            Map.Entry<Agent, Socket> agentFrom = getAgentsSocket().entrySet().stream().filter(x -> x.getKey().getAgentId().equals(inputMessage.getFrom())).findFirst().get();

                            agentFrom.getKey().setStatus(Agent.FREE);
                            agentFrom.getKey().setAgentConnectedId(clientReceiver.getKey().getAgentId());

                            clientReceiver.getKey().setStatus(Agent.FREE);
                            clientReceiver.getKey().setAgentConnectedId(agentFrom.getKey().getAgentId());

                            statusMessage = new Message(Message.SERVER, inputMessage.getFrom(), Message.MESSAGE_CONVERSATION_ENDED.replace("@ID", agentFrom.getKey().getAgentId()), Message.MESSAGE_FROM_SERVER, inputMessage.getMessageId());
                        }
                    }
                }

                sendMessage(getSocket(), statusMessage);
            }
        }

    }

    public synchronized void sendMessage(Socket socket, Message objMessage) {
        Gson gson = new GsonBuilder().create();
        String message = gson.toJson(objMessage);

        try {
            PrintStream out = new PrintStream(socket.getOutputStream());
            out.println(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized Message readMessage() {
        Gson gson = new GsonBuilder().create();
        Message message = null;

        try {
            message = gson.fromJson(getIn().readLine(), Message.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return message;
    }

    public synchronized ServerSocket getServerSocket() {
        return serverSocket;
    }

    public synchronized void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public synchronized Socket getSocket() {
        return socket;
    }

    public synchronized void setSocket(Socket socket) {
        this.socket = socket;
    }

    public synchronized Map<Agent, Socket> getAgentsSocket() {
        return agentsSocket;
    }

    public synchronized void setAgentsSocket(Map<Agent, Socket> agentsSocket) {
        this.agentsSocket = agentsSocket;
    }

    public PrintStream getOut() {
        return out;
    }

    public void setOut(PrintStream out) {
        this.out = out;
    }

    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }
}
