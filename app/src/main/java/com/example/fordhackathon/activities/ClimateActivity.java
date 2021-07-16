package com.example.fordhackathon.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fordhackathon.MainActivity;
import com.example.fordhackathon.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import okhttp3.Headers;

public class ClimateActivity extends AppCompatActivity {

    private TextView tvInsideTemp,tvOutsideTemp,tvSliderTemp,tvSetscale,tvFrontDefroster,tvRearDefroster;
    private SeekBar TempSeekBar;
    private Button btnSetTemp;
    private int SliderValue;
    private JSONObject jsonObject;
    private static final long START_TIME_IN_MILLIS = 900000;
    private CountDownTimer mCountDownTimer;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private double RealCurrentTemp;
    private Switch TempSwitch,RearDefrosterSwitch,FrontDefrosterSwitch;
    private boolean TempSwitchChecked,RearDefrosterChecked,FrontDefrosterChecked;
    private ConstraintLayout mylayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climate);
        getSupportActionBar().hide();
        Window window = getWindow();                   //setting status bar color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.AppGray));
        TempSwitchChecked=false;
        RearDefrosterChecked=false;
        FrontDefrosterChecked=false;

        mylayout=findViewById(R.id.CALayout);
        TempSwitch=findViewById(R.id.TempSwitch);
        RearDefrosterSwitch=findViewById(R.id.SwitchRearDefroster);
        FrontDefrosterSwitch=findViewById(R.id.SwitchFrontDefroster);
        tvSetscale=findViewById(R.id.tvSetScale);
        tvFrontDefroster=findViewById(R.id.tvFrontDefrosterStatus);
        tvRearDefroster=findViewById(R.id.tvRearDefrosterStatus);

        if (MainActivity.CurrentScale.equals(MainActivity.FahrenheitCode)) {
            TempSwitch.setText(MainActivity.FahrenheitCode);
        }
        else{
            TempSwitch.setText(MainActivity.CelsiusCode);
            TempSwitch.setChecked(true);
            TempSwitchChecked=true;
        }
        TempSwitch.setOnClickListener(new View.OnClickListener() {          //converting celsius/fahrenheit to the other
            @Override
            public void onClick(View v) {           //convert to celsius
                if (TempSwitchChecked==false) {
                    TempSwitch.setText(MainActivity.CelsiusCode);
                    tvSetscale.setText("Celsius");
                    TempSwitchChecked=true;

                }
                else if (TempSwitchChecked==true){      //convert to fahrenheit
                    TempSwitch.setText(MainActivity.FahrenheitCode);
                    tvSetscale.setText("Fahrenheit");
                    TempSwitchChecked=false;

                }                                   //repopulating views on screen
                MainActivity.ConvertScale();
                tvInsideTemp.setText((int)MainActivity.CurrentTempInside+MainActivity.CurrentScale);
                tvOutsideTemp.setText((int)MainActivity.CurrentTempOutside+MainActivity.CurrentScale);
                tvSliderTemp.setText((int)MainActivity.SetTemperatureInsideCar+MainActivity.CurrentScale);
                TempSeekBar.setProgress((int)MainActivity.SetTemperatureInsideCar);

            }
        });

        FrontDefrosterSwitch.setOnClickListener(new View.OnClickListener() {    //simply checking/unchecking defroster ON or OFF
            @Override
            public void onClick(View v) {
                if (FrontDefrosterChecked==false) {
                    tvFrontDefroster.setText("ON");
                    FrontDefrosterChecked=true;
                }
                else if (FrontDefrosterChecked==true){
                    tvFrontDefroster.setText("OFF");
                    FrontDefrosterChecked=false;
                }
            }
        });

        RearDefrosterSwitch.setOnClickListener(new View.OnClickListener() {         //simply checking/unchecking defroster ON or OFF
            @Override
            public void onClick(View v) {
                if (RearDefrosterChecked==false) {
                    tvRearDefroster.setText("ON");
                    RearDefrosterChecked=true;
                }
                else if (RearDefrosterChecked==true){
                    tvRearDefroster.setText("OFF");
                    RearDefrosterChecked=false;
                }
            }
        });

        tvInsideTemp=findViewById(R.id.tvTempInsideCar);
        tvOutsideTemp=findViewById(R.id.tvTempOutsideCar);
        tvOutsideTemp.setText((int)MainActivity.CurrentTempOutside+MainActivity.CurrentScale);

        tvSliderTemp=findViewById(R.id.tvSlidingTemp);
        TempSeekBar=findViewById(R.id.SeekBarTemp);
        btnSetTemp=findViewById(R.id.btnTempSubmit);
        RealCurrentTemp=-1;


        btnSetTemp.setOnClickListener(new View.OnClickListener() {      //user gets pop up when sets new temperature, and live change begins
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

        TempSeekBar.setProgress((int)MainActivity.SetTemperatureInsideCar);
        tvSliderTemp.setText((int)MainActivity.SetTemperatureInsideCar+MainActivity.CurrentScale);       //decide if want default or not

        TempSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSliderTemp.setText(String.valueOf(progress) + MainActivity.CurrentScale);
                SliderValue=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //not using
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //not using
            }
        });

        tvInsideTemp.setText((int)MainActivity.CurrentTempInside+MainActivity.CurrentScale);
    }

    private void PostNewTemperatureSetting(){       //when user sets new temperature value, posts to our API

        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        asyncHttpClient.get(MainActivity.TemperatureAPIUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                jsonObject=json.jsonObject;
                int NewValue=SliderValue;

                try {
                    if (MainActivity.CurrentScale.equals(MainActivity.CelsiusCode)){         //if in celsius,converting before posting
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
                        MainActivity.NewTempSet=true;
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

    private void BeginLiveTempChange(){         //when user sets new temperature, temperature increments/decrements every 2 seconds

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                QueryWebApi();      //Each time this is queried, Inside temperature increments/decrements by 1

            }
            @Override
            public void onFinish() {
                //never using
            }
        }.start();

    }

    private void QueryWebApi(){     //Querying Temperature API changes it's stored current value by 1

        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();

        asyncHttpClient.get(MainActivity.TemperatureAPIUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                JSONObject Object= json.jsonObject;
                try {
                    if (MainActivity.CurrentScale.equals(MainActivity.FahrenheitCode)) {
                        RealCurrentTemp = Object.getDouble("current_temperature_f");        //storing value from our API
                    }
                    else if (MainActivity.CurrentScale.equals(MainActivity.CelsiusCode)) {
                        RealCurrentTemp = Object.getDouble("current_temperature_c");
                    }

                    MainActivity.CurrentTempInside=RealCurrentTemp;
                   tvInsideTemp.setText((int)RealCurrentTemp+MainActivity.CurrentScale);

                    if ((int)RealCurrentTemp==(int)MainActivity.SetTemperatureInsideCar){       //when set temperature reached
                        mCountDownTimer.cancel();
                        Snackbar.make(mylayout,"Set Temperature Reached",Snackbar.LENGTH_LONG).show();
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