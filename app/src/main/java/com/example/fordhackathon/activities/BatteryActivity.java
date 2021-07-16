package com.example.fordhackathon.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fordhackathon.MainActivity;
import com.example.fordhackathon.R;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;

public class BatteryActivity extends AppCompatActivity {

    private JSONObject jsonObject;
    private ProgressBar BatteryBar;
    private TextView tvPercentage;
    private String Message;
    private static final long START_TIME_IN_MILLIS = 900000;
    private CountDownTimer mCountDownTimer;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);
        BatteryBar=findViewById(R.id.BatteryProgressBar);
        tvPercentage=findViewById(R.id.tvBatteryPercent);
        tvPercentage.setText(MainActivity.BatteryPercentage+"%");
        BatteryBar.setScaleY(3f);
        BatteryBar.setProgress(MainActivity.BatteryPercentage);



        if (MainActivity.BatteryNeedsCharging==true) {
            SetBatteryAPI();

        }



    }

    /*
    private void BeginCharging(){

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                MainActivity.BatteryPercentage++;
                tvPercentage.setText(MainActivity.BatteryPercentage+"%");
                BatteryBar.setProgress(MainActivity.BatteryPercentage);
                if (MainActivity.BatteryPercentage>=30){
                    MainActivity.BatteryNeedsCharging=false;
                    mCountDownTimer.cancel();
                    ShowMessage();
                    return;
                }
            }
            @Override
            public void onFinish() {
                //never going to get here
            }
        }.start();


    }
*/
    private void BeginCountUp(){

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                QueryBatteryAPI();      //Each time this is queried, Battery % increments by 1

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

                asyncHttpClient.post(MainActivity.BatteryAPIURL, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Headers headers, JSON json) {
                        JSONObject Object=json.jsonObject;
                        try {
                            Message=Object.getString("status");
                            MainActivity.BatteryPercentage=Object.getInt("current_charge");
                            tvPercentage.setText(MainActivity.BatteryPercentage+"%");
                            BatteryBar.setProgress(MainActivity.BatteryPercentage);
                            if (MainActivity.BatteryPercentage==30){
                                MainActivity.BatteryNeedsCharging=false;
                                ShowMessage();
                                mCountDownTimer.cancel();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int i, Headers headers, String s, Throwable throwable) {

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
}