package Data;

import android.graphics.drawable.Drawable;

public class Weather {
    private int temperature;
    private Drawable drw;
    private String day;
    private final static int KELVIN = 273;

    public Weather(int temperature, Drawable imgLabel, String day) {
        this.temperature = temperature - KELVIN;
        this.drw = imgLabel;
        this.day = day;
    }


    public int getTemperature() {
        return temperature;
    }

    public Drawable getDrawable() {
        return drw;
    }

    public String getDay() {
        return day;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "temperature=" + temperature +
                ", imgLabel='" + drw.toString() + '\'' +
                ", day='" + day + '\'' +
                '}';
    }
}
