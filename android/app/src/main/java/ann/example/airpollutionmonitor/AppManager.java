package ann.example.airpollutionmonitor;

import java.util.ArrayList;

import ann.example.airpollutionmonitor.Model.Location;
import ann.example.airpollutionmonitor.Model.SensorData;

public class AppManager {
    private static AppManager instance = null;

    private AppManager() {
    }

    public static AppManager getInstance() {
        if (instance == null)
            instance = new AppManager();
        return instance;
    }

    public MainActivity mainActivity;

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public static final String[][] SERIALS = {{"sn100", "강의실 100"}, {"sn101", "강의실 101"}};

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    private ArrayList<Location> locations;

    public SensorData sensorData;

    public SensorData getSensorData() {
        return sensorData;
    }

    public void setSensorData(SensorData sensorData) {
        this.sensorData = sensorData;
    }

    // 기준
    public float maxCOLevel = 50f;
    public float COUpperLimit = 9f;
    public float maxCH4Level = 500f;
    public float CH4UpperLimit = 180f;

}

