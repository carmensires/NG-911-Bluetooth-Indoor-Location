package com.android.albert.ng911;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

public class LocationHelper {

    private Context context;
    private LocationManager lm;

    /**
     * Criteria parameter will select best provider service based on:
     *      - Accuracy      - Power consumption
     *      - Response      - Monetary cost
     */
    private Criteria criteria;
    private String provider;

    public LocationHelper(Context context) {
        this.context = context;
        lm =(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();

        //set second parameter to false to get best provider
        provider = lm.getBestProvider(criteria, false);
    }

    public Location getLocation() {
        Location location = lm.getLastKnownLocation(provider);

        if(location != null) {
            return location;
        } else {
            return null;
        }
    }

}
