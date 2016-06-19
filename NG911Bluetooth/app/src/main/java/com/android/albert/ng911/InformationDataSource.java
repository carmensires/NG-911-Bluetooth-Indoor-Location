package com.android.albert.ng911;

import java.util.ArrayList;

/**
 * Created by Albert on 2/18/2016.
 * Getters and Setters for using the arrays of pictures and text
 */
public class InformationDataSource {

    //Create ArrayList of integers
    private ArrayList<Integer> mPhotoPool;
    private ArrayList<Integer> mInfoPool;
    private ArrayList<Integer> mPhotoHdPool;

    public ArrayList<Integer> getmPhotoHdPool() {
        return mPhotoHdPool;
    }

    public ArrayList<Integer> getmPhotoPool() {
        return mPhotoPool;
    }

    public ArrayList<Integer> getmInfoPool() {
        return mInfoPool;
    }

    //Constructor initializes ArrayLists and calls functions
    public InformationDataSource() {
        mPhotoPool = new ArrayList();
        mInfoPool = new ArrayList();
        mPhotoHdPool = new ArrayList();
        setupPhotoPool();
        setupInfoPool();
        setupPhotoHDPool();

    }

    public int getDataSourceLength() {
        return mPhotoPool.size();
    }

    //Fill Arrays with the data (images or quotes)
    private void setupPhotoPool() {
        mPhotoPool.add(R.drawable.infopic1);
        mPhotoPool.add(R.drawable.infopic2);
        mPhotoPool.add(R.drawable.infopic3);
        mPhotoPool.add(R.drawable.infopic4);
        mPhotoPool.add(R.drawable.infopic5);
    }
    private void setupInfoPool() {
        mInfoPool.add(R.string.info1);
        mInfoPool.add(R.string.info2);
        mInfoPool.add(R.string.info3);
        mInfoPool.add(R.string.info4);
        mInfoPool.add(R.string.info5);
    }
    private void setupPhotoHDPool() {
        mPhotoHdPool.add(R.drawable.infopic1);
        mPhotoHdPool.add(R.drawable.infopic2);
        mPhotoHdPool.add(R.drawable.infopic3);
        mPhotoHdPool.add(R.drawable.infopic4);
        mPhotoHdPool.add(R.drawable.infopic5);
    }
}
