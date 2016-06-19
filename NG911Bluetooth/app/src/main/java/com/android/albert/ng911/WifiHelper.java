package com.android.albert.ng911;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiHelper {

    private Context context;    //application context
    private WifiManager wifimg;

    public WifiHelper(Context c) {
        this.wifimg = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        this.context = c;
    }

    /*
 * Checks if WiFi is enabled
 * If it's not, enables WiFi
 * Returns a message showing the results
 */
    public void enableWifi() {

        if(wifimg.getWifiState() == WifiManager.WIFI_STATE_ENABLED){
            Log.d("NG911 [enableWifi()]", "Wifi is enabled");
        } else {
            Log.d("NG911 [enableWifi()]", "Wifi is NOT enabled, enabling WiFi...");
            wifimg.setWifiEnabled(true);
            wifimg.startScan();
        }
    }

    public String getMacAddress() {
        String macAddress = "";

        if(wifimg.getWifiState() == WifiManager.WIFI_STATE_ENABLED){
            //Wifi is enabled, no need to enable it
            Log.d("NG911 [getMacAddress]", "Wifi is enabled");
        } else {
            //Wifi is NOT enabled, enabling WiFi...
            Log.d("NG911 [getMacAddress]", "Wifi is NOT enabled, enabling WiFi...");
            wifimg.setWifiEnabled(true);
        }

        if(wifimg.startScan()) {
            macAddress = wifimg.getConnectionInfo().getMacAddress();
        }

        return macAddress;
    }



}
