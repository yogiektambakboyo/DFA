package com.bcp.DFA;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created with IntelliJ IDEA.
 * User: IT-SUPERMASTER
 * Date: 10/6/14
 * Time: 9:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class ActivityMaps extends Activity implements FN_MapsRoutingListener {
    private static final LatLng BORWITA = new LatLng(-7.354119,112.6966055);
    private static final LatLng AHMADYANI = new LatLng(-7.3090876,112.7344894);
    private static final LatLng MARGOREJO = new LatLng(-7.3167517,112.7366036);
    private static LatLng NETWORKLOCATION = null;

    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_LONGITUDE = "longitude";
    private final String TAG_LATITUDE = "latitude";

    protected LatLng start;
    protected LatLng end;

    private GoogleMap googleMap;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_maps);

        NETWORKLOCATION = new LatLng(Double.parseDouble(getPref(TAG_LATITUDE)),Double.parseDouble(getPref(TAG_LONGITUDE)));

        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        //googleMap.addMarker(new MarkerOptions().position(MARGOREJO).title("Kose Mas Yogi"));

        //googleMap.addMarker(new MarkerOptions().position(BORWITA).title("Tempat Kerjoe Mas Yogi"));

        //googleMap.addMarker(new MarkerOptions().position(NETWORKLOCATION).title("Lokasi Network"));

        start = new LatLng(18.015365, -77.499382);
        end = new LatLng(18.012590, -77.500659);
        FN_MapsRouting routing = new FN_MapsRouting(FN_MapsRouting.TravelMode.WALKING);
        routing.registerListener(this);
        routing.execute(MARGOREJO, BORWITA);

        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setMyLocationEnabled(true);


        // move camera to zoom on map
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(MARGOREJO));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MARGOREJO,13));

    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }

    @Override
    public void onRoutingFailure() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onRoutingStart() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onRoutingSuccess(PolylineOptions mPolyOptions, FN_MapsRoute route) {
        PolylineOptions polyoptions = new PolylineOptions();
        polyoptions.color(Color.BLUE);
        polyoptions.width(10);
        polyoptions.addAll(mPolyOptions.getPoints());
        googleMap.addPolyline(polyoptions);
        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(MARGOREJO);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.dfa_marker_start));
        googleMap.addMarker(options);
        // End marker
        options = new MarkerOptions();
        options.position(BORWITA);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.dfa_marker_endpng));
        googleMap.addMarker(options);
    }
}