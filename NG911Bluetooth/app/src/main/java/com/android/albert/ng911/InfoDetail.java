package com.android.albert.ng911;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Albert on 2/28/2016.
 * After clicking on some of the scroll view options in the infoActivity activity we will come up to this activity
 * opening the information from the clicked location.
 */
public class InfoDetail extends AppCompatActivity {
    private ImageView mImageView;
    private TextView mInfo;
    private int mPosition;
    private InformationDataSource mInformationDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infodetail);
        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarInfo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ng911icon2);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //Use main intent and retrieve the position sent by QuoteReader
        Intent i = getIntent();
        mPosition = i.getIntExtra("position", 0);

        mInformationDataSource = new InformationDataSource();
        mImageView = (ImageView) findViewById(R.id.image);
        mInfo = (TextView) findViewById(R.id.info);

        mImageView.setImageResource(mInformationDataSource.getmPhotoHdPool().
                get(mPosition));

        mInfo.setText(getResources().getString(mInformationDataSource.getmInfoPool()
                .get(mPosition)));
    }

}


