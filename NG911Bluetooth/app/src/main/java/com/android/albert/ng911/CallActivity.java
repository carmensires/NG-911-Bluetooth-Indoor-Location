package com.android.albert.ng911;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import org.sipdroid.sipua.ui.Sipdroid;

/**
 * Created by Albert on 3/30/2016.
 * Modified by Carmen on April 2019
 * The call activity create a bluetooth scanner to scan the beacons in the building,
 * while showing a splash screen. After the splash display time it will open the sipdroid activity
 */
public class CallActivity extends AppCompatActivity {

    public static Json json;
    public static boolean sended;
    final String CALL_ACTIVITY="CallActivity";
    public static String url = "https://api.iitrtclab.com/indoorlocation/xml?";
    public static Context c;
    //If this splash time is reduced the app won't have time to execute the http get and have the xml as response
    private static final int SPLASH_DISPLAY_TIME = 8000;
    private IBeaconScanner bluetoothScanner;
    public double period = .5;
    public int scan_period = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sended=false;

        //When the Splash time finishes, it will open the Sipdroid Activity
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Toast.makeText(CallActivity.this,getResources().getString(R.string.initializing_SIP_client),Toast.LENGTH_LONG);
                Intent intent = new Intent();
                intent.setClass(CallActivity.this, Sipdroid.class);
                CallActivity.this.startActivity(intent);
                CallActivity.this.finish();
            }
        }, SPLASH_DISPLAY_TIME);

        try {
            //Create bluetooth scanner
            Log.i("AAAA " + CALL_ACTIVITY,"Creating bluetooth scanner. Period: "+period+", scan period: "+scan_period);
            bluetoothScanner = new IBeaconScanner(this, period);
            bluetoothScanner.start(scan_period);
        }
        catch(Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //noinspection SimplifiableIfStatement
            case R.id.action_settings:
                Intent myIntent = new Intent(this, org.sipdroid.sipua.ui.Settings.class);
                startActivity(myIntent);
                return true;
            // For going back to home. Handle back button toolbar
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}