package com.android.albert.ng911;


import android.util.Log;

/**
 * Created by Albert on 4/8/2016.
 * Class for interacting with data
 */
public class Data {

    public String captured,json;
    public final static Data data= new Data( );

    public Data(){ }

    /* Static 'instance' method */
    public static Data getInstance( ) {
        return data;
    }

    public String getReceived()
    {
        return this.captured;
    }

    public void setReceived(String a)
    {
        this.captured = a;
        Log.i("carmenlog[DATA]","received: "+a);
    }

    public String getJson()
    {
        return this.json;
    }

    public void setJson(String b)
    {
        this.json = b;
    }

    public void deleteReceived()
    {
        this.captured=null;
    }

    public void clear() {
        this.captured="";
    }
}
