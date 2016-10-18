package com.android.albert.ng911;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Albert on 3/30/2016.
 * information activity
 */
public class infoActivity extends AppCompatActivity {
    URL url;
    BufferedReader reader;
    StringBuilder builder;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarInfo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ng911icon2);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        mListView = (ListView) findViewById(R.id.info_list);
        mListView.setAdapter(new InfoAdapter(this));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //Setting item click listener, each click intent is created...
            @Override
            public void onItemClick(AdapterView arg0, View arg1, int position,
                                    long arg3) {
                Intent i = new Intent(infoActivity.this, InfoDetail.class);
                //passing position variable to the intent
                i.putExtra("position", position);
                startActivity(i);
            }
        });
        try {
            url = new URL("http://www.webmd.com/heart-disease/features/5-emergencies-do-you-know-what-to-do");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        reader = null;
        StringBuilder builder = new StringBuilder();
        /*try {
            reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            for (String line; (line = reader.readLine()) != null; ) {
                builder.append(line.trim());
            }
            //close reader.close()!!

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public class InfoAdapter extends BaseAdapter {

        private Context mContext;
        private LayoutInflater mInflator;
        private InformationDataSource mInformationDataSource;

        public InfoAdapter(Context c) {
            mContext = c;
            //instantiate layout XML
            //file into its corresponding View objects
            mInflator = (LayoutInflater)
                    mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mInformationDataSource = new InformationDataSource();
        }

        //Returns the number of items present in the data set.
        @Override
        public int getCount() {
            return mInformationDataSource.getDataSourceLength();
        }

        //Gets the data item associated with the specified position in the data set.
        @Override
        public Object getItem(int position) {
            return position;
        }

        //Gets the row id associated with the specified position.
        @Override
        public long getItemId(int position) {
            return position;
        }

        //This is used to get a view that displays the data at the specified position in the data set.
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView thumbnail;
            TextView info;

            if (convertView == null) {
                convertView = mInflator.inflate(R.layout.list_item_layout, parent,
                        false);
            }

            thumbnail = (ImageView) convertView.findViewById(R.id.thumb);
            thumbnail.setImageResource(mInformationDataSource.getmPhotoPool().get(position));
            info = (TextView) convertView.findViewById(R.id.text);
            info.setText(mInformationDataSource.getmInfoPool().get(position));
            return convertView;
        }

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
                NavUtils.navigateUpFromSameTask(this);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

