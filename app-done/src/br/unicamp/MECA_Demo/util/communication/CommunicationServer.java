package br.unicamp.MECA_Demo.util.communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by du on 01/06/17.
 */
public class CommunicationServer {

    private String name;
    private ServerSocket serverSocket;
    private boolean bStopped = false;
    private boolean bEndConnections = false;
    private int iPort = 4011;
    private Map<Agent, Socket> agents;

    private Thread connectThread;
    protected List<Thread> clientThread;

    public CommunicationServer(String name, int port) {
        this.setName(name);
        this.setAgents(new HashMap<Agent, Socket>());
        this.setiPort(port);
        this.clientThread = new ArrayList<>();
    }

    public void startServer() {
        try {
            setServerSocket(new ServerSocket(getiPort()));

            try {
                System.out.println("Initializing Communication Server on " + InetAddress.getLocalHost().getHostAddress() + ":" + getiPort());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            connectThread = new Thread() {
                public void run() {
                    while (!isbEndConnections() && !isbStopped()) {

                        try {
                            Socket clientSocket = getServerSocket().accept();

                            EchoThread echoThread = new EchoThread(getServerSocket(), clientSocket, getAgents());

                            clientThread.add(echoThread);

                            echoThread.start();


                        } catch (IOException e) {
                            throw new RuntimeException(
                                    "Error accepting client connection.", e);
                        }
                    }
                }
            };

            connectThread.start();
            //messageThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void setEndConnections(boolean value) {
        this.bEndConnections = value;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized boolean isbStopped() {
        return bStopped;
    }

    public synchronized void setbStopped(boolean bStopped) {
        this.bStopped = bStopped;
    }


    public synchronized Map<Agent, Socket> getAgents() {
        return agents;
    }

    public void setAgents(Map<Agent, Socket> agents) {
        this.agents = agents;
    }

    public int getiPort() {
        return iPort;
    }

    public void setiPort(int iPort) {
        this.iPort = iPort;
    }

    public boolean isbEndConnections() {
        return bEndConnections;
    }

    public void setbEndConnections(boolean bEndConnections) {
        this.bEndConnections = bEndConnections;
    }
}


