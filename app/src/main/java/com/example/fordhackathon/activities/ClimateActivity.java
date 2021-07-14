package com.example.fordhackathon.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fordhackathon.MainActivity;
import com.example.fordhackathon.R;

import org.json.JSONObject;

import okhttp3.Headers;

public class ClimateActivity extends AppCompatActivity {

    private TextView tvInsideTemp,tvOutsideTemp,tvSliderTemp;
    private SeekBar TempSeekBar;
    private Button btnSetTemp;
    private int SliderValue;
    private JSONObject jsonObject;
    private static final long START_TIME_IN_MILLIS = 900000;
    private CountDownTimer mCountDownTimer;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private int RealCurrentTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climate);
        tvInsideTemp=findViewById(R.id.tvTempInsideCar);
        tvOutsideTemp=findViewById(R.id.tvTempOutsideCar);
        tvOutsideTemp.setText(MainActivity.CurrentTempOutside+MainActivity.CurrentScale);

        tvSliderTemp=findViewById(R.id.tvSlidingTemp);
        TempSeekBar=findViewById(R.id.SeekBarTemp);
        btnSetTemp=findViewById(R.id.btnTempSubmit);
        RealCurrentTemp=-1;



        btnSetTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ClimateActivity.this);

                builder.setMessage("Inside Temperature Will change to " +SliderValue+ MainActivity.CurrentScale);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       MainActivity.SetTemperatureInsideCar=SliderValue;
                       PostNewTemperatureSetting();

                    }
                });

                builder.create().show();

            }
        });

        TempSeekBar.setProgress(MainActivity.SetTemperatureInsideCar);
        tvSliderTemp.setText(MainActivity.SetTemperatureInsideCar+MainActivity.CurrentScale);       //decide if want default or not

        TempSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSliderTemp.setText(String.valueOf(progress) + MainActivity.CurrentScale);
                SliderValue=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        tvInsideTemp.setText(MainActivity.CurrentTempInside+MainActivity.CurrentScale);


    }

    private void PostNewTemperatureSetting(){

        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();

        asyncHttpClient.get(MainActivity.TemperatureAPIUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                jsonObject=json.jsonObject;
                int NewValue=SliderValue;

                try {
                    if (MainActivity.CurrentScale.equals(MainActivity.CelsiusCode)){         //if in celsius
                        NewValue=((SliderValue*9/5)+32);
                    }

                    jsonObject.put("set_temperature_f", NewValue);


                }catch (Exception e){
                        e.printStackTrace();
                }

                asyncHttpClient.post(MainActivity.TemperatureAPIUrl, jsonObject.toString(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Headers headers, JSON json) {
                        Log.i("ClimateActivity","Posted new temperature setting");
                        BeginLiveTempChange();
                    }

                    @Override
                    public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                        Log.e("ClimateActivity","Failed to post new temperature setting");

                    }
                });


            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.e("ClimateActivity","Error retrieving API in GET method");
            }
        });




    }

    private void BeginLiveTempChange(){
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                QueryWebApi();      //Each time this is queried, Inside temperature decrements by 1

            }
            @Override
            public void onFinish() {
                //never going to get here
            }
        }.start();

    }

    private void QueryWebApi(){
        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();

        asyncHttpClient.get(MainActivity.TemperatureAPIUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                JSONObject jsonObject= json.jsonObject;
                try {
                    if (MainActivity.CurrentScale.equals(MainActivity.FahrenheitCode)) {
                        RealCurrentTemp = jsonObject.getInt("current_temperature_f");
                    }
                    else if (MainActivity.CurrentScale.equals(MainActivity.CelsiusCode)) {
                        RealCurrentTemp = jsonObject.getInt("current_temperature_c");
                    }

                   tvInsideTemp.setText(RealCurrentTemp+MainActivity.CurrentScale);
                    MainActivity.CurrentTempInside--;
                    if (RealCurrentTemp==MainActivity.SetTemperatureInsideCar){
                        mCountDownTimer.cancel();
                        return;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.e("ClimateActivity","Failed to Query API for decrementing");
            }
        });



    }
}