package com.xsvtsx.weatherapplication;

import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import Data.Weather;
import Data.WeatherHolder;

public class WeatherActivity extends AppCompatActivity {

    private TextView currentTown, day, dayText;
    private RecyclerView recyclerView;

    private static final String lat = "LATITUDE_PARAM";
    private static final String lon = "LONGITUDE_PARAM";

    private static final String MAIN = "main";
    private static final String TEMP_MAX = "temp_max";
    private static final String WEATHER = "weather";

    private double latitude, longitude;

    private String currentDayOfWeek = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        latitude = getIntent().getDoubleExtra(lat, 0);
        longitude = getIntent().getDoubleExtra(lon, 0);
        day = findViewById(R.id.todayText);
        dayText = findViewById(R.id.dateText);
        currentTown = findViewById(R.id.coordinates);
        recyclerView = findViewById(R.id.weatherList);



        retrieveCityFromCoord();
        setCurrentDayAndDate();



        DataTask dataTask = new DataTask();
        dataTask.execute(latitude, longitude);


    }




    private void retrieveCityFromCoord(){
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String city = addresses.get(0).getLocality();
            String country = addresses.get(0).getCountryName();
            currentTown.setText(String.format("%s,   %s", city, country));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // setting to view
    private void setCurrentDayAndDate(){

        Date date = new Date(new Date().getTime());

        SimpleDateFormat dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        SimpleDateFormat dayOfMonth = new SimpleDateFormat("dd", Locale.ENGLISH);
        SimpleDateFormat month = new SimpleDateFormat("MM", Locale.ENGLISH);

        currentDayOfWeek = dayOfWeek.format(date);
        String currentDayOfMonth = dayOfMonth.format(date);
        String currentMonth = month.format(date);


        day.setText(currentDayOfWeek.toUpperCase());
        dayText.setText(String.format("%s.%s", currentDayOfMonth, currentMonth));
    }



    // For doing stuff in background
    class DataTask extends AsyncTask<Double, Integer, WeatherHolder> {

        private final String key = "b3e78025eb7fac18cd661c283873adcc";
        private final String urlPath = "http://api.openweathermap.org/data/2.5/forecast?";


        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected WeatherHolder doInBackground(Double... doubles) {
            JSONObject result;
            WeatherHolder weatherData = null;

            try{
                result = getWeatherJsonFromURL(urlPath + "lat=" + doubles[0] + "&lon=" + doubles[1] +
                        "&APPID=" + key);

                weatherData = parseData(result);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return weatherData;
        }

        @Override
        protected void onPostExecute(WeatherHolder weatherHolder) {
            // doing stuff in main(ui) thread

            WeatherAdapter weatherAdapter = new WeatherAdapter(WeatherActivity.this, weatherHolder);
            recyclerView.setLayoutManager(new LinearLayoutManager(WeatherActivity.this){
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
            recyclerView.setAdapter(weatherAdapter);


        }



        //parsing DATA and holding it in WeatherHolder Object
        @RequiresApi(api = Build.VERSION_CODES.O)
        private WeatherHolder parseData(JSONObject jsonObject) throws JSONException, ParseException {
            List<Weather> weatherList = new ArrayList<>();
            JSONArray jsonArrayList, jsonArrayWeather;
            JSONObject main, jsonWeather;
            int temperature;
            String imgLabel;
            Calendar calendar = Calendar.getInstance();


            calendar.setTime(new Date());
            SimpleDateFormat dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH);


            jsonArrayList = jsonObject.getJSONArray("list");

            for (int i = 0; i < jsonArrayList.length(); i++){
                if (indexWeatherForCurrentTime(jsonArrayList.getJSONObject(i).getString("dt_txt").split(" ")[1])){
                    jsonObject = jsonArrayList.getJSONObject(i);
                    jsonArrayWeather = jsonArrayList.getJSONObject(i).getJSONArray(WEATHER);
                    main = jsonObject.getJSONObject(MAIN);
                    temperature = main.getInt(TEMP_MAX);
                    jsonWeather = jsonArrayWeather.getJSONObject(0);
                    imgLabel = jsonWeather.getString(MAIN).toLowerCase();
                    // Constructing weather object
                    Weather weather = new Weather(temperature, convertImgLabelToDrawable(imgLabel), dayOfWeek.format(calendar.getTime()));
                    weatherList.add(weather);
                    calendar.add(Calendar.DATE, 1);
                }
            }

            return new WeatherHolder(weatherList);

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private boolean indexWeatherForCurrentTime(String timeFromData) throws ParseException {
            // getting current time
            Calendar time = Calendar.getInstance();
            Calendar reserved = Calendar.getInstance();

            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            reserved.setTime(dateFormat.parse(timeFromData));

            // updates in 3hrs
            reserved.add(Calendar.HOUR_OF_DAY, 3);
            LocalTime currentTime = LocalTime.parse(dateFormat.format(time.getTime()));
            LocalTime lowerBound = LocalTime.parse(timeFromData);
            LocalTime upperBound = LocalTime.parse(dateFormat.format(reserved.getTime()));

            return  currentTime.isAfter(lowerBound) && currentTime.isBefore(upperBound);
        }





        private Drawable convertImgLabelToDrawable(String imgLabel){

            Drawable drawable = null;

            switch (imgLabel){
                case "clouds":
                    drawable =  WeatherActivity.this.getDrawable(R.drawable.ic_cloudy);
                    break;
                case "rain":
                    drawable =  WeatherActivity.this.getDrawable(R.drawable.ic_rain);
                    break;
                case "clear":
                    drawable =  WeatherActivity.this.getDrawable(R.drawable.ic_sun);
                    break;
                case "wind":
                    drawable =  WeatherActivity.this.getDrawable(R.drawable.ic_wind);
                    break;
            }

            return drawable;

        }



        // Retrieving DATAWEATHER from openweather API.
        private JSONObject getWeatherJsonFromURL(String urlPath) throws IOException, JSONException {
            URL url = new URL(urlPath);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));

            StringBuilder sb = new StringBuilder();
            String line;
            int lenght = 0;
            while ((line = bufferedReader.readLine()) != null){
                sb.append(line).append("\n");
            }
            bufferedReader.close();
            httpURLConnection.disconnect();


            String jsonString = sb.toString();
            return new JSONObject(jsonString);

        }



    }





}
