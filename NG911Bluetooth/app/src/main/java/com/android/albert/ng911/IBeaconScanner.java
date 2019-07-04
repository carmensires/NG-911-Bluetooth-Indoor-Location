package com.android.albert.ng911;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONException;

/*
 * Created by Carmen on April 2019.
 * This class detects beacons and gateways using
 * the altbeacon library. It will start a scan and then
 * end it after "scan_period" seconds has elapsed.
 * When it has finished, it will perform a HTTP get request
 * to the bossa platform, to obtain the indoor location
 * and store this information in the Data class.
 * */

public class IBeaconScanner extends TimerTask implements BeaconConsumer {

    private Context context = null;
    final String IBEACON_SCANNER="IBeaconScanner";

    //Beacon scanning information
    public  final static String IBEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private final static String targetUUID = "fda50693-a4e2-4fb1-afcf-c6eb07647825";
    private final static String gateUUID = "A0DF207C-142F-4A39-A457-6FC44D524C04";
    private BeaconManager beaconManager;
    boolean scan_enabled = false;

    //Scan timing information
    private Timer timer = null;         //The timer that will scan for BLE signals
    private double period = .5;         //How many seconds until timer rescans for signals
    private double current_time = 0;    //How many seconds have elapsed since timer started
    private int scan_period = 5;        //How many seconds the timer should last

    //Test case information
    public static  volatile  LinkedList <IBeacon> beaconList;
    private boolean finished;

    //HTTP Request information
    private HttpTx httptx = new HttpTx();
    public static Json json = new Json();

    //The constructor requires the context and the period between scans
    IBeaconScanner(Context context, double period) {
        Log.i("AAAA " + IBEACON_SCANNER,"Period: "+period+" seconds");
        try {
            this.period = period;
            this.context = context;
            this.timer = new Timer();
            this.beaconList = new LinkedList<IBeacon>();
            this.beaconManager = BeaconManager.getInstanceForApplication(context);
            this.beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON_FORMAT));
            this.beaconManager.setForegroundScanPeriod(Math.round(period*1000));
            this.finished = false;
        }
        catch(Exception e) {
            ((Activity)context).finish();
            e.printStackTrace();
        }
    }

    //Returns the list of beacons
    public String getBeaconsString(){
        Log.i("AAAA " + IBEACON_SCANNER,"getBeaconsString: "+beaconList.toString());
        return beaconList.toString();
    }

    //Starts the scanning during the established scan_period
    public void start(int scan_period) {
        Log.i("AAAA " + IBEACON_SCANNER,"start scanning for "+scan_period+" seconds");
        try {
            this.scan_period = scan_period;
            this.current_time = 0;
            this.beaconManager.bind(this);
            this.timer.schedule(this, 0, Math.round(period * 1000));
            this.scan_enabled = true;
        }
        catch(Exception e) {
            e.printStackTrace();
            stop();
        }
    }

    public boolean isFinished() {
        return finished;
    }

    //This is called when the scanning stops
    public void stop() {
        Log.i("AAAA " + IBEACON_SCANNER,"Scanning stopped");
        timer.cancel();
        timer.purge();
        beaconManager.unbind(this);
        scan_enabled = false;
        this.finished= true;
        makeHttpRequest();
    }

    //Makes an HTTP get request to obtain the indoor location
    public void makeHttpRequest(){
        Log.i("AAAA " + IBEACON_SCANNER,"making HTTP get request");
        for(IBeacon b: beaconList){
            try {
                json.updateMyJsonIndoor(String.valueOf(b.getMajor()),String.valueOf(b.getMinor()),String.valueOf(b.getRssi()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            Log.i("AAAA " + IBEACON_SCANNER,"JSON: "+json.readMyJson());
            String result = httptx.HttpGetRequest(json.readMyJson());
            Log.i("AAAA " + IBEACON_SCANNER,"Result: "+result);
            Data d = Data.getInstance();
            d.setReceived(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void finish() {
        ((Activity)context).finish();
    }

    @Override
    public void run() {
        if(current_time >= scan_period) {
            stop();
            return;
        }
        current_time += period;
    }


    @Override
    public void onBeaconServiceConnect() {
        try {
            beaconManager.removeAllRangeNotifiers();
            beaconManager.addRangeNotifier(beaconCB);
            beaconManager.startRangingBeaconsInRegion(new Region(targetUUID, Identifier.parse(targetUUID), null, null));
            beaconManager.startRangingBeaconsInRegion(new Region(gateUUID, Identifier.parse(gateUUID),null,null));
        }
        catch (Exception e){
            e.printStackTrace();
            stop();
        }
    }

    @Override
    public Context getApplicationContext() {
        return context;
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        context.unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return context.bindService(intent,serviceConnection,i);
    }


    private final RangeNotifier beaconCB = new RangeNotifier() {
        @Override
        public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
            if(!scan_enabled)
                return;
            for(Beacon x:beacons){
                IBeacon beacon = new IBeacon(x.getRssi(), x.getId2().toInt(),x.getId3().toInt(),x.getId1().toString());
                Log.i(IBEACON_SCANNER,"beacon detected: "+beacon.toString());
                beaconList.add(beacon);

            }
            Log.i(IBEACON_SCANNER,"beacon list: "+beaconList.toString());
        }
    };
}
