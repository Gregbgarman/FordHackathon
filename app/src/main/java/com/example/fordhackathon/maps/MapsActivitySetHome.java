package com.example.fordhackathon.maps;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fordhackathon.MainActivity;
import com.example.fordhackathon.R;
import com.example.fordhackathon.dialogs.EngineDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Headers;

public class MapsActivitySetHome extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap TheGoogleMap;
    private Double Lat,Long;
    private Button btnConfirm,btnSearch;
    private EditText etSearchBar;
    private String EnteredAddress,LocationAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_set_home);
        getSupportActionBar().hide();
        btnConfirm=findViewById(R.id.btnConfirmAddress);
        btnSearch=findViewById(R.id.btnAddress);
        etSearchBar=findViewById(R.id.etAddress);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeybaord(v);
                SearchLocation();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (LocationAddress != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivitySetHome.this);
                    builder.setMessage("Set " + LocationAddress + " As Home Address?");
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //set address to home address however we are storing it

                        }
                    });
                    builder.setNegativeButton("Go Back", null);
                    builder.create().show();
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.MyGooglemap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        TheGoogleMap=googleMap;
        TheGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.0473,-95.6752), 3));

    }



    private void SearchLocation(){
        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        EnteredAddress=etSearchBar.getText().toString().trim();
        String QueryString=new String();
        QueryString="";
        for (int i=0;i<EnteredAddress.length();i++){
            if (EnteredAddress.charAt(i)==' '){
                QueryString+="+";
            }
            else{
                QueryString+=EnteredAddress.charAt(i);
            }

        }


        String url="https://maps.googleapis.com/maps/api/place/textsearch/json?query="+QueryString+"&key="+getResources().getString(R.string.GoogleAPIKey);


        asyncHttpClient.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                JSONObject jsonObject=json.jsonObject;


                try {
                    JSONArray jsonArray=jsonObject.getJSONArray("results");
                    LocationAddress=jsonArray.getJSONObject(0).getString("formatted_address");
                    Lat = jsonArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    Long = jsonArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                }catch (Exception e){
                    int p=3;
                }

                if (Lat!=null && Long!=null) {
                    etSearchBar.setText("");
                    LatLng latLng = new LatLng(Lat, Long);
                    TheGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                    TheGoogleMap.addMarker(new MarkerOptions().position(latLng).title(EnteredAddress)).showInfoWindow();

                }
                else{
                    Toast.makeText(MapsActivitySetHome.this, "Couldn't Locate Address", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {

            }
        });





    }
    public void hideKeybaord(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
    }

}