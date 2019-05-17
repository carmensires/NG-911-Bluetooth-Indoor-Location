package com.android.albert.ng911;


/*
 * This class will detect beacons and gateways using
 * the altbeacon library. It will initiate a scan and then
 * end it after "scan_period" seconds has elapsed. The results
 * of the scan will be automatically loaded into the
 * BOSSA platform after the scan has completed
 * successfully.
 * */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.support.v7.app.AppCompatActivity;
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


public class IBeaconScanner extends TimerTask implements BeaconConsumer {

    /*CONSTANT and VARIABLE DECLARATIONS*/

    //Graphics information
    private Context context = null;
    //private ProgressBar progressBar = null;

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
    private HttpTx httptx = new HttpTx();
    public static Json json = new Json();


    IBeaconScanner(Context context, double period) {
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
        }
    }


    public String getBeaconsString(){
        return beaconList.toString();
    }



    public void start(int scan_period) {
        try {
            this.scan_period = scan_period;
            this.current_time = 0;
            this.beaconManager.bind(this);
            this.timer.schedule(this, 0, Math.round(period * 1000));
            this.scan_enabled = true;
        }
        catch(Exception e) {
            stop();
        }
    }

    public boolean isFinished() {
        return finished;
    }


    public void stop() {
        timer.cancel();
        timer.purge();
        beaconManager.unbind(this);
        scan_enabled = false;
        this.finished= true;
        Log.i("carmenlog","finished");
        makeHttpRequest();
    }

    public void makeHttpRequest(){
        Log.i("carmenlog","making http request");
        Log.i("carmenlog","http req: "+getBeaconsString());

        for(IBeacon b: beaconList){
            String major = String.valueOf(b.getMajor());
            try {
                json.updateMyJsonIndoor(String.valueOf(b.getMajor()),String.valueOf(b.getMinor()),String.valueOf(b.getRssi()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            Log.i("carmenlog","http req json: "+json.readMyJson());
            String result = httptx.HttpGetRequest(json.readMyJson());
            Log.i("carmenlog[RESULT-HTTP]",result);
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
                Log.i("carmenlog","beacon: "+beacon.toString());
                beaconList.add(beacon);

            }
            Log.i("carmenlog","beacon list"+beaconList.toString());
        }
    };
}
