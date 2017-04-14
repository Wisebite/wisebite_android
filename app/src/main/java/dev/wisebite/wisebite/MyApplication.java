package dev.wisebite.wisebite;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by albert on 14/04/17.
 *
 * @author albert
 */
public class MyApplication extends Application {
    
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    
}
