package com.example.simpleweatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements LocationListener {

    TextView mainResult, tempResult, nyResult, sgResult, mbResult, dhResult, snResult, mnResult;
    Button getButton;
    LocationManager locManager;
    double latitude, longitude;

    private final String url = "https://api.openweathermap.org/data/2.5/weather?";
    private final String appId = "to be inserted with your own API Key";
    DecimalFormat df = new DecimalFormat("#.##");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainResult = findViewById(R.id.mainOutput);
        getButton = findViewById(R.id.get);

        //Ask for permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }


        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocationWeather();
            }
        });

    }

    //Retrieve weather details using JSON and Volley from openweathermap API
    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(this, "Weather updated", Toast.LENGTH_SHORT).show();
        String tempURL = "";
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        tempURL = url + "lat=" + latitude + "&lon=" + longitude + "&appid=" + appId;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //used to test response
                //Log.d("response", response);

                String output = "";
                try {
                    //Current location's weather
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                    JSONObject jsonWeather = jsonArray.getJSONObject(0);
                    String desc = jsonWeather.getString("description");
                    JSONObject jsonMain = jsonResponse.getJSONObject("main");
                    double temp = jsonMain.getDouble("temp");
                    int humidity = jsonMain.getInt("humidity");

                    output += "Current Location's Weather: " + "\n"  + desc
                            + "\n" + "Temperature is " + temp + "\n Humidity is " +
                            humidity;

                    mainResult.setText(output);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        //display the other 6 weather forecasts
        nyResult = findViewById(R.id.NewOutput);
        sgResult = findViewById(R.id.SinOutput);
        mbResult = findViewById(R.id.MumOutput);
        dhResult = findViewById(R.id.DelOutput);
        snResult = findViewById(R.id.SydOutput);
        mnResult = findViewById(R.id.MelOutput);
        String url2 = "https://api.openweathermap.org/data/2.5/weather?q=";
        String[] array = {"New York", "Singapore", "Mumbai", "Delhi", "Sydney", "Melbourne"};
        TextView[] array2 = {nyResult, sgResult, mbResult, dhResult, snResult, mnResult};
        List<String> countryList = Arrays.asList(array);
        List<TextView> tvList = Arrays.asList(array2);

        for (int i = 0; i < countryList.size(); i++) {
            String cityName = countryList.get(i);
            tempResult = tvList.get(i);
            displayOtherWeather(cityName, tempResult, url2);
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    private void displayOtherWeather(String cityName,TextView tempResult, String url2) {

            String tempURL = url2 + cityName + "&appid=" + appId;

            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //used to test response
                    Log.d("response", response);

                    String output = "";
                    try {
                        //Current location's weather
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonWeather = jsonArray.getJSONObject(0);
                        String desc = jsonWeather.getString("description");
                        JSONObject jsonMain = jsonResponse.getJSONObject("main");
                        double temp = jsonMain.getDouble("temp");
                        int humidity = jsonMain.getInt("humidity");

                        output += cityName + "\n" + desc + ", temp: " + temp + ", hum:" + humidity;

                        tempResult.setText(output);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }


    @SuppressLint("MissingPermission")
    private void getLocationWeather() {

        try {
            locManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5000, 5, MainActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}