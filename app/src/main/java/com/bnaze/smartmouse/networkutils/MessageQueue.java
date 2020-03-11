package com.bnaze.smartmouse.networkutils;

import java.util.ArrayList;
import java.util.List;

public class MessageQueue {

    //Private constructor for singleton classes, we only need to create this object once
    private MessageQueue() {}

    //Use this to get access to MessageQueue
    private static final MessageQueue messageQueue = new MessageQueue();

    //ArrayList used to pop and push messages to the server
    private static final List<Message> messageList = new ArrayList<>();

    public void push(Message message){
        messageList.add(message);
    }

    public Message pop(){
        if(messageList.size() == 0){
            return Message.noMessage();
        }
        return messageList.remove(0);
    }

    //Getter method to return instanceOf variable
    public static MessageQueue getInstance() {
        return messageQueue;
    }

}
