package com.example.fordhackathon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fordhackathon.activities.ClimateActivity;
import com.example.fordhackathon.activities.StartEngineActivity;

import org.json.JSONObject;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {


    public static String FahrenheitCode="\u2109";
    public static String CelsiusCode="\u2103";

    public static int SetTemperatureInsideCar,CurrentTempOutside,CurrentTempInside;
    public static boolean PersonOneTempChanging,EngineRunning;
    public static String CurrentScale;
    public static final String TemperatureAPIUrl="https://af61d91cbb8d.ngrok.io/climate/temperature";

    private Button btnSettings,btnClimate,btnBattery,btnStartCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        btnSettings=findViewById(R.id.btnSettings);
        btnClimate=findViewById(R.id.btnClimate);
        btnBattery=findViewById(R.id.btnBattery);
        btnStartCar=findViewById(R.id.btnStartCar);
        btnSettings.setOnClickListener(this);
        btnClimate.setOnClickListener(this);
        btnBattery.setOnClickListener(this);
        btnStartCar.setOnClickListener(this);

        CurrentScale=FahrenheitCode;        //defaulting to fahrenheit

        QueryTemperatures();        //Querying data now so mainactivity acts more like a viewmodel
        QueryBattery();
        EngineRunning=false;

    }

    private void QueryTemperatures(){
        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();

       asyncHttpClient.get(TemperatureAPIUrl, new JsonHttpResponseHandler() {
           @Override
           public void onSuccess(int i, Headers headers, JSON json) {
               JSONObject jsonObject=json.jsonObject;

               try {
                   CurrentTempInside=jsonObject.getInt("current_temperature_f");
                   CurrentTempOutside=jsonObject.getInt("outside_temperature_f");
                   SetTemperatureInsideCar= jsonObject.getInt("set_temperature_f");
               }catch (Exception e){
                    e.printStackTrace();
               }

           }

           @Override
           public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.e("MainActivity","Failed initial temperature query");
           }
       });

    }

    private void ConvertScale(){
        if (CurrentScale.equals(FahrenheitCode)){     //if fahrenheit-go to C
            CurrentScale="\u2103";
            CurrentTempInside=(CurrentTempInside-32)*5/9;
            SetTemperatureInsideCar=(SetTemperatureInsideCar-32)*5/9;
            CurrentTempOutside=(CurrentTempOutside-32)*5/9;

        }
        else if (CurrentScale.equals(CelsiusCode)){      //if celsius-go to F
            CurrentScale="\u2109";
            CurrentTempInside=((CurrentTempInside*9/5)+32);
            SetTemperatureInsideCar=((SetTemperatureInsideCar*9/5)+32);
            CurrentTempOutside=((CurrentTempOutside*9/5)+32);

        }

    }

    private void QueryBattery(){
        //ToDO: Battery API
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSettings:

                break;
            case R.id.btnClimate:
                startActivity(new Intent(MainActivity.this, ClimateActivity.class));
                break;

            case R.id.btnBattery:

                break;
            case R.id.btnStartCar:
               startActivity(new Intent(MainActivity.this, StartEngineActivity.class));
                break;

        }
    }
}