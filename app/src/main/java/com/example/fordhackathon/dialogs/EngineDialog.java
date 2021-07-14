package com.example.fordhackathon.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fordhackathon.MainActivity;
import com.example.fordhackathon.R;
import com.example.fordhackathon.fragments.StartEngineFragment;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;


public class EngineDialog extends DialogFragment {

    private TextView tvInsideTemp,tvOutsideTemp,tvSliderTemp;
   // private ImageView ivInside,ivOutside;
    private SeekBar TempSeekBar;
    private Button btnSetTemp;
    private int SliderValue;
    private boolean ClickedPopUp;

    private String CurrentScale;
    //will need to know which scale using, then set that equal to code in main


    public EngineDialog() {
        // Required empty public constructor
    }

    JSONObject jsonObject;

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ClickedPopUp=false;
        tvInsideTemp=view.findViewById(R.id.tvTempInsideCar);
        tvOutsideTemp=view.findViewById(R.id.tvTempOutsideCar);
        tvOutsideTemp.setText(String.valueOf(MainActivity.CurrentTempOutside) + MainActivity.FahrenheitCode);

        tvSliderTemp=view.findViewById(R.id.tvSlidingTemp);
        TempSeekBar=view.findViewById(R.id.SeekBarTemp);
        btnSetTemp=view.findViewById(R.id.btnTempSubmit);
        btnSetTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setMessage("You will receive a notification when temperature inside car is " +SliderValue+ MainActivity.FahrenheitCode);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SetNewInsideTemperature();
                      dismiss();

                    }
                });

                builder.create().show();
            }
        });


        TempSeekBar.setProgress(MainActivity.SetTemperatureInsideCar);
        tvSliderTemp.setText(MainActivity.SetTemperatureInsideCar+MainActivity.FahrenheitCode);
        TempSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSliderTemp.setText(String.valueOf(progress) + MainActivity.FahrenheitCode);
                SliderValue=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        tvInsideTemp.setText(MainActivity.CurrentTempInside+MainActivity.FahrenheitCode);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_engine_dialog, container, false);
    }

    private void SetNewInsideTemperature(){

        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        String url="https://af61d91cbb8d.ngrok.io/climate/temperature";

        asyncHttpClient.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                jsonObject=json.jsonObject;

                try {
                    jsonObject.put("set_temperature_f",SliderValue);

                }catch (Exception e){
                    e.printStackTrace();
                }

                MainActivity.SetTemperatureInsideCar=SliderValue;

                asyncHttpClient.post(url, jsonObject.toString(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Headers headers, JSON json) {
                        Log.i("EngineDialog","Post success");
                        MainActivity.PersonOneTempChanging=true;        //now go to person one fragment to see
                    }

                    @Override
                    public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                        Toast.makeText(getContext(), "Failed to save new temp", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.e("EngineDialog","JSON Error getting data");
            }


        });






    }
}
