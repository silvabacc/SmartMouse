package com.bnaze.smartmouse.networkutils;

public interface ConnectionCondition {
    void onConnected();

    void onDisconnected();

    void onConnectionFailed();
}
