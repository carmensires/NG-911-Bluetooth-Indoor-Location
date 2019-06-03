package com.android.albert.ng911;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Albert on 1/30/2016.
 * Modified by Carmen on April 2019
 * Creation of the JSON format for the location server
 * Example: json[]={"major":1000,"minor":575,"rssi":-91}
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
        jsonOb.put("rssi", Integer.parseInt(Rssi));
        beaconsJson.put(jsonOb);
        //main.put("location",beaconsJson);
    }
    public void updateMyJsonIndoor( String Major, String Minor, String Rssi) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("major", Integer.parseInt(Major));
        json.put("minor", Integer.parseInt(Minor));
        json.put("rssi", Integer.parseInt(Rssi));
        beaconsJson.put(json);
    }

    public void createMyJsonOutdoor(Context c) throws JSONException {
        LocationHelper location=new LocationHelper(c);
        jsonObOutdoor.put("Lat", location.getLocation().getLatitude());
        jsonObOutdoor.put("Long", location.getLocation().getLongitude());
        beaconsJson.put(jsonObOutdoor);
    }

    public String readMyJson()throws JSONException {
        String jsonStr = "";
        JSONObject json_i;
        for(int i=0;i<beaconsJson.length();i++){
            json_i = beaconsJson.getJSONObject(i);
            jsonStr+="json[]="+json_i.toString()+"&";
        }
        jsonStr+="algorithim=1";

        //To perform tests while not in SB, use this example URL
        String jsonStrExample = "json[]={\"major\":1000,\"minor\":575,\"rssi\":-91}&json[]={\"major\":1000,\"minor\":539,\"rssi\":-84}&json[]={\"major\":1000,\"minor\":515,\"rssi\":-91}&json[]={\"major\":1000,\"minor\":575,\"rssi\":-94}&json[]={\"major\":1000,\"minor\":515,\"rssi\":-91}&json[]={\"major\":1000,\"minor\":515,\"rssi\":-92}&json[]={\"major\":1000,\"minor\":552,\"rssi\":-85}&algorithim=1";
        return jsonStrExample;
        //return jsonStr;
    }
}
