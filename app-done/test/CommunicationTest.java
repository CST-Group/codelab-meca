import br.unicamp.MECA_Demo.util.communication.CommunicationAgent;
import br.unicamp.MECA_Demo.util.communication.CommunicationServer;
import br.unicamp.MECA_Demo.util.communication.Message;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by du on 02/06/17.
 */
public class CommunicationTest {

    private CommunicationServer server;

    @Before
    public void setUp(){
        setServer(new CommunicationServer("Message Mediator", 4011));

        getServer().startServer();
    }

    @Test
    public void connectionToServerTest(){

        CommunicationAgent agent1 = new CommunicationAgent("TrafficLight1", "127.0.0.1", 4011);
        CommunicationAgent agent2 = new CommunicationAgent("TrafficLight2", "127.0.0.1", 4011);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Message message3 = new Message(agent1.getName(), agent2.getName(), "OPAA!", Message.INFO_AGENT);
        agent1.sendMessage(message3);

        Message message4 = new Message(agent1.getName(), agent2.getName(), "OPAA 2!", Message.INFO_AGENT);
        agent1.sendMessage(message4);


    }

    public CommunicationServer getServer() {
        return server;
    }

    public void setServer(CommunicationServer server) {
        this.server = server;
    }
}
