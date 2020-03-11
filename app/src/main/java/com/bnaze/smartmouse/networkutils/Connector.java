package com.bnaze.smartmouse.networkutils;

import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class Connector {
    private ConnectionState state;
    private final List<ConnectionCondition> conditions = new ArrayList<>();

    //Private constructor for singleton classes, we only need to create this object once
    private Connector() {}

    //Use this to access this object. Created the object here
    private static final Connector connector = new Connector();

    //using PW to send messages to the server (string messages which are converted into JSON)
    private PrintWriter pw;

    public void connect() {
        //Check if the we are attempting to connect or have already connected
        if (state == ConnectionState.CONNECTING || state == ConnectionState.CONNECTED) {
            return;
        }

        //Get the host ip and postcode for dialog settings
        final String host = Settings.getInstanceOf().getHost();
        final int port = Settings.getInstanceOf().getPort();

        //If we're not already connecting or connected, continue the connecting procedure
        this.state = ConnectionState.CONNECTING;

        //Thread to attempt connection
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean connected = false;
                //Attempt to connect done here. 5 attempts are allowed before connection failed
                for (int i = 0; i < 5; i++) {
                    try {
                        Socket socket = new Socket(host, port);
                        pw = new PrintWriter(socket.getOutputStream(), true);
                        connected = true;
                        break;
                    } catch (IOException e) {
                        Log.e("Connection Failed", "Connection failed, reattempting in 5 seconds." + e.getMessage());
                    }

                    //If we failed to connect, wait for 5 seconds before reattempting
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        Log.e("Reattempting connection", "Couldn't attempt again ");
                    }
                }

                //Client is connected if reached here
                if (connected) {
                    state = ConnectionState.CONNECTED;
                    for (ConnectionCondition l : conditions) {
                        l.onConnected();
                    }
                } else {
                    //Failed to connect here
                    state = ConnectionState.FAILED;
                    for (ConnectionCondition l : conditions) {
                        l.onConnectionFailed();
                    }
                }
            }
        }).start();
    }

    public void sendMessage(String message){
        Log.d("Message", message);
        pw.println(message);
    }

    //This method can be used to disconnect the client from the server (prompted by the user)
    public void disconnect() {
        state = ConnectionState.DISCONNECTED;
        for(ConnectionCondition l : conditions) {
            l.onDisconnected();
        }
    }

    public void addConnectionCondition(ConnectionCondition condition) {
        conditions.add(condition);
    }

    //Getter method to return instance variable
    public static Connector getInstance() {
        return connector;
    }

}
