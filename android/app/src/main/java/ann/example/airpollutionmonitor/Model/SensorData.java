package ann.example.airpollutionmonitor.Model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SensorData implements Serializable {
    Date date;
    String strDate;
    double tem, hum, CO, CH4;

    public SensorData(String strDate, double tem, double hum, double CO, double CH4) {
        this.strDate =strDate;
        this.tem = tem;
        this.hum = hum;
        this.CO = CO;
        this.CH4 = CH4;

        // Date 형식으로 변환
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        try {
            date = dateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "SensorData{" +
                "date=" + date +
                ", strDate='" + strDate + '\'' +
                ", tem=" + tem +
                ", hum=" + hum +
                ", CO=" + CO +
                ", CH4=" + CH4 +
                '}';
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getTem() {
        return tem;
    }

    public void setTem(double tem) {
        this.tem = tem;
    }

    public double getHum() {
        return hum;
    }

    public void setHum(double hum) {
        this.hum = hum;
    }

    public double getCO() {
        return CO;
    }

    public void setCO(double CO) {
        this.CO = CO;
    }

    public double getCH4() {
        return CH4;
    }

    public void setCH4(double CH4) {
        this.CH4 = CH4;
    }
}