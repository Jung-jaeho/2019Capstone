package ann.example.airpollutionmonitor.Model;

import java.io.Serializable;

public class Location implements Serializable {
    String serialNumber;
    String name;
    boolean isConnected;

    public Location(String serialNumber, String name) {
        this.serialNumber = serialNumber;
        this.name = name;
        isConnected = false;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
