package com.xsvtsx.weatherapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import Data.Weather;
import Data.WeatherHolder;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private WeatherHolder weatherHolder;
    private Context context;

    WeatherAdapter(Context context, WeatherHolder wh) {
        this.weatherHolder = wh;
        this.context = context;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_list_item, viewGroup, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder weatherViewHolder, int i) {
        Weather weather = weatherHolder.getWeatherList().get(i);

        weatherViewHolder.dayOfWeek.setText(weather.getDay().toLowerCase());
        weatherViewHolder.temperatureData.setText(String.format("%s\u00b0", weather.getTemperature()));
        weatherViewHolder.iconWeather.setImageDrawable(weather.getDrawable());

    }

    @Override
    public int getItemCount() {
        return weatherHolder.getWeatherList().size();
    }

    class WeatherViewHolder extends RecyclerView.ViewHolder{

        private TextView dayOfWeek, temperatureData;
        private ImageView iconWeather;
        private RelativeLayout parentLayout;


         WeatherViewHolder(@NonNull View itemView) {
            super(itemView);

            dayOfWeek = itemView.findViewById(R.id.dayOfWeek);
            temperatureData = itemView.findViewById(R.id.temperatureData);
            iconWeather = itemView.findViewById(R.id.iconOfWeather);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }
}
