package com.android.albert.ng911;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Albert on 1/30/2016.
 * Creation of the JSON format for the location server
 * [{"major":"xx","minor":"xx","rss":"xx"},{"major":"xy","minor":"xy","rss":"xy”}]
 */
public class Json {
    /**
     * @param Uuid
     * @param Major
     * @param Minor
     * @param Rssi
     * @return
     */
    String Uuid;
    String Major;
    String Minor;
    String Rssi;
    JSONObject jsonOb;
    JSONObject jsonObOutdoor;
    JSONArray beaconsJson;
    JSONObject main;

   Json(String Uuid, String Major, String Minor, String Rssi){
       this.Uuid=Uuid;
       this.Major=Major;
       this.Minor=Minor;
       this.Rssi=Rssi;
       jsonOb = new JSONObject();
       jsonObOutdoor = new JSONObject();
       beaconsJson = new JSONArray();
       main=new JSONObject();
    }

    public Json() {
        jsonOb = new JSONObject();
        jsonObOutdoor = new JSONObject();
        beaconsJson = new JSONArray();
        main=new JSONObject();
    }

    public void createMyJsonIndoor() throws JSONException {
        jsonOb.put("major", Integer.parseInt(Major));
        jsonOb.put("minor", Integer.parseInt(Minor));
        jsonOb.put("rssi:", Integer.parseInt(Rssi));
        beaconsJson.put(jsonOb);
        //main.put("location",beaconsJson);
    }
    public void updateMyJsonIndoor( String Major, String Minor, String Rssi) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("major", Integer.parseInt(Major));
        json.put("minor", Integer.parseInt(Minor));
        json.put("rssi:", Integer.parseInt(Rssi));
        beaconsJson.put(json);
        //main.put("location",beaconsJson);//
    }

    public void createMyJsonOutdoor(Context c) throws JSONException {
        LocationHelper location=new LocationHelper(c);
        jsonObOutdoor.put("Lat", location.getLocation().getLatitude());
        jsonObOutdoor.put("Long", location.getLocation().getLongitude());
        beaconsJson.put(jsonObOutdoor);
        //main.put("location",beaconsJson);
    }

    public String readMyJson()throws JSONException {
        return beaconsJson.toString();
        //return main.toString();
    }

}
