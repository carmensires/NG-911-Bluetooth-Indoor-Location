package com.android.albert.ng911;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpGetRequestTask extends AsyncTask<String,Integer,String> {

    @Override
    protected String doInBackground(String... params) {
        String my_url = params[0];
        try {
            URL url = new URL(my_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            // setting the  Request Method Type
            httpURLConnection.setRequestMethod("GET");
            try{
                httpURLConnection.connect();
                BufferedReader rd = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String content = "", line;
                while ((line = rd.readLine()) != null) {
                    content += line + "\n";
                }
                // this is used for just in case we don't know about the data size associated with our request
                //httpURLConnection.setChunkedStreamingMode(0);

                // to log the response code of the request
                Log.d("carmenlog[CODE]", "MyHttpRequestTask doInBackground : " +httpURLConnection.getResponseCode());
                // to log the response message from your server after you have tried the request.
                Log.d("carmenlog[MESSAGE]", "MyHttpRequestTask doInBackground : " +httpURLConnection.getResponseMessage());
                Log.d("carmenlog[CONTENT]",content);
                return content;

            }catch (Exception e){
                Log.d("carmenlog[CODE]", "MyHttpRequestTask doInBackground : " +httpURLConnection.getResponseCode());
                Log.d("carmenlog[ERROR]", "MyHttpRequestTask doInBackground : " +httpURLConnection.getErrorStream());
                e.printStackTrace();
            }finally {
                // this is done so that there are no open connections left when this task is going to complete
                httpURLConnection.disconnect();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    protected void onPostExecute(Long result) {
        Log.d("carmenlog[ONPOSTXECUTE]",""+result);
    }
}