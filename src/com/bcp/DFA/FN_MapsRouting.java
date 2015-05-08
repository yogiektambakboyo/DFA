package com.bcp.DFA;

/**
 * Async Task to access the Google Direction API and return the routing data
 * which is then parsed and converting to a route overlay using some classes created by Hesham Saeed.
 * @author Joel Dean
 * Requires an instance of the map activity and the application's current context for the progress dialog.
 *
 */

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class FN_MapsRouting extends AsyncTask<LatLng, Void, FN_MapsRoute> {
    protected ArrayList<FN_MapsRoutingListener> _aListeners;
    protected TravelMode _mTravelMode;
    public enum TravelMode {
        BIKING("biking"),
        DRIVING("driving"),
        WALKING("walking"),
        TRANSIT("transit");
        protected String _sValue;
        private TravelMode(String sValue) {
            this._sValue = sValue;
        }
        protected String getValue() {
            return _sValue;
        }
    }
    public FN_MapsRouting(TravelMode mTravelMode) {
        this._aListeners = new ArrayList<FN_MapsRoutingListener>();
        this._mTravelMode = mTravelMode;
    }
    public void registerListener(FN_MapsRoutingListener mListener) {
        _aListeners.add(mListener);
    }
    protected void dispatchOnStart() {
        for (FN_MapsRoutingListener mListener : _aListeners) {
            mListener.onRoutingStart();
        }
    }
    protected void dispatchOnFailure() {
        for (FN_MapsRoutingListener mListener : _aListeners) {
            mListener.onRoutingFailure();
        }
    }
    protected void dispatchOnSuccess(PolylineOptions mOptions, FN_MapsRoute route) {
        for (FN_MapsRoutingListener mListener : _aListeners) {
            mListener.onRoutingSuccess(mOptions, route);
        }
    }
    /**
     * Performs the call to the google maps API to acquire routing data and
     * deserializes it to a format the map can display.
     *
     * @param aPoints
     * @return
     */
    @Override
    protected FN_MapsRoute doInBackground(LatLng... aPoints) {
        for (LatLng mPoint : aPoints) {
            if (mPoint == null) return null;
        }
        return new FN_MapsGoogleParser(constructURL(aPoints)).parse();
    }
    protected String constructURL(LatLng... points) {
        LatLng start = points[0];
        LatLng dest = points[1];
        String sJsonURL = "http://maps.googleapis.com/maps/api/directions/json?";
        final StringBuffer mBuf = new StringBuffer(sJsonURL);
        mBuf.append("origin=");
        mBuf.append(start.latitude);
        mBuf.append(',');
        mBuf.append(start.longitude);
        mBuf.append("&destination=");
        mBuf.append(dest.latitude);
        mBuf.append(',');
        mBuf.append(dest.longitude);
        mBuf.append("&sensor=true&mode=");
        mBuf.append(_mTravelMode.getValue());
        return mBuf.toString();
    }
    @Override
    protected void onPreExecute() {
        dispatchOnStart();
    }
    @Override
    protected void onPostExecute(FN_MapsRoute result) {
        if (result == null) {
            dispatchOnFailure();
        } else {
            PolylineOptions mOptions = new PolylineOptions();
            for (LatLng point : result.getPoints()) {
                mOptions.add(point);
            }
            dispatchOnSuccess(mOptions, result);
        }
    }//end onPostExecute method
}