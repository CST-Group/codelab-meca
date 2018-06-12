package br.unicamp.MECA_Demo.util.communication;

import java.util.UUID;

/**
 * Created by du on 13/12/16.
 */
public class Message {

    private String messageId;
    private String from;
    private String to;
    private String content;
    private String type;
    private String responseMessageId;

    //-----------SENDER------------
    public static String SERVER = "SERVER";

    //-----------MESSAGE TYPE--------------
    public static final String HELP_AGENT = "HELP_AGENT";
    public static final String INFO_AGENT = "INFO_AGENT";
    public static final String DENY_HELP_AGENT = "DENY_HELP_AGENT";
    public static final String MESSAGE_TO_SERVER = "MESSAGE_TO_SERVER";
    public static final String MESSAGE_FROM_SERVER = "MESSAGE_FROM_SERVER";
    public static final String CONNECTED_TO_SERVER = "CONNECTED_TO_SERVER";
    public static final String SENT_TO_AGENT = "SENT_TO_AGENT";
    public static final String ERROR = "ERROR";
    public static final String CLIENT_ACCEPT = "CLIENT_ACCEPT";
    public static final String START_CONVERSATION = "START_CONVERSATION";
    public static final String END_CONVERSATION = "END_CONVERSATION";

    //-----------DEFAULT MESSAGES-----------
    public static final String MESSAGE_NOT_FOUND_AGENT = "Agent @ID not found";
    public static final String MESSAGE_OK = "Ok.";
    public static final String MESSAGE_AGENT_BUSY = "Agent @ID is busy.";
    public static final String MESSAGE_CONVERSATION_STARTED = "Conversation with agent @ID was started.";
    public static final String MESSAGE_CONVERSATION_ENDED = "Conversation with agent @ID was ended.";


    public Message(String from, String to, String content, String type){
        this.setMessageId(UUID.randomUUID().toString());
        this.setFrom(from);
        this.setTo(to);
        this.setContent(content);
        this.setType(type);
    }

    public Message(String from, String to, String content, String type, String responseMessageId){
        this.setMessageId(UUID.randomUUID().toString());
        this.setFrom(from);
        this.setTo(to);
        this.setContent(content);
        this.setType(type);
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResponseMessageId() {
        return responseMessageId;
    }

    public void setResponseMessageId(String responseMessageId) {
        this.responseMessageId = responseMessageId;
    }
}
