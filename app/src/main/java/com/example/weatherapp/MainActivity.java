package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText cityName;
    Button search;
    TextView showWeather;
    String apiUrl;

    class GetWeather extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                showWeather.setText("Unable to fetch weather data.");
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject main = jsonObject.getJSONObject("main");
                StringBuilder weatherInfo = new StringBuilder();

                weatherInfo.append("Temperature: ").append(main.getDouble("temp") - 273.15).append(" °C\n");
                weatherInfo.append("Feels Like: ").append(main.getDouble("feels_like") - 273.15).append(" °C\n");
                weatherInfo.append("Humidity: ").append(main.getInt("humidity")).append(" %\n");
                weatherInfo.append("Pressure: ").append(main.getInt("pressure")).append(" hPa");

                showWeather.setText(weatherInfo.toString());
            } catch (Exception e) {
                e.printStackTrace();
                showWeather.setText("Error parsing weather data.");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.cityName);
        search = findViewById(R.id.search);
        showWeather = findViewById(R.id.weather);

        search.setOnClickListener(v -> {
            String city = cityName.getText().toString().trim();
            if (city.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
                return;
            }

            apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=4dfae3cc6add4d040742fc8b4fec0b85";

            new GetWeather().execute(apiUrl);
        });
    }
}
