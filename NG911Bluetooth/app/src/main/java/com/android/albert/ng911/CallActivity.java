package com.android.albert.ng911;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import org.sipdroid.sipua.ui.Sipdroid;
import org.zoolu.tools.Assert;

/**
 * Created by Albert on 3/30/2016.
 * Core of the app. The call activity will use the cellphone Bluetooth controller for
 * gathering iBeacon identifiers and its signal strength. This information will be sent
 * as HTTP GET to the Location Server (Where the location database will contain the location of the sensors and identifiers)
 */
public class CallActivity extends AppCompatActivity {

    public static Json json;
    public static boolean sended;
    public static String url = "https://api.iitrtclab.com/indoorlocation/xml?";
    public static Context c;
    //If this splash time is reduced the app won't have time to execute the http get and have the xml as response
    private static final int SPLASH_DISPLAY_TIME = 8000;

    private IBeaconScanner testCase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  setContentView(R.layout.activity_call);
        setContentView(R.layout.activity_splash);
        sended=false;

        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent();
                intent.setClass(CallActivity.this, Sipdroid.class);
                CallActivity.this.startActivity(intent);
                CallActivity.this.finish();
            }
        }, SPLASH_DISPLAY_TIME);

        try {
            //Create bluetooth scanner
            testCase = new IBeaconScanner(this, .5);
            testCase.start(5);
        }
        catch(Exception e) {
            finish();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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