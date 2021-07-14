package com.example.fordhackathon.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fordhackathon.MainActivity;
import com.example.fordhackathon.R;

import org.json.JSONObject;

import okhttp3.Headers;

public class StartEngineActivity extends AppCompatActivity {

    private Button btnStartEngine,btnStopEngine;
    public static String FordAPIUrl="https://api.mps.ford.com/api/fordconnect/vehicles/v1/8a7f9fa878849d8a0179579d2f26043a?12345432122";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_engine);
        btnStartEngine=findViewById(R.id.btnStartEnginebtn);
        btnStopEngine=findViewById(R.id.btnStopEnginebtn);
        btnStartEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryFordApi("START");
            }
        });

        btnStopEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryFordApi("STOP");
            }
        });

        if (MainActivity.EngineRunning==true){
            btnStartEngine.setVisibility(View.INVISIBLE);
            btnStopEngine.setVisibility(View.VISIBLE);
        }
        else{
            btnStartEngine.setVisibility(View.VISIBLE);
            btnStopEngine.setVisibility(View.INVISIBLE);
        }
    }

    private void QueryFordApi(String action){
        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        RequestParams requestParams=new RequestParams();
        requestParams.put("GregKeyGreg","123aa321");

        asyncHttpClient.get(FordAPIUrl, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                JSONObject jsonObject= json.jsonObject;

                try{
                   // if (MainActivity.EngineRunning==true){

                //    }
                 //   else{


                  //  }
    int x=4;

                }catch (Exception e){
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.e("StartEngineActivity","Failed to reach Ford API");
            }
        });


    }
}