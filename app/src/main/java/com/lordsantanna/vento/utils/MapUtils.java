package com.lordsantanna.vento.utils;

import android.content.Context;

import com.lordsantanna.vento.R;
import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Created by Carlos on 18/03/2018.
 */

public class MapUtils {

    public static String staticMapURL(LatLng latLng, float zoom, Context context){
        return "https://api.mapbox.com/styles/v1/mapbox/dark-v9/static/pin-s-circle+f44("+latLng.getLongitude()+","+latLng.getLatitude()+")/"+latLng.getLongitude()+","+latLng.getLatitude()+","+ Float.toString(zoom)+",0,0/400x300@2x?access_token=" + context.getResources().getString(R.string.mapbox_key);
    }
}
