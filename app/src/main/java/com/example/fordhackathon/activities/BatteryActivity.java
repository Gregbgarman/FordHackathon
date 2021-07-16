package com.example.fordhackathon.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fordhackathon.MainActivity;
import com.example.fordhackathon.R;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;

public class BatteryActivity extends AppCompatActivity {

    private ProgressBar BatteryBar;
    private TextView tvTime,tvChargeStatus,tvChargeWithBar,tvResume;
    private String Message;
    private static final long START_TIME_IN_MILLIS = 900000;
    private CountDownTimer mCountDownTimer;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private int ClockMinutes;
    private boolean CanResumeCharging;
    private CardView cvCharge;
    private Switch PeakSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);
        getSupportActionBar().hide();
        Window window = getWindow();                   //setting status bar color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.AppGray));
        BatteryBar=findViewById(R.id.BatteryProgressBar);
        tvTime=findViewById(R.id.tvTime);
        tvChargeStatus=findViewById(R.id.tvChargeStatus);
        tvChargeWithBar=findViewById(R.id.tvChargeWithBar);
        tvResume=findViewById(R.id.tvResume);
        cvCharge=findViewById(R.id.cvChARGE);
        PeakSwitch=findViewById(R.id.PeakSwitch);
        PeakSwitch.setChecked(true);
        tvResume.setVisibility(View.INVISIBLE);
        CanResumeCharging=false;
        ClockMinutes=604;

        BatteryBar.setScaleY(5f);
        BatteryBar.setProgress(MainActivity.BatteryPercentage);

        if (MainActivity.BatteryNeedsCharging==true) {          //set to true after setting new temperature value
            SetBatteryAPI();                                    //done this way for creation of simulation-nothing functional
            tvChargeStatus.setText("Charging");
        }
        else{
            tvChargeStatus.setText(MainActivity.BatteryPercentage+"%");
            tvChargeWithBar.setText(MainActivity.BatteryPercentage+"%");
        }

        cvCharge.setOnClickListener(new View.OnClickListener() {        //this can be called once battery charge is paused
            @Override
            public void onClick(View v) {
                if (CanResumeCharging==true){
                    tvResume.setVisibility(View.INVISIBLE);
                    tvChargeStatus.setText("Charging");
                    BeginCountUp();

                }
            }
        });
    }

    private void BeginCountUp(){            //every second, battery charge increases, and time increases also

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                if (CanResumeCharging==false) {
                    QueryBatteryAPI();      //Each time this is queried, Battery % increments by 1
                }
                else{                       //block called when user resumes charging after it stops to wait for peak off hours
                    IncrementClock();
                    MainActivity.BatteryPercentage++;
                    tvChargeWithBar.setText(MainActivity.BatteryPercentage+"%");
                    BatteryBar.setProgress(MainActivity.BatteryPercentage);
                    if (MainActivity.BatteryPercentage==100){
                        mCountDownTimer.cancel();
                    }

                }

            }
            @Override
            public void onFinish() {
                //never going to get here
            }
        }.start();

    }


    private void QueryBatteryAPI(){
        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        asyncHttpClient.get(MainActivity.BatteryAPIURL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {

                asyncHttpClient.post(MainActivity.BatteryAPIURL, new JsonHttpResponseHandler() {        //calling post on the API resets the charge value to
                    @Override                                                                           //30% so can be shown in our demo
                    public void onSuccess(int i, Headers headers, JSON json) {
                        JSONObject Object=json.jsonObject;              //populating views with values on our Battery API
                        try {
                            Message=Object.getString("status");
                            MainActivity.BatteryPercentage=Object.getInt("current_charge");
                            tvChargeWithBar.setText(MainActivity.BatteryPercentage+"%");
                            BatteryBar.setProgress(MainActivity.BatteryPercentage);
                            IncrementClock();

                            if (MainActivity.BatteryPercentage==30){            //our demo threshold value is 30%
                                MainActivity.BatteryNeedsCharging=false;
                                ShowMessage();
                                tvChargeStatus.setText("On Hold");
                                tvResume.setVisibility(View.VISIBLE);
                                CanResumeCharging=true;
                                mCountDownTimer.cancel();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                        Log.e("BatteryActivity","Failed to reset battery API");
                    }
                });
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.e("BatteryActivity","Failed to query battery API");
            }
        });

    }

    private void SetBatteryAPI(){
        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        asyncHttpClient.post("https://af61d91cbb8d.ngrok.io/charging/reset", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {          //starting at 23%

                BeginCountUp();
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {

            }
        });
    }

    private void ShowMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(BatteryActivity.this);

        builder.setMessage(Message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }

    private void IncrementClock(){      //time increments by 6 minutes with each 1% battery charge
        String AmPm="PM";
        ClockMinutes+=6;
        int hours=ClockMinutes/60;
        if (hours>=12){
            hours=12;
            AmPm="AM";
        }
        int minutes=ClockMinutes%60;

        if (minutes<10){
            tvTime.setText(hours + ":" +"0"+ minutes + " " + AmPm);

        }
        else {
            tvTime.setText(hours + ":" + minutes + " " + AmPm);
        }

    }
}