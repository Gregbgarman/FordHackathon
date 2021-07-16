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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
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

    private void QueryFordApi(){

        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        RequestHeaders requestHeaders=new RequestHeaders();
        requestHeaders.put("Application-Id","afdc085b-377a-4351-b23e-5e1d35fb3700");
        requestHeaders.put("api-version", "2020-06-01");
        requestHeaders.put("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6IlMxUEZhdzdkR2s3bHNFQmEzUjVWMnRLSzVYYnNIWEJsemFXZGhjNUVNdW8ifQ.eyJpc3MiOiJodHRwczovL2RhaDJ2YjJjcHJvZC5iMmNsb2dpbi5jb20vOTE0ZDg4YjEtMzUyMy00YmY2LTliZTQtMWI5NmI0ZjZmOTE5L3YyLjAvIiwiZXhwIjoxNjI2NDA5NjM5LCJuYmYiOjE2MjY0MDg0MzksImF1ZCI6ImMxZTRjMWEwLTI4NzgtNGU2Zi1hMzA4LTgzNmIzNDQ3NGVhOSIsImxvY2FsZSI6ImVuIiwiaWRwIjoiYjJjX0RwSzFPQW44ZFEiLCJtdG1JZCI6IjRmYWQyMzM4LTliMTQtNDIxNi05YWM0LWYwOTI0NGZjZjllZiIsInVzZXJHdWlkIjoidmE4ODFJako1aW5RelhLOUFKUTk4SXk4UTVBbVloWEhlYlg5azNuNk82QUpwYndsVm1PWG1KekoxbklaSFFhUyIsInN1YiI6IjcwMGJlMzhiLTI5ZmItNDliMy1iNDk2LWI1NjMwNTI2NDY0YyIsIm5vbmNlIjoiMTIzNDU2Iiwic2NwIjoiYWNjZXNzIiwiYXpwIjoiMzA5OTAwNjItOTYxOC00MGUxLWEyN2ItN2M2YmNiMjM2NThhIiwidmVyIjoiMS4wIiwiaWF0IjoxNjI2NDA4NDM5fQ.Y6PywrcIgv3eVAMdaAYwzbs_LxxzStEbCJfIM8GDngIj2zVZ5dHuL6mRc6ltLhfQEX0yaIPqBo3dAKA3DMIBBf0wFmjNeGaawZxOhWDjti5MIyDY3mJFZhnKQq50YF-AhZaDpiM5lIxdlFRpZHww5oEYwWQFpe7lfSnk4DJA8WBiGJy0U_ffn_od8r6Tx53PheoJFRwPt24VcRBxzzFT52s8JWdeZ4QCdwzbIVU82hGIqNPBHPrCEiGRNnvDWzVPf_VoqzwiqUim9RG3NVSX8V3d_047ekcyvAq97FUZPMN8ixi9MwgeOaq67w67-x8nCSYj73tW6fk-dEkXYOrxgg");

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

    private void ShowMessage(int Miles){
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