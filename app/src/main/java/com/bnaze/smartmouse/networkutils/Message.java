package com.bnaze.smartmouse.networkutils;

public class Message {
    private MessageType type;
    private String message;

    public Message(MessageType type, String message) {
        this.type = type;
        this.message = message;
    }

    public MessageType getType(){
        return type;
    }

    public String getMessageValue() {
        return message;
    }

    public static Message noMessage() {
        return new Message(MessageType.NO_MESSAGE, null);
    }

    public static Message newMessage(MessageType type, String value){
        return new Message(type, value);
    }

    @Override
    public String toString() {
        return String.format("{\"type\": \"%s\", \"value\": %s}", getType(), getMessageValue());
    }
}
