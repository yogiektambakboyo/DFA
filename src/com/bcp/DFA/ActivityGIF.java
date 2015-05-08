package com.bcp.DFA;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.*;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: IT-SUPERMASTER
 * Date: 9/3/14
 * Time: 11:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class ActivityGIF extends Activity {

    String longitude = "",latitude = "",cityname="";

    TextView TxtLongitude,TxtLatitude,TxtCityName;
    Button BtnGeo;

    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_LONGITUDE = "longitude";
    private final String TAG_LATITUDE = "latitude";
    private final String TAG_NAMAJALAN = "namajalan";
    private final String TAG_TGL = "tgl";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_test);


        TxtLongitude = (TextView) findViewById(R.id.longitude);
        TxtLongitude.setText("Longitude: " +longitude);

        TxtLatitude = (TextView) findViewById(R.id.latitude);
        TxtLatitude.setText("Latitude: " +latitude);

        TxtCityName = (TextView) findViewById(R.id.cityname);
        TxtCityName.setText("City Name: " +cityname);

        BtnGeo = (Button) findViewById(R.id.Btn);
        BtnGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*double latitude = Double.parseDouble(getPref(TAG_LATITUDE));
                double longitude = Double.parseDouble(getPref(TAG_LONGITUDE));
                String label = "I'm Here!";
                String uriBegin = "geo:" + latitude + "," + longitude;
                String query = latitude + "," + longitude + "(" + label + ")";
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                Uri uri = Uri.parse(uriString);
                Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                startActivity(mapIntent);*/
                Toast.makeText(getApplicationContext(),"Location : "+getPref(TAG_LONGITUDE)+ " - "+ getPref(TAG_LATITUDE)+ " - " + getPref(TAG_NAMAJALAN),Toast.LENGTH_SHORT).show();
            }
        });

    }


    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }
}