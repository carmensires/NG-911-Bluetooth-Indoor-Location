package com.android.albert.ng911;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import org.altbeacon.beacon.service.ArmaRssiFilter;
import org.json.JSONException;
import org.sipdroid.sipua.ui.Sipdroid;

import java.text.DecimalFormat;
import java.util.Collection;

/**
 * Created by Albert on 3/30/2016.
 * Activity for testing the iBeacons Status (identifiers and power)
 */
public class status extends AppCompatActivity implements BeaconConsumer {

    public static final int MAX = 5;
    protected static final String STATUS = "[NG911 Status]";
    private BeaconManager beaconManager;
    private TextView textNumBeacons;
    private TextView textRSSI;
    private TextView textMajor;
    private TextView textMinor;
    private TextView textUuid;
    private TextView textDistance;
    private Button startButton;
    private TextView textFound;
    private boolean detected = false;
    private int[] rssi;
    private Double[] distance;
    private int numBeacons = 0;
    private int numBeaconstotal;
    private Identifier[] major, minor, uuid;
    DecimalFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        //toolbar design
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarStatus);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ng911icon2);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //Manager for bluetooth beacons
        beaconManager = BeaconManager.getInstanceForApplication(this);
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        /*This signifies that the beacon type will be decoded when an advertisement is found with 0x0215 in bytes 2-3, and a three-part identifier will be pulled out of bytes 4-19,
         bytes 20-21 and bytes 22-23, respectively. A signed power calibration value will be pulled out of byte 24, and a data field will be pulled out of byte 25.
          m - matching byte sequence for this beacon type to parse (exactly one required)
          s - ServiceUuid for this beacon type to parse (optional, only for Gatt-based beacons)
          i - identifier (at least one required, multiple allowed)
          p - power calibration field (exactly one required)...*/
        beaconManager.bind(this);
        beaconManager.setRssiFilterImplClass(ArmaRssiFilter.class);
        rssi = new int[MAX];
        distance = new Double[MAX];
        major = new Identifier[MAX];
        minor = new Identifier[MAX];
        uuid = new Identifier[MAX];

        numBeaconstotal=MAX;

        //views and buttons create
        bindViews();
        bindButtons();

        df = new DecimalFormat("##.00");

        final Handler h = new Handler();
        final int delay = 1000; //Do handler every 1 second

        h.postDelayed(new Runnable(){
            public void run(){
                if (detected) {
                    try {
                        textRSSI.setText(Integer.toString(rssi[numBeacons]));
                        textFound.setText("Yes!");
                        textMajor.setText(major[numBeacons].toString());
                        textMinor.setText(minor[numBeacons].toString());
                        textUuid.setText(uuid[numBeacons].toString());
                        numBeaconstotal++;
                        textNumBeacons.setText(Integer.toString(numBeaconstotal));
                    } catch (NullPointerException e) {
                        try {
                            //get values from previous capture
                            textRSSI.setText(Integer.toString(rssi[MAX-2]));
                            textFound.setText("Yes!");
                            textMajor.setText(major[MAX-2].toString());
                            textMinor.setText(minor[MAX-2].toString());
                            textUuid.setText(uuid[MAX-2].toString());
                        } catch (NullPointerException e2) {
                        Log.i("Bluetooth", "Data not available yet...");
                        textMajor.setText("N/A");
                        textMinor.setText("N/A");
                        textUuid.setText("N/A");}
                    }

                } else {
                    textFound.setText("No");
                    textMajor.setText("N/A");
                    textMinor.setText("N/A");
                    textUuid.setText("N/A");
                    textRSSI.setText("N/A");
                    textNumBeacons.setText("0");
                }
                h.postDelayed(this, delay);
            }
        }, delay);

        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
                beaconManager.unbind(this);
                NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
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

    private void bindViews() {
        textMajor = (TextView) findViewById(R.id.majorTextView);
        textMinor = (TextView) findViewById(R.id.minorTextView);
        textFound = (TextView) findViewById(R.id.textViewFound);
        textRSSI = (TextView) findViewById(R.id.rssiTextView);
        textUuid = (TextView) findViewById(R.id.uuidTextView);
        textNumBeacons = (TextView) findViewById(R.id.numBeacTextView);
        startButton = (Button) findViewById(R.id.lastButton);
    }

    private void bindButtons() {
        startButton.setOnClickListener(new StartOnClickListener());
    }

    private class StartOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //OnClick for checking the las value infoActivity through the app.
            if (detected) {
                textRSSI.setText(Integer.toString(rssi[numBeacons]));
                textFound.setText("Yes!");
                try {
                    textMajor.setText(major[numBeacons].toString());
                    textMinor.setText(minor[numBeacons].toString());
                    textUuid.setText(uuid[numBeacons].toString());
                } catch (NullPointerException e) {
                    try {
                        //get values from previous capture
                        textRSSI.setText(Integer.toString(rssi[MAX-2]));
                        textFound.setText("Yes!");
                        textMajor.setText(major[MAX-2].toString());
                        textMinor.setText(minor[MAX-2].toString());
                        textUuid.setText(uuid[MAX-2].toString());
                    } catch (NullPointerException e2) {
                        Log.i("Bluetooth", "Data not available yet...");
                    textMajor.setText("N/A");
                    textMinor.setText("N/A");
                    textUuid.setText("N/A");
                }}
                textNumBeacons.setText(Integer.toString(numBeacons));
            } else {
                textFound.setText("No");
                textMajor.setText("N/A");
                textMinor.setText("N/A");
                textUuid.setText("N/A");
                textRSSI.setText("N/A");
                textNumBeacons.setText("0");
            }
        }
    }

    private class CustomMonitorNotifier implements MonitorNotifier {
        @Override
        public void didEnterRegion(Region region) {
            Log.i(STATUS, "I just saw a beacon for the first time!");
            detected = true;
        }

        @Override
        public void didExitRegion(Region region) {
            Log.i(STATUS, "I no longer see a beacon");
            detected = false;
        }

        @Override
        public void didDetermineStateForRegion(int state, Region region) {
            Log.i(STATUS, "I have just switched from seeing/not seeing beacons: " + state);
        }
    }

    private class CustomRangeNotifier implements RangeNotifier {
        @Override
        public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
            if (beacons.size() > 0) {
                for (Beacon lastBeacon : beacons) {
                    detected=true;
                    //capture Max-1 BEACONS AROUND
                    if (numBeacons < MAX-1) {
                        Log.i("[NG911 Status]", "The beacon " + lastBeacon.toString() + " is about " + df.format(lastBeacon.getDistance()) + " meters away.");
                        rssi[numBeacons] = lastBeacon.getRssi();
                        major[numBeacons] = lastBeacon.getId2();
                        minor[numBeacons] = lastBeacon.getId3();
                        uuid[numBeacons] = lastBeacon.getId1();
                        distance[numBeacons] = Double.parseDouble(df.format(lastBeacon.getDistance()));
                        numBeacons++;
                        Log.i(STATUS, "The beacon " + lastBeacon.toString() + " RSSI: " + lastBeacon.getRssi() + " (major: " + lastBeacon.getId2() + ", minor: " + lastBeacon.getId3() + ").");

                    } else {
                        numBeacons = 0;
                    }
                }
            }
        }
    }
}



