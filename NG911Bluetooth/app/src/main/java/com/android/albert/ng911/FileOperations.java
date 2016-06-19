package com.android.albert.ng911;

/**
 * Created by Albert on 4/7/2016.
 */

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileOperations {
    Context c;

    public FileOperations(Context c) {
        this.c = c;
    }

    public Boolean write(String fname, String fcontent) {
        String content = fcontent;
        FileOutputStream outputStream;
        try {
            outputStream = c.openFileOutput(fname, c.getApplicationContext().MODE_PRIVATE);
            outputStream.write(content.getBytes());
            Log.i("Write", content + " saved as " + fname + ".txt");
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public String read(String fname){
            FileInputStream fis = null;
            String line = null;
            try {
                fis = c.openFileInput(fname+".txt");
            } catch (FileNotFoundException |NullPointerException e) {
                return "";
            }
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException |NullPointerException e) {
                return "";
            }
            return sb.toString();
        }

    }

