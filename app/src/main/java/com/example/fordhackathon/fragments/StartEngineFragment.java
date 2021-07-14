package com.example.fordhackathon.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fordhackathon.MainActivity;
import com.example.fordhackathon.R;
import com.example.fordhackathon.dialogs.EngineDialog;
import com.example.fordhackathon.maps.MapsActivitySetHome;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;


public class StartEngineFragment extends Fragment {

    private Button btnStartEngine;


    Button btnbtnGoMaps;

    public StartEngineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        QueryLastTemp();

        btnbtnGoMaps=view.findViewById(R.id.btnbtnSetHome);
        btnbtnGoMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MapsActivitySetHome.class));

            }
        });


        btnStartEngine=view.findViewById(R.id.btnStartEngine);
        btnStartEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ShowDialog();

            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start_engine, container, false);
    }


    private void QueryLastTemp(){
        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        String url="https://af61d91cbb8d.ngrok.io/climate/temperature";
        asyncHttpClient.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                JSONObject jsonObject= json.jsonObject;

                try {
                    MainActivity.SetTemperatureInsideCar = jsonObject.getInt("set_temperature_f");
                    MainActivity.CurrentTempInside=jsonObject.getInt("current_temperature_f");
                    MainActivity.CurrentTempOutside=jsonObject.getInt("outside_temperature_f");
                }catch (Exception e){

                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {

            }
        });



    }

    private void ShowDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Car Started! Temperature inside Car Will Be Set To " +MainActivity.SetTemperatureInsideCar + MainActivity.FahrenheitCode +". It is Currently "
        + MainActivity.CurrentTempOutside + MainActivity.FahrenheitCode + " Outside");
        builder.setPositiveButton("Adjust", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FragmentManager fm=getChildFragmentManager();
                EngineDialog engineDialog=new EngineDialog();
                engineDialog.show(fm,"go");

            }
        });
        builder.setNegativeButton("Dismiss", null);
        builder.create().show();

    }
}