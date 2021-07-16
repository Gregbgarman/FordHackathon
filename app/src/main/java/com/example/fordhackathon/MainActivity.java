package com.example.fordhackathon;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fordhackathon.activities.BatteryActivity;
import com.example.fordhackathon.activities.ClimateActivity;

import org.json.JSONObject;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    public static final String FahrenheitCode="\u2109";
    public static final String CelsiusCode="\u2103";

    public static double SetTemperatureInsideCar,CurrentTempOutside,CurrentTempInside;
    public static boolean EngineRunning,NewTempSet,BatteryNeedsCharging;
    public static String CurrentScale;
    public static final String TemperatureAPIUrl="https://af61d91cbb8d.ngrok.io/climate/temperature";
    public static final String BatteryAPIURL="https://af61d91cbb8d.ngrok.io/charging/status";
    public static String FordAPIUrl="https://api.mps.ford.com/api/fordconnect/vehicles/v1/8a7f9fa878849d8a0179579d2f26043a";

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
        QueryTemperatures();                //Querying data now so mainactivity acts more like a viewmodel
        EngineRunning=false;
        NewTempSet=false;                   //more default values
        BatteryPercentage=80;
        tvBatteryCharge.setText(BatteryPercentage+"%");

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NewTempSet==true){                  //boolean set to true after change temperature setting in climate controls
          MainActivity.BatteryPercentage=23;
          BatteryNeedsCharging=true;
          tvBatteryCharge.setText("23%");
          NewTempSet=false;
        }

        tvCurrentTemp.setText((int)CurrentTempInside+CurrentScale);
        tvBatteryCharge.setText(String.valueOf(BatteryPercentage)+"%");

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
               QueryFordApi();
                break;

        }
    }

    private void QueryFordApi(){        //querying for API to get ignition status and miles to E

        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        RequestHeaders requestHeaders=new RequestHeaders();
        requestHeaders.put("Application-Id","afdc085b-377a-4351-b23e-5e1d35fb3700");
        requestHeaders.put("api-version", "2020-06-01");
        requestHeaders.put("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6IlMxUEZhdzdkR2s3bHNFQmEzUjVWMnRLSzVYYnNIWEJsemFXZGhjNUVNdW8ifQ.eyJpc3MiOiJodHRwczovL2RhaDJ2YjJjcHJvZC5iMmNsb2dpbi5jb20vOTE0ZDg4YjEtMzUyMy00YmY2LTliZTQtMWI5NmI0ZjZmOTE5L3YyLjAvIiwiZXhwIjoxNjI2NDU2NDQ3LCJuYmYiOjE2MjY0NTUyNDcsImF1ZCI6ImMxZTRjMWEwLTI4NzgtNGU2Zi1hMzA4LTgzNmIzNDQ3NGVhOSIsImxvY2FsZSI6ImVuIiwiaWRwIjoiYjJjX0RwSzFPQW44ZFEiLCJtdG1JZCI6IjRmYWQyMzM4LTliMTQtNDIxNi05YWM0LWYwOTI0NGZjZjllZiIsInVzZXJHdWlkIjoidmE4ODFJako1aW5RelhLOUFKUTk4SXk4UTVBbVloWEhlYlg5azNuNk82QUpwYndsVm1PWG1KekoxbklaSFFhUyIsInN1YiI6IjcwMGJlMzhiLTI5ZmItNDliMy1iNDk2LWI1NjMwNTI2NDY0YyIsIm5vbmNlIjoiMTIzNDU2Iiwic2NwIjoiYWNjZXNzIiwiYXpwIjoiMzA5OTAwNjItOTYxOC00MGUxLWEyN2ItN2M2YmNiMjM2NThhIiwidmVyIjoiMS4wIiwiaWF0IjoxNjI2NDU1MjQ3fQ.d7xm92go9DA1SSSu6Xi0qRmQjcApJO3SR0aqpDI76Z0lRYhMM2Pan0SXeBcUb9NEAg9eJbifZMmMWwwv7i7M8BaELn2YDhg5BGD4fHEY6NibOIDHSnuAHzYee-vhLojEc4MSQ1ssWU3HVeJx9TuH1GwO0ZB49QmSvRqmbk3_lGlTeEdslpuNX_lYGrluZGlxoPZDLbdPrXHxAFN1W9Lqpj488_G-W4AbJ294zleYGVjy0YwOLk7w4BObWk5tO4OjTglnqeXDFd8eNkK7gaSF6-DaNvjQRfzhGxmp5-hB9XU3o0XfHrHw4B4Eu2twFp04hVNfMbool-950Y7kwWvE1w");

        asyncHttpClient.get(FordAPIUrl, requestHeaders,null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                JSONObject jsonObject= json.jsonObject;

                try{
                    String Engine_Status=jsonObject.getJSONObject("vehicle").getJSONObject("vehicleStatus").getJSONObject("remoteStartStatus").getString("status");
                    int MilesRemaining=jsonObject.getJSONObject("vehicle").getJSONObject("vehicleDetails").getJSONObject("batteryChargeLevel").getInt("distanceToEmpty");
                    if (Engine_Status.equals("ENGINE_RUNNING")){
                        ShowMessage(MilesRemaining);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.e("StartEngineActivity","Failed to reach Ford API");
                Toast.makeText(MainActivity.this, "Need Refresh Token", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void ShowMessage(int Miles){        //displays upon successfully reaching Ford API

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage("Ford API Accessed:"+ "\n" +"Car Running and has " + Miles +" Miles To Empty");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }
}