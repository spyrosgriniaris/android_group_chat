package com.myhost.spyros.mywallchat;

import java.util.Date;

public class ChatMessage {
    private String messageText;
    private String messageUsername;
    private long messageTime;

    public ChatMessage(String messageText, String messageUsername) {
        this.messageText = messageText;
        this.messageUsername = messageUsername;

        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public ChatMessage(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUsername() {
        return messageUsername;
    }

    public void setMessageUser(String messageUsername) {
        this.messageUsername = messageUsername;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
