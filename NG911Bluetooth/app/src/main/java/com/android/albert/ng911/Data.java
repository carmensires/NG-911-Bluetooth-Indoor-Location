package com.android.albert.ng911;

/**
 * Created by Albert on 4/8/2016.
 */
public class Data {

    private String captured,b;
    final static Data data= new Data( );

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
    }

    public void deleteReceived()
    {
        this.captured=null;
    }
}
