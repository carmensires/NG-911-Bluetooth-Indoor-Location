package com.android.albert.ng911;



import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;

/**
 * Created by Albert on 2/11/2016.
 * Class for doing http gets to the server and capture the response.
 */
public class HttpTx {
    String result;
    public static String url = "https://api.iitrtclab.com/indoorlocation/xml?";
    public static String exampleUrl = "https://api.iitrtclab.com/indoorlocation/xml?json[]={\"major\":1000,\"minor\":539,\"rssi\":-69}&json[]={\"major\":1000,\"minor\":577,\"rssi\":-77}&json[]={\"major\":1000,\"minor\":511,\"rssi\":-82}&json[]={\"major\":1000,\"minor\":541,\"rssi\":-81}&json[]={\"major\":1000,\"minor\":539,\"rssi\":-74}&json[]={\"major\":1000,\"minor\":539,\"rssi\":-77}&json[]={\"major\":1000,\"minor\":602,\"rssi\":-81}&json[]={\"major\":1000,\"minor\":541,\"rssi\":-79}&json[]={\"major\":1000,\"minor\":539,\"rssi\":-83}&json[]={\"major\":1000,\"minor\":602,\"rssi\":-82}&algorithim=1";

    public void HttpGetRequest(String url, final Context context, String json) throws JSONException {

        String urlfinal = url+json;

        try {
            Log.d("carmenlog[HTTP]","trying http get request");
            result = new HttpGetRequestTask().execute(urlfinal).get();
            Log.d("carmenlog[HTTP]","done http get request");
            Log.d("carmenlog[HTTP]","result: "+result);
        } catch (ExecutionException e) {
            Log.d("carmenlog[ERR exec]",e.toString());
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.d("carmenlog[ERR inter]",e.toString());
            e.printStackTrace();
        }
    }

    public String HttpGetRequest(String json) {

        String urlfinal = url+json;

        try {
            Log.d("carmenlog[INFO]","trying http get request");
            result = new HttpGetRequestTask().execute(urlfinal).get();
            Log.d("carmenlog[INFO]","done http get request");
            Log.d("carmenlog[INFO]","result: "+result);
        } catch (ExecutionException e) {
            Log.d("carmenlog[ERROR exec]",e.toString());
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.d("carmenlog[ERROR inter]",e.toString());
            e.printStackTrace();
        }

        return result;

    }
}