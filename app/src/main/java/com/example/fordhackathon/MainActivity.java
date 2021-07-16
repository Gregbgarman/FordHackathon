package com.example.fordhackathon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fordhackathon.activities.BatteryActivity;
import com.example.fordhackathon.activities.ClimateActivity;
import com.example.fordhackathon.activities.StartEngineActivity;

import org.json.JSONObject;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    //todo: inside temp needs to be viewable from main screen, add comments to code, use ford api, UI,min/max scale values


    public static final String FahrenheitCode="\u2109";
    public static final String CelsiusCode="\u2103";

    public static double SetTemperatureInsideCar,CurrentTempOutside,CurrentTempInside;
    public static boolean EngineRunning,NewTempSet,BatteryNeedsCharging;
    public static String CurrentScale;
    public static final String TemperatureAPIUrl="https://af61d91cbb8d.ngrok.io/climate/temperature";
    public static final String BatteryAPIURL="https://af61d91cbb8d.ngrok.io/charging/status";

    public static int BatteryPercentage;
    private CardView CVStartCar,CVClimate,CVBattery;
    private TextView tvCurrentTemp,tvBatteryCharge;
    private ImageView ivParkingSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        Window window = getWindow();                   //setting status bar color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.AppGray));
        CVClimate=findViewById(R.id.CVClimate);
        CVClimate.setOnClickListener(this);
        CVBattery=findViewById(R.id.CVBattery);
        CVBattery.setOnClickListener(this);
        CVStartCar=findViewById(R.id.CVStartCar);
        CVStartCar.setOnClickListener(this);
        tvCurrentTemp=findViewById(R.id.tvCurrentTempMain);
        tvBatteryCharge=findViewById(R.id.tvBatteryPercentageHome);
        ivParkingSign=findViewById(R.id.ivParkingsign);
        Glide.with(this).load(R.drawable.parkingsymbol).into(ivParkingSign);

        CurrentScale=FahrenheitCode;        //defaulting to fahrenheit when program first starts
        QueryTemperatures();        //Querying data now so mainactivity acts more like a viewmodel
        EngineRunning=false;
        NewTempSet=false;
        BatteryPercentage=80;
        tvBatteryCharge.setText(BatteryPercentage+"%");

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NewTempSet==true){
            //set the textview to equal CurrentTempInside
          MainActivity.BatteryPercentage=23;
          BatteryNeedsCharging=true;
          tvBatteryCharge.setText("23%");
            NewTempSet=false;
        }

        tvCurrentTemp.setText((int)CurrentTempInside+CurrentScale);
        //could also add something like celsius temp changed-then run convertscale

    }

    private void QueryTemperatures(){       //Querying temperature API we created

        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
       asyncHttpClient.get(TemperatureAPIUrl, new JsonHttpResponseHandler() {
           @Override
           public void onSuccess(int i, Headers headers, JSON json) {
               JSONObject jsonObject=json.jsonObject;

               try {
                   CurrentTempInside=jsonObject.getInt("current_temperature_f");
                   CurrentTempOutside=jsonObject.getInt("outside_temperature_f");
                   SetTemperatureInsideCar= jsonObject.getInt("set_temperature_f");
                   tvCurrentTemp.setText((int)CurrentTempInside+FahrenheitCode);
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


    public static void ConvertScale(){
        double temp;
        if (CurrentScale.equals(FahrenheitCode)){     //if Fahrenheit-go to Celsius
            CurrentScale="\u2103";
            CurrentTempInside=(CurrentTempInside-32)*5/9;
            SetTemperatureInsideCar=(SetTemperatureInsideCar-32)*5/9;
            CurrentTempOutside=(CurrentTempOutside-32)*5/9;

        }
        else if (CurrentScale.equals(CelsiusCode)){      //if Celsius-go to Fahrenheit
            CurrentScale="\u2109";
            CurrentTempInside=((CurrentTempInside*9/5)+32);
            SetTemperatureInsideCar=((SetTemperatureInsideCar*9/5)+32);
            CurrentTempOutside=((CurrentTempOutside*9/5)+32);
        }

    }


    @Override
    public void onClick(View v) {       //events when clicking buttons
        switch (v.getId()){
            case R.id.CVClimate:
                startActivity(new Intent(MainActivity.this, ClimateActivity.class));
                break;

            case R.id.CVBattery:
                startActivity(new Intent(MainActivity.this, BatteryActivity.class));
                break;
            case R.id.CVStartCar:
               startActivity(new Intent(MainActivity.this, StartEngineActivity.class));
                break;

        }
    }
}