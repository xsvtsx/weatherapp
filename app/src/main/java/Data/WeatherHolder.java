package Data;

import java.util.List;

public class WeatherHolder {

    private List<Weather> weatherList;

    public WeatherHolder(List<Weather> weatherList) {
        this.weatherList = weatherList;
    }

    public List<Weather> getWeatherList() {
        return weatherList;
    }

    @Override
    public String toString() {
        return "WeatherHolder{" +
                "weatherList=" + weatherList +
                '}';
    }
}
