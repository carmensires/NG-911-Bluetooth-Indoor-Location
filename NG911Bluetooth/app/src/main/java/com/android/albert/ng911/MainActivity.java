package com.android.albert.ng911;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Profile;


/**
 * Created by Albert on 1/24/2016.
 * Modified by Carmen on April 2019
 * Main activity with the 3 buttons and the settings option. Turns on the Bluetooth for having the app ready to make an emergency call.
 */
public class MainActivity extends AppCompatActivity {
    final String MAIN_ACTIVITY="[NG 911] MainActivity";
    public static final int MAX = 50;
    BluetoothChecker bluetooth;
    TextView textFname;
    Button callButton,statusButton,infoButton;
    private boolean detected = false;
    TextView nameView;
    public static Activity fa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ng911icon2);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //Bluetooth turn on
        bluetooth=new BluetoothChecker(getApplicationContext());
        bluetooth.enableBluetooth();
        //turn wifi on
        WifiHelper wfhelper = new WifiHelper(this);
        wfhelper.enableWifi();
        //create views
        bindViews();
        bindButtons();

        fa=this;

        //Show first name of the user
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String strFName = SP.getString("fname", "");
        nameView = (TextView) findViewById(R.id.firstname);
        //if registered get from facebook account
        try {
            if (Profile.getCurrentProfile().getFirstName()!="")
                strFName=Profile.getCurrentProfile().getFirstName();
        }catch(Exception e){System.out.println(e);}
        nameView.setText(strFName);
        permissionsCheck();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void permissionsCheck() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            finish();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH},
                    1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_ADMIN},
                    2);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    3);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    4);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    5);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALL_LOG},
                    6);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALL_LOG},
                    7);
        }
    }


    private void bindViews() {
        textFname = (TextView) findViewById(R.id.firstname);
        callButton = (Button) findViewById(R.id.callButton);
        statusButton = (Button) findViewById(R.id.statusButton);
        infoButton = (Button) findViewById(R.id.infoButton);
    }

    private void bindButtons() {
        callButton.setOnClickListener(new StartOnClickListener());
        statusButton.setOnClickListener(new StartOnClickListener());
        infoButton.setOnClickListener(new StartOnClickListener());
    }

    public class StartOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.callButton:
                        Intent intent = new Intent(v.getContext(), CallActivity.class);
                        startActivity(intent);
                        Log.i(MAIN_ACTIVITY,"Call button pressed");
                        break;

                    case R.id.statusButton:
                        Intent intent2 = new Intent(v.getContext(), status.class);
                        startActivity(intent2);
                        Log.i(MAIN_ACTIVITY, "Status button pressed");
                        break;

                    case R.id.infoButton:
                        Intent intent3 = new Intent(v.getContext(), infoActivity.class);
                        startActivity(intent3);
                        Log.i(MAIN_ACTIVITY, "Info button pressed");
                        break;
                }

        }
    }

}
