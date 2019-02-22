package com.android.albert.ng911;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.sipdroid.SipdroidApplication;

/**
 * Created by Albert on 2/11/2016.
 * Class for doing http gets to the server and capture the response.
 */
public class HttpTx {
    String result;
    public static String url = "http://nead.bramsoft.com/indexupdate.php"; // previous URL
    public static String exampleUrl = "api.iitrtclab.com/indoorlocation/xml?json[]={\"major\":1000,\"minor\":539,\"rssi\":-69}&json[]={\"major\":1000,\"minor\":577,\"rssi\":-77}&json[]={\"major\":1000,\"minor\":511,\"rssi\":-82}&json[]={\"major\":1000,\"minor\":541,\"rssi\":-81}&json[]={\"major\":1000,\"minor\":539,\"rssi\":-74}&json[]={\"major\":1000,\"minor\":539,\"rssi\":-77}&json[]={\"major\":1000,\"minor\":602,\"rssi\":-81}&json[]={\"major\":1000,\"minor\":541,\"rssi\":-79}&json[]={\"major\":1000,\"minor\":539,\"rssi\":-83}&json[]={\"major\":1000,\"minor\":602,\"rssi\":-82}&algorithim=1";
    private RequestQueue queue;

    public void HttpGetRequest(String url, final Context context, String json,final VolleyCallback callback) throws JSONException {

        //get to the server
        if (queue==null)
            queue = Volley.newRequestQueue(context.getApplicationContext());
        String urlfinal = url + "?json=" + json;
        Log.i("[NG911 HTTP Get val] ", urlfinal);
        Log.i("carmen[EXAMPLE URL] ", exampleUrl);
        StringRequest myReq = new StringRequest(Request.Method.GET, exampleUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        result = response;
                        Log.i("carmen[RESPONSE] ", response);
                       // Log.d("Response", response);
                        Data d = Data.getInstance();
                        d.setReceived(response);
                        //FileOperations op = new FileOperations(context.getApplicationContext());
                        //op.write("LocServerResponse", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Response Error HTTP GET", error + "");

                        // in case of an error, create an empty response with no location on it
                        // and send it on the INVITE
                        String response = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><presence></presence>";
                        Data d = Data.getInstance();
                        d.setReceived(response);
                       // FileOperations op = new FileOperations(context.getApplicationContext());
                       // op.write("LocServerResponse", response);

                        if (error instanceof NoConnectionError) {
                            Log.d("NoConnectionError", "NoConnectionError.......");
                            //turn wifi on if it is off
                            WifiHelper wfhelper = new WifiHelper(context);
                            String macAddress = wfhelper.getMacAddress();
                            try {
                                Thread.sleep(250);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(context, "WIFI TURNED ON: Please Call Again", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Log.d("AuthFailureError", "AuthFailureError.......");
                        } else if (error instanceof ServerError) {
                            Log.d("ServerError", "ServerError.......");
                        } else if (error instanceof NetworkError) {
                            Log.d("NetworkError", "NetworkError.......");
                        } else if (error instanceof ParseError) {
                            Log.d("ParseError", "ParseError.......");
                        } else if (error instanceof TimeoutError) {
                            Log.d("TimeoutError", "TimeoutError.......");
                        }
                    }
                }
        );
        Log.i("carmen[RESULT] ", result);
        //Set maximum timeout to support NG911 Location Provider response
        myReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,  //maximum timeout set to 10s
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        queue.add(myReq);
    }

    public void HttpGetRequest(String json) {
        RequestQueue queue = Volley.newRequestQueue(CallActivity.c);
        String urlfinal = url + "?json=" + json;
        Log.i("[NG911 HTTP Get val] ", urlfinal);
        StringRequest myReq = new StringRequest(Request.Method.GET, urlfinal,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        result = response;
                        // Log.d("Response", response);
                        Data d = Data.getInstance();
                        d.setReceived(response);
                        //FileOperations op = new FileOperations(context.getApplicationContext());
                        //op.write("LocServerResponse", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Response Error HTTP GET", error + "");

                        // in case of an error, create an empty response with no location on it
                        // and send it on the INVITE
                        String response = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><presence></presence>";
                        Data d = Data.getInstance();
                        d.setReceived(response);
                        // FileOperations op = new FileOperations(context.getApplicationContext());
                        // op.write("LocServerResponse", response);

                        if (error instanceof NoConnectionError) {
                            Log.d("NoConnectionError", "NoConnectionError.......");
                        } else if (error instanceof AuthFailureError) {
                            Log.d("AuthFailureError", "AuthFailureError.......");
                        } else if (error instanceof ServerError) {
                            Log.d("ServerError", "ServerError.......");
                        } else if (error instanceof NetworkError) {
                            Log.d("NetworkError", "NetworkError.......");
                        } else if (error instanceof ParseError) {
                            Log.d("ParseError", "ParseError.......");
                        } else if (error instanceof TimeoutError) {
                            Log.d("TimeoutError", "TimeoutError.......");
                        }
                    }
                }
        );
        //Set maximum timeout to support NG911 Location Provider response
        myReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,  //maximum timeout set to 10s
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        queue.add(myReq);
    }
}