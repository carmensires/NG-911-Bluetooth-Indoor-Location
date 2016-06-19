package com.android.albert.ng911;

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
import java.util.Collection;
import org.sipdroid.sipua.ui.Sipdroid;

/**
 * Created by Albert on 3/30/2016.
 * Core of the app. The call activity will use the cellphone Bluetooth controller for
 * gathering iBeacon identifiers and its signal strength. This information will be sent
 * as HTTP GET to the Location Server (Where the location database will contain the location of the sensors and identifiers)
 */
public class CallActivity extends AppCompatActivity implements BeaconConsumer {
    public static final int MAX = 20;
    protected static final String CALL = "[NG911 BL]";
    BeaconManager beaconManager;
    private boolean detected = false;
    private int[] rssi;
    private int numBeacons = 0;
    private Identifier[] major, minor;
    private Identifier[] uuid;
    Json json;
    long firstTime;
    RequestQueue queue;
    private boolean sended = false;
    String url = "http://nead.bramsoft.com/index.php";//ip of the location server (will respond with XML file)
    HttpTx httptx;
    //LocationHelper outdoorLocation;
    BluetoothChecker bluetooth;
    //If this splash time is reduced the app won't have time to execute the http get and have the xml as response
    private static final int SPLASH_DISPLAY_TIME = 7500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  setContentView(R.layout.activity_call);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent();
                intent.setClass(CallActivity.this, Sipdroid.class);
                CallActivity.this.startActivity(intent);
                CallActivity.this.finish();
            }
        }, SPLASH_DISPLAY_TIME);
        //Manager for bluetooth beacons
        beaconManager = BeaconManager.getInstanceForApplication(this);
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        //Binds an Android Activity sipdroid to the BeaconService.
        beaconManager.bind(this);
        //Blutooth ibeacons available parameters
        rssi = new int[MAX];
        major = new Identifier[MAX];
        minor = new Identifier[MAX];
        uuid = new Identifier[MAX];
        json = new Json();
        //Declaration of the volley que for receiving the http response
        queue = Volley.newRequestQueue(this);

        //Server outdoor location method
       // outdoorLocation=new LocationHelper(this);

        //Bluetooth turn on
        bluetooth=new BluetoothChecker(getApplicationContext());
        bluetooth.enableBluetooth();
        
        httptx=new HttpTx();
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
                beaconManager.unbind(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        //setRangeNotifier specifies a class that should be called each time the BeaconService gets ranging data, which is nominally once per second when beacons are detected.

        beaconManager.setRangeNotifier(new CustomRangeNotifier());
        beaconManager.setMonitorNotifier(new CustomMonitorNotifier());

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }
    
    private class CustomMonitorNotifier implements MonitorNotifier {

        @Override
        public void didEnterRegion(Region region) {
            Log.i(CALL, "I just saw a beacon for the first time!");
            detected = true;
        }

        @Override
        public void didExitRegion(Region region) {
            Log.i(CALL, "I no longer see a beacon");
            detected = false;
        }

        @Override
        public void didDetermineStateForRegion(int state, Region region) {
            Log.i(CALL, "I have just switched from seeing/not seeing beacons: " + state);
        }
    }

    private class CustomRangeNotifier implements RangeNotifier {
        @Override
        public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
            if (beacons.size() > 0) {
                for (Beacon beacon : beacons) {
                    Log.i(CALL,"The beacon " + beacon.toString() + " is about " + beacon.getDistance() + " meters away.");
                    rssi[numBeacons] = beacon.getRssi();
                    major[numBeacons] = beacon.getId2();
                    minor[numBeacons] = beacon.getId3();
                    uuid[numBeacons] = beacon.getId1();
                    numBeacons++;
                    if(numBeacons==1){
                        Log.i(CALL, "The beacon " + beacon.toString() + " (major: " + beacon.getId2() + ", minor: " + beacon.getId3() + ").");
                        firstTime = System.currentTimeMillis();
                        json = new Json(uuid.toString(), major[numBeacons-1].toString(), minor[numBeacons-1].toString(), Integer.toString(rssi[numBeacons-1]));
                        //json object for major,minor and rssid
                        try {
                            json.createMyJsonIndoor();
                            Log.i(CALL, "This is the 1st created JSON: "+json.readMyJson());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else if ((System.currentTimeMillis()-firstTime<4500) && numBeacons<5) {
                        try {
                            //Update JSON with next values
                            json.updateMyJsonIndoor(major[numBeacons-1].toString(), minor[numBeacons-1].toString(), Integer.toString(rssi[numBeacons-1]));

                            Log.i("[NG911 BL]", "JSON Object " + json.readMyJson());
                            Log.i("[NG911 BL]", "Waiting to gather more sensor data for transmission... ");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if (!sended){ //If haven't sended the captured beacons to the Location server then do the following
                            Log.i(CALL, "HTTP GET transmission to " + url);
                            try {
                                //Sending Get request to the server
                                httptx.HttpGetRequest(url, getApplicationContext(), json.readMyJson(), new VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                       // Data d = Data.getInstance();
                                       // d.setReceived(result);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            sended = true;
                            numBeacons=0;
                        }else {
                        numBeacons=0;
                    }
                }
            }
        }
    }
}