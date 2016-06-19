package org.sipdroid;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import com.android.albert.ng911.*;

public class SipdroidApplication extends Application {
    public static SipdroidApplication instance;

    public SipdroidApplication() {
        instance = this;
    }

    public static SipdroidApplication getInstance() {
        return instance;
    }
}