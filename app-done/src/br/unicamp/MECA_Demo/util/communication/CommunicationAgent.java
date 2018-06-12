package br.unicamp.MECA_Demo.util.communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by du on 02/06/17.
 */
public class CommunicationAgent {

    private String name;
    private String address;
    private Socket socket;
    private PrintStream out;
    private BufferedReader in;
    private boolean bStopped = false;

    private List<Message> messagesSent;
    private List<Message> receivedMessages;

    private Message receivedMessage;
    private Message sentMessage;
    private List<String> neighborAgents;
    private boolean bServerVersion = false;
    private int port = 4011;

    private Thread readMessageThread;

    public CommunicationAgent(String agentName, String address, int port) {
        this.setName(agentName);
        this.setAddress(address);
        this.setPort(port);
        this.OpenConnection();

        setMessagesSent(new ArrayList<>());
        setReceivedMessages(new ArrayList<>());

    }

    private void initializeReadThreads(){
        readMessageThread = new Thread() {
            public void run() {
                while (!isbStopped()) {
                    readCurrentMessage();
                }
            }
        };

        readMessageThread.start();
    }

    public Message readCurrentMessage() {
        Message message = readMessage();

        receivedMessages.add(message);

        if(message.getResponseMessageId() != null) {
            messagesSent.removeIf(msg -> msg.getResponseMessageId().equals(message.getResponseMessageId()));
        }

        return message;
    }

    public void OpenConnection() {
        try {
            setSocket(new Socket(getAddress(), getPort()));
            setOut(new PrintStream(getSocket().getOutputStream()));
            setIn(new BufferedReader(new InputStreamReader(
                    getSocket().getInputStream())));

            Message message = new Message(getName(), Message.SERVER, getName(), Message.CONNECTED_TO_SERVER);
            sendMessage(message);

            messagesSent.add(message);

            initializeReadThreads();

        } catch (UnknownHostException e) {
            System.out.println("Unknown host.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized void sendMessage(Message objMessage) {
        Gson gson = new GsonBuilder().create();
        setSentMessage(objMessage);
        String message = gson.toJson(objMessage);
        getOut().println(message);
    }

    public synchronized Message readMessage() {
        Gson gson = new GsonBuilder().create();
        Message message = null;

        try {
            message = gson.fromJson(getIn().readLine(), Message.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setReceivedMessage(message);


        return message;
    }

    public synchronized Socket getSocket() {
        return socket;
    }

    public synchronized void setSocket(Socket socket) {
        this.socket = socket;
    }

    public synchronized PrintStream getOut() {
        return out;
    }

    public synchronized void setOut(PrintStream out) {
        this.out = out;
    }

    public synchronized BufferedReader getIn() {
        return in;
    }

    public synchronized void setIn(BufferedReader in) {
        this.in = in;
    }

    public boolean isbStopped() {
        return bStopped;
    }

    public void setbStopped(boolean bStopped) {
        this.bStopped = bStopped;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public synchronized Message getReceivedMessage() {
        return receivedMessage;
    }

    public synchronized void setReceivedMessage(Message receivedMessage) {
        this.receivedMessage = receivedMessage;
    }

    public synchronized List<String> getNeighborAgents() {
        return neighborAgents;
    }

    public void setNeighborAgents(List<String> neighborAgents) {
        this.neighborAgents = neighborAgents;
    }

    public boolean isbServerVersion() {
        return bServerVersion;
    }

    public void setbServerVersion(boolean bServerVersion) {
        this.bServerVersion = bServerVersion;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Message getSentMessage() {
        return sentMessage;
    }

    public void setSentMessage(Message sentMessage) {
        this.sentMessage = sentMessage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Message> getMessagesSent() {
        return messagesSent;
    }

    public void setMessagesSent(List<Message> messagesSent) {
        this.messagesSent = messagesSent;
    }

    public List<Message> getReceivedMessages() {
        return receivedMessages;
    }

    public void setReceivedMessages(List<Message> receivedMessages) {
        this.receivedMessages = receivedMessages;
    }
}
