package model;

/**
 * Created by aljon on 27/10/2016.
 */

public class Terminal
{
    private String terminalName;
    private double latitude;
    private double longitude;
    private int type;
    private int status;

    public static final int TRANSIT = 0;
    public static final int BUS = 1;
    public static final int JEEP = 2;
    public static final int UV = 3;

    public static final int HEAVY = 3;
    public static final int MEDIUM = 2;
    public static final int LOW = 1;

    public Terminal(String name, double lat, double longi, int type)
    {
        terminalName = name;
        latitude = lat;
        longitude = longi;
        this.type = type;
        status = 0;
    }

    public Terminal()
    {}

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public double getLatitude() {
        return latitude;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Terminal{" +
                "terminalName='" + terminalName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", type=" + type +
                ", status=" + status +
                '}';
    }
}