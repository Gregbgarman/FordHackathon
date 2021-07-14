package com.example.fordhackathon.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fordhackathon.MainActivity;
import com.example.fordhackathon.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import okhttp3.Headers;


public class PersonOneFragment extends Fragment {

    private ConstraintLayout constraintLayout;

    private ImageView ivProfilePic;

    private static final long START_TIME_IN_MILLIS = 900000;
    private CountDownTimer mCountDownTimer;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private int RealCurrentTemp;

    private TextView tvTemp;


    public PersonOneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivProfilePic=view.findViewById(R.id.ivProfilePicP1);
        constraintLayout=view.findViewById(R.id.ThePoneFragmentLayout);
        Glide.with(getContext()).load(R.drawable.gdubsanfran).circleCrop().into(ivProfilePic);

        tvTemp=view.findViewById(R.id.tvTempCountDown);
        RealCurrentTemp=-1;

        if (MainActivity.PersonOneTempChanging==true){
            tvTemp.setText(MainActivity.CurrentTempInside+MainActivity.FahrenheitCode);
            ShowTempCountDown();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_person_one, container, false);
    }

    private void ShowTempCountDown(){
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                QueryWebApi();

            }
            @Override
            public void onFinish() {
                //never going to get here
            }
        }.start();

    }

    private void QueryWebApi(){
        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        String url="https://af61d91cbb8d.ngrok.io/climate/temperature";
        asyncHttpClient.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                JSONObject jsonObject= json.jsonObject;
                try {
                    RealCurrentTemp = jsonObject.getInt("current_temperature_f");
                    tvTemp.setText(RealCurrentTemp+MainActivity.FahrenheitCode);
                    if (RealCurrentTemp==MainActivity.SetTemperatureInsideCar){
                        mCountDownTimer.cancel();
                        ShowSnackBar();
                        return;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {

            }
        });

    }

    private void ShowSnackBar(){
        Snackbar.make(constraintLayout,"Inside Temperature Reached",Snackbar.LENGTH_LONG).show();
    }
}