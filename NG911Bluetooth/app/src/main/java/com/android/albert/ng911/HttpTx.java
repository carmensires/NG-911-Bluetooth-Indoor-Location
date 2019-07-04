package com.android.albert.ng911;


import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Carmen on March 2019.
 * This class performs a HTTP get request to the API to obtain the indoor location.
 * In the URL, it sends the detected beacons in json format.
 * It uses the HttpGetRequestTask.java
 */
public class HttpTx {
    String result;
    final String HTTP_TX="HttpTx";
    public static String baseUrl = "https://api.iitrtclab.com/indoorlocation/xml?";

    public void HttpGetRequest(String baseUrl, final Context context, String json) throws JSONException {
        String url = baseUrl+json;
        try {
            result = new HttpGetRequestTask().execute(url).get();
            Log.i("AAAA " + HTTP_TX,"result: "+result);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String HttpGetRequest(String json) {
        String url = baseUrl+json;
        try {
            result = new HttpGetRequestTask().execute(url).get();
            Log.i("AAAA " + HTTP_TX,"result: "+result);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}