package com.android.albert.ng911;

import android.util.Log;

/**
 * Created by Albert on 4/8/2016.
 * Modified by Carmen on April 2019
 * Class for interacting with data
 */
public class Data {

    public String captured,json;
    public final static Data data= new Data( );
    final String DATA="Data";


    public Data(){ }

    /* Static 'instance' method */
    public static Data getInstance( ) {
        return data;
    }

    public String getReceived()
    {
        return this.captured;
    }

    public void setReceived(String captured)
    {
        this.captured = captured;
        Log.i(DATA,"received data: "+captured);
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
