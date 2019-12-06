package com.dsitvision.myapplication;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by vishptl on 9/5/2017.
 */
public class ChatDTO implements Serializable {
    public String getSendip() {
        return sendip;
    }

    public void setSendip(String sendip) {
        this.sendip = sendip;
    }

    String sendip;
    public String getSendport() {
        return sendport;
    }

    public void setSendport(String sendport) {
        this.sendport = sendport;
    }

    String sendport;
    private String message;
    private String sentBy;
    private long localTimestamp;
    private String fromIP;
    private int port;
    private boolean isMyChat = false;

    public boolean isMyChat() {
        return isMyChat;
    }

    public void setMyChat(boolean myChat) {
        isMyChat = myChat;
    }

    public String getMessage() {
        return message;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public long getLocalTimestamp() {
        return localTimestamp;
    }

    public void setLocalTimestamp(long localTimestamp) {
        this.localTimestamp = localTimestamp;
    }

    public String getFromIP() {
        return fromIP;
    }

    public void setFromIP(String fromIP) {
        this.fromIP = fromIP;
    }

    @Override
    public String toString() {
        String stringRep = (new Gson()).toJson(this);
        return stringRep;
    }

    public static ChatDTO fromJSON(String jsonRep) {
        Gson gson = new Gson();
        ChatDTO chatObject = gson.fromJson(jsonRep, ChatDTO.class);
        return chatObject;
    }
}
