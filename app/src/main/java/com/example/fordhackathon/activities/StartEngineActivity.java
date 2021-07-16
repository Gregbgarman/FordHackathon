package com.example.fordhackathon.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fordhackathon.MainActivity;
import com.example.fordhackathon.R;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StartEngineActivity extends AppCompatActivity {

    private Button btnStartEngine,btnStopEngine;
    public static String FordAPIUrl="https://api.mps.ford.com/api/fordconnect/vehicles/v1/8a7f9fa878849d8a0179579d2f26043a";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_engine);
        btnStartEngine=findViewById(R.id.btnStartEnginebtn);
        btnStopEngine=findViewById(R.id.btnStopEnginebtn);
        btnStartEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  QueryFordApi();
               // TestQuery();
              //  QueryFordApi("START");
                btnStartEngine.setVisibility(View.INVISIBLE);
                btnStopEngine.setVisibility(View.VISIBLE);
                ShowMessage();
            }
        });

        btnStopEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // QueryFordApi("STOP");
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

    private void TestQuery(){

        Handler myhandler = new Handler();
        myhandler.postDelayed(new Runnable() {      //user will only be in this activity for 2 seconds before being
            @Override                               //sent to Login
            public void run() {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                Request request = new Request.Builder()
                        .url("https://api.mps.ford.com/api/fordconnect/vehicles/v1/8a7f9fa878849d8a0179579d2f26043a")
                        .method("GET", null)
                        .addHeader("Application-Id", "afdc085b-377a-4351-b23e-5e1d35fb3700")
                        .addHeader("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6IlMxUEZhdzdkR2s3bHNFQmEzUjVWMnRLSzVYYnNIWEJsemFXZGhjNUVNdW8ifQ.eyJpc3MiOiJodHRwczovL2RhaDJ2YjJjcHJvZC5iMmNsb2dpbi5jb20vOTE0ZDg4YjEtMzUyMy00YmY2LTliZTQtMWI5NmI0ZjZmOTE5L3YyLjAvIiwiZXhwIjoxNjI2Mzk4ODQ4LCJuYmYiOjE2MjYzOTc2NDgsImF1ZCI6ImMxZTRjMWEwLTI4NzgtNGU2Zi1hMzA4LTgzNmIzNDQ3NGVhOSIsImxvY2FsZSI6ImVuIiwiaWRwIjoiYjJjX0RwSzFPQW44ZFEiLCJtdG1JZCI6IjRmYWQyMzM4LTliMTQtNDIxNi05YWM0LWYwOTI0NGZjZjllZiIsInVzZXJHdWlkIjoidmE4ODFJako1aW5RelhLOUFKUTk4SXk4UTVBbVloWEhlYlg5azNuNk82QUpwYndsVm1PWG1KekoxbklaSFFhUyIsInN1YiI6IjcwMGJlMzhiLTI5ZmItNDliMy1iNDk2LWI1NjMwNTI2NDY0YyIsIm5vbmNlIjoiMTIzNDU2Iiwic2NwIjoiYWNjZXNzIiwiYXpwIjoiMzA5OTAwNjItOTYxOC00MGUxLWEyN2ItN2M2YmNiMjM2NThhIiwidmVyIjoiMS4wIiwiaWF0IjoxNjI2Mzk3NjQ4fQ.FJv7bAClrTriFMSUy43Df6vbZadYk-BMMtI5I9Jq5mAh292SadB3UYMYapeiFlhMbSFvUOuUBRekBpaebVGWXkusnEpF82eFyfdbPx-VE2QadnhpBatWZ1AEiAdQ2LW634DkV_wTk9cVGNQ9uUKWjW-3uuKdzZ1aeHfJKEvtb9YsuOT379MRHRhoCwRu6J6YONebpey2gI9qIZKjqAZz3_npPIJN9NaSBTI55ZMHCaVhL1e6AkKLlbCPec-tgQ903Zkm7ARGGOww8F1f6npeqJ_lqRNc4L-UHruwwEjvv1R6zEVBQ5PrCJEPI5rH43T30Fz0EwGf8p54sCFCvN7uSQ")
                        .addHeader("api-version", "2020-06-01")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    int x=4;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }, 1000);




    }

    private void QueryFordApi(){
        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        RequestParams requestParams=new RequestParams();
        requestParams.put("Application-Id","afdc085b-377a-4351-b23e-5e1d35fb3700");
        requestParams.put("api-version", "2020-06-01");
        requestParams.put("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6IlMxUEZhdzdkR2s3bHNFQmEzUjVWMnRLSzVYYnNIWEJsemFXZGhjNUVNdW8ifQ.eyJpc3MiOiJodHRwczovL2RhaDJ2YjJjcHJvZC5iMmNsb2dpbi5jb20vOTE0ZDg4YjEtMzUyMy00YmY2LTliZTQtMWI5NmI0ZjZmOTE5L3YyLjAvIiwiZXhwIjoxNjI2Mzk4ODQ4LCJuYmYiOjE2MjYzOTc2NDgsImF1ZCI6ImMxZTRjMWEwLTI4NzgtNGU2Zi1hMzA4LTgzNmIzNDQ3NGVhOSIsImxvY2FsZSI6ImVuIiwiaWRwIjoiYjJjX0RwSzFPQW44ZFEiLCJtdG1JZCI6IjRmYWQyMzM4LTliMTQtNDIxNi05YWM0LWYwOTI0NGZjZjllZiIsInVzZXJHdWlkIjoidmE4ODFJako1aW5RelhLOUFKUTk4SXk4UTVBbVloWEhlYlg5azNuNk82QUpwYndsVm1PWG1KekoxbklaSFFhUyIsInN1YiI6IjcwMGJlMzhiLTI5ZmItNDliMy1iNDk2LWI1NjMwNTI2NDY0YyIsIm5vbmNlIjoiMTIzNDU2Iiwic2NwIjoiYWNjZXNzIiwiYXpwIjoiMzA5OTAwNjItOTYxOC00MGUxLWEyN2ItN2M2YmNiMjM2NThhIiwidmVyIjoiMS4wIiwiaWF0IjoxNjI2Mzk3NjQ4fQ.FJv7bAClrTriFMSUy43Df6vbZadYk-BMMtI5I9Jq5mAh292SadB3UYMYapeiFlhMbSFvUOuUBRekBpaebVGWXkusnEpF82eFyfdbPx-VE2QadnhpBatWZ1AEiAdQ2LW634DkV_wTk9cVGNQ9uUKWjW-3uuKdzZ1aeHfJKEvtb9YsuOT379MRHRhoCwRu6J6YONebpey2gI9qIZKjqAZz3_npPIJN9NaSBTI55ZMHCaVhL1e6AkKLlbCPec-tgQ903Zkm7ARGGOww8F1f6npeqJ_lqRNc4L-UHruwwEjvv1R6zEVBQ5PrCJEPI5rH43T30Fz0EwGf8p54sCFCvN7uSQ");



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

    private void ShowMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(StartEngineActivity.this);

        builder.setMessage("Car Started. See Climate Settings to Adjust Temperature");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();


    }




}