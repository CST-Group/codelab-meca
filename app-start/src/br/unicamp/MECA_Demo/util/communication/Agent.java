package br.unicamp.MECA_Demo.util.communication;

/**
 * Created by du on 08/06/17.
 */
class Agent{

    private String status;
    private String agentId;
    private String agentConnectedId;

    public static final String BUSY = "BUSY";
    public static final String FREE = "FREE";

    public Agent(String agentId, String status){
        this.setStatus(status);
        this.setAgentId(agentId);
        this.setAgentConnectedId("");
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentConnectedId() {
        return agentConnectedId;
    }

    public void setAgentConnectedId(String agentConnectedId) {
        this.agentConnectedId = agentConnectedId;
    }
}
