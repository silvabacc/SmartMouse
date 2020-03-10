package com.bnaze.smartmouse.networkutils;

public class Settings {
    private static final Settings instanceOf = new Settings();
    private String host;
    private int port;

    public static Settings getInstanceOf() {
        return instanceOf;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
