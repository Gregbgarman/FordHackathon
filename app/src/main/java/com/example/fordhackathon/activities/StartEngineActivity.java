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
import com.codepath.asynchttpclient.RequestHeaders;
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
              //  ThreadClass threadClass=new ThreadClass();
              //  threadClass.run();
                QueryFordApi();

            //    btnStartEngine.setVisibility(View.INVISIBLE);
             //   btnStopEngine.setVisibility(View.VISIBLE);
             //   ShowMessage();
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




    }

    private void QueryFordApi(){
        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        RequestHeaders requestHeaders=new RequestHeaders();
        requestHeaders.put("Application-Id","afdc085b-377a-4351-b23e-5e1d35fb3700");
        requestHeaders.put("api-version", "2020-06-01");
        requestHeaders.put("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6IlMxUEZhdzdkR2s3bHNFQmEzUjVWMnRLSzVYYnNIWEJsemFXZGhjNUVNdW8ifQ.eyJpc3MiOiJodHRwczovL2RhaDJ2YjJjcHJvZC5iMmNsb2dpbi5jb20vOTE0ZDg4YjEtMzUyMy00YmY2LTliZTQtMWI5NmI0ZjZmOTE5L3YyLjAvIiwiZXhwIjoxNjI2NDA4MjYwLCJuYmYiOjE2MjY0MDcwNjAsImF1ZCI6ImMxZTRjMWEwLTI4NzgtNGU2Zi1hMzA4LTgzNmIzNDQ3NGVhOSIsImxvY2FsZSI6ImVuIiwiaWRwIjoiYjJjX0RwSzFPQW44ZFEiLCJtdG1JZCI6IjRmYWQyMzM4LTliMTQtNDIxNi05YWM0LWYwOTI0NGZjZjllZiIsInVzZXJHdWlkIjoidmE4ODFJako1aW5RelhLOUFKUTk4SXk4UTVBbVloWEhlYlg5azNuNk82QUpwYndsVm1PWG1KekoxbklaSFFhUyIsInN1YiI6IjcwMGJlMzhiLTI5ZmItNDliMy1iNDk2LWI1NjMwNTI2NDY0YyIsIm5vbmNlIjoiMTIzNDU2Iiwic2NwIjoiYWNjZXNzIiwiYXpwIjoiMzA5OTAwNjItOTYxOC00MGUxLWEyN2ItN2M2YmNiMjM2NThhIiwidmVyIjoiMS4wIiwiaWF0IjoxNjI2NDA3MDYwfQ.X-CS336rGDVaiDUbFQAIQt9yWjpqEBt9ESKnggb7-nTi_0OtPWwIuGxYap3j1Z9M0u7iBN7v6bQLG-NBLWZf7JQPeBbW0x-FzhWg4tfkbkKbHRGHsQr-pdkd2NtCSNZTx0nN5xuGVV_YNC89RzFlOotBxvT7YE2wgrOGxcKW6dFTLH_hJZIzMdqnCnHeCd2XKSfT2Ah56bjmPq5fSxvImc3aoBq3dmJiojXwfXUeQJGY_gJhfAFAz-kj1O5SBTBWnmtaMcyUiPilthXm2D1nCg0xRWOQln0Y29gfeK_q-2EW9vBK0OVCwItKqbTjQRLamM_64IjIxMP1kK59WaCW8g");

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
            }
        });

    }

    private void ShowMessage(int Miles){
        AlertDialog.Builder builder = new AlertDialog.Builder(StartEngineActivity.this);

        builder.setMessage("Car Running and has " + Miles +" Miles To Empty");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();


    }



}