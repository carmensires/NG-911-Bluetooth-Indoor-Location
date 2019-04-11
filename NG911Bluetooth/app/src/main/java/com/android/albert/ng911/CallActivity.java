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
public class CallActivity extends AppCompatActivity implements BeaconConsumer {
    //public static final int MAX = 500;
    public static final String FILTERMAJOR="101";
    protected static final String CALL = "[NG911 BL]";
    BeaconManager beaconManager;
    private boolean detected = false;
    private ArrayList<Integer> rssi;
    private int numBeacons = 0;
    private ArrayList<Identifier> major, minor;
    private ArrayList<Identifier> uuid;
    public static Json json;
    long firstTime;
    RequestQueue queue;
    public static boolean sended;
    public static String prevUrl = "http://nead.bramsoft.com/indexupdate.php"; // previous URL
    public static String url = "https://api.iitrtclab.com/indoorlocation/xml?";
    HttpTx httptx;
    public static Context c;
    //LocationHelper outdoorLocation;
    BluetoothChecker bluetooth;
    //If this splash time is reduced the app won't have time to execute the http get and have the xml as response
    private static final int SPLASH_DISPLAY_TIME = 8000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  setContentView(R.layout.activity_call);
        setContentView(R.layout.activity_splash);
        sended=false;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (!sended){ //if (!sended && numBeacons>1){ Delete the numBeacons>1 condition to make tests when there are no beacons
                    try {
                        httptx.HttpGetRequest(url, getApplicationContext(), json.readMyJson(), new VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {
                                // Data d = Data.getInstance();
                                // d.setReceived(result);
                            }
                        });
                        //wait to get server response
                        try {
                            Thread.sleep(2500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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
        rssi = new ArrayList<>();
        major = new ArrayList<>();
        minor = new ArrayList<>();
        uuid = new ArrayList<>();
        json = new Json();
        //Declaration of the volley que for receiving the http response
        queue = Volley.newRequestQueue(this);

        //Server outdoor location method
       // outdoorLocation=new LocationHelper(this);
        c=getApplicationContext();

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
            //beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", Identifier.parse("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6"), Identifier.parse("1"), null));
            //above filters for uuid and major =1
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
                    Identifier majorp = beacon.getId2();
                    //filter our hardcoded major, we only want to see our beacons identifiers
                    if(FILTERMAJOR.equals(majorp.toString())) {
                        // Log.i(CALL,"The beacon " + beacon.toString() + " is about " + beacon.getDistance() + " meters away.");
                        rssi.add(beacon.getRssi());
                        major.add(majorp);
                        minor.add(beacon.getId3());
                        uuid.add(beacon.getId1());
                        numBeacons++;
                        if (numBeacons == 1) {
                            Log.i(CALL, "The beacon " + beacon.toString() + " (major: " + beacon.getId2() + ", minor: " + beacon.getId3() + ").");
                            firstTime = System.currentTimeMillis();
                            json = new Json(uuid.toString(), major.get(numBeacons - 1).toString(), minor.get(numBeacons - 1).toString(), Integer.toString(rssi.get(numBeacons - 1)));
                            Data d = Data.getInstance();
                            try {
                                d.setJson(json.readMyJson());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //json object for major,minor and rssid
                            try {
                                json.createMyJsonIndoor();
                                Log.i(CALL, "This is the 1st created JSON: " + json.readMyJson());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //time less than 4.5 seconds and num of captures less than 6
                        } else if ((System.currentTimeMillis() - firstTime < 4500) && numBeacons < 6) {
                            try {
                                //Update JSON with next values
                                json.updateMyJsonIndoor(major.get(numBeacons - 1).toString(), minor.get(numBeacons - 1).toString(), Integer.toString(rssi.get(numBeacons - 1)));
                                Data d = Data.getInstance();
                                d.setJson(json.readMyJson());
                                Log.i("[NG911 BL]", "JSON Object " + json.readMyJson());
                                Log.i("[NG911 BL]", "Waiting to gather more sensor data for transmission... ");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (!sended) { //If haven't sended the captured beacons to the Location server then do the following
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
                            //numBeacons=0;
                        } else {
                            try {
                                try {
                                    Thread.sleep(150);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                json.updateMyJsonIndoor(major.get(numBeacons - 1).toString(), minor.get(numBeacons - 1).toString(), Integer.toString(rssi.get(numBeacons - 1)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

    }
}