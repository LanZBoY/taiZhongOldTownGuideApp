package com.usrProject.taizhongoldtownguideapp.utils;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class LocationUtils {

    public static double getDistance(LatLng p1, LatLng p2){
        if(p1 == null || p2 == null){
            return 0.0f;
        }
        Double EARTH_RADIUS = 6378.137;
        Double latr1 = p1.latitude * Math.PI / 180.0;
        Double latr2 = p2.latitude * Math.PI / 180.0;
        Double lngr1 = p1.longitude * Math.PI / 180.0;
        Double lngr2 = p2.longitude * Math.PI / 180.0;
        Double a = latr1 - latr2;
        Double b = lngr1 - lngr2;
        Double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(latr1) * Math.cos(latr2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS  * 1000;
        Log.i("距離",s+"公尺");
        return s;
    }
}
