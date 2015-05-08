package com.bcp.DFA;

import com.google.android.gms.maps.model.PolylineOptions;

public interface FN_MapsRoutingListener {
    public void onRoutingFailure();

    public void onRoutingStart();

    public void onRoutingSuccess(PolylineOptions mPolyOptions, FN_MapsRoute route);
}
