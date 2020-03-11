package com.bnaze.smartmouse.networkutils;

import android.util.Log;

public class MessageSender implements ConnectionCondition {

    private static MessageSender messageSender = new MessageSender();
    private boolean connected;

    private MessageSender(){
        Connector.getInstance().addConnectionCondition(this);
    }

    public void init(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(connected == false){
                        continue;
                    }

                    Message message = MessageQueue.getInstance().pop();
                    if(message == null || message.getType().equals(MessageType.NO_MESSAGE) || message.getType() == null){
                        continue;
                    }
                    Connector.getInstance().sendMessage(message.toString());
                }
            }
        }).start();
    }

    @Override
    public void onConnected() {
        connected=true;
    }

    @Override
    public void onDisconnected() {
        connected=false;
    }

    @Override
    public void onConnectionFailed() {
        connected=false;
    }

    public static MessageSender getInstance(){
        return messageSender;
    }
}
